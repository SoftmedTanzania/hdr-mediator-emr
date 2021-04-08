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
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseOrchestrator extends UntypedActor {
    /**
     * The serializer.
     */
    protected static final JsonSerializer serializer = new JsonSerializer();

    /**
     * Possible date formats used by the source systems
     */
    private static List<String> formatStrings = Arrays.asList("yyyy-MM-dd HH:mm:ss:ms", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMdd");
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
    public static String checkDateFormatStrings(String dateString) {
        for (String formatString : formatStrings) {
            try {
                new SimpleDateFormat(formatString).parse(dateString);
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

            //Convert the msg to Domain Object
            Object object = convertMessageBodyToPojo(((MediatorHTTPRequest) msg).getBody());
            sendDataToHdr(object, validateData(object));
        } else {
            unhandled(msg);
        }
    }

    /**
     * Abstract method to handle Convertion the msg string payload to Correct Domain Object
     *
     * @param msg payload to be converted
     * @return domain object
     */
    protected abstract Object convertMessageBodyToPojo(String msg);

    /**
     * Abstract method to handle data validations
     *
     * @param receivedMessage the objects to be validated
     * @return list of valid objects that passed data validations
     */
    protected abstract List<ResultDetail> validateData(Object receivedMessage);

    /**
     * Method that handles sending of data to the HDR Actor
     *
     * @param objectsList   list of objects to be sent to HDR
     * @param resultDetails Result details of the data validation
     */
    private void sendDataToHdr(Object objectsList, List<ResultDetail> resultDetails) {
        // if there are any errors
        // we need to serialize the results and return
        if (resultDetails.stream().anyMatch(c -> c.getType() == ResultDetail.ResultsDetailsType.ERROR)) {
            FinishRequest finishRequest = new FinishRequest(serializer.serializeToString(resultDetails), "application/json", HttpStatus.SC_BAD_REQUEST);
            originalRequest.getRequestHandler().tell(finishRequest, getSelf());

        } else {
            log.info("Sending data to Hdr Actor");
            ActorRef actor = getContext().actorOf(Props.create(HdrActor.class, config));
            actor.tell(
                    new SimpleMediatorRequest<>(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            new Gson().toJson(objectsList)), getSelf());
        }
    }
}
