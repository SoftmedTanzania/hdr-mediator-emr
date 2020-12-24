package com.softmed.hdr_mediator_emr;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.hdr_mediator_emr.domain.ServiceReceived;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;

import java.lang.reflect.Type;
import java.util.List;

public class ServiceReceivedOrchestrator extends UntypedActor {
    private final MediatorConfig config;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    public ServiceReceivedOrchestrator(MediatorConfig config) {
        this.config = config;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {

            List<ServiceReceived> serviceReceivedList;

            /*
             * Check whether the received payload is a valid json or not
             * his endpoint will expect either a json payload or a csv payload
             */
            try {
                Type listType = new TypeToken<List<ServiceReceived>>() {
                }.getType();
                serviceReceivedList = new Gson().fromJson(((MediatorHTTPRequest) msg).getBody(), listType);
            } catch (com.google.gson.JsonSyntaxException ex) {
                serviceReceivedList = (List<ServiceReceived>) CsvAdapterUtils.csvToArrayList(((MediatorHTTPRequest) msg).getBody(), ServiceReceived.class);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(serviceReceivedList));

            FinishRequest finishRequest = new FinishRequest("A message from my new mediator!", "text/plain", HttpStatus.SC_OK);
            ((MediatorHTTPRequest) msg).getRequestHandler().tell(finishRequest, getSelf());
        } else {
            unhandled(msg);
        }
    }
}
