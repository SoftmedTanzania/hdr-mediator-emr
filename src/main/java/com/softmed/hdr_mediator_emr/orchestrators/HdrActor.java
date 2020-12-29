package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.softmed.hdr_mediator_emr.messages.HdrRequestMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HdrActor extends UntypedActor {
    private final MediatorConfig config;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef requestHandler;


    public HdrActor(MediatorConfig config) {
        this.config = config;
    }

    private void forwardToHdr(String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        if (config.getProperty("hdr.secure").equals("true")) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        List<Pair<String, String>> params = new ArrayList<>();
        MediatorHTTPRequest request = new MediatorHTTPRequest(
                requestHandler, getSelf(), "HDR", "POST", scheme,
                config.getProperty("hdr.host"), Integer.parseInt(config.getProperty("hdr.api.port")), config.getProperty("hdr.api.path"),
                message, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(request, getSelf());
    }

    private void processMsg(SimpleMediatorRequest<HdrRequestMessage> msg) {
        requestHandler = msg.getRequestHandler();
        forwardToHdr(new Gson().toJson(msg.getRequestObject()));
    }

    private void finalizeResponse(MediatorHTTPResponse response) {
        requestHandler.tell(response.toFinishRequest(), getSelf());
    }


    @Override
    public void onReceive(Object msg) throws Exception {
        if (SimpleMediatorRequest.isInstanceOf(HdrRequestMessage.class, msg)) { //process message
            log.info("Sending data HDR ...");
            processMsg((SimpleMediatorRequest<HdrRequestMessage>) msg);

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from HDR");
            finalizeResponse((MediatorHTTPResponse) msg);
        } else {
            unhandled(msg);
        }
    }
}
