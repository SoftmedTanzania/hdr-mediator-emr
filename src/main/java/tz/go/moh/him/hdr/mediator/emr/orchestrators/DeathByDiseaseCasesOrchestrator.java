package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.DeathByDiseaseCasesWithinFacilityCsvRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.DeathByDiseaseCasesOutsideFacilityJsonRequest;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeathByDiseaseCasesOrchestrator extends BaseOrchestrator {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject deathByDiseaseCasesErrorMessageResource;

    /**
     * Initializes a new instance of the {@link DeathByDiseaseCasesOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public DeathByDiseaseCasesOrchestrator(MediatorConfig config) {
        super(config);
        deathByDiseaseCasesErrorMessageResource = errorMessageResource.getJSONObject("DEATH_BY_DISEASE_CASES_ERROR_MESSAGES");
    }

    /**
     * Validate Death by Disease Cases Required Fields
     *
     * @param deathByDiseaseCasesWithinFacilityCsvRequest to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(DeathByDiseaseCasesWithinFacilityCsvRequest deathByDiseaseCasesWithinFacilityCsvRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getDob()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DOB_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getDateDeathOccurred()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getWardName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_WARD_NAME_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getDiseaseCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DISEASE_CODE_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getWardId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_WARD_ID_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, deathByDiseaseCasesErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityCsvRequest.getGender()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));


        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<DeathByDiseaseCasesWithinFacilityCsvRequest> deathByDiseaseCasesWithinFacilityCsvRequestList = new ArrayList<>();
        try {
            Object json = new JSONTokener(msg).nextValue();
            if (json instanceof JSONObject) {

                //converting the Death by Disease Cases Json Request to the normal Death by Disease cases Object also used by CSV Payloads
                DeathByDiseaseCasesOutsideFacilityJsonRequest deathByDiseaseCasesOutsideFacilityJsonRequest = new Gson().fromJson(msg, DeathByDiseaseCasesOutsideFacilityJsonRequest.class);
                for (DeathByDiseaseCasesOutsideFacilityJsonRequest.Item item : deathByDiseaseCasesOutsideFacilityJsonRequest.getItems()) {
                    DeathByDiseaseCasesWithinFacilityCsvRequest deathByDiseaseCasesWithinFacilityCsvRequest = new DeathByDiseaseCasesWithinFacilityCsvRequest();
                    deathByDiseaseCasesWithinFacilityCsvRequest.setMessageType(deathByDiseaseCasesOutsideFacilityJsonRequest.getMessageType());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setLocalOrgID(deathByDiseaseCasesOutsideFacilityJsonRequest.getLocalOrgID());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setOrgName(deathByDiseaseCasesOutsideFacilityJsonRequest.getOrgName());

                    deathByDiseaseCasesWithinFacilityCsvRequest.setDateDeathOccurred(item.getDateDeathOccurred());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setDob(item.getDob());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setDiseaseCode(item.getDiseaseCode());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setGender(item.getGender());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setPatID(item.getPatID());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setWardId(item.getWardId());
                    deathByDiseaseCasesWithinFacilityCsvRequest.setWardName(item.getWardName());

                    deathByDiseaseCasesWithinFacilityCsvRequestList.add(deathByDiseaseCasesWithinFacilityCsvRequest);
                }
            } else if (json instanceof JSONArray) {
                //the payload is a JSONArray
                Type listType = new TypeToken<List<DeathByDiseaseCasesWithinFacilityCsvRequest>>() {
                }.getType();
                deathByDiseaseCasesWithinFacilityCsvRequestList = new Gson().fromJson((originalRequest).getBody(), listType);
            } else if (json instanceof String) {
                //the payload is a CSV string
                deathByDiseaseCasesWithinFacilityCsvRequestList = (List<DeathByDiseaseCasesWithinFacilityCsvRequest>) CsvAdapterUtils.csvToArrayList(msg, DeathByDiseaseCasesWithinFacilityCsvRequest.class);
            }
        } catch (com.google.gson.JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        return deathByDiseaseCasesWithinFacilityCsvRequestList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<DeathByDiseaseCasesWithinFacilityCsvRequest> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            DeathByDiseaseCasesWithinFacilityCsvRequest deathByDiseaseCasesWithinFacilityCsvRequest = null;
            if (object != null && DeathByDiseaseCasesWithinFacilityCsvRequest.class.isAssignableFrom(object.getClass()))
                deathByDiseaseCasesWithinFacilityCsvRequest = (DeathByDiseaseCasesWithinFacilityCsvRequest) object;

            if (deathByDiseaseCasesWithinFacilityCsvRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));

            } else {
                resultDetailsList.addAll(validateRequiredFields(deathByDiseaseCasesWithinFacilityCsvRequest));

                try {
                    if (!DateValidatorUtils.isValidPastDate(deathByDiseaseCasesWithinFacilityCsvRequest.getDateDeathOccurred(), checkDateFormatStrings(deathByDiseaseCasesWithinFacilityCsvRequest.getDateDeathOccurred()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_NOT_A_VALID_PAST_DATE"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));
                    } else {

                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(deathByDiseaseCasesWithinFacilityCsvRequest.getDateDeathOccurred()));

                        //Reformatting the date to the format required by the HDR
                        deathByDiseaseCasesWithinFacilityCsvRequest.setDateDeathOccurred(hdrDateFormat.format(emrDateFormat.parse(deathByDiseaseCasesWithinFacilityCsvRequest.getDateDeathOccurred())));

                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_INVALID_FORMAT"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }

                try {
                    if (!DateValidatorUtils.isValidPastDate(deathByDiseaseCasesWithinFacilityCsvRequest.getDob(), checkDateFormatStrings(deathByDiseaseCasesWithinFacilityCsvRequest.getDob()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DOB_IS_NOT_A_VALID_PAST_DATE"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), null));
                    } else {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(deathByDiseaseCasesWithinFacilityCsvRequest.getDob()));

                        //Reformatting the date to the format required by the HDR
                        deathByDiseaseCasesWithinFacilityCsvRequest.setDob(hdrDateFormat.format(emrDateFormat.parse(deathByDiseaseCasesWithinFacilityCsvRequest.getDob())));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(deathByDiseaseCasesErrorMessageResource.getString("ERROR_DOB_INVALID_FORMAT"), deathByDiseaseCasesWithinFacilityCsvRequest.getPatID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(deathByDiseaseCasesWithinFacilityCsvRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    @Override
    protected HdrRequestMessage parseMessage(String openHimClientId, List<?> validatedObjects) {
        //create hdr messages and send them to HDR

        HdrRequestMessage.HdrClient hdrClient = new HdrRequestMessage.HdrClient();
        hdrClient.setName(openHimClientId);
        hdrClient.setOpenHimClientId(openHimClientId);

        List<HdrRequestMessage.HdrEvent> hdrEvents = new ArrayList<>();
        for (Object object : validatedObjects) {
            DeathByDiseaseCasesWithinFacilityCsvRequest deathByDiseaseCasesWithinFacilityCsvRequest = null;
            if (object != null && DeathByDiseaseCasesWithinFacilityCsvRequest.class.isAssignableFrom(object.getClass()))
                deathByDiseaseCasesWithinFacilityCsvRequest = (DeathByDiseaseCasesWithinFacilityCsvRequest) object;
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-death-by-disease-cases");

            hdrEvent.setOpenHimClientId(openHimClientId);
            hdrEvent.setEventDate(new Date());

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));
            hdrEvent.setPayload(deathByDiseaseCasesWithinFacilityCsvRequest);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
