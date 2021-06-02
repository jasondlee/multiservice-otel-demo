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

package com.steeplesoft.otel.integration;

import io.grpc.Context;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class TracerProducer {
    @Inject
    private OpenTelemetry otel;
    private Object mutex = new Object();
    private Tracer tracer;

    @Produces
    public Tracer getTracer() {
        if (tracer == null) {
            synchronized (mutex) {
                if (tracer == null) {
                    tracer = otel.getTracer("com.steeplesoft.otel", "1.0.0-SNAPSHOT");
                }
            }
        }
        return tracer;
    }
}
