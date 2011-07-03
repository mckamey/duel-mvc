package com.example.mvcapp.aspects;

import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.aopalliance.intercept.*;
import com.google.inject.*;
import com.google.inject.name.Named;

/**
 * AOP proof of concept: this intercepts every controller action and logs the timings
 */
@Singleton
public class ActionTimer implements MethodInterceptor {

	private final Logger log = Logger.getLogger(ActionTimer.class.getSimpleName());
	private Provider<UriInfo> uriInfoProvider;
	private double timingThreshold;

	@Inject
	public void init(
		@Named("ACTION_THRESHOLD") double timingThreshold,
		Provider<UriInfo> uriInfoProvider) {

		this.timingThreshold = timingThreshold;
		this.uriInfoProvider = uriInfoProvider;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		// Note: this only includes the action method body timings, not serialization or render timings

		long start = System.nanoTime();
		double elapsed;
		Object result = null;

		try {
			result = invocation.proceed();

		} finally {
			elapsed = (System.nanoTime()-start) / 1000000.0;//ms

			UriInfo uriInfo = this.uriInfoProvider.get();
			String label = '/'+uriInfo.getPath()+" action: ";

			if (elapsed > this.timingThreshold) {
				log.warning(label+elapsed+" ms");
			} else {
				log.info(label+elapsed+" ms");
			}
		}

		// emit a header with the timing
		ResponseBuilder response = (result instanceof Response) ? Response.fromResponse((Response)result) : Response.ok(result);
		return response.header("X-Action-MS", elapsed).build();
	}
}
