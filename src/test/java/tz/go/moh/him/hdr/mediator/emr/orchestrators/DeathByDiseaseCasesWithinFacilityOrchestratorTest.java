package tz.go.moh.him.hdr.mediator.emr.orchestrators;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests for the {@link DeathByDiseaseCasesWithinFacilityOrchestrator} class.
 */
public class DeathByDiseaseCasesWithinFacilityOrchestratorTest extends BaseOrchestratorTest {
    /**
     * Represents the orchestrator.
     */
    private final ActorRef orchestrator = system.actorOf(Props.create(DeathByDiseaseCasesWithinFacilityOrchestrator.class, configuration));

    /**
     * Runs initialization before each test execution.
     */
    @Before
    public void before() {
        setupDestinationMock("DeathByDiseaseCasesWithinFacilityRequest");
    }

    @Test
    public void testMediatorHTTPRequest() throws Exception {
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
    public void testBadRequest() throws Exception {
        InputStream stream = DeathByDiseaseCasesWithinFacilityOrchestratorTest.class.getClassLoader().getResourceAsStream("bad_request.json");
        assertNotNull(stream);

        assertNotNull(system);
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

            assertTrue(Arrays.stream(out).anyMatch(c -> c instanceof FinishRequest));
            assertTrue(Arrays.stream(out).allMatch(c -> (c instanceof FinishRequest) && 400 == ((FinishRequest) c).getResponseStatus()));
        }};
    }
}
