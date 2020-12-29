package com.softmed.hdr_mediator_emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.hdr_mediator_emr.domain.BedOccupancy;
import com.softmed.hdr_mediator_emr.messages.HdrRequestMessage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BedOccupancyOrchestrator extends BaseOrchestrator {
    public BedOccupancyOrchestrator(MediatorConfig config) {
        super(config);
    }


    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<BedOccupancy> bedOccupancyList;
        try {
            Type listType = new TypeToken<List<BedOccupancy>>() {
            }.getType();
            bedOccupancyList = new Gson().fromJson((originalRequest).getBody(), listType);
        } catch (com.google.gson.JsonSyntaxException ex) {
            bedOccupancyList = (List<BedOccupancy>) CsvAdapterUtils.csvToArrayList(msg, BedOccupancy.class);
        }
        return bedOccupancyList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<BedOccupancy> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            BedOccupancy bedOccupancy = null;
            if (object != null && BedOccupancy.class.isAssignableFrom(object.getClass()))
                bedOccupancy = (BedOccupancy) object;

            if (!DateValidatorUtils.isValidPastDate(bedOccupancy.getAdmissionDate(), "yyyymmdd")) {
                errorMessage += bedOccupancy.getPatID() + " - date death occurred is of invalid format/is not a valid past date;";
                continue;
            }
            //TODO implement additional data validations checks
            validReceivedList.add(bedOccupancy);
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
        for (Object object : validatedObjects) {
            BedOccupancy bedOccupancy = null;
            if (object != null && BedOccupancy.class.isAssignableFrom(object.getClass()))
                bedOccupancy = (BedOccupancy) object;
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-bed-occupancy");

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            hdrEvent.setMediatorVersion(model.getVersion());

            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            hdrEvent.setJson(bedOccupancy);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
