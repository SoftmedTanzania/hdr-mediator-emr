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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public abstract class BaseOrchestrator extends UntypedActor {
    protected final MediatorConfig config;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    protected List<ErrorMessage> errorMessages = new ArrayList<>();
    protected MediatorHTTPRequest originalRequest;
    protected JSONObject errorMessageResource;
    protected SimpleDateFormat hdrDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    protected SimpleDateFormat emrDateFormat = new SimpleDateFormat("yyyyMMdd");

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
        hdrDateFormat.setTimeZone(TimeZone.getTimeZone("+0300"));
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
