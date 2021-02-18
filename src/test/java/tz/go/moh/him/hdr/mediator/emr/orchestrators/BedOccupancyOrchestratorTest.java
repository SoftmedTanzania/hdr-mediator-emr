package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import org.openhim.mediator.engine.testing.MockLauncher;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.hdr.mediator.emr.domain.BedOccupancy;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BedOccupancyOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Pat ID,Admission Date,Discharge Date,Ward ID,Ward Name\n" +
                    "BEDOCC,Muhimbili,105651-4,1,20201220,20201225,1,Pediatric";

    protected JSONObject bedOccupancyErrorMessageResource;

    @Override
    public void before() throws Exception {
        super.before();

        bedOccupancyErrorMessageResource = errorMessageResource.getJSONObject("BED_OCCUPANCY_ERROR_MESSAGES");
        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockHdr.class));
        TestingUtils.launchActors(system, testConfig.getName(), toLaunch);
    }

    @After
    public void after() {
        TestingUtils.clearRootContext(system, testConfig.getName());
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testMediatorCsvPayloadHTTPRequest() throws Exception {
        assertNotNull(testConfig);
        new JavaTestKit(system) {{
            createActorAndSendRequest(system, testConfig, getRef(), csvPayload, BedOccupancyOrchestrator.class, "/bed_occupancy");

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            boolean foundResponse = false;

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    foundResponse = true;
                    break;
                }
            }

            assertTrue("Must send FinishRequest", foundResponse);
        }};
    }

    @Test
    public void testMediatorJSONPayloadHTTPRequest() throws Exception {
        assertNotNull(testConfig);
        new JavaTestKit(system) {{

            String jsonPayload = "{\"messageType\":\"BEDOCC\",\"orgName\":\"Muhimbili\",\"localOrgID\":\"105651-4\",\"items\":[{\"wardId\":\"1\",\"wardName\":\"Pediatric\",\"patID\":\"1\",\"admissionDate\":\"20201220\",\"dischargeDate\":\"20201225\"}]}";
            createActorAndSendRequest(system, testConfig, getRef(), jsonPayload, BedOccupancyOrchestrator.class, "/bed_occupancy");

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            int responseStatus = 0;

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    responseStatus = ((FinishRequest) o).getResponseStatus();
                    break;
                }
            }

            assertEquals(200, responseStatus);
        }};
    }

    @Test
    public void testInValidAdmissionDate() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidAdmissionDate =
                    "Message Type,Org Name,Local Org ID,Pat ID,Admission Date,Discharge Date,Ward ID,Ward Name\n" +
                            "BEDOCC,Muhimbili,105651-4,1,20501220,20201225,1,Pediatric";
            createActorAndSendRequest(system, testConfig, getRef(), invalidAdmissionDate, BedOccupancyOrchestrator.class, "/bed_occupancy");

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            int responseStatus = 0;
            String responseMessage = "";

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    responseStatus = ((FinishRequest) o).getResponseStatus();
                    responseMessage = ((FinishRequest) o).getResponse();
                    break;
                }
            }

            assertEquals(400, responseStatus);
            System.out.println(String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_NOT_A_VALID_PAST_DATE"), 1));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_NOT_A_VALID_PAST_DATE"), 1)));
        }};
    }

    @Test
    public void testInValidPayload() throws Exception {
        String invalidPayload = "Message Type";
        testInvalidPayload(BedOccupancyOrchestrator.class, invalidPayload, "/bed_occupancy");
    }


    @Test
    public void validateRequiredFields() {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidPayload = "Message Type,Org Name,Local Org ID,Pat ID,Admission Date,Discharge Date,Ward ID,Ward Name\n" +
                    ",,,,,,,";
            createActorAndSendRequest(system, testConfig, getRef(), invalidPayload, BedOccupancyOrchestrator.class, "/bed_occupancy");

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            String responseMessage = "";
            int responseStatus = 0;

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    responseStatus = ((FinishRequest) o).getResponseStatus();
                    responseMessage = ((FinishRequest) o).getResponse();
                    break;
                }
            }

            assertEquals(400, responseStatus);
            assertTrue(responseMessage.contains(bedOccupancyErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK")));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_WARD_NAME_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_WARD_ID_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(bedOccupancyErrorMessageResource.getString("ERROR_ADMISSION_DATE_IS_BLANK"), "")));
        }};
    }

    private static class MockHdr extends MockHTTPConnector {
        private final String response;
        private final List<BedOccupancy> payloadConvertedIntoArrayList;

        public MockHdr() throws IOException {
            response = "successful test response";
            payloadConvertedIntoArrayList = (List<BedOccupancy>) CsvAdapterUtils.csvToArrayList(csvPayload, BedOccupancy.class);

        }

        @Override
        public String getResponse() {
            return response;
        }

        @Override
        public Integer getStatus() {
            return 200;
        }

        @Override
        public Map<String, String> getHeaders() {
            return Collections.emptyMap();
        }

        @Override
        public void executeOnReceive(MediatorHTTPRequest msg) {
            System.out.println(msg.getBody());
            JSONObject messageJsonObject = new JSONObject(msg.getBody());
            JSONObject objectPayload = messageJsonObject.getJSONArray("hdrEvents").getJSONObject(0).getJSONObject("payload");

            BedOccupancy expectedPayload = payloadConvertedIntoArrayList.get(0);

            BedOccupancy receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload), BedOccupancy.class);

            assertEquals(expectedPayload.getPatID(), receivedObjectInMessage.getPatID());
            assertEquals(expectedPayload.getWardName(), receivedObjectInMessage.getWardName());
            assertEquals(expectedPayload.getWardId(), receivedObjectInMessage.getWardId());
            assertEquals(expectedPayload.getMessageType(), receivedObjectInMessage.getMessageType());
            assertEquals(expectedPayload.getLocalOrgID(), receivedObjectInMessage.getLocalOrgID());
            assertEquals(expectedPayload.getOrgName(), receivedObjectInMessage.getOrgName());
            assertEquals("2020-12-20T00:00:00", receivedObjectInMessage.getAdmissionDate());
            assertEquals("2020-12-25T00:00:00", receivedObjectInMessage.getDischargeDate());

            System.out.println("message is okay ");
        }
    }
}
