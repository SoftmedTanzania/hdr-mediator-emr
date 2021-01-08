package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.domain.ServiceReceived;
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


public class ServiceReceivedOrchestrator extends BaseOrchestrator {
    protected JSONObject serviceReceivedErrorMessageResource;

    public ServiceReceivedOrchestrator(MediatorConfig config) {
        super(config);
        serviceReceivedErrorMessageResource = errorMessageResource.getJSONObject("SERVICE_RECEIVED_ERROR_MESSAGES");
    }

    /**
     * Validate Service Received Required Fields
     *
     * @param serviceReceived to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(ServiceReceived serviceReceived) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(serviceReceived.getPatID()))
            resultDetailsList.add(new ResultDetail("Error", serviceReceivedErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK"), null));

        if (StringUtils.isBlank(serviceReceived.getMessageType()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getOrgName()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getLocalOrgID()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getDeptName()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_DEPT_NAME_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getDeptID()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_DEPT_ID_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getGender()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getMedSvcCode()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_MED_SVC_CODE_IS_BLANK"), serviceReceived.getPatID()), null));

        if (StringUtils.isBlank(serviceReceived.getServiceDate()))
            resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_CODE_IS_BLANK"), serviceReceived.getPatID()), null));

        return resultDetailsList;
    }

    @Override
    protected List<?> convertMessageBodyToPojoList(String msg) throws IOException {
        List<ServiceReceived> serviceReceivedList;
        try {
            Type listType = new TypeToken<List<ServiceReceived>>() {
            }.getType();
            serviceReceivedList = new Gson().fromJson((originalRequest).getBody(), listType);
        } catch (com.google.gson.JsonSyntaxException ex) {
            serviceReceivedList = (List<ServiceReceived>) CsvAdapterUtils.csvToArrayList(msg, ServiceReceived.class);
        }
        return serviceReceivedList;
    }

    @Override
    protected List<?> validateData(List<?> receivedList) {
        List<ServiceReceived> validReceivedList = new ArrayList<>();

        for (Object object : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(object));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            ServiceReceived serviceReceived = null;
            if (object != null && ServiceReceived.class.isAssignableFrom(object.getClass()))
                serviceReceived = (ServiceReceived) object;

            if (serviceReceived == null) {
                resultDetailsList.add(new ResultDetail("Error", errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(serviceReceived));
                try {
                    if (!DateValidatorUtils.isValidPastDate(serviceReceived.getServiceDate(), "yyyymmdd")) {
                        resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_IS_NOT_A_VALID_PAST_DATE"),serviceReceived.getPatID()), null));
                    }
                } catch (ParseException e) {
                    resultDetailsList.add(new ResultDetail("Error", String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_INVALID_FORMAT"),serviceReceived.getPatID()), new Gson().toJson(e.getStackTrace())));
                }
            }

            //TODO implement additional data validations checks

            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(serviceReceived);
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
        for (ServiceReceived serviceReceived : (List<ServiceReceived>) validatedObjects) {
            HdrRequestMessage.HdrEvent hdrEvent = new HdrRequestMessage.HdrEvent();
            hdrEvent.setEventType("save-service-received");
            hdrEvent.setEventDate(new Date());
            hdrEvent.setOpenHimClientId(openHimClientId);

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));

            hdrEvent.setMediatorVersion(model.getVersion());
            hdrEvent.setJson(serviceReceived);

            hdrEvents.add(hdrEvent);
        }

        HdrRequestMessage hdrRequestMessage = new HdrRequestMessage();
        hdrRequestMessage.setHdrClient(hdrClient);
        hdrRequestMessage.setHdrEvents(hdrEvents);

        return hdrRequestMessage;
    }

}
