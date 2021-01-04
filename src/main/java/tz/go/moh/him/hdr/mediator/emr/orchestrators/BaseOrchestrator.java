package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;

import java.io.IOException;
import java.util.List;

import static tz.go.moh.him.hdr.mediator.emr.Constants.ErrorMessages.ERROR_INVALID_PAYLOAD;

public abstract class BaseOrchestrator extends UntypedActor {
    protected final MediatorConfig config;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    protected String errorMessage = "";
    protected MediatorHTTPRequest originalRequest;


    public BaseOrchestrator(MediatorConfig config) {
        this.config = config;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;

            List<?> objects = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            log.info("Received payload in JSON = " + new Gson().toJson(objects));

            List<?> validatedObjects = validateData(objects);
            sendDataToHdr(msg, validatedObjects);
        } else {
            unhandled(msg);
        }
    }


    protected abstract List<?> convertMessageBodyToPojoList(String msg) throws IOException;

    protected abstract List<?> validateData(List<?> receivedList);

    protected abstract HdrRequestMessage parseMessage(String openHimClientId, List<?> validatedObjects) throws IOException, XmlPullParserException;

    private void sendDataToHdr(Object msg, List<?> validatedObjects) throws IOException, XmlPullParserException {
        if (!errorMessage.isEmpty()) {
            String errorMessageTobeSent = "";
            if (errorMessage.equals(ERROR_INVALID_PAYLOAD)) {
                errorMessageTobeSent = ERROR_INVALID_PAYLOAD;
            } else {
                errorMessageTobeSent = "Failed to process the following entries with patient ids: " + errorMessage;
            }
            FinishRequest finishRequest = new FinishRequest(errorMessageTobeSent, "text/plain", HttpStatus.SC_BAD_REQUEST);
            (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
        } else {
            log.info("Sending data to Hdr Actor");
            HdrRequestMessage hdrRequestMessage = parseMessage((originalRequest).getHeaders().get("x-openhim-clientid"), validatedObjects);
            ActorRef actor = getContext().actorOf(Props.create(HdrActor.class, config));
            actor.tell(
                    new SimpleMediatorRequest<>(
                            ((MediatorHTTPRequest) msg).getRequestHandler(),
                            getSelf(),
                            hdrRequestMessage), getSelf());
        }
    }
}
