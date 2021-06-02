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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.extension.annotations.WithSpan;

@Path("/endpoint1")
public class RestEndpoint1 {
    @Inject
    private Tracer trace;
    @Inject
    private OpenTelemetry otel;
    @javax.ws.rs.core.Context
    private UriInfo uriInfo;


    @GET
    public String getString() {
        final Span span = trace.spanBuilder("Doing some work")
                .setParent(Context.current())
                .startSpan();

        span.makeCurrent();
        span.setAttribute("in.my", "application");
        span.addEvent("Test Event");
        sleep(2);
        doSomeMoreWork();
        span.addEvent("After work");
        doEvenMoreWork();
        span.end();

        return "Hello World, from service 1!";
    }

    private void doSomeMoreWork() {
        final Span span = trace.spanBuilder("Doing some more work")
                .setParent(Context.current())
                .startSpan();
        span.makeCurrent();
        sleep(1);
        doEvenMoreWork();
        span.end();
    }

    private void doEvenMoreWork() {
        final Span span = trace.spanBuilder("Doing even more work")
                .setParent(Context.current())
                .startSpan();
        span.makeCurrent();
        sleep(8);
        span.end();
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(/*seconds * */1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
