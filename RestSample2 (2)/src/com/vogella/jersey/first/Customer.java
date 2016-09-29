package com.vogella.jersey.first;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="customer")
@XmlType(propOrder={ "id", "name", "address", "age" })
public class Customer implements Serializable {

	private int id;
	private String name;
	private String address;
	private int age;
	private Date dateOfBirth;
	private List<Order> orders;
	
	public Customer() {
	}

	public Customer(int id, String name, String address, int age,
			Date dateOfBirth, List<Order> orders) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.age = age;
		this.dateOfBirth = dateOfBirth;
		this.orders = orders;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="permanentAddress")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@XmlElement(name="customerAge")
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@XmlTransient
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@XmlTransient
	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
}
