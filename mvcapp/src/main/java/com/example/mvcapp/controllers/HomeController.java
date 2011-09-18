package com.example.mvcapp.controllers;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.example.mvcapp.model.Foo;
import com.example.mvcapp.views.FooPage;
import com.google.inject.Singleton;

/**
 * Example controller which verifies HTML/JSON/XML and error display are all working.
 */
@Singleton
@Path("/")
@Produces("text/html")
public class HomeController extends BaseController {

	@GET
	public Object getHTML() {
		return getHTML("/");
	}

	@GET
	@Path("{path:.*}")
	public Object getHTML(@PathParam("path") String path) {
		Foo foo = new Foo(path, new Date());

		return view(FooPage.class).data(foo);
	}

	@GET
	@Path("{path:.*}.json")
	@Produces("application/json")
	public Object getJSON(@PathParam("path") String path) {
		return new Foo(path, new Date());
	}

	@GET
	@Path("{path:.*}.txt")
	@Produces("text/plain")
	public Object getTEXT(@PathParam("path") String path) {
		return path;
	}

	@GET
	@Path("{path:.*}.xml")
	@Produces("application/xml")
	public Object getXML(@PathParam("path") String path) {
		return new Foo(path, new Date());
	}

	/*------------------------------------------------*/

	@GET
	@Path("throw")
	public Object throwErrorHTML() {
		throw new IllegalStateException("This action always throws an exception in HTML.");
	}

	@GET
	@Path("throw.json")
	@Produces("application/json")
	public Object throwErrorJSON() {
		throw new IllegalStateException("This action always throws an exception in JSON.");
	}

	@GET
	@Path("throw.txt")
	@Produces("text/plain")
	public Object throwErrorText() {
		throw new IllegalStateException("This action always throws an exception in TEXT.");
	}

	@GET
	@Path("throw.xml")
	@Produces("application/xml")
	public Object throwErrorXML() {
		throw new IllegalStateException("This action always throws an exception in XML.");
	}
}
