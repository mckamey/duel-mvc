package org.duelengine.bootstrap;

import java.io.File;

public class Bootstrap {

	private static final String SEPARATOR = "========================================";
	private static final String HELP = "java -jar ctrl-bootstrap.jar\n"+
			"  --help       : this help text\n"+
			"  -war <path>  : set the name of war (default: ./root.war)\n" +
			"  -p <port>    : set the HTTP listening port (default: 8443)\n"+
			"  -s <port>    : set the HTTPS listening port (default: none)\n"+
			"  -c <context> : set the context path (default: /)";

	public static void main(String[] args) {

		String warPath = "./root.war";
		String context = "/";
		int port = 8080;
		int https = -1;
		ServletServer server = null;

		System.out.println(SEPARATOR);
		System.out.println("WAR Bootstrap\n");
		for (int i=0; i<args.length; i++) {
			String arg = args[i];
			if ("-p".equals(arg)) {
				port = Integer.parseInt(args[++i]);

			} else if ("-s".equals(arg)) {
				https = Integer.parseInt(args[++i]);

			} else if ("-war".equals(arg)) {
				warPath = args[++i];

			} else if ("-c".equals(arg)) {
				context = args[++i];

			} else if ("--glassfish".equalsIgnoreCase(arg)) {
				server = new GlassFishServletServer();

			} else if ("--jetty".equalsIgnoreCase(arg)) {
				server = new JettyServletServer();

			} else if ("--tomcat".equalsIgnoreCase(arg)) {
				server = new TomcatServletServer();

			} else if ("--help".equalsIgnoreCase(arg)) {
				System.out.println(HELP);
				System.out.println(SEPARATOR);
				return;

			} else {
				System.out.println(HELP);
				System.out.println(SEPARATOR);
				return;
			}
		}

		if (server == null) {
			server = new TomcatServletServer();
		}
		
		try {
			System.out.println(" - servlet container: "+server.getName());

			warPath = new File(warPath).getCanonicalPath();

			System.out.println(" - war path: "+warPath);
			System.out.println(" - context root: "+context);
			if (port > 0) {
				System.out.println(" - Listening to HTTP on port: "+port);
			} else {
				System.out.println(" - Not listening to HTTP");
			}
			if (https > 0) {
				System.out.println(" - Listening to HTTPS on port: "+https);
			} else {
				System.out.println(" - Not listening to HTTPS");
			}

			System.out.println("\nPress ENTER to exit.");
			System.out.println(SEPARATOR);
			System.out.println();

			server.start(warPath, context, port, https);

			System.in.read();

			System.out.println(SEPARATOR);
			System.out.println("WAR Bootstrap exiting...");
			System.out.println(SEPARATOR);
			System.out.println();

			server.stop();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
