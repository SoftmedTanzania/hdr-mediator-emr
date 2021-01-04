package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancy;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
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

import static tz.go.moh.him.hdr.mediator.emr.Constants.ErrorMessages.ERROR_ADMISSION_DATE_OCCURRED_IS_OF_INVALID_FORMAT_IS_NOT_A_VALID_PAST_DATE;
import static tz.go.moh.him.hdr.mediator.emr.Constants.ErrorMessages.ERROR_INVALID_PAYLOAD;
import static tz.go.moh.him.hdr.mediator.emr.Constants.ErrorMessages.ERROR_REQUIRED_FIELDS_CHECK_FAILED;

public class BedOccupancyOrchestrator extends BaseOrchestrator {
    public BedOccupancyOrchestrator(MediatorConfig config) {
        super(config);
    }

    public static boolean validateRequiredFields(BedOccupancy bedOccupancy) {
        if (StringUtils.isBlank(bedOccupancy.getMessageType()))
            return false;
        if (StringUtils.isBlank(bedOccupancy.getAdmissionDate()))
            return false;
        if (StringUtils.isBlank(bedOccupancy.getOrgName()))
            return false;
        if (StringUtils.isBlank(bedOccupancy.getLocalOrgID()))
            return false;
        if (StringUtils.isBlank(bedOccupancy.getWardId()))
            return false;
        if (StringUtils.isBlank(bedOccupancy.getWardName()))
            return false;
        return !StringUtils.isBlank(bedOccupancy.getPatID());
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

        if (receivedList == null || receivedList.size() == 0) {
            errorMessage += ERROR_INVALID_PAYLOAD;
            return receivedList;
        }

        for (Object object : receivedList) {
            BedOccupancy bedOccupancy = null;
            if (object != null && BedOccupancy.class.isAssignableFrom(object.getClass()))
                bedOccupancy = (BedOccupancy) object;

            if (bedOccupancy == null) {
                errorMessage += ERROR_INVALID_PAYLOAD;
                continue;
            }

            if (!validateRequiredFields(bedOccupancy)) {
                errorMessage += bedOccupancy.getPatID() + ERROR_REQUIRED_FIELDS_CHECK_FAILED;
                continue;
            }

            if (!DateValidatorUtils.isValidPastDate(bedOccupancy.getAdmissionDate(), "yyyymmdd")) {
                errorMessage += bedOccupancy.getPatID() + ERROR_ADMISSION_DATE_OCCURRED_IS_OF_INVALID_FORMAT_IS_NOT_A_VALID_PAST_DATE;
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
