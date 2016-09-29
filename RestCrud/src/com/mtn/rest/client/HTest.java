package com.mtn.rest.client;

import static org.junit.Assert.*;
import org.junit.Test;

public class HTest {
	@Test
	public void testAdd() {
		MyUnit unit = new MyUnit();
		String result = unit.concatenate("one", "two");
		assertEquals("onetwo", result);
	}
}

class MyUnit {
	public String concatenate(String one, String two) {
		return one + two;
	}
}
