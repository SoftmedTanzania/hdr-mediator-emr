package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HdrActor extends UntypedActor {
    /**
     * The mediator configuration.
     */
    private final MediatorConfig config;

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * The request handler that handles requests and responses.
     */
    private ActorRef requestHandler;


    /**
     * Initializes a new instance of the {@link HdrActor} class.
     *
     * @param config The mediator configuration.
     */
    public HdrActor(MediatorConfig config) {
        this.config = config;
    }

    /**
     * Forwards the message to the Health Data Repository
     *
     * @param message to be sent to the HDR
     */
    private void forwardToHdr(String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        String host;
        String path;
        int portNumber;
        if (config.getDynamicConfig().isEmpty()) {
            if (config.getProperty("hdr.secure").equals("true")) {
                scheme = "https";
            } else {
                scheme = "http";
            }

            host = config.getProperty("hdr.host");
            portNumber = Integer.parseInt(config.getProperty("hdr.api.port"));
            path = config.getProperty("hdr.api.path");
        } else {
            JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("hdrConnectionProperties");

            host = connectionProperties.getString("hdrHost");
            portNumber = connectionProperties.getInt("hdrPort");
            path = connectionProperties.getString("hdrPath");
            scheme = connectionProperties.getString("hdrScheme");
        }

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToHdrRequest = new MediatorHTTPRequest(
                requestHandler, getSelf(), "Sending Data to the HDR Server", "POST", scheme,
                host, portNumber, path,
                message, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToHdrRequest, getSelf());
    }

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */
    @Override
    public void onReceive(Object msg) throws Exception {
        if (SimpleMediatorRequest.isInstanceOf(HdrRequestMessage.class, msg)) { //process message
            log.info("Sending data HDR ...");
            requestHandler = ((SimpleMediatorRequest) msg).getRequestHandler();
            forwardToHdr(new Gson().toJson(((SimpleMediatorRequest) msg).getRequestObject()));

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from HDR");
            requestHandler.tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
