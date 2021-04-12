package tz.go.moh.him.hdr.mediator.emr.orchestrators;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.hdr.mediator.emr.mock.CustomMockLauncher;
import tz.go.moh.him.hdr.mediator.emr.mock.MockDestination;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseOrchestratorTest {

    /**
     * Represents the configuration.
     */
    protected static MediatorConfig configuration;

    /**
     * Represents the system actor.
     */
    protected static ActorSystem system;

    /**
     * Runs cleanup after class execution.
     */
    @AfterClass
    public static void afterClass() {
        TestingUtils.clearRootContext(system, configuration.getName());
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Runs initialization before each class execution.
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        configuration = loadConfig(null);
        system = ActorSystem.create();
    }

    /**
     * Loads the mediator configuration.
     *
     * @param configPath The configuration path.
     * @return Returns the mediator configuration.
     */
    public static MediatorConfig loadConfig(String configPath) throws IOException {
        MediatorConfig config = new MediatorConfig();

        if (configPath != null) {
            Properties props = new Properties();
            File conf = new File(configPath);
            InputStream in = FileUtils.openInputStream(conf);
            props.load(in);
            IOUtils.closeQuietly(in);

            config.setProperties(props);
        } else {
            config.setProperties("mediator.properties");
        }

        config.setName(config.getProperty("mediator.name"));
        config.setServerHost(config.getProperty("mediator.host"));
        config.setServerPort(Integer.parseInt(config.getProperty("mediator.port")));
        config.setRootTimeout(Integer.parseInt(config.getProperty("mediator.timeout")));

        config.setCoreHost(config.getProperty("core.host"));
        config.setCoreAPIUsername(config.getProperty("core.api.user"));
        config.setCoreAPIPassword(config.getProperty("core.api.password"));

        config.setCoreAPIPort(Integer.parseInt(config.getProperty("core.api.port")));
        config.setHeartbeatsEnabled(true);

        return config;
    }

    /**
     * Runs cleanup after each test execution.
     */
    @After
    public void after() {
        system = ActorSystem.create();
    }

    /**
     * Sets up the hdr mock actor with the expected message type
     *
     * @param expectedMessageType to be verified by the hdr mock in the received request body
     */
    protected void setupDestinationMock(String expectedMessageType) {
        system.actorOf(Props.create(CustomMockLauncher.class, MockDestination.class, expectedMessageType, "http-connector"), configuration.getName());
    }
}
