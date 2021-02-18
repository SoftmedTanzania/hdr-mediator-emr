package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.DailyDeathCount;
import tz.go.moh.him.hdr.mediator.emr.domain.DailyDeathCountJsonRequest;
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

public class DailyDeathCountOrchestrator extends BaseOrchestrator {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject dailyDeathCountErrorMessageResource;

    /**
     * Initializes a new instance of the {@link DailyDeathCountOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public DailyDeathCountOrchestrator(MediatorConfig config) {
        super(config);
        dailyDeathCountErrorMessageResource = errorMessageResource.getJSONObject("DAILY_DEATH_COUNT_ERROR_MESSAGES");
    }

    /**
     * Validate Daily Death Count Required Fields
     *
     * @param dailyDeathCount to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(DailyDeathCount dailyDeathCount) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(dailyDeathCount.getDob()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DOB_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getDateDeathOccurred()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getWardName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_WARD_NAME_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getDiseaseCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DISEASE_CODE_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getWardId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_WARD_ID_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, dailyDeathCountErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(dailyDeathCount.getGender()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), dailyDeathCount.getPatID()), null));


        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<DailyDeathCount> dailyDeathCountList = new ArrayList<>();
        try {
            Object json = new JSONTokener(msg).nextValue();
            if (json instanceof JSONObject) {

                //converting the Death by Disease Count Json Request to the normal Daily Death Count Object also used by CSV Payloads
                DailyDeathCountJsonRequest dailyDeathCountJsonRequest = new Gson().fromJson(msg, DailyDeathCountJsonRequest.class);
                for (DailyDeathCountJsonRequest.Item item : dailyDeathCountJsonRequest.getItems()) {
                    DailyDeathCount dailyDeathCount = new DailyDeathCount();
                    dailyDeathCount.setMessageType(dailyDeathCountJsonRequest.getMessageType());
                    dailyDeathCount.setLocalOrgID(dailyDeathCountJsonRequest.getLocalOrgID());
                    dailyDeathCount.setOrgName(dailyDeathCountJsonRequest.getOrgName());

                    dailyDeathCount.setDateDeathOccurred(item.getDateDeathOccurred());
                    dailyDeathCount.setDob(item.getDob());
                    dailyDeathCount.setDiseaseCode(item.getDiseaseCode());
                    dailyDeathCount.setGender(item.getGender());
                    dailyDeathCount.setPatID(item.getPatID());
                    dailyDeathCount.setWardId(item.getWardId());
                    dailyDeathCount.setWardName(item.getWardName());

                    dailyDeathCountList.add(dailyDeathCount);
                }
            } else if (json instanceof JSONArray) {
                //the payload is a JSONArray
                Type listType = new TypeToken<List<DailyDeathCount>>() {
                }.getType();
                dailyDeathCountList = new Gson().fromJson((originalRequest).getBody(), listType);
            } else if (json instanceof String) {
                //the payload is a CSV string
                dailyDeathCountList = (List<DailyDeathCount>) CsvAdapterUtils.csvToArrayList(msg, DailyDeathCount.class);
            }
        } catch (com.google.gson.JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        return dailyDeathCountList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<DailyDeathCount> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            DailyDeathCount dailyDeathCount = null;
            if (object != null && DailyDeathCount.class.isAssignableFrom(object.getClass()))
                dailyDeathCount = (DailyDeathCount) object;

            if (dailyDeathCount == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));

            } else {
                resultDetailsList.addAll(validateRequiredFields(dailyDeathCount));

                try {
                    if (!DateValidatorUtils.isValidPastDate(dailyDeathCount.getDateDeathOccurred(), CheckDateFormatStrings(dailyDeathCount.getDateDeathOccurred()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_NOT_A_VALID_PAST_DATE"), dailyDeathCount.getPatID()), null));
                    } else {

                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(CheckDateFormatStrings(dailyDeathCount.getDateDeathOccurred()));

                        //Reformatting the date to the format required by the HDR
                        dailyDeathCount.setDateDeathOccurred(hdrDateFormat.format(emrDateFormat.parse(dailyDeathCount.getDateDeathOccurred())));

                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_INVALID_FORMAT"), dailyDeathCount.getPatID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }

                try {
                    if (!DateValidatorUtils.isValidPastDate(dailyDeathCount.getDob(), CheckDateFormatStrings(dailyDeathCount.getDob()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DOB_IS_NOT_A_VALID_PAST_DATE"), dailyDeathCount.getPatID()), null));
                    } else {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(CheckDateFormatStrings(dailyDeathCount.getDob()));

                        //Reformatting the date to the format required by the HDR
                        dailyDeathCount.setDob(hdrDateFormat.format(emrDateFormat.parse(dailyDeathCount.getDob())));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DOB_INVALID_FORMAT"), dailyDeathCount.getPatID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(dailyDeathCount);
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
            DailyDeathCount dailyDeathCount = null;
            if (object != null && DailyDeathCount.class.isAssignableFrom(object.getClass()))
                dailyDeathCount = (DailyDeathCount) object;
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-daily-death-count");

            hdrEvent.setOpenHimClientId(openHimClientId);
            hdrEvent.setEventDate(new Date());

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));
            hdrEvent.setPayload(dailyDeathCount);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
