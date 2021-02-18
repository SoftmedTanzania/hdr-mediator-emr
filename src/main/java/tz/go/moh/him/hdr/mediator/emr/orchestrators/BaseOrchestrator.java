package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class BaseOrchestrator extends UntypedActor {
    /**
     * The mediator configuration.
     */
    protected final MediatorConfig config;

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Represents a list of error messages, if any,that have been caught during payload data validation to be returned to the source system as response.
     */
    protected List<ErrorMessage> errorMessages = new ArrayList<>();

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */
    protected MediatorHTTPRequest originalRequest;

    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject errorMessageResource;

    /**
     * Simple Date Format used for payloads to be sent to the Health Data Repository
     */
    protected SimpleDateFormat hdrDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Initializes a new instance of the {@link BaseOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public BaseOrchestrator(MediatorConfig config) {
        this.config = config;
        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        try {
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles checking for the correct date string format from a varierity of formats
     *
     * @param dateString of the date
     * @return the matching date string format
     */
    public static String CheckDateFormatStrings(String dateString) {
        List<String> formatStrings = Arrays.asList("yyyy-MM-dd HH:mm:ss:ms", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd","yyyyMMdd");

        for (String formatString : formatStrings) {
            try {
                Date date = new SimpleDateFormat(formatString).parse(dateString);
                return formatString;
            } catch (ParseException e) {
                //Invalid Date String
            }
        }

        return "";
    }

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */
    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + originalRequest.getHost() + " " + originalRequest.getMethod() + " " + originalRequest.getPath());

            //Converting the received request body to POJO List
            List<?> objects = new ArrayList<>();
            try {
                objects = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        originalRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(objects));

            List<?> validatedObjects;
            if (objects.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        originalRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(objects);
            }
            sendDataToHdr(validatedObjects);
        } else {
            unhandled(msg);
        }
    }

    /**
     * Abstract method to handle Convertion the msg string payload to Correct POJO list
     *
     * @param msg payload to be converted
     * @return list of POJO
     * @throws IOException if an I/O exception occurs
     */
    protected abstract List<?> convertMessageBodyToPojoList(String msg) throws IOException;

    /**
     * Abstract method to handle data validations
     *
     * @param receivedList array list of the objects to be validated
     * @return list of valid objects that passed data validations
     */
    protected abstract List<?> validateData(List<?> receivedList);

    /**
     * Abstract method to handle converting of the valid payloads that passed data validations to the format required by HDR
     *
     * @param openHimClientId  openHIMClient id of the system that initiated the request
     * @param validatedObjects list of valid objects to be added to the payload to be sent to HDR
     * @return HDR Request Message object to be sent to HDR
     */
    protected abstract HdrRequestMessage parseMessage(String openHimClientId, List<?> validatedObjects);

    /**
     * Method that handles sending of data to the HDR
     *
     * @param validatedObjects list of objects that passed data validations to be sent to HDR
     */
    private void sendDataToHdr(List<?> validatedObjects) {
        if (!errorMessages.isEmpty()) {
            FinishRequest finishRequest = new FinishRequest(new Gson().toJson(errorMessages), "text/plain", HttpStatus.SC_BAD_REQUEST);
            (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
        } else {
            log.info("Sending data to Hdr Actor");
            HdrRequestMessage hdrRequestMessage = parseMessage((originalRequest).getHeaders().get("x-openhim-clientid"), validatedObjects);
            ActorRef actor = getContext().actorOf(Props.create(HdrActor.class, config));
            actor.tell(
                    new SimpleMediatorRequest<>(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            hdrRequestMessage), getSelf());
        }
    }
}
