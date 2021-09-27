#!/bin/bash

clear
mvn -o clean package && \
    jboss-cli.sh -c "deploy --force service1/target/service1-1.0-SNAPSHOT.war" && \
    jboss-cli.sh -c "deploy --force service2/target/service2-1.0-SNAPSHOT.war" && \
    http :8080/service1-1.0-SNAPSHOT/endpoint1 && \
    http :8080/service1-1.0-SNAPSHOT/endpoint1 && \
    http :8080/service1-1.0-SNAPSHOT/endpoint1 && \
    http :8080/service1-1.0-SNAPSHOT/endpoint1 && \
    http :8080/service1-1.0-SNAPSHOT/endpoint1
