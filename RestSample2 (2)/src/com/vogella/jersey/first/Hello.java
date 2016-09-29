package com.vogella.jersey.first;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/employee")
public class Hello {

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Employee sayPlainTextHello() {
		return new Employee(1, "Givind", "Banglore");
	}

	// This method is called if XML is request
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Employee sayXMLHello() {
		return new Employee(1, "Givind", "Banglore");
	}

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}

	// This method is called if XML is request
	@Path("/getAll")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	// @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public Employee getAll() {
		return new Employee(1, "Givind", "Banglore");
	}
}