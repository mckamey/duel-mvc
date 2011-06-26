package com.example.mvcapp.model;

@javax.xml.bind.annotation.XmlRootElement
public class ErrorResult {

	private String type;
	private String error;

	public ErrorResult() {}

	public ErrorResult(Throwable ex) {
		type = ex.getClass().getSimpleName();
		error = ex.getMessage();
	}

	public String getType() {
		return type;
	}
	public void setType(String value) {
		type = value;
	}

	public String getError() {
		return error;
	}
	public void setError(String value) {
		error = value;
	}
}
