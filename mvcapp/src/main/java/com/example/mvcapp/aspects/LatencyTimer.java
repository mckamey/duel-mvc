package com.example.mvcapp.aspects;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.duelengine.duel.mvc.ActionFilter;
import org.duelengine.duel.mvc.ActionFilterContext;
import org.duelengine.duel.mvc.ResultFilter;
import org.duelengine.duel.mvc.ResultFilterContext;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LatencyTimer implements ActionFilter, ResultFilter {

	private static final AtomicLong counter = new AtomicLong();
	private static final double NANO2MS = 1000000.0;
	private static final Logger log = Logger.getLogger("LatencyFilter");

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
		return counter.get();
	}
	
	@Inject
	public LatencyTimer(
			@Named("ACTION_THRESHOLD") double actionThreshold,
			@Named("RENDER_THRESHOLD") double renderThreshold,
			@Named("LATENCY_THRESHOLD") double latencyThreshold) {

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
		double elapsed = (System.nanoTime()-actionStart) / NANO2MS;//ms

		String label = context.getController().getClass().getName()+": "+elapsed+" ms";

		if (elapsed > actionThreshold) {
			log.warning(label);
		} else {
			log.info(label);
		}
	}

	@Override
	public void onResultExecuting(ResultFilterContext context) {
		renderStart = System.nanoTime();
	}

	@Override
	public void onResultExecuted(ResultFilterContext context) {
		double elapsed = (System.nanoTime()-renderStart) / NANO2MS;//ms

		String label = context.getViewResult().getViewType().getName()+": "+elapsed+" ms";

		if (elapsed > renderThreshold) {
			log.warning(label);
		} else {
			log.info(label);
		}

		double latency = (System.nanoTime()-actionStart) / NANO2MS;//ms
		long count = counter.incrementAndGet();

		label = "Request latency: "+latency+" ms (counter: "+count+")";

		if (latency > latencyThreshold) {
			log.warning(label);
		} else {
			log.info(label);
		}
	}
}
