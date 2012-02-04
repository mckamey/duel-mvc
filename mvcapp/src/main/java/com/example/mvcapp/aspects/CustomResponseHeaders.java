package com.example.mvcapp.aspects;

import javax.servlet.http.HttpServletResponse;

import org.duelengine.duel.mvc.ResultFilter;
import org.duelengine.duel.mvc.ResultFilterContext;

import com.google.inject.Inject;

/**
 * Example filter which injects custom response headers
 */
public class CustomResponseHeaders implements ResultFilter {

	private final HttpServletResponse response;

	@Inject
	public CustomResponseHeaders(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void onResultRendering(ResultFilterContext context) {
		response.addHeader("X-UA-Compatible", "IE=edge,chrome=1");
//		response.addHeader("X-Frame-Options", "sameorigin");
	}

	@Override
	public void onResultRendered(ResultFilterContext context) {}
}
