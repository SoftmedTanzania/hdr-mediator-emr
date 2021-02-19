package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import org.openhim.mediator.engine.testing.MockLauncher;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.hdr.mediator.emr.domain.ServiceReceived;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceReceivedOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                    "SVCREC,Muhimbili,105651-4,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20201224";
    protected JSONObject serviceReceivedErrorMessageResource;

    @Override
    public void before() throws Exception {
        super.before();

        serviceReceivedErrorMessageResource = errorMessageResource.getJSONObject("SERVICE_RECEIVED_ERROR_MESSAGES");
        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockHdr.class));
        TestingUtils.launchActors(system, testConfig.getName(), toLaunch);
    }

    @Test
    public void testMediatorCsvPayloadHTTPRequest() throws Exception {
        assertNotNull(testConfig);
        new JavaTestKit(system) {{
            final ActorRef serviceReceivedOrchestrator = system.actorOf(Props.create(ServiceReceivedOrchestrator.class, testConfig));
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "text/plain");
            headers.put("x-openhim-clientid", "csv-sync-service");
            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/hdr-service-received",
                    csvPayload,
                    headers,
                    Collections.<Pair<String, String>>emptyList()
            );

            serviceReceivedOrchestrator.tell(POST_Request, getRef());

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
            String jsonPayload = "{\"messageType\":\"SVCREC\",\"orgName\":\"Muhimbili\",\"localOrgID\":\"105651-4\",\"items\":[{\"deptName\":\"Radiology\",\"deptID\":\"80\",\"patID\":\"1\",\"gender\":\"Male\",\"dob\":\"19900131\",\"medSvcCode\":\"002923, 00277, 002772\",\"icd10Code\":\"A17.8, M60.1\",\"serviceDate\":\"20201224\"}]}";

            final ActorRef serviceReceivedOrchestrator = system.actorOf(Props.create(ServiceReceivedOrchestrator.class, testConfig));
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "text/plain");
            headers.put("x-openhim-clientid", "csv-sync-service");
            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/hdr-service-received",
                    jsonPayload,
                    headers,
                    Collections.<Pair<String, String>>emptyList()
            );

            serviceReceivedOrchestrator.tell(POST_Request, getRef());


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
    public void testInValidServiceDate() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidServiceDate =
                    "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                            "SVCREC,Muhimbili,105651-4,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20501224";
            createActorAndSendRequest(system, testConfig, getRef(), invalidServiceDate, ServiceReceivedOrchestrator.class, "/hdr-service-received");

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
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_IS_NOT_A_VALID_PAST_DATE"), 1)));
        }};
    }

    @Test
    public void testInValidPayload() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidPayload = "Message Type";
            createActorAndSendRequest(system, testConfig, getRef(), invalidPayload, ServiceReceivedOrchestrator.class, "/hdr-service-received");

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
            assertTrue(responseMessage.contains(errorMessageResource.getString("ERROR_INVALID_PAYLOAD")));
        }};
    }

    @Test
    public void validateRequiredFields() {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidPayload = "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                    ",,,,,,,,,,";

            createActorAndSendRequest(system, testConfig, getRef(), invalidPayload, ServiceReceivedOrchestrator.class, "/hdr-service-received");

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
            assertTrue(responseMessage.contains(serviceReceivedErrorMessageResource.getString("ERROR_PATIENT_ID_IS_BLANK")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_ORG_NAME_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_LOCAL_ORG_ID_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_DEPT_NAME_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_DEPT_ID_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_GENDER_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_MED_SVC_CODE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(serviceReceivedErrorMessageResource.getString("ERROR_SVC_DATE_CODE_IS_BLANK"), "")));
        }};
    }


    private static class MockHdr extends MockHTTPConnector {
        private final String response;
        private final List<ServiceReceived> payloadConvertedIntoArrayList;

        public MockHdr() throws IOException {
            response = "successful test response";
            payloadConvertedIntoArrayList = (List<ServiceReceived>) CsvAdapterUtils.csvToArrayList(csvPayload, ServiceReceived.class);

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

            ServiceReceived expectedPayload = payloadConvertedIntoArrayList.get(0);

            ServiceReceived receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload), ServiceReceived.class);

            assertEquals(expectedPayload.getPatID(), receivedObjectInMessage.getPatID());
            assertEquals(expectedPayload.getDeptName(), receivedObjectInMessage.getDeptName());
            assertEquals(expectedPayload.getDeptID(), receivedObjectInMessage.getDeptID());
            assertEquals(expectedPayload.getMessageType(), receivedObjectInMessage.getMessageType());
            assertEquals("1990-01-31T00:00:00", receivedObjectInMessage.getDob());
            assertEquals(expectedPayload.getGender(), receivedObjectInMessage.getGender());
            assertEquals(expectedPayload.getIcd10Code(), receivedObjectInMessage.getIcd10Code());
            assertEquals(expectedPayload.getLocalOrgID(), receivedObjectInMessage.getLocalOrgID());
            assertEquals(expectedPayload.getMedSvcCode(), receivedObjectInMessage.getMedSvcCode());
            assertEquals(expectedPayload.getOrgName(), receivedObjectInMessage.getOrgName());
            assertEquals("2020-12-24T00:00:00", receivedObjectInMessage.getServiceDate());

            System.out.println("message is okay ");
        }
    }
}
