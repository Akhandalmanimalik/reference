//Encoding JSON in Java
package com.json.bizruntime;

import org.json.JSONObject;

public class Java2Json {
	public static void main(String[] args) {

		JSONObject obj = new JSONObject();

		obj.put("name", "foo");
		obj.put("num", new Integer(100));
		obj.put("balance", new Double(1000.21));
		obj.put("is_vip", new Boolean(true));

		System.out.print(obj);
	}
}
