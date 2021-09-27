package com.steeplesoft.otel.service1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

//@ApplicationPath("/api")
public class Sevice2Application extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>(1);
        Arrays.asList(RestEndpoint2.class
//                OtelProducer.class,
//                OpenTelemetryFilter.class
//                TracerProducer.class
        ).forEach(classes::add);
        return classes;
    }
}
