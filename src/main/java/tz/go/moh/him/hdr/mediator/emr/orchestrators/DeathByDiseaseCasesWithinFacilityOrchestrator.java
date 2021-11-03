package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.Constants;
import tz.go.moh.him.hdr.mediator.emr.domain.DeathByDiseaseCasesWithinFacilityRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;

import java.util.ArrayList;
import java.util.List;

public class DeathByDiseaseCasesWithinFacilityOrchestrator extends BaseOrchestrator {
    /**
     * Initializes a new instance of the {@link DeathByDiseaseCasesWithinFacilityOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public DeathByDiseaseCasesWithinFacilityOrchestrator(MediatorConfig config) {
        super(config);
        payloadType = Constants.DDC;
    }

    @Override
    protected Object convertMessageBodyToPojo(String msg) {
        return serializer.deserialize(msg, DeathByDiseaseCasesWithinFacilityRequest.class);
    }

    @Override
    protected List<ResultDetail> validateData(Object receivedMessage) {
        ArrayList<ResultDetail> results = new ArrayList<>();

        DeathByDiseaseCasesWithinFacilityRequest deathByDiseaseCasesWithinFacilityRequest = (DeathByDiseaseCasesWithinFacilityRequest) receivedMessage;

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityRequest.getMessageType()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "messageType"), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityRequest.getOrgName()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "orgName"), null));

        if (StringUtils.isBlank(deathByDiseaseCasesWithinFacilityRequest.getFacilityHfrCode()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "facilityHfrCode"), null));

        if (deathByDiseaseCasesWithinFacilityRequest.getItems() != null) {
            for (DeathByDiseaseCasesWithinFacilityRequest.Item item : deathByDiseaseCasesWithinFacilityRequest.getItems()) {
                if (StringUtils.isBlank(item.getDob()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "dob"), null));

                if (StringUtils.isBlank(item.getDateDeathOccurred()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "dateDeathOccurred"), null));

                if (StringUtils.isBlank(item.getWardName()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "wardName"), null));

                if (StringUtils.isBlank(item.getCauseOfDeath()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "causeOfDeath"), null));

                if (StringUtils.isBlank(item.getUnderlyingCauseOfDeath()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "underlyingCauseOfDeath"), null));

                if (StringUtils.isBlank(item.getWardId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "wardId"), null));

                if (StringUtils.isBlank(item.getPatId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "patId"), null));

                if (StringUtils.isBlank(item.getFirstName()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "firstName"), null));

                if (StringUtils.isBlank(item.getLastName()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "lastName"), null));

                if (StringUtils.isBlank(item.getGender()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "gender"), null));

            }
        }
        return results;
    }

}
