package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.ServiceReceivedCsvRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.ServiceReceivedJsonRequest;
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


public class ServiceReceivedOrchestrator extends BaseOrchestrator {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject serviceReceivedErrorMessageResource;

    /**
     * Initializes a new instance of the {@link ServiceReceivedOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ServiceReceivedOrchestrator(MediatorConfig config) {
        super(config);
        serviceReceivedErrorMessageResource = errorMessageResource.getJSONObject("SERVICE_RECEIVED_ERROR_MESSAGES");
    }

    /**
     * Validate Service Received Required Fields
     *
     * @param serviceReceivedCsvRequest to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(ServiceReceivedCsvRequest serviceReceivedCsvRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(serviceReceivedCsvRequest.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, serviceReceivedErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getDeptName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_DEPT_NAME_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getDeptID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_DEPT_ID_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getGender()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getMedSvcCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_MED_SVC_CODE_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(serviceReceivedCsvRequest.getServiceDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_CODE_IS_BLANK"), serviceReceivedCsvRequest.getPatID()), null));

        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<ServiceReceivedCsvRequest> serviceReceivedCsvRequestList = new ArrayList<>();
        try {
            Object json = new JSONTokener(msg).nextValue();
            if (json instanceof JSONObject) {

                //converting the Services Received Json Request to the normal Services Received Object also used by CSV Payloads
                ServiceReceivedJsonRequest serviceReceivedJsonRequest = new Gson().fromJson(msg, ServiceReceivedJsonRequest.class);
                for (ServiceReceivedJsonRequest.Item item : serviceReceivedJsonRequest.getItems()) {
                    ServiceReceivedCsvRequest serviceReceivedCsvRequest = new ServiceReceivedCsvRequest();
                    serviceReceivedCsvRequest.setMessageType(serviceReceivedJsonRequest.getMessageType());
                    serviceReceivedCsvRequest.setLocalOrgID(serviceReceivedJsonRequest.getLocalOrgID());
                    serviceReceivedCsvRequest.setOrgName(serviceReceivedJsonRequest.getOrgName());

                    serviceReceivedCsvRequest.setServiceDate(item.getServiceDate());
                    serviceReceivedCsvRequest.setDob(item.getDob());
                    serviceReceivedCsvRequest.setMedSvcCode(item.getMedSvcCode());
                    serviceReceivedCsvRequest.setGender(item.getGender());
                    serviceReceivedCsvRequest.setPatID(item.getPatID());
                    serviceReceivedCsvRequest.setIcd10Code(item.getIcd10Code());
                    serviceReceivedCsvRequest.setDeptID(item.getDeptID());
                    serviceReceivedCsvRequest.setDeptName(item.getDeptName());

                    serviceReceivedCsvRequestList.add(serviceReceivedCsvRequest);
                }
            } else if (json instanceof JSONArray) {
                //the payload is a JSONArray
                Type listType = new TypeToken<List<ServiceReceivedCsvRequest>>() {
                }.getType();
                serviceReceivedCsvRequestList = new Gson().fromJson((originalRequest).getBody(), listType);
            } else if (json instanceof String) {
                //the payload is a CSV string
                serviceReceivedCsvRequestList = (List<ServiceReceivedCsvRequest>) CsvAdapterUtils.csvToArrayList(msg, ServiceReceivedCsvRequest.class);
            }

        } catch (com.google.gson.JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        return serviceReceivedCsvRequestList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<ServiceReceivedCsvRequest> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            ServiceReceivedCsvRequest serviceReceivedCsvRequest = null;
            if (object != null && ServiceReceivedCsvRequest.class.isAssignableFrom(object.getClass()))
                serviceReceivedCsvRequest = (ServiceReceivedCsvRequest) object;

            if (serviceReceivedCsvRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(serviceReceivedCsvRequest));
                try {
                    if (!DateValidatorUtils.isValidPastDate(serviceReceivedCsvRequest.getServiceDate(), checkDateFormatStrings(serviceReceivedCsvRequest.getServiceDate()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_IS_NOT_A_VALID_PAST_DATE"), serviceReceivedCsvRequest.getPatID()), null));
                    } else {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(serviceReceivedCsvRequest.getServiceDate()));

                        //Reformatting the date to the format required by the HDR
                        serviceReceivedCsvRequest.setServiceDate(hdrDateFormat.format(emrDateFormat.parse(serviceReceivedCsvRequest.getServiceDate())));
                    }

                    if (!StringUtils.isBlank(serviceReceivedCsvRequest.getDob())) {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(serviceReceivedCsvRequest.getDob()));

                        //Reformatting the date to the format required by the HDR
                        serviceReceivedCsvRequest.setDob(hdrDateFormat.format(emrDateFormat.parse(serviceReceivedCsvRequest.getDob())));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_INVALID_FORMAT"), serviceReceivedCsvRequest.getPatID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }
            }

            //TODO implement additional data validations checks

            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(serviceReceivedCsvRequest);
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
        for (ServiceReceivedCsvRequest serviceReceivedCsvRequest : (List<ServiceReceivedCsvRequest>) validatedObjects) {
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-service-received");
            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));
            hdrEvent.setPayload(serviceReceivedCsvRequest);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
