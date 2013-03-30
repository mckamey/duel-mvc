#!/bin/bash

clear;

# ensure mvcapp is built, then launch with bootstrap
mvn clean package && \
java $JREBEL_OPTS \
	-Djava.awt.headless=true \
	-Drebel.jersey_plugin=true \
	-jar bootstrap/target/bootstrap.jar \
	-war /=mvcapp/target/mvcapp/ \
	-p 8080 \
	--tomcat
