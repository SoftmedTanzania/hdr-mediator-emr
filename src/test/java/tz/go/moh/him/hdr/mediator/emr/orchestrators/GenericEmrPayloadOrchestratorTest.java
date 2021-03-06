package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GenericEmrPayloadOrchestratorTest extends BaseOrchestratorTest {
    /**
     * Represents the orchestrator.
     */
    private final ActorRef orchestrator = system.actorOf(Props.create(GenericEmrPayloadOrchestrator.class, configuration));

    @Test
    public void testRevenueReceivedRequest() throws Exception {
        setupDestinationMock("RevenueReceivedRequest");

        InputStream stream = GenericEmrPayloadOrchestratorTest.class.getClassLoader().getResourceAsStream("revenue_received_request.json");
        assertNotNull(stream);
        new JavaTestKit(system) {{
            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/hdr",
                    IOUtils.toString(stream),
                    Collections.singletonMap("Content-Type", "text/plain"),
                    Collections.emptyList()
            );

            orchestrator.tell(POST_Request, getRef());

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

            InputStream responseStream = RevenueReceivedOrchestratorTest.class.getClassLoader().getResourceAsStream("success_response.json");

            assertNotNull(responseStream);

            String expectedResponse = IOUtils.toString(responseStream);

            assertNotNull(expectedResponse);

            assertTrue(Arrays.stream(out).anyMatch(c -> c instanceof FinishRequest));
            assertTrue(Arrays.stream(out).allMatch(c -> (c instanceof FinishRequest) && JsonParser.parseString(expectedResponse).equals(JsonParser.parseString(((FinishRequest) c).getResponse()))));
        }};
    }

    @Test
    public void testServiceReceivedRequest() throws Exception {
        setupDestinationMock("ServiceReceivedRequest");

        InputStream stream = ServiceReceivedOrchestratorTest.class.getClassLoader().getResourceAsStream("service_received_request.json");
        assertNotNull(stream);
        new JavaTestKit(system) {{
            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/hdr",
                    IOUtils.toString(stream),
                    Collections.singletonMap("Content-Type", "text/plain"),
                    Collections.emptyList()
            );

            orchestrator.tell(POST_Request, getRef());

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

            InputStream responseStream = ServiceReceivedOrchestratorTest.class.getClassLoader().getResourceAsStream("success_response.json");

            assertNotNull(responseStream);

            String expectedResponse = IOUtils.toString(responseStream);

            assertNotNull(expectedResponse);

            assertTrue(Arrays.stream(out).anyMatch(c -> c instanceof FinishRequest));
            assertTrue(Arrays.stream(out).allMatch(c -> (c instanceof FinishRequest) && JsonParser.parseString(expectedResponse).equals(JsonParser.parseString(((FinishRequest) c).getResponse()))));
        }};
    }
    @Test
    public void testDeathByDiseaseCasesWithinFacilityRequest() throws Exception {
        setupDestinationMock("DeathByDiseaseCasesWithinFacilityRequest");

        InputStream stream = DeathByDiseaseCasesWithinFacilityOrchestratorTest.class.getClassLoader().getResourceAsStream("death_by_disease_cases_within_facility_request.json");
        assertNotNull(stream);
        new JavaTestKit(system) {{
            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/hdr",
                    IOUtils.toString(stream),
                    Collections.singletonMap("Content-Type", "text/plain"),
                    Collections.emptyList()
            );

            orchestrator.tell(POST_Request, getRef());

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

            InputStream responseStream = DeathByDiseaseCasesWithinFacilityOrchestratorTest.class.getClassLoader().getResourceAsStream("success_response.json");

            assertNotNull(responseStream);

            String expectedResponse = IOUtils.toString(responseStream);

            assertNotNull(expectedResponse);

            assertTrue(Arrays.stream(out).anyMatch(c -> c instanceof FinishRequest));
            assertTrue(Arrays.stream(out).allMatch(c -> (c instanceof FinishRequest) && JsonParser.parseString(expectedResponse).equals(JsonParser.parseString(((FinishRequest) c).getResponse()))));
        }};
    }

    @Test
    public void testBedOccupancyRequest() throws Exception {
        setupDestinationMock("BedOccupancyRequest");

        InputStream stream = BedOccupancyOrchestrator.class.getClassLoader().getResourceAsStream("bed_occupancy_request.json");
        assertNotNull(stream);
        new JavaTestKit(system) {{
            MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/hdr",
                    IOUtils.toString(stream),
                    Collections.singletonMap("Content-Type", "text/plain"),
                    Collections.emptyList()
            );

            orchestrator.tell(POST_Request, getRef());

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

            InputStream responseStream = BedOccupancyOrchestratorTest.class.getClassLoader().getResourceAsStream("success_response.json");

            assertNotNull(responseStream);

            String expectedResponse = IOUtils.toString(responseStream);

            assertNotNull(expectedResponse);

            assertTrue(Arrays.stream(out).anyMatch(c -> c instanceof FinishRequest));
            assertTrue(Arrays.stream(out).allMatch(c -> (c instanceof FinishRequest) && JsonParser.parseString(expectedResponse).equals(JsonParser.parseString(((FinishRequest) c).getResponse()))));
        }};
    }

}