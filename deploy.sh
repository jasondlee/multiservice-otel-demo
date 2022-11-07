#!/bin/bash

clear

jboss-cli.sh -c << EOL
        if (outcome == success) of /subsystem=microprofile-opentracing-smallrye:read-resource
            /subsystem=microprofile-opentracing-smallrye:remove()
            /extension=org.wildfly.extension.microprofile.opentracing-smallrye:remove()
        end-if


        if (outcome != success) of /subsystem=opentelemetry:read-resource
            /extension=org.wildfly.extension.opentelemetry:add()
            /subsystem=opentelemetry:add()

            reload
        end-if
EOL

mvn  package && \
    jboss-cli.sh -c "deploy --force service1/target/service1-1.0-SNAPSHOT.war" && \
    jboss-cli.sh -c "deploy --force service2/target/service2-1.0-SNAPSHOT.war" && \
    http :8080/service1-1.0-SNAPSHOT/endpoint1 && \
    http :8080/service2-1.0-SNAPSHOT/endpoint2
    #http :8080/service1-1.0-SNAPSHOT/endpoint1 && \
    #http :8080/service1-1.0-SNAPSHOT/endpoint1
