package com.example.mvcapp.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import com.example.mvcapp.model.ErrorResult;
import com.example.mvcapp.views.ErrorPage;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.Stage;

/**
 * Error message controller
 */
@Singleton
public class ErrorController extends BaseController {

	private final boolean printStackTrace;

	@Inject
	public ErrorController(Stage stage) {
		this.printStackTrace = (stage == Stage.DEVELOPMENT);
	}

	@GET
	@Produces({"text/plain"})
	public Object errorText(Throwable ex) {
		return ex.getMessage();
	}

	@GET
	@Produces({"text/html", "application/xhtml+xml"})
	public Object errorView(Throwable ex) {
		ErrorResult error = this.errorData(ex);

		// TODO: customize error responses
		return view(ErrorPage.class).data(error);
	}

	@GET
	@Produces({"application/json", "application/xml"})
	public ErrorResult errorData(Throwable ex) {
		return new ErrorResult(ex, this.printStackTrace);
	}
}
