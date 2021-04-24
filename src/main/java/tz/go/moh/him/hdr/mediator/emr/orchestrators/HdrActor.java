package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.hdr.mediator.emr.Constants;

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
     * The message type.
     */
    private final String messageType;
    /**
     * The request handler that handles requests and responses.
     */
    private ActorRef requestHandler;


    /**
     * Initializes a new instance of the {@link HdrActor} class.
     *
     * @param config The mediator configuration.
     */
    public HdrActor(MediatorConfig config, String messageType) {
        this.config = config;
        this.messageType = messageType;
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
            switch (messageType) {
                case Constants.REV:
                    path = config.getProperty("hdr.api.path_revenue_received");
                    break;
                case Constants.SVCREC:
                    path = config.getProperty("hdr.api.path_services_received");
                    break;
                case Constants.BEDOCC:
                    path = config.getProperty("hdr.api.path_bed_occupancy");
                    break;
                case Constants.DDCOUT:
                    path = config.getProperty("hdr.api.path_death_by_disease_cases_outside_facility");
                    break;
                case Constants.DDC:
                    path = config.getProperty("hdr.api.path_death_by_disease_cases_within_facility");
                    break;
                default:
                    path = null;
                    break;
            }
        } else {
            JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("hdrConnectionProperties");

            host = connectionProperties.getString("hdrHost");
            portNumber = connectionProperties.getInt("hdrPort");
            scheme = connectionProperties.getString("hdrScheme");

            switch (messageType) {
                case Constants.REV:
                    path = connectionProperties.getString("hdrRevenueReceivedPath");
                    break;
                case Constants.SVCREC:
                    path = connectionProperties.getString("hdrServiceReceivedPath");
                    break;
                case Constants.BEDOCC:
                    path = connectionProperties.getString("hdrBedOccupancyPath");
                    break;
                case Constants.DDCOUT:
                    path = connectionProperties.getString("hdrDeathByDiseaseCasesOutsideFacilityPath");
                    break;
                case Constants.DDC:
                    path = connectionProperties.getString("hdrDeathByDiseaseCasesWithinFacilityPath");
                    break;
                default:
                    path = null;
                    break;
            }
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
        if (SimpleMediatorRequest.isInstanceOf(String.class, msg)) { //process message
            log.info("Sending data to HDR ...");
            requestHandler = ((SimpleMediatorRequest) msg).getRequestHandler();
            forwardToHdr(((SimpleMediatorRequest) msg).getRequestObject().toString());

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from HDR");
            requestHandler.tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
