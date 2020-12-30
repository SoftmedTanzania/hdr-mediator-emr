package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import com.softmed.hdr_mediator_emr.domain.DailyDeathCount;
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

public class DailyDeathCountOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Ward ID,Ward Name,Pat ID,Gender,Disease Code,DOB,Date Death Occurred\n" +
                    "DDC,Muhimbili,105651-4,1,Pediatric,1,Male,B50.9,19850101,20201225";
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
            createActorAndSendRequest(system, testConfig, getRef(), csvPayload, DailyDeathCountOrchestrator.class, "/daily_death_count");

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
        private final List<DailyDeathCount> payloadConvertedIntoArrayList;

        public MockHdr() throws IOException {
            response = "successful test response";
            payloadConvertedIntoArrayList = (List<DailyDeathCount>) CsvAdapterUtils.csvToArrayList(csvPayload, DailyDeathCount.class);

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

            DailyDeathCount expectedPayload = payloadConvertedIntoArrayList.get(0);

            DailyDeathCount receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload), DailyDeathCount.class);

            assertEquals(expectedPayload.getPatID(), receivedObjectInMessage.getPatID());
            assertEquals(expectedPayload.getWardName(), receivedObjectInMessage.getWardName());
            assertEquals(expectedPayload.getWardId(), receivedObjectInMessage.getWardId());
            assertEquals(expectedPayload.getMessageType(), receivedObjectInMessage.getMessageType());
            assertEquals(expectedPayload.getDob(), receivedObjectInMessage.getDob());
            assertEquals(expectedPayload.getGender(), receivedObjectInMessage.getGender());
            assertEquals(expectedPayload.getDiseaseCode(), receivedObjectInMessage.getDiseaseCode());
            assertEquals(expectedPayload.getLocalOrgID(), receivedObjectInMessage.getLocalOrgID());
            assertEquals(expectedPayload.getOrgName(), receivedObjectInMessage.getOrgName());
            assertEquals(expectedPayload.getDateDeathOccurred(), receivedObjectInMessage.getDateDeathOccurred());

            System.out.println("message is okay ");
        }
    }
}
