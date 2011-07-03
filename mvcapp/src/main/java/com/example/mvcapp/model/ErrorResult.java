package com.example.mvcapp.model;

import java.io.*;

@javax.xml.bind.annotation.XmlRootElement
public class ErrorResult {

	private String type;
	private String error;
	private String stackTrace;

	public ErrorResult() {}

	public ErrorResult(Throwable ex, boolean showStackTrace) {
		type = ex.getClass().getSimpleName();
		error = ex.getMessage();

		if (showStackTrace) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer, true));
			stackTrace = writer.toString();
		}
	}

	public String getType() { return type; }
	public void setType(String value) { type = value; }

	public String getError() { return error; }
	public void setError(String value) { error = value; }

	public String getStackTrace() { return stackTrace; }
	public void setStackTrace(String value) { stackTrace = value; }
}
