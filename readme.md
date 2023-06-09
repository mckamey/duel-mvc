DUEL MVC
========

Environment
-----------

###Minimum:

- Java SE JDK 1.6 (1.7 should work but still needs more testing)
	http://www.oracle.com/technetwork/java/javase/downloads/
- Maven 3.0.x
	http://maven.apache.org/download.html

###Recommended:

- Eclipse IDE 3.7+
	http://www.eclipse.org/downloads/
- m2e 1.0+
	http://www.eclipse.org/m2e/download/
- JRebel 5.0+
	http://zeroturnaround.com/software/jrebel/download/

Usage
-----
	# NOTE: you'll want to change these values per your project
	GROUP_ID="com.example"
	ROOT_DIR="myapp"
	MVC_APP="foo"
	VERSION="1.0-SNAPSHOT"
	BOOTSTRAP="bootstrap"
	
	# NOTE: make sure these are the latest versions
	ARCHETYPE_VER="0.9.3"
	BOOTSTRAP_VER="0.5.0"
	
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
	java -jar $BOOTSTRAP/target/$BOOTSTRAP.jar -war /=$MVC_APP/target/$MVC_APP.war

JRebel Setup
------------

Configure JRebel with $JREBEL_OPTS:

	export JREBEL_OPTS "-noverify -javaagent:/Applications/ZeroTurnaround/JRebel/jrebel.jar"

Eclipse Usage (Helios & Indigo)
-------------------------------

1. Create new Workspace
2. Import... >> Maven >> Existing Maven Projects
3. Build paths >> Add as Source Folders
	- `target/generated-sources/duel`
4. Preferences >> General >> Workspace:
	- Build Automatically
	- Refresh Automatically
5. Map `*.duel` extension to HTML editor
6. Map `*.merge` extension to text editor

Development
-----------

1. Start the dev webserver (using the exploded WAR directory):

		mvn clean package && \
		java $JREBEL_OPTS \
			-Drebel.jersey_plugin=true \
			-jar bootstrap/target/bootstrap.jar \
			-war /=mvcapp/target/mvcapp/

2. View at `http://127.0.0.1:8080/`
3. Edit with Eclipse... refresh browser
4. Repeat
5. May need to restart on Guice binding changes / major refactorings as these may not propagate as well
