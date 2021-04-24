package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.Constants;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancyRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;

import java.util.ArrayList;
import java.util.List;

public class BedOccupancyOrchestrator extends BaseOrchestrator {
    /**
     * Initializes a new instance of the {@link BedOccupancyOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public BedOccupancyOrchestrator(MediatorConfig config) {
        super(config);
        payloadType = Constants.BEDOCC;
    }

    @Override
    protected Object convertMessageBodyToPojo(String msg) {
        return serializer.deserialize(msg, BedOccupancyRequest.class);
    }

    @Override
    protected List<ResultDetail> validateData(Object receivedObject) {
        ArrayList<ResultDetail> results = new ArrayList<>();

        BedOccupancyRequest bedOccupancyRequest = (BedOccupancyRequest) receivedObject;

        if (StringUtils.isBlank(bedOccupancyRequest.getMessageType()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "messageType"), null));

        if (StringUtils.isBlank(bedOccupancyRequest.getOrgName()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "orgName"), null));

        if (StringUtils.isBlank(bedOccupancyRequest.getFacilityHfrCode()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "facilityHfrCode"), null));


        if (bedOccupancyRequest.getItems() != null) {
            for (BedOccupancyRequest.Item item : bedOccupancyRequest.getItems()) {

                if (StringUtils.isBlank(item.getPatId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "patId"), null));


                if (StringUtils.isBlank(item.getAdmissionDate()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "admissionDate"), null));

                if (StringUtils.isBlank(item.getWardId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "wardId"), null));

                if (StringUtils.isBlank(item.getWardName()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "wardName"), null));
            }
        }
        return results;
    }

}
