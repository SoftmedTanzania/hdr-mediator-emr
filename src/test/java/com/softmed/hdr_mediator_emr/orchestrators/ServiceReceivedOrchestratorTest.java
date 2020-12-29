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
import org.junit.Assert;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceReceivedOrchestratorTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                    "SVCREC,Masana,108627-1,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20201224";
    static ActorSystem system;
    MediatorConfig testConfig;

    public ServiceReceivedOrchestratorTest() {
    }

    @Before
    public void before() throws Exception {
        system = ActorSystem.create();

        testConfig = new MediatorConfig();
        testConfig.setName("hdr-mediator-emr-tests");
        testConfig.setProperties("mediator-unit-test.properties");

        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockRegistry.class));
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
        new JavaTestKit(system) {{
            final ActorRef serviceReceivedOrchestrator = system.actorOf(Props.create(ServiceReceivedOrchestrator.class, testConfig));

            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/service_received",
                    "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                            "SVCREC,Masana,108627-1,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20201224",
                    Collections.<String, String>singletonMap("Content-Type", "text/plain"),
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
                }
            }

            assertTrue("Must send FinishRequest", foundResponse);
        }};
    }

    private static class MockRegistry extends MockHTTPConnector {
        private final String response;
        private final List<ServiceReceived> payloadConvertedIntoArrayList;

        public MockRegistry() throws IOException {
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
            JSONObject messageJsonObject = new JSONObject(msg.getBody());
            JSONObject objectPayload = messageJsonObject.getJSONArray("hdrEvents").getJSONObject(0).getJSONObject("payload");

            ServiceReceived expectedPayload = payloadConvertedIntoArrayList.get(0);

            ServiceReceived receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload),ServiceReceived.class);

            Assert.assertEquals(expectedPayload.getPatID(),receivedObjectInMessage.getPatID());
            Assert.assertEquals(expectedPayload.getDeptName(),receivedObjectInMessage.getDeptName());
            Assert.assertEquals(expectedPayload.getDeptID(),receivedObjectInMessage.getDeptID());
            Assert.assertEquals(expectedPayload.getMessageType(),receivedObjectInMessage.getMessageType());
            Assert.assertEquals(expectedPayload.getDob(),receivedObjectInMessage.getDob());
            Assert.assertEquals(expectedPayload.getGender(),receivedObjectInMessage.getGender());
            Assert.assertEquals(expectedPayload.getIcd10Code(),receivedObjectInMessage.getIcd10Code());
            Assert.assertEquals(expectedPayload.getLocalOrgID(),receivedObjectInMessage.getLocalOrgID());
            Assert.assertEquals(expectedPayload.getMedSvcCode(),receivedObjectInMessage.getMedSvcCode());
            Assert.assertEquals(expectedPayload.getOrgName(),receivedObjectInMessage.getOrgName());
            Assert.assertEquals(expectedPayload.getServiceDate(),receivedObjectInMessage.getServiceDate());

            System.out.println("message is okay ");
        }
    }
}
