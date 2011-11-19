#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.aspects;

import java.util.concurrent.atomic.AtomicLong;

import org.duelengine.duel.compiler.DuelCompiler;
import org.duelengine.duel.mvc.ActionFilter;
import org.duelengine.duel.mvc.ActionFilterContext;
import org.duelengine.duel.mvc.ErrorFilter;
import org.duelengine.duel.mvc.ErrorFilterContext;
import org.duelengine.duel.mvc.ResultFilter;
import org.duelengine.duel.mvc.ResultFilterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.Globals;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LatencyTimer implements ActionFilter, ResultFilter, ErrorFilter {

	private static final AtomicLong requestCounter = new AtomicLong();
	private static final AtomicLong errorCounter = new AtomicLong();
	private static final double MS_PER_NANO = 1e6;
	private static final Logger log = LoggerFactory.getLogger(LatencyTimer.class);

	private final double actionThreshold;
	private final double renderThreshold;
	private final double latencyThreshold;
	private long actionStart;
	private long renderStart;

	/**
	 * Gets the count of requests
	 * @return
	 */
	public static long getRequestCount() {
		return requestCounter.get();
	}

	/**
	 * Gets the count of requests
	 * @return
	 */
	public static long getErrorCount() {
		return errorCounter.get();
	}

	@Inject
	public LatencyTimer(
			@Named(Globals.ACTION_THRESHOLD) double actionThreshold,
			@Named(Globals.RENDER_THRESHOLD) double renderThreshold,
			@Named(Globals.LATENCY_THRESHOLD) double latencyThreshold) {

		this.actionThreshold = actionThreshold;
		this.renderThreshold = renderThreshold;
		this.latencyThreshold = latencyThreshold;
	}

	@Override
	public void onActionExecuting(ActionFilterContext context) {
		actionStart = System.nanoTime();
	}

	@Override
	public void onActionExecuted(ActionFilterContext context) {
		double elapsed = (System.nanoTime()-actionStart) / MS_PER_NANO;

		String label = context.getController().getClass().getName()+": "+elapsed+" ms";

		if (elapsed > actionThreshold) {
			log.warning(label);
		} else {
			log.info(label);
		}
	}

	@Override
	public void onResultRendering(ResultFilterContext context) {
		renderStart = System.nanoTime();
	}

	@Override
	public void onResultRendered(ResultFilterContext context) {
		double elapsed = (System.nanoTime()-renderStart) / MS_PER_NANO;
		double latency = (System.nanoTime()-actionStart) / MS_PER_NANO;
		long count = requestCounter.incrementAndGet();

		String label = context.getResult().getViewType().getName()+": "+elapsed+" ms";

		if (elapsed > renderThreshold) {
			log.warning(label);
		} else {
			log.info(label);
		}

		label = "Request latency: "+latency+" ms (requests: "+count+", errors: "+errorCounter.get()+")";

		if (latency > latencyThreshold) {
			log.warning(label);
		} else {
			log.info(label);
		}
	}

	@Override
	public void onError(ErrorFilterContext context) {
		errorCounter.incrementAndGet();
	}
}
