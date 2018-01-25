/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.data.json;

import java.io.ByteArrayInputStream;

import org.json.JSONObject;
import org.junit.Test;

import junit.framework.TestCase;

public class SaJsonTokenerTest extends TestCase {
	@Test
	public void testLineEndings() {
		final String jsonWithLineEndings = "{\n//foo\n}";
		final SaJsonTokener tokener = new SaJsonTokener(new ByteArrayInputStream(jsonWithLineEndings.getBytes()));
		new JSONObject(tokener);
	}
}