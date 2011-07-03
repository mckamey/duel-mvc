#!/bin/sh

clear;

# ensure bootstrap is built
if test ! -e ./bootstrap/target/bootstrap.jar
then
	cd bootstrap;
	mvn clean package;
	cd ..;
fi

# ensure mvcapp is built, then launch
cd mvcapp && \
mvn clean package && \
java $JREBEL_OPTS \
	-jar ../bootstrap/target/bootstrap.jar \
	-war ../mvcapp/target/mvcapp/ \
	-c / \
	-p 8080 \
	--tomcat
