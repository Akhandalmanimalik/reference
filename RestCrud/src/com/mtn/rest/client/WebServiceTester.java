package com.mtn.rest.client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import com.mtn.rest.User;

public class WebServiceTester {
	private Client client;
	private String REST_SERVICE_URL = "http://localhost:8080/RestCrudOperation/rest/user";
	private static final String SUCCESS_RESULT = "<result>success</result>";
	private static final String PASS = "pass";
	private static final String FAIL = "fail";

	private void init() {
		this.client = ClientBuilder.newClient();
	}

	public static void main(String[] args) {
		WebServiceTester tester = new WebServiceTester();
		// initialize the tester
		tester.init();
		
//		tester.testGetAllUsers();
//		tester.testGetUser();
//		tester.testAddUser();
//		tester.testUpdateUser();
		tester.testDeleteUser();
	}

	// Test: Get list of all users
	// Test: Check if list is not empty
	private void testGetAllUsers() {
		GenericType<List<User>> list = new GenericType<List<User>>() {};
		List<User> users = client.target(REST_SERVICE_URL)
				.request(MediaType.APPLICATION_XML).get(list);
		String result = PASS;
		if (users.isEmpty()) {
			result = FAIL;
		}

		System.out.println("Test case name: testGetAllUsers, Result: " + result);
		for (User user : users) {
			System.out.println(user.getId() + " / " + user.getName() + " / "
					+ user.getProfession());
		}
	}

	// Test: Get User of id 1
	// Test: Check if user is same as sample user
	private void testGetUser() {
		User sampleUser = new User();
		sampleUser.setId(1);

		User user = client.target(REST_SERVICE_URL).path("/userid/{userid}")
				.resolveTemplate("userid", 2)
				.request(MediaType.APPLICATION_XML).get(User.class);
		String result = FAIL;
		if (sampleUser != null && sampleUser.getId() == user.getId()) {
			result = PASS;
		}
		System.out.println("Test case name: testGetUser, Result: " + result);
		System.out.println(user.getId() + " / " + user.getName() + " / "
				+ user.getProfession());
	}

	// Test: Update User of id 1
	// Test: Check if result is success XML.
	private void testUpdateUser() {
		User user = new User(6, "manas", "SSE");
		String callResult = client
				.target(REST_SERVICE_URL)
				.request(MediaType.APPLICATION_XML)
				.put(Entity.entity(user,
						MediaType.APPLICATION_XML),
						String.class);
		String result = PASS;
		if (!SUCCESS_RESULT.equals(callResult)) {
			result = FAIL;
		}
		System.out.println(callResult);
		testGetAllUsers();
	}

	// Test: Add User of id 2
	// Test: Check if result is success XML.
	private void testAddUser() {
		User user = new User(6, "manas", "bepari");
		String callResult = client
				.target(REST_SERVICE_URL)
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(user,
						MediaType.APPLICATION_XML),
						String.class);

		String result = PASS;
		if (!SUCCESS_RESULT.equals(callResult)) {
			result = FAIL;
		}
		System.out.println("Test case name: testAddUser, Result: " + result);
		testGetAllUsers();
	}

	// Test: Delete User of id 2
	// Test: Check if result is success XML.
	private void testDeleteUser() {
		String callResult = client.target(REST_SERVICE_URL).path("/userid/{userid}")
				.resolveTemplate("userid", 6)
				.request(MediaType.APPLICATION_XML).delete(String.class);

		String result = PASS;
		if (!SUCCESS_RESULT.equals(callResult)) {
			result = FAIL;
		}

		System.out.println("Test case name: testDeleteUser, Result: " + result);
		testGetAllUsers();
	}
}
