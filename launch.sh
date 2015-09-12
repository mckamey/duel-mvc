#!/bin/bash

clear;

# ensure mvcapp is built, then launch with bootstrap
mvn clean package && \
java -Xms256m -Xmx1g \
	-Djava.awt.headless=true \
	-jar 'bootstrap/target/bootstrap.jar' \
	-war 'mvcapp/target/mvcapp/' \
	-p 8080 \
	--tomcat
