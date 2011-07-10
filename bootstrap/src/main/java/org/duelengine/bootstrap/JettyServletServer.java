package org.duelengine.bootstrap;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.webapp.WebAppContext;

class JettyServletServer implements ServletServer {
	private Server server;

	public String getName() {
		return "Jetty";
	}

	public void start(String warPath, String contextPath, int httpPort, int httpsPort) throws Exception {
		if (server != null) {
			throw new IllegalStateException("Web server is already running.");
		}

		server = new Server(httpPort);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(contextPath);
		webapp.setWar(warPath);

		// wire up DefaultServlet for static files
		webapp.addServlet(DefaultServlet.class, "/*");

		server.setHandler(webapp);
		server.start();
	}

	public void stop() throws Exception {
		if (server == null) {
			throw new IllegalStateException("Web server is not running.");
		}

		server.stop();

		server = null;
	}
}
