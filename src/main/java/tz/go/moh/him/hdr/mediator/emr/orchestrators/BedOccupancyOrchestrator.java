package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancy;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BedOccupancyOrchestrator extends BaseOrchestrator {
    protected JSONObject bedOccupancyErrorMessageResource;

    public BedOccupancyOrchestrator(MediatorConfig config) {
        super(config);
        bedOccupancyErrorMessageResource = errorMessageResource.getJSONObject("BED_OCCUPANCY_ERROR_MESSAGES");
    }

    /**
     * Validate bed Occupancy Required Fields
     *
     * @param bedOccupancy to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(BedOccupancy bedOccupancy) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(bedOccupancy.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, bedOccupancyErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));


        if (StringUtils.isBlank(bedOccupancy.getAdmissionDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_BLANK"), bedOccupancy.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancy.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), bedOccupancy.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancy.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), bedOccupancy.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancy.getWardId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_WARD_ID_IS_BLANK"), bedOccupancy.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancy.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), bedOccupancy.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancy.getWardName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_WARD_NAME_IS_BLANK"), bedOccupancy.getPatID()), null));

        return resultDetailsList;
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
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            BedOccupancy bedOccupancy = null;
            if (object != null && BedOccupancy.class.isAssignableFrom(object.getClass()))
                bedOccupancy = (BedOccupancy) object;

            if (bedOccupancy == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(bedOccupancy));

                try {
                    if (!DateValidatorUtils.isValidPastDate(bedOccupancy.getAdmissionDate(), "yyyymmdd")) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_NOT_A_VALID_PAST_DATE"), bedOccupancy.getPatID()), null));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_INVALID_FORMAT"), bedOccupancy.getPatID()), new Gson().toJson(e.getStackTrace())));
                }
            }
            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(bedOccupancy);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
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

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));

            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            hdrEvent.setPayload(bedOccupancy);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
