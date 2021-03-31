package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.RevenueReceivedCsvRequest;
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
     * @param revenueReceivedCsvRequest to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(RevenueReceivedCsvRequest revenueReceivedCsvRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(revenueReceivedCsvRequest.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getSystemTransID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, revenueReceivedErrorMessageResource.getString("ERROR_SYSTEM_TRANS_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getTransactionDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_TRANSACTION_DATE_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getBilledAmount()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_BILLED_AMOUNT_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getWaivedAmount()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_WAIVED_AMOUNT_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getGender()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getMedSvcCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_MED_SVC_CODE_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        if (StringUtils.isBlank(revenueReceivedCsvRequest.getPayerId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_PAYER_ID_IS_BLANK"), revenueReceivedCsvRequest.getSystemTransID()), null));

        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<RevenueReceivedCsvRequest> revenueReceivedCsvRequestList = new ArrayList<>();
        try {
            Object json = new JSONTokener(msg).nextValue();
            if (json instanceof JSONObject) {

                //converting the Revenue Received Json Request to the normal Revenue Received Object also used by CSV Payloads
                RevenueReceivedJsonRequest revenueReceivedJsonRequest = new Gson().fromJson(msg, RevenueReceivedJsonRequest.class);
                for (RevenueReceivedJsonRequest.Item item : revenueReceivedJsonRequest.getItems()) {
                    RevenueReceivedCsvRequest revenueReceivedCsvRequest = new RevenueReceivedCsvRequest();
                    revenueReceivedCsvRequest.setMessageType(revenueReceivedJsonRequest.getMessageType());
                    revenueReceivedCsvRequest.setLocalOrgID(revenueReceivedJsonRequest.getLocalOrgID());
                    revenueReceivedCsvRequest.setOrgName(revenueReceivedJsonRequest.getOrgName());

                    revenueReceivedCsvRequest.setTransactionDate(item.getTransactionDate());
                    revenueReceivedCsvRequest.setDob(item.getDob());
                    revenueReceivedCsvRequest.setBilledAmount(item.getBilledAmount());
                    revenueReceivedCsvRequest.setWaivedAmount(item.getWaivedAmount());
                    revenueReceivedCsvRequest.setExemptionCategoryId(item.getExemptionCategoryId());
                    revenueReceivedCsvRequest.setMedSvcCode(item.getMedSvcCode());
                    revenueReceivedCsvRequest.setGender(item.getGender());
                    revenueReceivedCsvRequest.setPatID(item.getPatID());
                    revenueReceivedCsvRequest.setSystemTransID(item.getSystemTransID());
                    revenueReceivedCsvRequest.setPayerId(item.getPayerId());

                    revenueReceivedCsvRequestList.add(revenueReceivedCsvRequest);
                }
            } else if (json instanceof JSONArray) {
                //the payload is a JSONArray
                Type listType = new TypeToken<List<RevenueReceivedCsvRequest>>() {
                }.getType();
                revenueReceivedCsvRequestList = new Gson().fromJson((originalRequest).getBody(), listType);
            } else if (json instanceof String) {
                //the payload is a CSV string
                revenueReceivedCsvRequestList = (List<RevenueReceivedCsvRequest>) CsvAdapterUtils.csvToArrayList(msg, RevenueReceivedCsvRequest.class);
            }
        } catch (com.google.gson.JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        return revenueReceivedCsvRequestList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<RevenueReceivedCsvRequest> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            RevenueReceivedCsvRequest revenueReceivedCsvRequest = null;
            if (object != null && RevenueReceivedCsvRequest.class.isAssignableFrom(object.getClass()))
                revenueReceivedCsvRequest = (RevenueReceivedCsvRequest) object;

            if (revenueReceivedCsvRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(revenueReceivedCsvRequest));

                try {
                    if (!DateValidatorUtils.isValidPastDate(revenueReceivedCsvRequest.getTransactionDate(), checkDateFormatStrings(revenueReceivedCsvRequest.getTransactionDate()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_TRANSACTION_DATE_IS_NOT_A_VALID_PAST_DATE"), revenueReceivedCsvRequest.getSystemTransID()), null));
                    } else {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(revenueReceivedCsvRequest.getTransactionDate()));

                        //Reformatting the date to the format required by the HDR
                        revenueReceivedCsvRequest.setTransactionDate(hdrDateFormat.format(emrDateFormat.parse(revenueReceivedCsvRequest.getTransactionDate())));
                    }

                    if (!StringUtils.isBlank(revenueReceivedCsvRequest.getDob())) {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(revenueReceivedCsvRequest.getDob()));

                        //Reformatting the date to the format required by the HDR
                        revenueReceivedCsvRequest.setDob(hdrDateFormat.format(emrDateFormat.parse(revenueReceivedCsvRequest.getDob())));
                    }

                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(revenueReceivedErrorMessageResource.getString("ERROR_TRANSACTION_DATE_INVALID_FORMAT"), revenueReceivedCsvRequest.getSystemTransID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }
            }
            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(revenueReceivedCsvRequest);
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
            RevenueReceivedCsvRequest revenueReceivedCsvRequest = null;
            if (object != null && RevenueReceivedCsvRequest.class.isAssignableFrom(object.getClass()))
                revenueReceivedCsvRequest = (RevenueReceivedCsvRequest) object;

            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();

            hdrEvent.setOpenHimClientId(openHimClientId);
            hdrEvent.setEventDate(new Date());
            hdrEvent.setEventType("save-revenue-received");

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));
            hdrEvent.setPayload(revenueReceivedCsvRequest);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
