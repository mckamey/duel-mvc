package com.example.mvcapp;

/**
 * Keys for Guice constants
 */
public final class Globals {

	/**
	 * CDN server hostname, e.g. "cdn.example.com"
	 */
	public static final String CDN_HOST = "CDN_HOST";

	/**
	 * The name of the properties file for resolving CDN resources
	 */
	public static final String CDN_MAP_NAME = "CDN_MAP_NAME";

	/**
	 * The maximum threshold for action timings
	 */
	public static final String ACTION_THRESHOLD = "ACTION_THRESHOLD";

	/**
	 * The maximum threshold for view rendering
	 */
	public static final String RENDER_THRESHOLD = "RENDER_THRESHOLD";

	/**
	 * The maximum threshold for total request
	 */
	public static final String LATENCY_THRESHOLD = "LATENCY_THRESHOLD";
}
