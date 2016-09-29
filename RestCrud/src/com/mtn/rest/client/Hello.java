package com.mtn.rest.client;

public class Hello {
	private int counter;
	private int id;
	
	public Hello(int counter,int id) {
		super();
		this.counter = counter;
	}

	public static void main(String[] args) {
		Hello h1 = new Hello(4,8);
		System.out.println(h1.counter+" "+h1.id);
	}
}
