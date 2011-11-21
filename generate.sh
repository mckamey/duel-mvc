#!/bin/sh

# NOTE: you'll want to change these values for your project
GROUP_ID="com.example"
ROOT_DIR="myapp"
MVC_APP="foo"
VERSION="1.0-SNAPSHOT"
BOOTSTRAP="bootstrap"

# NOTE: make sure these are the latest versions
ARCHETYPE_VER="0.7.1"
BOOTSTRAP_VER="0.2.2"

# generate a root project folder
mvn archetype:generate \
  -DarchetypeGroupId=org.codehaus.mojo.archetypes \
  -DarchetypeArtifactId=pom-root \
  -DarchetypeVersion=1.1 \
  -DgroupId=$GROUP_ID \
  -DartifactId=$ROOT_DIR \
  -Dversion=$VERSION \
  -DinteractiveMode=false

cd $ROOT_DIR

# generate a new DUEL MVC project
mvn archetype:generate \
  -DarchetypeGroupId=org.duelengine \
  -DarchetypeArtifactId=duel-mvc-archetype \
  -DarchetypeVersion=$ARCHETYPE_VER \
  -DgroupId=$GROUP_ID \
  -DartifactId=$MVC_APP \
  -Dversion=$VERSION \
  -Dpackage=$GROUP_ID.$MVC_APP \
  -DinteractiveMode=false

# generate a new test server bootstrap
mvn archetype:generate \
  -DarchetypeGroupId=org.duelengine \
  -DarchetypeArtifactId=war-bootstrap-archetype \
  -DarchetypeVersion=$BOOTSTRAP_VER \
  -DgroupId=$GROUP_ID \
  -DartifactId=$BOOTSTRAP \
  -Dversion=$VERSION \
  -Dpackage=$GROUP_ID.$BOOTSTRAP \
  -DinteractiveMode=false

# build your project
mvn package

# run the resulting WAR on the test bootstrap
java -jar $BOOTSTRAP/target/$BOOTSTRAP.jar -war $MVC_APP/target/$MVC_APP.war
