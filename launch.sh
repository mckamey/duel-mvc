#!/bin/sh

clear;

# ensure mvcapp is built, then launch with bootstrap
mvn clean package && \
java $JREBEL_OPTS \
	-Drebel.jersey_plugin=true \
	-jar bootstrap/target/bootstrap.jar \
	-war mvcapp/target/mvcapp/ \
	-c / \
	-p 8080 \
	--tomcat
