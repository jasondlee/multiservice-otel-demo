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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessorBuilder;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

@ApplicationScoped
public class OpenTelemetryProducer {
    @javax.annotation.Resource(lookup="java:app/AppName")
    private String applicationName;

    private volatile OpenTelemetry openTelemetry;

    @Produces
    public OpenTelemetry getOpenTelemetryInstance() {
        OpenTelemetry localRef = openTelemetry;
        if (localRef == null) {
            synchronized (this) {
                localRef = openTelemetry;
                if (localRef == null) {
                    openTelemetry = localRef = localBuild();
                }
            }
        }

        return localRef;
    }

    private OpenTelemetrySdk localBuild() {
        final JaegerGrpcSpanExporterBuilder exporterBuilder = JaegerGrpcSpanExporter.builder();
        final BatchSpanProcessorBuilder spanProcessorBuilder = BatchSpanProcessor.builder(exporterBuilder.build());

        final SdkTracerProviderBuilder tracerProviderBuilder = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessorBuilder.build())
                .setResource(Resource.create(Attributes.of(
                        ResourceAttributes.SERVICE_NAME, applicationName)));

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProviderBuilder.build())
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }
}


