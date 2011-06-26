package com.example.mvcapp.controllers;

import javax.ws.rs.*;
import com.google.inject.*;

/**
 * Health monitoring controller
 */
@Singleton
@Path("/ping")
public class PingController {

	@GET
	@Produces("text/plain")
	public Object ping() {

		// TODO: perform health test
		return "OK";
	}
}
