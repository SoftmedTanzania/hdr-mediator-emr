package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.Constants;
import tz.go.moh.him.hdr.mediator.emr.domain.DeathByDiseaseCasesOutsideFacilityRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;

import java.util.ArrayList;
import java.util.List;

public class DeathByDiseaseCasesOutsideFacilityOrchestrator extends BaseOrchestrator {
    /**
     * Initializes a new instance of the {@link BaseOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public DeathByDiseaseCasesOutsideFacilityOrchestrator(MediatorConfig config) {
        super(config);
        payloadType = Constants.DDCOUT;
    }

    @Override
    protected Object convertMessageBodyToPojo(String msg) {
        return serializer.deserialize(msg, DeathByDiseaseCasesOutsideFacilityRequest.class);
    }

    @Override
    protected List<ResultDetail> validateData(Object receivedMessage) {
        ArrayList<ResultDetail> results = new ArrayList<>();

        DeathByDiseaseCasesOutsideFacilityRequest deathByDiseaseCasesOutsideFacilityRequest = (DeathByDiseaseCasesOutsideFacilityRequest) receivedMessage;

        if (StringUtils.isBlank(deathByDiseaseCasesOutsideFacilityRequest.getMessageType()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "messageType"), null));

        if (StringUtils.isBlank(deathByDiseaseCasesOutsideFacilityRequest.getOrgName()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "orgName"), null));

        if (StringUtils.isBlank(deathByDiseaseCasesOutsideFacilityRequest.getFacilityHfrCode()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "facilityHfrCode"), null));

        if (deathByDiseaseCasesOutsideFacilityRequest.getItems() != null) {
            for (DeathByDiseaseCasesOutsideFacilityRequest.Item item : deathByDiseaseCasesOutsideFacilityRequest.getItems()) {
                if (StringUtils.isBlank(item.getDob()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "dob"), null));

                if (StringUtils.isBlank(item.getDateDeathOccurred()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "dateDeathOccurred"), null));

                if (StringUtils.isBlank(item.getPlaceOfDeathId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "placeOfDeathId"), null));

//                if (StringUtils.isBlank(item.getIcd10Code()))
//                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "icd10Code"), null));

                if (StringUtils.isBlank(item.getDeathId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "deathId"), null));

                if (StringUtils.isBlank(item.getGender()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "gender"), null));

            }
        }
        return results;
    }

}
