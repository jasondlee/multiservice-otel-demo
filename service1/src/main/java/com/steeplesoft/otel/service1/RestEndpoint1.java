/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.steeplesoft.otel.service1;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Path("/endpoint1")
public class RestEndpoint1 {
    @Inject
    private OpenTelemetry openTelemetry;

    @Inject
    private Tracer tracer;

    @GET
    public String method1() {

        final Span span = tracer.spanBuilder("Doing some work")
                .startSpan();

        span.makeCurrent();
        span.setAttribute("in.my", "application");
        span.addEvent("Test Event");
        doSomeMoreWork();
        span.addEvent("After work");

        String service2 = sendRequest();
        doEvenMoreWork();

        span.end();

        return "Hello World, from service 1! Service 2 happened to say '" + service2 + "'. ";
    }

    private void doSomeMoreWork() {
        final Span span = tracer.spanBuilder("Doing some more work")
                .startSpan();
        span.makeCurrent();
        doEvenMoreWork();
        span.end();
    }

    private void doEvenMoreWork() {
        final Span span = tracer.spanBuilder("Doing even more work")
                .startSpan();
        span.makeCurrent();
        span.end();
    }

    private String sendRequest() {
        Client client = ClientBuilder.newClient();
        String response = client
                .target("http://localhost:8080/service2-1.0-SNAPSHOT/endpoint2")
                .request(MediaType.TEXT_PLAIN)
                .get()
                .readEntity(String.class);
        client.close();
        return response;
    }

}
