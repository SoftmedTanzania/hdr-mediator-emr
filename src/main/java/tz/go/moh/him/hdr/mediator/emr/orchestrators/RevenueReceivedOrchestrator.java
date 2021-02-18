package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.RevenueReceived;
import tz.go.moh.him.hdr.mediator.emr.domain.RevenueReceivedJsonRequest;
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

public class RevenueReceivedOrchestrator extends BaseOrchestrator {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject revenueReceivedErrorMessageResource;

    /**
     * Initializes a new instance of the {@link RevenueReceivedOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public RevenueReceivedOrchestrator(MediatorConfig config) {
        super(config);
        revenueReceivedErrorMessageResource = errorMessageResource.getJSONObject("REVENUE_RECEIVED_ERROR_MESSAGES");
    }

    /**
     * Validate Revenue Received Required Fields
     *
     * @param revenueReceived to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(RevenueReceived revenueReceived) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(revenueReceived.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getSystemTransID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, revenueReceivedErrorMessageResource.getString("ERROR_SYSTEM_TRANS_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(revenueReceived.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getTransactionDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_TRANSACTION_DATE_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getBilledAmount()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_BILLED_AMOUNT_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getWaivedAmount()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_WAIVED_AMOUNT_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getGender()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getMedSvcCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_MED_SVC_CODE_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceived.getPayerId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_PAYER_ID_IS_BLANK"), revenueReceived.getSystemTransID()), null));

        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<RevenueReceived> revenueReceivedList = new ArrayList<>();
        try {
            Object json = new JSONTokener(msg).nextValue();
            if (json instanceof JSONObject) {

                //converting the Revenue Received Json Request to the normal Revenue Received Object also used by CSV Payloads
                RevenueReceivedJsonRequest revenueReceivedJsonRequest = new Gson().fromJson(msg, RevenueReceivedJsonRequest.class);
                for (RevenueReceivedJsonRequest.Item item : revenueReceivedJsonRequest.getItems()) {
                    RevenueReceived revenueReceived = new RevenueReceived();
                    revenueReceived.setMessageType(revenueReceivedJsonRequest.getMessageType());
                    revenueReceived.setLocalOrgID(revenueReceivedJsonRequest.getLocalOrgID());
                    revenueReceived.setOrgName(revenueReceivedJsonRequest.getOrgName());

                    revenueReceived.setTransactionDate(item.getTransactionDate());
                    revenueReceived.setDob(item.getDob());
                    revenueReceived.setBilledAmount(item.getBilledAmount());
                    revenueReceived.setWaivedAmount(item.getWaivedAmount());
                    revenueReceived.setExemptionCategoryId(item.getExemptionCategoryId());
                    revenueReceived.setMedSvcCode(item.getMedSvcCode());
                    revenueReceived.setGender(item.getGender());
                    revenueReceived.setPatID(item.getPatID());
                    revenueReceived.setSystemTransID(item.getSystemTransID());
                    revenueReceived.setPayerId(item.getPayerId());

                    revenueReceivedList.add(revenueReceived);
                }
            } else if (json instanceof JSONArray) {
                //the payload is a JSONArray
                Type listType = new TypeToken<List<RevenueReceived>>() {
                }.getType();
                revenueReceivedList = new Gson().fromJson((originalRequest).getBody(), listType);
            } else if (json instanceof String) {
                //the payload is a CSV string
                revenueReceivedList = (List<RevenueReceived>) CsvAdapterUtils.csvToArrayList(msg, RevenueReceived.class);
            }
        } catch (com.google.gson.JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        return revenueReceivedList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<RevenueReceived> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            RevenueReceived revenueReceived = null;
            if (object != null && RevenueReceived.class.isAssignableFrom(object.getClass()))
                revenueReceived = (RevenueReceived) object;

            if (revenueReceived == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(revenueReceived));

                try {
                    if (!DateValidatorUtils.isValidPastDate(revenueReceived.getTransactionDate(), CheckDateFormatStrings(revenueReceived.getTransactionDate()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_TRANSACTION_DATE_IS_NOT_A_VALID_PAST_DATE"), revenueReceived.getSystemTransID()), null));
                    } else {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(CheckDateFormatStrings(revenueReceived.getTransactionDate()));

                        //Reformatting the date to the format required by the HDR
                        revenueReceived.setTransactionDate(hdrDateFormat.format(emrDateFormat.parse(revenueReceived.getTransactionDate())));
                    }

                    if (!StringUtils.isBlank(revenueReceived.getDob())) {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(CheckDateFormatStrings(revenueReceived.getDob()));

                        //Reformatting the date to the format required by the HDR
                        revenueReceived.setDob(hdrDateFormat.format(emrDateFormat.parse(revenueReceived.getDob())));
                    }

                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_TRANSACTION_DATE_INVALID_FORMAT"), revenueReceived.getSystemTransID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }
            }
            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(revenueReceived);
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
            RevenueReceived revenueReceived = null;
            if (object != null && RevenueReceived.class.isAssignableFrom(object.getClass()))
                revenueReceived = (RevenueReceived) object;

            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();

            hdrEvent.setOpenHimClientId(openHimClientId);
            hdrEvent.setEventDate(new Date());
            hdrEvent.setEventType("save-revenue-received");

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));
            hdrEvent.setPayload(revenueReceived);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
