package bootstrap;

public interface ServletServer {

	String getName();

	void start(String warPath, String context, int httpPort, int httpsPort) throws Exception;

	void stop() throws Exception;
}