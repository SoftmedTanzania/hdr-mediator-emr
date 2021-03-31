package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancyCsvRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancyJsonRequest;
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

public class BedOccupancyOrchestrator extends BaseOrchestrator {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject bedOccupancyErrorMessageResource;

    /**
     * Initializes a new instance of the {@link BedOccupancyOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public BedOccupancyOrchestrator(MediatorConfig config) {
        super(config);
        bedOccupancyErrorMessageResource = errorMessageResource.getJSONObject("BED_OCCUPANCY_ERROR_MESSAGES");
    }

    /**
     * Validate bed Occupancy Required Fields
     *
     * @param bedOccupancyCsvRequest to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(BedOccupancyCsvRequest bedOccupancyCsvRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(bedOccupancyCsvRequest.getPatID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, bedOccupancyErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));


        if (StringUtils.isBlank(bedOccupancyCsvRequest.getAdmissionDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_BLANK"), bedOccupancyCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancyCsvRequest.getMessageType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), bedOccupancyCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancyCsvRequest.getOrgName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), bedOccupancyCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancyCsvRequest.getWardId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_WARD_ID_IS_BLANK"), bedOccupancyCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancyCsvRequest.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), bedOccupancyCsvRequest.getPatID()), null));

        if (StringUtils.isBlank(bedOccupancyCsvRequest.getWardName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_WARD_NAME_IS_BLANK"), bedOccupancyCsvRequest.getPatID()), null));

        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<BedOccupancyCsvRequest> bedOccupancyCsvRequestList = new ArrayList<>();
        try {
            Object json = new JSONTokener(msg).nextValue();
            if (json instanceof JSONObject) {
                //converting the Bed Occupancy Json Request to the normal BedOccupancy Object also used by CSV Payloads
                BedOccupancyJsonRequest bedOccupancyJsonRequest = new Gson().fromJson(msg, BedOccupancyJsonRequest.class);
                for(BedOccupancyJsonRequest.Item item: bedOccupancyJsonRequest.getItems()){
                    BedOccupancyCsvRequest bedOccupancyCsvRequest = new BedOccupancyCsvRequest();
                    bedOccupancyCsvRequest.setMessageType(bedOccupancyJsonRequest.getMessageType());
                    bedOccupancyCsvRequest.setLocalOrgID(bedOccupancyJsonRequest.getLocalOrgID());
                    bedOccupancyCsvRequest.setOrgName(bedOccupancyJsonRequest.getOrgName());

                    bedOccupancyCsvRequest.setAdmissionDate(item.getAdmissionDate());
                    bedOccupancyCsvRequest.setDischargeDate(item.getDischargeDate());
                    bedOccupancyCsvRequest.setPatID(item.getPatID());
                    bedOccupancyCsvRequest.setWardId(item.getWardId());
                    bedOccupancyCsvRequest.setWardName(item.getWardName());

                    bedOccupancyCsvRequestList.add(bedOccupancyCsvRequest);
                }
            } else if (json instanceof JSONArray) {
                //the payload is a JSONArray
                Type listType = new TypeToken<List<BedOccupancyCsvRequest>>() {
                }.getType();
                bedOccupancyCsvRequestList = new Gson().fromJson((originalRequest).getBody(), listType);
            }else if (json instanceof String){
                //the payload is a CSV string
                bedOccupancyCsvRequestList = (List<BedOccupancyCsvRequest>) CsvAdapterUtils.csvToArrayList(msg, BedOccupancyCsvRequest.class);
            }
        } catch (com.google.gson.JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        return bedOccupancyCsvRequestList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<BedOccupancyCsvRequest> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            BedOccupancyCsvRequest bedOccupancyCsvRequest = null;
            if (object != null && BedOccupancyCsvRequest.class.isAssignableFrom(object.getClass()))
                bedOccupancyCsvRequest = (BedOccupancyCsvRequest) object;

            if (bedOccupancyCsvRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(bedOccupancyCsvRequest));


                try {
                    if (!DateValidatorUtils.isValidPastDate(bedOccupancyCsvRequest.getAdmissionDate(), checkDateFormatStrings(bedOccupancyCsvRequest.getAdmissionDate()))) {
                        resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_NOT_A_VALID_PAST_DATE"), bedOccupancyCsvRequest.getPatID()), null));
                    } else {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(bedOccupancyCsvRequest.getAdmissionDate()));

                        //Reformatting the date to the format required by the HDR
                        bedOccupancyCsvRequest.setAdmissionDate(hdrDateFormat.format(emrDateFormat.parse(bedOccupancyCsvRequest.getAdmissionDate())));
                    }

                    if (!StringUtils.isBlank(bedOccupancyCsvRequest.getDischargeDate())) {
                        //Simple Date Format used in payloads from EMR systems
                        SimpleDateFormat emrDateFormat = new SimpleDateFormat(checkDateFormatStrings(bedOccupancyCsvRequest.getDischargeDate()));

                        //Reformatting the date to the format required by the HDR
                        bedOccupancyCsvRequest.setDischargeDate(hdrDateFormat.format(emrDateFormat.parse(bedOccupancyCsvRequest.getDischargeDate())));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_INVALID_FORMAT"), bedOccupancyCsvRequest.getPatID()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
                }
            }
            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(bedOccupancyCsvRequest);
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
            BedOccupancyCsvRequest bedOccupancyCsvRequest = null;
            if (object != null && BedOccupancyCsvRequest.class.isAssignableFrom(object.getClass()))
                bedOccupancyCsvRequest = (BedOccupancyCsvRequest) object;
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-bed-occupancy");

            JSONObject registrationConfig = new JSONObject(config.getRegistrationConfig().getContent());
            hdrEvent.setMediatorVersion(registrationConfig.getString("version"));

            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            hdrEvent.setPayload(bedOccupancyCsvRequest);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
