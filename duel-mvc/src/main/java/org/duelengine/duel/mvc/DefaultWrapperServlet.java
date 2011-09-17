package org.duelengine.duel.mvc;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Simple wrapper for passing the request onto the default servlet
 */
class DefaultWrapperServlet extends HttpServlet {

	private static final long serialVersionUID = -351152009226372681L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		getServletContext().getNamedDispatcher("default").forward(
			new HttpServletRequestWrapper(request) {
				public String getServletPath() { return ""; }
			},
			response);
	}
}
