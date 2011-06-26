package com.example.mvcapp.aspects;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.inject.*;

@Singleton
@SuppressWarnings("serial")
public class DefaultWrapperServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		RequestDispatcher dispatch = getServletContext().getNamedDispatcher("default");

		HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
			public String getServletPath() { return ""; }
		};

		dispatch.forward(wrapped, response);
	}
}
