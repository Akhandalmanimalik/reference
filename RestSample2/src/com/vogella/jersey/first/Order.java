package com.vogella.jersey.first;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="order")
public class Order implements Serializable {

	private int id;
	private String name;
	private String desciption;
	
	public Order() {
	}

	public Order(int id, String name, String desciption) {
		this.id = id;
		this.name = name;
		this.desciption = desciption;
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

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}
}
