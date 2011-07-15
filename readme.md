DUEL MVC
========

Environment
-----------

- Java SE JDK 1.6u26
	http://www.oracle.com/technetwork/java/javase/downloads/
- Mercurial SCM 1.8.4
	http://mercurial.selenic.com/downloads/
- Maven 3.0.3
	http://maven.apache.org
- Eclipse IDE 2.7
	http://www.eclipse.org/downloads/
- m2e 1.0.0
	http://www.eclipse.org/m2e/download/
- JRebel 4.0.2
	http://www.zeroturnaround.com/jrebel/current/

Maven Setup
-----------

	mvn archetype:generate \
		-DarchetypeGroupId=org.duelengine \
		-DarchetypeArtifactId=duel-mvc-archetype \
		-DarchetypeVersion=0.2.0

	cd mvcapp
	mvn package
	cd ..

	mvn archetype:generate \
		-DarchetypeGroupId=org.duelengine \
		-DarchetypeArtifactId=war-bootstrap-archetype \
		-DarchetypeVersion=0.2.0

	cd bootstrap
	mvn package
	cd ..

	java -jar bootstrap/target/bootstrap.jar -war mvcapp/target/mvcapp.war


JRebel Setup
------------

Configure JRebel with $JREBEL_OPTS:

	export JREBEL_OPTS "-noverify -javaagent:/Applications/ZeroTurnaround/JRebel/jrebel.jar"

Eclipse Instructions (Helios & Indigo)
--------------------------------------

0. Create new Workspace
1. Import... >> Maven >> Existing Maven Projects
2. Build paths >> Add as Source Folders
	- `target/generated-sources/duel`
3. Preferences >> General >> Workspace:
	- Build Automatically
	- Refresh Automatically
4. Map `*.duel` extension to HTML editor
4. Map `*.merge` extension to text editor

Development
-----------

1. Start the dev webserver with:
		mvn clean package && \
		java $JREBEL_OPTS \
			-jar bootstrap/target/bootstrap.jar \
			-war mvcapp/target/mvcapp/
2. View at `http://127.0.0.1:8080/`
3. Edit with Eclipse... refresh browser
4. Repeat
5. Restart on Guice binding changes / major refactorings as these don't seem to propagate as well
