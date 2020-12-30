package com.softmed.hdr_mediator_emr;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.softmed.hdr_mediator_emr.orchestrators.BedOccupancyOrchestrator;
import com.softmed.hdr_mediator_emr.orchestrators.DailyDeathCountOrchestrator;
import com.softmed.hdr_mediator_emr.orchestrators.RevenueReceivedOrchestrator;
import com.softmed.hdr_mediator_emr.orchestrators.ServiceReceivedOrchestrator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.MediatorServer;
import org.openhim.mediator.engine.RegistrationConfig;
import org.openhim.mediator.engine.RoutingTable;
import org.openhim.mediator.engine.StartupActorsConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MediatorMain {

    private static RoutingTable buildRoutingTable() throws RoutingTable.RouteAlreadyMappedException {
        RoutingTable routingTable = new RoutingTable();

        routingTable.addRoute("hdr_mediator/service_received", ServiceReceivedOrchestrator.class);
        routingTable.addRoute("hdr_mediator/daily_death_count", DailyDeathCountOrchestrator.class);
        routingTable.addRoute("hdr_mediator/bed_occupancy", BedOccupancyOrchestrator.class);
        routingTable.addRoute("hdr_mediator/revenue_received", RevenueReceivedOrchestrator.class);

        return routingTable;
    }

    private static StartupActorsConfig buildStartupActorsConfig() {
        //You can Add additional startup single instance actors here
        return new StartupActorsConfig();
    }

    private static MediatorConfig loadConfig(String configPath) throws IOException, RoutingTable.RouteAlreadyMappedException {
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
        if (config.getProperty("core.api.port") != null) {
            config.setCoreAPIPort(Integer.parseInt(config.getProperty("core.api.port")));
        }

        config.setRoutingTable(buildRoutingTable());
        config.setStartupActors(buildStartupActorsConfig());

        InputStream regInfo = MediatorMain.class.getClassLoader().getResourceAsStream("mediator-registration-info.json");
        RegistrationConfig regConfig = null;
        if (regInfo != null) {
            regConfig = new RegistrationConfig(regInfo);
        }
        config.setRegistrationConfig(regConfig);

        if (config.getProperty("mediator.heartbeats") != null && "true".equalsIgnoreCase(config.getProperty("mediator.heartbeats"))) {
            config.setHeartbeatsEnabled(true);
        }

        return config;
    }

    public static void main(String... args) throws Exception {
        //setup actor system
        final ActorSystem system = ActorSystem.create("mediator");
        //setup logger for main
        final LoggingAdapter log = Logging.getLogger(system, "main");

        //setup actors
        log.info("Initializing mediator actors...");

        String configPath = null;
        if (args.length == 2 && args[0].equals("--conf")) {
            configPath = args[1];
            log.info("Loading mediator configuration from '" + configPath + "'...");
        } else {
            log.info("No configuration specified. Using default properties...");
        }

        MediatorConfig config = loadConfig(configPath);

        //TODO this should be removed in production environments it is unsafe
        config.setSSLContext(new MediatorConfig.SSLContext(true));

        final MediatorServer server = new MediatorServer(system, config);

        //setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting down mediator");
                server.stop();
                system.shutdown();
            }
        });

        log.info("Starting mediator server...");
        server.start();

        log.info(String.format("%s listening on %s:%s", config.getName(), config.getServerHost(), config.getServerPort()));
        Thread.currentThread().join();
    }
}
