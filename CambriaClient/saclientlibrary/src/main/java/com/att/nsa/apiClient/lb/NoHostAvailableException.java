/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.lb;

public class NoHostAvailableException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoHostAvailableException() {
		super();
	}
	
	public NoHostAvailableException(String message) {
		super(message);
	}
	
	public NoHostAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
