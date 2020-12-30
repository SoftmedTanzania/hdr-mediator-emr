package com.softmed.hdr_mediator_emr.orchestrators;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.apache.commons.lang3.tuple.Pair;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {
    public void createActorAndSendRequest(ActorSystem system, MediatorConfig testConfig, ActorRef sender, String csvPayload, Class<?> type, String path) {
        final ActorRef orchestratorActor = system.actorOf(Props.create(type, testConfig));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("x-openhim-clientid", "csv-sync-service");
        MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                sender,
                sender,
                "unit-test",
                "POST",
                "http",
                null,
                null,
                path,
                csvPayload,
                headers,
                Collections.<Pair<String, String>>emptyList()
        );

        orchestratorActor.tell(POST_Request, sender);
    }
}
