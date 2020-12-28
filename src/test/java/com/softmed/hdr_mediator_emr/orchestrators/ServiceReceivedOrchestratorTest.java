package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.softmed.hdr_mediator_emr.orchestrators.ServiceReceivedOrchestrator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.*;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class ServiceReceivedOrchestratorTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMediatorHTTPRequest() throws Exception {
        new JavaTestKit(system) {{
            final MediatorConfig testConfig = new MediatorConfig("hdr-mediator-emr", "localhost", 3000);
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
}
