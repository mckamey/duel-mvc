package com.example.mvcapp.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.inject.Singleton;

/**
 * Health monitoring controller
 */
@Singleton
@Path("/ping")
public class PingController extends BaseController {

	@GET
	@Produces("text/plain")
	public Object ping() {

		// TODO: perform service health tests

		//return non-OK status to pull server from pool
		return Response.ok("pong");
	}
}
