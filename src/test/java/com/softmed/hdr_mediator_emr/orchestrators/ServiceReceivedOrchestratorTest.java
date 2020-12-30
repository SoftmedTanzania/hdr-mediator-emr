package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import com.softmed.hdr_mediator_emr.domain.ServiceReceived;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import org.openhim.mediator.engine.testing.MockLauncher;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.mediator.core.adapter.CsvAdapterUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.softmed.hdr_mediator_emr.Constants.ErrorMessages.ERROR_INVALID_PAYLOAD;
import static com.softmed.hdr_mediator_emr.Constants.ErrorMessages.ERROR_SERVICE_DATE_IS_OF_INVALID_FORMAT_IS_NOT_A_VALID_PAST_DATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceReceivedOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                    "SVCREC,Muhimbili,105651-4,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20201224";

    @Override
    public void before() throws Exception {
        super.before();

        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockHdr.class));
        TestingUtils.launchActors(system, testConfig.getName(), toLaunch);
    }

    @Test
    public void testMediatorHTTPRequest() throws Exception {
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
                    "/service_received",
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
    public void testInValidServiceDate() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidServiceDate =
                    "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                            "SVCREC,Muhimbili,105651-4,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20501224";
            createActorAndSendRequest(system, testConfig, getRef(), invalidServiceDate, ServiceReceivedOrchestrator.class, "/service_received");

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
            assertTrue(responseMessage.contains(ERROR_SERVICE_DATE_IS_OF_INVALID_FORMAT_IS_NOT_A_VALID_PAST_DATE));
        }};
    }

    @Test
    public void testInValidPayload() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            String invalidPayload =
                    "Message Type";
            createActorAndSendRequest(system, testConfig, getRef(), invalidPayload, ServiceReceivedOrchestrator.class, "/service_received");

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
            assertTrue(responseMessage.contains(ERROR_INVALID_PAYLOAD));
        }};
    }

    @Test
    public void validateRequiredFields() {
        ServiceReceived serviceReceived = new ServiceReceived();
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setMessageType("messageType");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setOrgName("Organization name");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setLocalOrgID("localid");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setDeptName("deptname");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setDeptID("deptId");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setPatID("patId");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setGender("Male");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setMedSvcCode("2000");
        assertFalse(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));

        serviceReceived.setServiceDate("20201201");

        //Valid payload
        assertTrue(ServiceReceivedOrchestrator.validateRequiredFields(serviceReceived));
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
            JSONObject objectPayload = messageJsonObject.getJSONArray("hdrEvents").getJSONObject(0).getJSONObject("json");

            ServiceReceived expectedPayload = payloadConvertedIntoArrayList.get(0);

            ServiceReceived receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload), ServiceReceived.class);

            assertEquals(expectedPayload.getPatID(), receivedObjectInMessage.getPatID());
            assertEquals(expectedPayload.getDeptName(), receivedObjectInMessage.getDeptName());
            assertEquals(expectedPayload.getDeptID(), receivedObjectInMessage.getDeptID());
            assertEquals(expectedPayload.getMessageType(), receivedObjectInMessage.getMessageType());
            assertEquals(expectedPayload.getDob(), receivedObjectInMessage.getDob());
            assertEquals(expectedPayload.getGender(), receivedObjectInMessage.getGender());
            assertEquals(expectedPayload.getIcd10Code(), receivedObjectInMessage.getIcd10Code());
            assertEquals(expectedPayload.getLocalOrgID(), receivedObjectInMessage.getLocalOrgID());
            assertEquals(expectedPayload.getMedSvcCode(), receivedObjectInMessage.getMedSvcCode());
            assertEquals(expectedPayload.getOrgName(), receivedObjectInMessage.getOrgName());
            assertEquals(expectedPayload.getServiceDate(), receivedObjectInMessage.getServiceDate());

            System.out.println("message is okay ");
        }
    }
}
