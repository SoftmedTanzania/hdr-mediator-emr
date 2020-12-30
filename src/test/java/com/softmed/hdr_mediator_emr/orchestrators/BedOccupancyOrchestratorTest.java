package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import com.softmed.hdr_mediator_emr.domain.BedOccupancy;
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

public class BedOccupancyOrchestratorTest extends BaseTest {
    private static final String csvPayload =
            "Message Type,Org Name,Local Org ID,Pat ID,Admission Date,Discharge Date,Ward ID,Ward Name\n" +
                    "BEDOCC,Muhimbili,105651-4,1,20201220,20201225,1,Pediatric";
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
            JSONObject objectPayload = messageJsonObject.getJSONArray("hdrEvents").getJSONObject(0).getJSONObject("json");

            BedOccupancy expectedPayload = payloadConvertedIntoArrayList.get(0);

            BedOccupancy receivedObjectInMessage = new Gson().fromJson(String.valueOf(objectPayload), BedOccupancy.class);

            assertEquals(expectedPayload.getPatID(), receivedObjectInMessage.getPatID());
            assertEquals(expectedPayload.getWardName(), receivedObjectInMessage.getWardName());
            assertEquals(expectedPayload.getWardId(), receivedObjectInMessage.getWardId());
            assertEquals(expectedPayload.getMessageType(), receivedObjectInMessage.getMessageType());
            assertEquals(expectedPayload.getLocalOrgID(), receivedObjectInMessage.getLocalOrgID());
            assertEquals(expectedPayload.getOrgName(), receivedObjectInMessage.getOrgName());
            assertEquals(expectedPayload.getAdmissionDate(), receivedObjectInMessage.getAdmissionDate());
            assertEquals(expectedPayload.getDischargeDate(), receivedObjectInMessage.getDischargeDate());

            System.out.println("message is okay ");
        }
    }
}
