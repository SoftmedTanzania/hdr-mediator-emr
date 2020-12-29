package com.softmed.hdr_mediator_emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.hdr_mediator_emr.domain.ServiceReceived;
import com.softmed.hdr_mediator_emr.messages.HdrRequestMessage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONArray;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ServiceReceivedOrchestrator extends BaseOrchestrator {
    public ServiceReceivedOrchestrator(MediatorConfig config) {
        super(config);
    }


    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<ServiceReceived> serviceReceivedList;
        try {
            Type listType = new TypeToken<List<ServiceReceived>>() {
            }.getType();
            serviceReceivedList = new Gson().fromJson((originalRequest).getBody(), listType);
        } catch (com.google.gson.JsonSyntaxException ex) {
            serviceReceivedList = (List<ServiceReceived>) CsvAdapterUtils.csvToArrayList(msg, ServiceReceived.class);
        }
        return serviceReceivedList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<ServiceReceived> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ServiceReceived serviceReceived = null;
            if (object != null && ServiceReceived.class.isAssignableFrom(object.getClass()))
                serviceReceived = (ServiceReceived) object;

            if (!departmentIDMappingValidation(serviceReceived)) {
                errorMessage += serviceReceived.getPatID() + " - Department not mapped;";
                continue;
            }

            if (!DateValidatorUtils.isValidPastDate(serviceReceived.getServiceDate(), "yyyymmdd")) {
                errorMessage += serviceReceived.getPatID() + " - service date is of invalid format/is not a valid past date;";
                continue;
            }
            //TODO implement additional data validations checks
            validReceivedList.add(serviceReceived);
        }
        return validReceivedList;
    }

    @Override
    protected HdrRequestMessage parseMessage(String openHimClientId, List<?> validatedObjects) throws IOException, XmlPullParserException {
        //create hdr messages and send them to HDR

        HdrRequestMessage.HdrClient hdrClient = new HdrRequestMessage.HdrClient();
        hdrClient.setName(openHimClientId);
        hdrClient.setOpenHimClientId(openHimClientId);

        List<HdrRequestMessage.HdrEvent> hdrEvents = new ArrayList<>();
        for (ServiceReceived serviceReceived : (List<ServiceReceived>) validatedObjects) {
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-service-received");
            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));

            hdrEvent.setMediatorVersion(model.getVersion());
            hdrEvent.setJson(serviceReceived);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
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
