/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * An observer for HTTP transactions made by this client.
 *
 */
public interface HttpTracer
{
	void outbound ( URI uri, Map<String,List<String>> headers, String method, byte[] entity );
	void inbound ( Map<String,List<String>> headers, int statusCode, String repsonseLine, byte[] entity );
}
