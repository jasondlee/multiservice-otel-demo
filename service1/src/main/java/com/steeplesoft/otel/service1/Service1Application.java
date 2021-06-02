package com.steeplesoft.otel.service1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.steeplesoft.otel.integration.OtelRequestFilter;

@ApplicationPath("/api")
public class Service1Application extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>(1);
        Arrays.asList(RestEndpoint1.class,
//                OtelProducer.class,
                OtelRequestFilter.class
//                TracerProducer.class
        ).forEach(classes::add);
        return classes;
    }
}
