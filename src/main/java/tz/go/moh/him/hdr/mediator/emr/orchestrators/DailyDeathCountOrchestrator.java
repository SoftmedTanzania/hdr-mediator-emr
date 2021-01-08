package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.DailyDeathCount;
import tz.go.moh.him.hdr.mediator.emr.messages.HdrRequestMessage;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyDeathCountOrchestrator extends BaseOrchestrator {
    protected JSONObject dailyDeathCountErrorMessageResource;

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
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DOB_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getMessageType()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getDateDeathOccurred()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getOrgName()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getWardName()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_WARD_NAME_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getDiseaseCode()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DISEASE_CODE_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getWardId()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_WARD_ID_IS_BLANK"), dailyDeathCount.getPatID()), null));

        if (StringUtils.isBlank(dailyDeathCount.getPatID()))
            resultDetailsList.add(new ResultDetail("Error", dailyDeathCountErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(dailyDeathCount.getGender()))
            resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), dailyDeathCount.getPatID()), null));


        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<DailyDeathCount> dailyDeathCountList;
        try {
            Type listType = new TypeToken<List<DailyDeathCount>>() {
            }.getType();
            dailyDeathCountList = new Gson().fromJson((originalRequest).getBody(), listType);
        } catch (com.google.gson.JsonSyntaxException ex) {
            dailyDeathCountList = (List<DailyDeathCount>) CsvAdapterUtils.csvToArrayList(msg, DailyDeathCount.class);
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
                resultDetailsList.add(new ResultDetail("Error", errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));

            } else {
                resultDetailsList.addAll(validateRequiredFields(dailyDeathCount));

                try {
                    if (!DateValidatorUtils.isValidPastDate(dailyDeathCount.getDateDeathOccurred(), "yyyymmdd")) {
                        resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_NOT_A_VALID_PAST_DATE"), dailyDeathCount.getPatID()), null));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_INVALID_FORMAT"), dailyDeathCount.getPatID()), null));
                }

                try {
                    if (!DateValidatorUtils.isValidPastDate(dailyDeathCount.getDob(), "yyyymmdd")) {
                        resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DOB_IS_NOT_A_VALID_PAST_DATE"), dailyDeathCount.getPatID()), null));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail("Error", String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DOB_INVALID_FORMAT"), dailyDeathCount.getPatID()), null));
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
    protected HdrRequestMessage parseMessage(String openHimClientId, List<?> validatedObjects) throws IOException, XmlPullParserException {
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

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));

            hdrEvent.setMediatorVersion(model.getVersion());
            hdrEvent.setJson(dailyDeathCount);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
