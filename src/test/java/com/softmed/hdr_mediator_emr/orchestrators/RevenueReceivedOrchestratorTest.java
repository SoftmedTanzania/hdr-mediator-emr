package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import com.softmed.hdr_mediator_emr.domain.RevenueReceived;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RevenueReceivedOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,System Trans ID,Org Name,Local Org ID,Transaction Date,Pat ID,Gender,DOB,Med Svc Code,Payer ID,Exemption Category ID,Billed Amount,Waived Amount\n" +
                    "REV,12231,Muhimbili,105651-4,20201225,1,Male,19890101,\"002923, 00277, 002772\",33,47,10000.00,0.00";
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
            createActorAndSendRequest(system, testConfig, getRef(), csvPayload, RevenueReceivedOrchestrator.class, "/revenue_received");

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
        private final List<RevenueReceived> payloadConvertedIntoArrayList;

        public MockHdr() throws IOException {
            response = "successful test response";
            payloadConvertedIntoArrayList = (List<RevenueReceived>) CsvAdapterUtils.csvToArrayList(csvPayload, RevenueReceived.class);

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

            RevenueReceived expectedPayload = payloadConvertedIntoArrayList.get(0);

            RevenueReceived receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload), RevenueReceived.class);

            assertEquals(expectedPayload.getPatID(), receivedObjectInMessage.getPatID());
            assertEquals(expectedPayload.getSystemTransID(), receivedObjectInMessage.getSystemTransID());
            assertEquals(expectedPayload.getTransactionDate(), receivedObjectInMessage.getTransactionDate());
            assertEquals(expectedPayload.getMessageType(), receivedObjectInMessage.getMessageType());
            assertEquals(expectedPayload.getDob(), receivedObjectInMessage.getDob());
            assertEquals(expectedPayload.getGender(), receivedObjectInMessage.getGender());
            assertEquals(expectedPayload.getBilledAmount(), receivedObjectInMessage.getBilledAmount());
            assertEquals(expectedPayload.getWaivedAmount(), receivedObjectInMessage.getWaivedAmount());
            assertEquals(expectedPayload.getExemptionCategoryId(), receivedObjectInMessage.getExemptionCategoryId());
            assertEquals(expectedPayload.getLocalOrgID(), receivedObjectInMessage.getLocalOrgID());
            assertEquals(expectedPayload.getOrgName(), receivedObjectInMessage.getOrgName());
            assertEquals(expectedPayload.getMedSvcCode(), receivedObjectInMessage.getMedSvcCode());
            assertEquals(expectedPayload.getPayerId(), receivedObjectInMessage.getPayerId());

            System.out.println("message is okay ");
        }
    }
}
