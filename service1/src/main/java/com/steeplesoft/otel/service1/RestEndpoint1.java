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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;

@Path("/endpoint1")
public class RestEndpoint1 {
    @Inject
    private Tracer tracer;
    @Inject
    private OpenTelemetry otel;


    @GET
    public String getString() {
        final Span span = tracer.spanBuilder("Doing some work")
//                .setParent(Context.current())
                .startSpan();

        span.makeCurrent();
        span.setAttribute("in.my", "application");
        span.addEvent("Test Event");
        sleep();
        doSomeMoreWork();
        span.addEvent("After work");

        String service2 = sendRequest();
        sleep();
        doEvenMoreWork();

        span.end();

        return "Hello World, from service 1! Service 2 happened to say '" + service2 + "'";
    }

    private void doSomeMoreWork() {
        final Span span = tracer.spanBuilder("Doing some more work")
//                .setParent(Context.current())
                .startSpan();
        span.makeCurrent();
        sleep();
        doEvenMoreWork();
        span.end();
    }

    private void doEvenMoreWork() {
        final Span span = tracer.spanBuilder("Doing even more work")
//                .setParent(Context.current())
                .startSpan();
        span.makeCurrent();
        sleep();
        span.end();
    }

    private HttpClient getClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    private String sendRequest() {
        TextMapSetter<HttpRequest.Builder> setter =
                (requestBuilder, key, value) -> {
                    requestBuilder.header (key, value);
                };

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/service2-1.0-SNAPSHOT/api/endpoint2"))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET();
        otel.getPropagators().getTextMapPropagator().inject(Context.current(), builder, setter);
        final HttpRequest request = builder.build();
        try {
            HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(new Random().nextInt(4) * 1000 + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
