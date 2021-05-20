package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.Constants;
import tz.go.moh.him.hdr.mediator.emr.domain.ServiceReceivedRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;

import java.util.ArrayList;
import java.util.List;

public class ServiceReceivedOrchestrator extends BaseOrchestrator {
    /**
     * Initializes a new instance of the {@link ServiceReceivedOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ServiceReceivedOrchestrator(MediatorConfig config) {
        super(config);
        payloadType = Constants.SVCREC;
    }

    @Override
    protected Object convertMessageBodyToPojo(String msg) {
        return serializer.deserialize(msg, ServiceReceivedRequest.class);
    }

    @Override
    protected List<ResultDetail> validateData(Object receivedObject) {
        ArrayList<ResultDetail> results = new ArrayList<>();

        ServiceReceivedRequest serviceReceivedRequest = (ServiceReceivedRequest) receivedObject;

        if (StringUtils.isBlank(serviceReceivedRequest.getMessageType()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "messageType"), null));

        if (StringUtils.isBlank(serviceReceivedRequest.getOrgName()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "orgName"), null));

        if (StringUtils.isBlank(serviceReceivedRequest.getFacilityHfrCode()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "facilityHfrCode"), null));

        if (serviceReceivedRequest.getItems() != null) {
            for (ServiceReceivedRequest.Item item : serviceReceivedRequest.getItems()) {
                if (StringUtils.isBlank(item.getDeptName()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "deptName"), null));

                if (StringUtils.isBlank(item.getPatId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "patId"), null));

                if (StringUtils.isBlank(item.getGender()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "gender"), null));

                if (StringUtils.isBlank(item.getDob()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "dob"), null));

                if (StringUtils.isBlank(item.getMedSvcCode()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "medSvcCode"), null));

//                if (StringUtils.isBlank(item.getIcd10Code()))
//                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "icd10Code"), null));

                if (StringUtils.isBlank(item.getServiceDate()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "serviceDate"), null));

                if (StringUtils.isBlank(item.getServiceProviderRankingId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "serviceProviderRankingId"), null));

                if (StringUtils.isBlank(item.getVisitType()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "visitType"), null));
            }
        }
        return results;
    }

}
