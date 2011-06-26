package com.example.mvcapp.controllers;

import javax.ws.rs.*;
import com.google.inject.*;
import com.google.inject.name.Named;

import com.example.mvcapp.model.*;

/**
 * Error message controller
 */
@Singleton
public class ErrorController {

	private final boolean printStackTrace;

	@Inject
	public ErrorController(@Named("DEBUG") boolean isDebug) {
		this.printStackTrace = isDebug;
	}

	@GET
	@Path("{path:.*}")
	@Produces({"text/html", "application/xhtml+xml"})
	public Object errorView(Throwable ex) {
		ErrorResult error = this.errorData(ex);

		// TODO: customize error responses
		return
			// poor man's view
			"<!doctype><html><head>"+
			"<title>Error</title>"+
			"</head><body>"+
			"<h1>ERROR</h1>"+
			"<p><strong>"+error.getType()+":</strong> "+error.getError()+"</p>"+
			(this.printStackTrace ? "<pre>"+ error.getStackTrace() +"</pre>" : "") +
			"</body></html>";
	}

	@GET
	@Path("{path:.*}")
	@Produces({"application/json", "application/xml"})
	public ErrorResult errorData(Throwable ex) {
		return new ErrorResult(ex, this.printStackTrace);
	}
}
