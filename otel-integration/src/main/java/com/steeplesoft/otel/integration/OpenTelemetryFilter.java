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

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.UriInfo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

@ApplicationScoped
public class OpenTelemetryFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Inject
    private OpenTelemetry openTelemetry;
    @Inject
    private Tracer tracer;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        System.out.println("In request filter...");

        Context extractedContext = openTelemetry.getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), requestContext, new TextMapGetter<>() {
                    @Override
                    public String get(ContainerRequestContext requestContext, String key) {
                        if (requestContext.getHeaders().containsKey(key)) {
                            return requestContext.getHeaders().get(key).get(0);
                        }
                        return null;
                    }

                    @Override
                    public Iterable<String> keys(ContainerRequestContext requestContext) {
                        return requestContext.getHeaders().keySet();
                    }
                });
        final UriInfo uriInfo = requestContext.getUriInfo();
        final URI requestUri = uriInfo.getRequestUri();
        final String method = requestContext.getMethod();
        final String uri = uriInfo.getPath();

        Span serverSpan = tracer.spanBuilder(method + " " + uri)
                .setSpanKind(SpanKind.SERVER)
                .setParent(extractedContext)
                .startSpan();
        serverSpan.makeCurrent();
        serverSpan.setAttribute(SemanticAttributes.HTTP_METHOD, method);
        serverSpan.setAttribute(SemanticAttributes.HTTP_SCHEME, requestUri.getScheme());
        serverSpan.setAttribute(SemanticAttributes.HTTP_HOST, requestUri.getHost() + ":" + requestUri.getPort());
        serverSpan.setAttribute(SemanticAttributes.HTTP_TARGET, uri);

        requestContext.setProperty("span", serverSpan);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        System.out.println("In response filter...");
        Object serverSpan = containerRequestContext.getProperty("span");
        if (serverSpan != null && serverSpan instanceof Span) {
            ((Span) serverSpan).end();
        }
    }
}
