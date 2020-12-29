package com.softmed.hdr_mediator_emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmed.hdr_mediator_emr.domain.RevenueReceived;
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

public class RevenueReceivedOrchestrator extends BaseOrchestrator {
    public RevenueReceivedOrchestrator(MediatorConfig config) {
        super(config);
    }


    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<RevenueReceived> revenueReceivedList;
        try {
            Type listType = new TypeToken<List<RevenueReceived>>() {
            }.getType();
            revenueReceivedList = new Gson().fromJson((originalRequest).getBody(), listType);
        } catch (com.google.gson.JsonSyntaxException ex) {
            revenueReceivedList = (List<RevenueReceived>) CsvAdapterUtils.csvToArrayList(msg, RevenueReceived.class);
        }
        return revenueReceivedList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<RevenueReceived> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            RevenueReceived revenueReceived = null;
            if (object != null && RevenueReceived.class.isAssignableFrom(object.getClass()))
                revenueReceived = (RevenueReceived) object;

            if (!DateValidatorUtils.isValidPastDate(revenueReceived.getTransactionDate(), "yyyymmdd")) {
                errorMessage += revenueReceived.getPatID() + " - date death occurred is of invalid format/is not a valid past date;";
                continue;
            }
            //TODO implement additional data validations checks
            validReceivedList.add(revenueReceived);
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
            RevenueReceived revenueReceived = null;
            if (object != null && RevenueReceived.class.isAssignableFrom(object.getClass()))
                revenueReceived = (RevenueReceived) object;

            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();

            hdrEvent.setOpenHimClientId(openHimClientId);
            hdrEvent.setEventDate(new Date());
            hdrEvent.setEventType("save-daily-death-count");

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));

            hdrEvent.setMediatorVersion(model.getVersion());
            hdrEvent.setJson(revenueReceived);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
