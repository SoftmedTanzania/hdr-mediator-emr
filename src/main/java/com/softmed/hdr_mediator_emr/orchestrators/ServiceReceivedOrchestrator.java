package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.hdr_mediator_emr.domain.ServiceReceived;
import com.softmed.hdr_mediator_emr.messages.HdrRequestMessage;
import org.apache.http.HttpStatus;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ServiceReceivedOrchestrator extends UntypedActor {
    private final MediatorConfig config;
    private final List<ServiceReceived> validReceivedList = new ArrayList<>();
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private String errorMessage = "";
    private MediatorHTTPRequest originalRequest;


    public ServiceReceivedOrchestrator(MediatorConfig config) {
        this.config = config;
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;
            List<ServiceReceived> serviceReceivedList;

            /*
             * Check whether the received payload is a valid json or not
             * his endpoint will expect either a json payload or a csv payload
             */
            try {
                Type listType = new TypeToken<List<ServiceReceived>>() {
                }.getType();
                serviceReceivedList = new Gson().fromJson((originalRequest).getBody(), listType);
            } catch (com.google.gson.JsonSyntaxException ex) {
                serviceReceivedList = (List<ServiceReceived>) CsvAdapterUtils.csvToArrayList((originalRequest).getBody(), ServiceReceived.class);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(serviceReceivedList));
            validateData(serviceReceivedList);

            if (!errorMessage.isEmpty()) {
                FinishRequest finishRequest = new FinishRequest("Failed to process the following entries with patient ids: " + errorMessage, "text/plain", HttpStatus.SC_BAD_REQUEST);
                (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
            } else {
                log.info("Sending data to Hdr Actor");
                HdrRequestMessage hdrRequestMessage = parseMessage((originalRequest).getHeaders().get("x-openhim-clientid"));
                ActorRef actor = getContext().actorOf(Props.create(HdrActor.class, config));
                actor.tell(
                        new SimpleMediatorRequest<>(
                                ((MediatorHTTPRequest) msg).getRequestHandler(),
                                getSelf(),
                                hdrRequestMessage), getSelf());
            }

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from HDR");
            originalRequest.getRequestHandler().tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }

    private void validateData(List<ServiceReceived> serviceReceivedList) {
        for (ServiceReceived serviceReceived : serviceReceivedList) {
            if (!departmentIDMappingValidation(serviceReceived)) {
                errorMessage += serviceReceived.getPatID() + " - Department not mapped;";
                continue;
            }

            //TODO implement additional data validations checks
            validReceivedList.add(serviceReceived);
        }
    }

    private boolean departmentIDMappingValidation(ServiceReceived serviceReceived) {
        Map<String, Object> mapping = config.getDynamicConfig();
        if (mapping != null && mapping.get("departmentMappings") != null) {
            JSONArray mappingJSONArray = new JSONArray(new Gson().toJson(mapping.get("departmentMappings")));
            for (int i = 0; i < mappingJSONArray.length(); i++) {
                String localDepartmentID = String.valueOf(mappingJSONArray.getJSONObject(i).getInt("localDepartmentId"));
                if (localDepartmentID.equals(serviceReceived.getDeptID()))
                    return true;
            }
            return false;
        }
        return true;
    }

    private HdrRequestMessage parseMessage(String openHimClientId) throws IOException, XmlPullParserException {
        //create hdr messages and send them to HDR

        HdrRequestMessage.HdrClient hdrClient = new HdrRequestMessage.HdrClient();
        hdrClient.setName(openHimClientId);
        hdrClient.setOpenHimClientId(openHimClientId);

        List<HdrRequestMessage.HdrEvent> hdrEvents = new ArrayList<>();
        for (ServiceReceived serviceReceived : validReceivedList) {
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-service-received");
            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));

            hdrEvent.setMediatorVersion(model.getVersion());
            hdrEvent.setPayload(new JSONObject(new Gson().toJson(serviceReceived)));

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;

    }
}
