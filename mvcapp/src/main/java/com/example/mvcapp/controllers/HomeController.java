package com.example.mvcapp.controllers;

import java.text.*;
import java.util.*;
import javax.ws.rs.*;
import com.google.inject.*;

import com.example.mvcapp.model.*;

/**
 * Example controller which verifies HTML/JSON/XML and error display are all working.
 */
@Singleton
@Path("/")
@Produces("text/html")
public class HomeController {

	@GET
	public Object getHTML() {
		return getHTML("/");
	}

	@GET
	@Path("{path:.*}")
	public Object getHTML(@PathParam("path") String path) {
		Foo foo = new Foo(path, new Date());

		// poor man's view
		return
			"<!doctype><html><head>"+
				"<title>"+foo.getPath()+"</title>"+
			"</head><body>"+
				"<h1>"+foo.getPath()+"</h1>"+
				"<p>"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(foo.getDate())+"</p>"+
			"</body></html>";
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
