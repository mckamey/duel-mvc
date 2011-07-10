#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import java.util.Date;

@javax.xml.bind.annotation.XmlRootElement
public class Foo {

	private String path;
	private Date date;

	public Foo() {}

	public Foo(String path, Date date) {
		this.path = path;
		this.date = date;
	}

	public String getPath() { return path; }
	public void setPath(String value) { path = value; }

	public Date getDate() { return date; }
	public void setDate(Date value) { date = value; }
}
