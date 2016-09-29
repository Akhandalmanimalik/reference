package com.vogella.jersey.first;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/customer")
public class CustomerEndpoint {

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Customer> getCustomers() {
		Customer customer = new Customer(1, "Akhandala", "HAL", 27, new Date(),
				null);
		Customer customer2 = new Customer(2, "Alok Das", "Murgeshplaya", 30,
				new Date(), null);

		List<Customer> customers = new ArrayList<Customer>(2);
		customers.add(customer);
		customers.add(customer2);

		return customers;
	}

	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Customer getCustomer(@PathParam("id") int id) {
		Customer customer = null;
		if (id == 1) {
			customer = new Customer(1, "Akhandala", "HAL", 27, new Date(), null);
		} else if (id == 2) {
			customer = new Customer(2, "Alok Das", "Murgeshplaya", 30,
					new Date(), null);
		} else {
			customer = new Customer();
			customer.setName("NOT EXIST");
		}
		return customer;
	}
}
