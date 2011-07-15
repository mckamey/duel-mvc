#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.io.File;
import org.glassfish.embeddable.*;

class GlassFishServletServer implements ServletServer {
	private GlassFish server;

	public String getName() {
		return "GlassFish";
	}

	public void start(String warPath, String contextPath, int httpPort, int httpsPort) throws Exception {
		if (server != null) {
			throw new IllegalStateException("Web server is already running.");
		}

		GlassFishProperties gfProps = new GlassFishProperties();
		if (httpPort > 0) {
			gfProps.setPort("http-listener", httpPort);
		}
		if (httpsPort > 0) {
			gfProps.setPort("https-listener", httpsPort);
		}

		server = GlassFishRuntime.${artifactId}().newGlassFish(gfProps);
		server.start();

		File war = new File(warPath);
		Deployer deployer = server.getDeployer();
		deployer.deploy(war, "--name=webapp", "--contextroot="+contextPath, "--force=true");
	}

	public void stop() throws Exception {
		if (server == null) {
			throw new IllegalStateException("Web server is not running.");
		}

		server.stop();
		server.dispose();

		server = null;
	}
}
