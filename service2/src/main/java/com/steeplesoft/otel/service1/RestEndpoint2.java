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

import java.util.Random;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.UriInfo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Path("/endpoint2")
public class RestEndpoint2 {
    @Inject
    private Tracer tracer;
    @Inject
    private Client client;

    @GET
    public String method2() {
        final Span span = tracer.spanBuilder("In service 2 doing some work.")
                .startSpan();

        span.makeCurrent();
        printSpan(span);
        span.addEvent("Service 2 has been called");
        sleep();
        doSomeMoreWork();
        span.end();

        return "Service 2 did something!";
    }

    private void doSomeMoreWork() {
        final Span span = tracer.spanBuilder("In Service 2, doing some more work")
                .startSpan();
        span.makeCurrent();
        printSpan(span);
        sleep();
        span.end();
    }

    private void sleep() {
//        try {
//            Thread.sleep(new Random().nextInt(4) * 500 + 1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void printSpan(Span serverSpan) {
//        System.out.println("\n\n\nspan = " + serverSpan);
    }
}
