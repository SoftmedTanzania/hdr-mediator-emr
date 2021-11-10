package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.HdrCPTRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.HdrResponse;


public class CPTInboundOrchestrator extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            FinishRequest finishRequest = null;
            try {
                ObjectMapper mapper = new ObjectMapper();

                HdrCPTRequest HdrCPTRequest = mapper.readValue(((MediatorHTTPRequest) msg).getBody(), HdrCPTRequest.class);

                HdrResponse hdrResponse = new HdrResponse(HttpStatus.SC_OK, "Success", "CPT");

                finishRequest = new FinishRequest(new Gson().toJson(hdrResponse), "text/json", HttpStatus.SC_OK);
            } catch (Exception e) {
                HdrResponse hdrResponse = new HdrResponse(HttpStatus.SC_BAD_REQUEST, "Failed", "CPT");
                finishRequest = new FinishRequest(new Gson().toJson(hdrResponse), "text/json", HttpStatus.SC_BAD_REQUEST);
            } finally {
                ((MediatorHTTPRequest) msg).getRequestHandler().tell(finishRequest, getSelf());
            }
        } else {
            unhandled(msg);
        }
    }
}
