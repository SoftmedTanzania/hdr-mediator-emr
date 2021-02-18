package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.hdr.mediator.emr.domain.EmrPayload;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The mediator is responsible for receiving the generic payloads requests to be sent to HDR, reading the message type of the payloads
 * and forwarding the request to the correct mediator orchestrator based on the message type for further processing and data validation.
 */
public class GenericEmrPayloadOrchestrator extends UntypedActor {
    /**
     * The mediator configuration.
     */
    private final MediatorConfig config;

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */
    protected MediatorHTTPRequest originalRequest;
    /**
     * Represents a list of error messages, if any,that have been caught to be returned to the source system as response.
     */
    protected List<ErrorMessage> errorMessages = new ArrayList<>();

    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject errorMessageResource;

    /**
     * The request handler that handles requests and responses.
     */
    private ActorRef requestHandler;

    /**
     * Initializes a new instance of the {@link GenericEmrPayloadOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public GenericEmrPayloadOrchestrator(MediatorConfig config) {
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
     * Handles the received message.
     *
     * @param msg The received message.
     */
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;
            requestHandler = originalRequest.getRequestHandler();

            log.info("Received request: " + originalRequest.getHost() + " " + originalRequest.getMethod() + " " + originalRequest.getPath());

            //Converting the received request body to POJO List of type EmrPayload
            List<EmrPayload> objects = new ArrayList<>();
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

            //Checking if there were any objects successfully converted POJOs
            if (objects.size() > 0) {

                //Obtaining a single object as a sample of objects in the payloads
                EmrPayload emrPayload = objects.get(0);

                //Reading the messageType so as to route the payload to the correct orchestrator
                ActorRef actor;
                switch (emrPayload.getMessageType()) {
                    case "BEDOCC":
                        actor = getContext().actorOf(Props.create(BedOccupancyOrchestrator.class, config));
                        log.info("Forwarding request to: " + BedOccupancyOrchestrator.class.getSimpleName());
                        break;
                    case "DDC":
                        actor = getContext().actorOf(Props.create(DeathByDiseaseCasesOrchestrator.class, config));
                        log.info("Forwarding request to: " + DeathByDiseaseCasesOrchestrator.class.getSimpleName());
                        break;
                    case "REV":
                        actor = getContext().actorOf(Props.create(RevenueReceivedOrchestrator.class, config));
                        log.info("Forwarding request to: " + RevenueReceivedOrchestrator.class.getSimpleName());
                        break;
                    case "SVCREC":
                        actor = getContext().actorOf(Props.create(ServiceReceivedOrchestrator.class, config));
                        log.info("Forwarding request to: " + ServiceReceivedOrchestrator.class.getSimpleName());
                        break;

                    default:
                        actor = null;
                        break;
                }

                //Forwarding the request to the correct orchestrator based on the payload message type
                if (actor != null) {
                    actor.tell(msg, getSelf());
                } else {
                    ErrorMessage errorMessage = new ErrorMessage(
                            originalRequest.getBody(),
                            Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_UNDEFINED"), null))
                    );
                    errorMessages.add(errorMessage);
                }
            } else { //In-case the conversion to POJO was not successful
                ErrorMessage errorMessage = new ErrorMessage(
                        originalRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null))
                );
                errorMessages.add(errorMessage);
            }

            //If any error messages were encountered, returning the error messages to the Core
            if (!errorMessages.isEmpty()) {
                FinishRequest finishRequest = new FinishRequest(new Gson().toJson(errorMessages), "text/plain", HttpStatus.SC_BAD_REQUEST);
                (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
            }

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            requestHandler.tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }

    protected List<EmrPayload> convertMessageBodyToPojoList(String msg) throws IOException {
        List<EmrPayload> payloads;
        try {
            Type listType = new TypeToken<List<EmrPayload>>() {
            }.getType();
            payloads = new Gson().fromJson((originalRequest).getBody(), listType);
        } catch (com.google.gson.JsonSyntaxException ex) {
            payloads = (List<EmrPayload>) CsvAdapterUtils.csvToArrayList(msg, EmrPayload.class);
        }
        return payloads;
    }
}
