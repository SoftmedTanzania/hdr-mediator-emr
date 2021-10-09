package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.HdrICD10Request;
import tz.go.moh.him.hdr.mediator.emr.domain.HdrResponse;


public class ICD10InboundOrchestrator extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            FinishRequest finishRequest = null;
            try {
                ObjectMapper mapper = new ObjectMapper();

                HdrICD10Request hdrICD10Request = mapper.readValue(((MediatorHTTPRequest) msg).getBody(), HdrICD10Request.class);

                HdrResponse hdrResponse = new HdrResponse(HttpStatus.SC_OK, hdrICD10Request.getIcd10CodeCategoryId(), "Success", "ICD10");

                finishRequest = new FinishRequest(new Gson().toJson(hdrResponse), "text/json", HttpStatus.SC_OK);
            } catch (Exception e) {
                HdrResponse hdrResponse = new HdrResponse(HttpStatus.SC_BAD_REQUEST, 0, "Failed", "ICD10");
                finishRequest = new FinishRequest(new Gson().toJson(hdrResponse), "text/json", HttpStatus.SC_BAD_REQUEST);
            } finally {
                ((MediatorHTTPRequest) msg).getRequestHandler().tell(finishRequest, getSelf());
            }
        } else {
            unhandled(msg);
        }
    }
}
