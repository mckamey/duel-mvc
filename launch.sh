#!/bin/sh

clear;

# ensure mvcapp is built, then launch
mvn clean package && \
java $JREBEL_OPTS \
	-jar bootstrap/target/bootstrap.jar \
	-war mvcapp/target/mvcapp/ \
	-c / \
	-p 8080 \
	--tomcat
