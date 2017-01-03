/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

/**
 * A specific not-found exception, since that's a common failure.
 */
public class HttpObjectNotFoundException extends HttpException
{
	public HttpObjectNotFoundException ( String msg )
	{
		super ( 404, msg );
	}

	private static final long serialVersionUID = 1L;
}
