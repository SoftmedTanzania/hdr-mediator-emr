package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import org.openhim.mediator.engine.testing.MockLauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GenericEmrPayloadOrchestratorTest extends BaseTest {

    /**
     * Sets up the hdr mock actor with the expected message type
     *
     * @param expectedMessageType to be verified by the hdr mock in the received request body
     */
    private void setupHdrMock(String expectedMessageType) {
        system.actorOf(Props.create(MockLauncherMock.class, MockHdr.class, expectedMessageType, "http-connector"), testConfig.getName());
    }

    /**
     * Tests sending service received payload to the Generic Emr Payload Mediator
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testServiceReceivedHTTPRequest() throws Exception {
        String servicesReceivedCsvPayload =
                "Message Type,Org Name,Local Org ID,Dept ID,Dept Name,Pat ID,Gender,DOB,Med SVC Code,ICD10 Code,Service Date\n" +
                        "SVCREC,Muhimbili,105651-4,80,Radiology,1,Male,19900131,\"002923, 00277, 002772\",\"A17.8, M60.1\",20201224";

        assertNotNull(testConfig);
        setupHdrMock("SVCREC");
        new JavaTestKit(system) {{
            createActorAndSendRequest(system, testConfig, getRef(), servicesReceivedCsvPayload, GenericEmrPayloadOrchestrator.class, "/hdr_mediator");

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

    /**
     * Tests sending bed occupancy payload to the Generic Emr Payload Mediator
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testSBedOccupancyHTTPRequest() throws Exception {
        String bedOccupancyCsvPayload =
                "Message Type,Org Name,Local Org ID,Pat ID,Admission Date,Discharge Date,Ward ID,Ward Name\n" +
                        "BEDOCC,Muhimbili,105651-4,1,20201220,20201225,1,Pediatric";

        assertNotNull(testConfig);
        setupHdrMock("BEDOCC");
        new JavaTestKit(system) {{
            createActorAndSendRequest(system, testConfig, getRef(), bedOccupancyCsvPayload, GenericEmrPayloadOrchestrator.class, "/hdr_mediator");

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

    /**
     * Tests sending daily death count payload to the Generic Emr Payload Mediator
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testDailyDeathCountHTTPRequest() throws Exception {
        String dailyDeathCountCsvPayload =
                "Message Type,Org Name,Local Org ID,Ward ID,Ward Name,Pat ID,Gender,Disease Code,DOB,Date Death Occurred\n" +
                        "DDC,Muhimbili,105651-4,1,Pediatric,1,Male,B50.9,19850101,20201225";

        assertNotNull(testConfig);
        setupHdrMock("DDC");
        new JavaTestKit(system) {{
            createActorAndSendRequest(system, testConfig, getRef(), dailyDeathCountCsvPayload, GenericEmrPayloadOrchestrator.class, "/hdr_mediator");

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

    /**
     * Tests sending revenue received payload to the Generic Emr Payload Mediator
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testRevenueReceivedHTTPRequest() throws Exception {
        String revenueReceivedCsvPayload =
                "Message Type,System Trans ID,Org Name,Local Org ID,Transaction Date,Pat ID,Gender,DOB,Med Svc Code,Payer ID,Exemption Category ID,Billed Amount,Waived Amount\n" +
                        "REV,12231,Muhimbili,105651-4,20201225,1,Male,19890101,\"002923, 00277, 002772\",33,47,10000.00,0.00";

        assertNotNull(testConfig);
        setupHdrMock("REV");
        new JavaTestKit(system) {{
            createActorAndSendRequest(system, testConfig, getRef(), revenueReceivedCsvPayload, GenericEmrPayloadOrchestrator.class, "/hdr_mediator");

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

    private static class MockLauncherMock extends MockLauncher {

        public MockLauncherMock(List<ActorToLaunch> actorsToLaunch) {
            super(actorsToLaunch);
        }

        public MockLauncherMock(Class<Object> aClass, String expectedMessageType, String name) {
            super(new ArrayList<>());
            this.getContext().actorOf(Props.create(aClass, expectedMessageType), name);
        }

    }

    private static class MockHdr extends MockHTTPConnector {
        private String expectedMessageType;

        public MockHdr(String expectedMessageType) {
            this.expectedMessageType = expectedMessageType;
        }

        @Override
        public String getResponse() {
            return "";
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

            assertTrue(msg.getBody().contains(expectedMessageType));
        }
    }


}