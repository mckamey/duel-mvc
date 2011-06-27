package com.example.mvcapp.controllers;

import java.util.*;
import javax.ws.rs.*;
import com.google.inject.*;

import com.example.mvcapp.model.*;
import com.example.mvcapp.views.*;

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
	@Path("throw.xml")
	@Produces("application/xml")
	public Object throwErrorXML() {
		throw new IllegalStateException("This action always throws an exception in XML.");
	}
}
