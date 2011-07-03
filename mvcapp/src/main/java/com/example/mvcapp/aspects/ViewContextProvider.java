package com.example.mvcapp.aspects;

import java.net.URISyntaxException;
import java.util.*;
import com.google.inject.*;
import com.google.inject.name.Named;
import org.duelengine.duel.*;

/**
 * Builds the view context for a request
 */
@Singleton
public class ViewContextProvider implements Provider<DuelContext> {

	private final boolean isDebug;
	private final LinkInterceptor interceptor;

	@Inject
	public ViewContextProvider(
		@Named("DEBUG") boolean isDebug,
		@Named("CDN_HOST") String cdnHost,
		@Named("CDN_MAP") String cdnMapName) throws URISyntaxException {

		this.isDebug = isDebug;

		ResourceBundle cdnBundle = ResourceBundle.getBundle(cdnMapName);
		this.interceptor = new CDNLinkInterceptor(cdnHost, cdnBundle, isDebug);
	}

	public DuelContext get() {
		DuelContext context = new DuelContext();

		context
			.setLinkInterceptor(this.interceptor)
			.setClientID(new IncClientIDStrategy("_"));

		if (this.isDebug) {
			// setup pretty-print formatting
			// add ambient client-side data
			context
				.setFormat(new FormatPrefs().setIndent("\t").setNewline("\n"))
				.putExtra("App.isDebug", true);
		}

		return context;
	}
}