package com.example.mvcapp.aspects;

import java.util.logging.Logger;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import org.duelengine.duel.*;
import org.duelengine.duel.rs.*;

public class TimedViewResult extends ViewResult {

	private final Logger log;
	private double timingThreshold;
	private final Class<? extends DuelView> view;

	public TimedViewResult(Class<? extends DuelView> view, DuelContext context, Logger log, double timingThreshold) {
		super(view, context);

		this.log = log;
		this.timingThreshold = timingThreshold;
		this.view = view;
	}

	@Override
	public void write(OutputStream stream)
		throws IOException, WebApplicationException {

		long start = System.nanoTime();
		double elapsed;
		try {
			super.write(stream);
		} finally {
			elapsed = (System.nanoTime()-start) / 1000000.0;//ms
			String label = view.getName()+" render: ";

			if (elapsed > this.timingThreshold) {
				log.warning(label+elapsed+" ms");
			} else {
				log.info(label+elapsed+" ms");
			}
		}
	}
}
