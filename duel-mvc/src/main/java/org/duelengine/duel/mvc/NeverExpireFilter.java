package org.duelengine.duel.mvc;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Sets cache control to "never" expire & enables cross-origin access.
 *
 * Only use for SHA1-named CDN resources which change name as content changes.
 * 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
 * 
 * To mark a response as "never expires," an origin server sends an
 * Expires date approximately one year from the time the response is sent.
 * HTTP/1.1 servers SHOULD NOT send Expires dates more than one year in the future.
 * 
 * CDN-based resources require permission to be accessed from another domain.
 * e.g. without, CSS references to other resources like fonts may be blocked
 *
 * http://www.w3.org/TR/cors/#access-control-allow-origin-response-hea
 * https://developer.mozilla.org/En/HTTP_access_control#Access-Control-Allow-Origin
 */
public class NeverExpireFilter implements Filter {

	// this just needs to be far out, do not need to worry about leap year
	private static final long ONE_YEAR_SEC = 365L * 24L * 60L * 60L;
	private static final long ONE_YEAR_MS = ONE_YEAR_SEC * 1000L;

	public void init(FilterConfig config) {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (response instanceof HttpServletResponse) {
			HttpServletResponse httpResponse = (HttpServletResponse)response;

			// expire one year from now
			long expiryDate = new Date().getTime() + ONE_YEAR_MS;

			// add cache control response headers
			httpResponse.setDateHeader("Expires", expiryDate);
			httpResponse.setHeader("Cache-Control", "public, max-age="+ONE_YEAR_SEC);

			// add header to encourage CDN to vary cache on compression
			httpResponse.setHeader("Vary", "Accept-Encoding");

			// add header to enable CDN cross-origin access
			// not conditionally sent since CDN will cache
			httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		}

		chain.doFilter(request, response);
	}
}
