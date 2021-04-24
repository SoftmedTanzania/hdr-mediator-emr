package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.hdr.mediator.emr.Constants;
import tz.go.moh.him.hdr.mediator.emr.domain.RevenueReceivedRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;

import java.util.ArrayList;
import java.util.List;

public class RevenueReceivedOrchestrator extends BaseOrchestrator {
    /**
     * Initializes a new instance of the {@link RevenueReceivedOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public RevenueReceivedOrchestrator(MediatorConfig config) {
        super(config);
        payloadType = Constants.REV;
    }

    @Override
    protected Object convertMessageBodyToPojo(String msg) {
        return serializer.deserialize(msg, RevenueReceivedRequest.class);
    }

    @Override
    protected List<ResultDetail> validateData(Object receivedObject) {
        ArrayList<ResultDetail> results = new ArrayList<>();

        RevenueReceivedRequest revenueReceivedRequest = (RevenueReceivedRequest) receivedObject;

        if (StringUtils.isBlank(revenueReceivedRequest.getMessageType()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "messageType"), null));

        if (StringUtils.isBlank(revenueReceivedRequest.getOrgName()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "orgName"), null));

        if (StringUtils.isBlank(revenueReceivedRequest.getFacilityHfrCode()))
            results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "facilityHfrCode"), null));

        if (revenueReceivedRequest.getItems() != null) {
            for (RevenueReceivedRequest.Item item : revenueReceivedRequest.getItems()) {
                if (StringUtils.isBlank(item.getSystemTransId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "systemTransId"), null));

                if (StringUtils.isBlank(item.getTransactionDate()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "transactionDate"), null));

                if (StringUtils.isBlank(item.getPayerId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "payerId"), null));

                if (StringUtils.isBlank(item.getExemptionCategoryId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "exemptionCategoryId"), null));

                if (StringUtils.isBlank(item.getServiceProviderRankingId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "serviceProviderRankingId"), null));

                if (StringUtils.isBlank(item.getPatId()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "patId"), null));

                if (StringUtils.isBlank(item.getGender()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "gender"), null));

                if (StringUtils.isBlank(item.getDob()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "dob"), null));

                if (StringUtils.isBlank(item.getMedSvcCode()))
                    results.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("NN_ERR01"), "medSvcCode"), null));

            }
        }
        return results;
    }

}
