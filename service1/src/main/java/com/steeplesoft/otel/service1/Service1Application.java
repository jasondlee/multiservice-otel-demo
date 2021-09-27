package com.steeplesoft.otel.service1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

//@ApplicationPath("/api")
public class Service1Application extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>(1);
        Arrays.asList(RestEndpoint1.class
//                RestClientFilter.class
//                OtelProducer.class,
//                OpenTelemetryFilter.class
//                TracerProducer.class
        ).forEach(classes::add);
        return classes;
    }
}
