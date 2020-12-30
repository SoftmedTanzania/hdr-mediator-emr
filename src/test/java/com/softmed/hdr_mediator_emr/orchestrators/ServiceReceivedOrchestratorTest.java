package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import com.softmed.hdr_mediator_emr.domain.ServiceReceived;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openhim.mediator.engine.MediatorConfig;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceReceivedOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                    "SVCREC,Muhimbili,105651-4,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20201224";
    private static ActorSystem system;
    private MediatorConfig testConfig;

    @Before
    public void before() throws Exception {
        system = ActorSystem.create();

        testConfig = new MediatorConfig();
        testConfig.setName("hdr-mediator-emr-tests");
        testConfig.setProperties("mediator-unit-test.properties");

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
    public void testInValidMapping() throws Exception {
        assertNotNull(testConfig);
        new JavaTestKit(system) {{
            createActorAndSendRequest(system, testConfig, getRef(), csvPayload, ServiceReceivedOrchestrator.class, "/service_received");

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
