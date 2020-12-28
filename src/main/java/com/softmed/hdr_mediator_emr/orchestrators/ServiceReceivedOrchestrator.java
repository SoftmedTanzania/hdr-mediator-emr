package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.hdr_mediator_emr.domain.ServiceReceived;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceReceivedOrchestrator extends UntypedActor {
    private final MediatorConfig config;
    private final List<ServiceReceived> validReceivedList = new ArrayList<>();
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private String errorMessage = "";


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
            validateData(serviceReceivedList);

            FinishRequest finishRequest;
            if (!errorMessage.isEmpty()) {
                finishRequest = new FinishRequest("Failed to process the following entries with patient ids: " + errorMessage, "text/plain", HttpStatus.SC_BAD_REQUEST);
            } else {
                finishRequest = new FinishRequest("SUCCESSFUL processed the payload", "text/plain", HttpStatus.SC_OK);
            }
            ((MediatorHTTPRequest) msg).getRequestHandler().tell(finishRequest, getSelf());
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
}
