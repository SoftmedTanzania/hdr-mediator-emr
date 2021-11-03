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
                DeathByDiseaseCasesOutsideFacilityRequest deathByDiseaseCasesOutsideFacilityRequest = serializer.deserialize(msg.getBody(), DeathByDiseaseCasesOutsideFacilityRequest.class);
                assertEquals("DDCOUT", deathByDiseaseCasesOutsideFacilityRequest.getMessageType());
                assertEquals("Muhimbili", deathByDiseaseCasesOutsideFacilityRequest.getOrgName());
                assertEquals("105651-4", deathByDiseaseCasesOutsideFacilityRequest.getFacilityHfrCode());
                assertEquals("1", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getDeathId());
                assertEquals("1", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getPlaceOfDeathId());
                assertEquals("Z91.81", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getCauseOfDeath());
                assertEquals("B50.9", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getImmediateCauseOfDeath());
                assertEquals("C30.1", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getUnderlyingCauseOfDeath());
                assertEquals("Male", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getGender());
                assertEquals("19850101", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getDob());
                assertEquals("20201225", deathByDiseaseCasesOutsideFacilityRequest.getItems().get(0).getDateDeathOccurred());

                break;
            case "DeathByDiseaseCasesWithinFacilityRequest":
                DeathByDiseaseCasesWithinFacilityRequest deathByDiseaseCasesWithinFacilityRequest = serializer.deserialize(msg.getBody(), DeathByDiseaseCasesWithinFacilityRequest.class);
                assertEquals("DDC", deathByDiseaseCasesWithinFacilityRequest.getMessageType());
                assertEquals("Muhimbili", deathByDiseaseCasesWithinFacilityRequest.getOrgName());
                assertEquals("105651-4", deathByDiseaseCasesWithinFacilityRequest.getFacilityHfrCode());
                assertEquals("1", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getWardId());
                assertEquals("Pediatric", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getWardName());
                assertEquals("1", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getPatId());
                assertEquals("Z91.81", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getCauseOfDeath());
                assertEquals("B50.9", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getImmediateCauseOfDeath());
                assertEquals("C30.1", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getUnderlyingCauseOfDeath());
                assertEquals("Male", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getGender());
                assertEquals("19850101", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getDob());
                assertEquals("20201225", deathByDiseaseCasesWithinFacilityRequest.getItems().get(0).getDateDeathOccurred());

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
                assertEquals("00231", revenueReceivedRequest.getItems().get(0).getMedSvcCode().get(0));
                assertEquals("99415", revenueReceivedRequest.getItems().get(0).getMedSvcCode().get(1));
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
                assertEquals("20201228", serviceReceivedRequest.getItems().get(0).getServiceDate());
                assertEquals("1", serviceReceivedRequest.getItems().get(0).getServiceProviderRankingId());
                assertEquals("IPD", serviceReceivedRequest.getItems().get(0).getVisitType());


                break;
            default:
                break;
        }
    }
}