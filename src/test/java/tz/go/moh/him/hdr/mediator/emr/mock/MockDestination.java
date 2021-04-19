package tz.go.moh.him.hdr.mediator.emr.mock;

import org.apache.commons.io.IOUtils;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancyRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.DeathByDiseaseCasesOutsideFacilityRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.DeathByDiseaseCasesWithinFacilityRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.RevenueReceivedRequest;
import tz.go.moh.him.hdr.mediator.emr.domain.ServiceReceivedRequest;
import tz.go.moh.him.hdr.mediator.emr.orchestrators.BedOccupancyOrchestrator;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Represents a mock destination.
 */
public class MockDestination extends MockHTTPConnector {
    /**
     * The serializer.
     */
    protected static final JsonSerializer serializer = new JsonSerializer();

    /**
     * The expected message type
     */
    private final String expectedMessageType;

    public MockDestination(String expectedMessageType) {
        this.expectedMessageType = expectedMessageType;
    }

    /**
     * Gets the response.
     *
     * @return Returns the response.
     */
    @Override
    public String getResponse() {
        try {
            return IOUtils.toString(BedOccupancyOrchestrator.class.getClassLoader().getResourceAsStream("success_response.json"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the status code.
     *
     * @return Returns the status code.
     */
    @Override
    public Integer getStatus() {
        return 200;
    }

    /**
     * Gets the HTTP headers.
     *
     * @return Returns the HTTP headers.
     */
    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    /**
     * Handles the message.
     *
     * @param msg The message.
     */
    @Override
    public void executeOnReceive(MediatorHTTPRequest msg) {
        switch (expectedMessageType) {
            case "BedOccupancyRequest":
                BedOccupancyRequest bedOccupancyRequest = serializer.deserialize(msg.getBody(), BedOccupancyRequest.class);
                assertEquals("BEDOCC", bedOccupancyRequest.getMessageType());
                assertEquals("Muhimbili", bedOccupancyRequest.getOrgName());
                assertEquals("105651-4", bedOccupancyRequest.getFacilityHfrCode());
                assertEquals("1", bedOccupancyRequest.getItems().get(0).getWardId());
                assertEquals("Pediatric", bedOccupancyRequest.getItems().get(0).getWardName());
                assertEquals("1", bedOccupancyRequest.getItems().get(0).getPatId());
                assertEquals("20201220", bedOccupancyRequest.getItems().get(0).getAdmissionDate());
                assertEquals("20201225", bedOccupancyRequest.getItems().get(0).getDischargeDate());
                break;
            case "DeathByDiseaseCasesOutsideFacilityRequest":
                DeathByDiseaseCasesOutsideFacilityRequest forecastAccuracyPerProgramRequests = serializer.deserialize(msg.getBody(), DeathByDiseaseCasesOutsideFacilityRequest.class);
                assertEquals("DDC", forecastAccuracyPerProgramRequests.getMessageType());
                assertEquals("Muhimbili", forecastAccuracyPerProgramRequests.getOrgName());
                assertEquals("105651-4", forecastAccuracyPerProgramRequests.getFacilityHfrCode());
                assertEquals("1", forecastAccuracyPerProgramRequests.getItems().get(0).getDeathId());
                assertEquals("1", forecastAccuracyPerProgramRequests.getItems().get(0).getPlaceOfDeathId());
                assertEquals("B50.9", forecastAccuracyPerProgramRequests.getItems().get(0).getIcd10Code());
                assertEquals("Male", forecastAccuracyPerProgramRequests.getItems().get(0).getGender());
                assertEquals("19850101", forecastAccuracyPerProgramRequests.getItems().get(0).getDob());
                assertEquals("20201225", forecastAccuracyPerProgramRequests.getItems().get(0).getDateDeathOccurred());

                break;
            case "DeathByDiseaseCasesWithinFacilityRequest":
                DeathByDiseaseCasesWithinFacilityRequest laboratoryDiagnosticEquipmentFunctionalityRequests = serializer.deserialize(msg.getBody(), DeathByDiseaseCasesWithinFacilityRequest.class);
                assertEquals("DDC", laboratoryDiagnosticEquipmentFunctionalityRequests.getMessageType());
                assertEquals("Muhimbili", laboratoryDiagnosticEquipmentFunctionalityRequests.getOrgName());
                assertEquals("105651-4", laboratoryDiagnosticEquipmentFunctionalityRequests.getFacilityHfrCode());
                assertEquals("1", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getWardId());
                assertEquals("Pediatric", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getWardName());
                assertEquals("1", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getPatId());
                assertEquals("B50.9", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getIcd10Code());
                assertEquals("Male", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getGender());
                assertEquals("19850101", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getDob());
                assertEquals("20201225", laboratoryDiagnosticEquipmentFunctionalityRequests.getItems().get(0).getDateDeathOccurred());

                break;
            case "RevenueReceivedRequest":
                RevenueReceivedRequest revenueReceivedRequest = serializer.deserialize(msg.getBody(), RevenueReceivedRequest.class);
                assertEquals("REV", revenueReceivedRequest.getMessageType());
                assertEquals("Muhimbili", revenueReceivedRequest.getOrgName());
                assertEquals("105651-4", revenueReceivedRequest.getFacilityHfrCode());
                assertEquals("12231", revenueReceivedRequest.getItems().get(0).getSystemTransId());
                assertEquals("20201225", revenueReceivedRequest.getItems().get(0).getTransactionDate());
                assertEquals("1", revenueReceivedRequest.getItems().get(0).getPatId());
                assertEquals("Male", revenueReceivedRequest.getItems().get(0).getGender());
                assertEquals("19890101", revenueReceivedRequest.getItems().get(0).getDob());
                assertEquals("002923", revenueReceivedRequest.getItems().get(0).getMedSvcCode());
                assertEquals("33", revenueReceivedRequest.getItems().get(0).getPayerId());
                assertEquals("47", revenueReceivedRequest.getItems().get(0).getExemptionCategoryId());
                assertEquals(10000.00, revenueReceivedRequest.getItems().get(0).getBilledAmount(), 0.01);
                assertEquals(0.00, revenueReceivedRequest.getItems().get(0).getWaivedAmount(), 0.01);
                assertEquals("1", revenueReceivedRequest.getItems().get(0).getServiceProviderRankingId());

                break;
            case "ServiceReceivedRequest":
                ServiceReceivedRequest serviceReceivedRequest = serializer.deserialize(msg.getBody(), ServiceReceivedRequest.class);
                assertEquals("SVCREC", serviceReceivedRequest.getMessageType());
                assertEquals("Muhimbili", serviceReceivedRequest.getOrgName());
                assertEquals("105651-4", serviceReceivedRequest.getFacilityHfrCode());
                assertEquals("Radiology", serviceReceivedRequest.getItems().get(0).getDeptName());
                assertEquals("80", serviceReceivedRequest.getItems().get(0).getDeptId());
                assertEquals("108627-1", serviceReceivedRequest.getItems().get(0).getPatId());
                assertEquals("Male", serviceReceivedRequest.getItems().get(0).getGender());
                assertEquals("19900101", serviceReceivedRequest.getItems().get(0).getDob());
                assertEquals("002923", serviceReceivedRequest.getItems().get(0).getMedSvcCode());
                assertEquals("A17.8, M60.1, B29", serviceReceivedRequest.getItems().get(0).getIcd10Code());
                assertEquals("20201228", serviceReceivedRequest.getItems().get(0).getServiceDate());
                assertEquals("1", serviceReceivedRequest.getItems().get(0).getServiceProviderRankingId());
                assertEquals("IPD", serviceReceivedRequest.getItems().get(0).getVisitType());


                break;
            default:
                break;
        }
    }
}