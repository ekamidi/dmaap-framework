/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient;

import org.apache.http.StatusLine;

public class SAClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public SAClientException(String message) {
		super(message);
	}

	public SAClientException(Throwable cause) {
		super(cause);
	}
	
	public SAClientException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SAClientException(int statusCode, StatusLine statusLine, String message) {
		super(message);
	}
}
