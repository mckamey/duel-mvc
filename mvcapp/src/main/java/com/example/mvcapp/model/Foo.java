package com.example.mvcapp.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo {

	public Foo() {}

	public Foo(String path, Date date) {
		this.path = path;
		this.date = date;
	}

	private String path;
	public String getPath() { return path; }
	public void setPath(String value) { path = value; }

	private Date date;
	public Date getDate() { return date; }
	public void setDate(Date value) { date = value; }
}
