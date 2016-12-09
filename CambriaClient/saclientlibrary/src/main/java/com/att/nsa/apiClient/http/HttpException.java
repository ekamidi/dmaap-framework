/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;

public class HttpException extends Exception
{
	public HttpException ( int statusCode, String msg )
	{
		super ();

		fStatusCode = statusCode;
		fMsg = msg;
		fEntity = "";
	}

	public HttpException ( StatusLine statusLine )
	{
		this ( statusLine.getStatusCode (), statusLine.toString () );
	}

	public HttpException ( StatusLine statusLine, HttpEntity responseEntity )
	{
		super ();

		fStatusCode = statusLine.getStatusCode ();
		fMsg = statusLine.toString ();

		final StringBuffer msg = new StringBuffer ();
		if ( responseEntity != null )
		{
			try
			{
				final InputStream is = responseEntity.getContent ();
				is.close ();
			}
			catch ( IllegalStateException e )
			{
				msg.append ( "Couldn't retrieve response body." );
			}
			catch ( IOException e )
			{
				msg.append ( "Couldn't retrieve response body." );
			}
		}
		fEntity = msg.toString ();
	}

	public int getStatusCode ()
	{
		return fStatusCode;
	}

	public String getEntity ()
	{
		return fEntity;
	}

	@Override
	public String getMessage ()
	{
		return fMsg;
	}

	private final int fStatusCode;
	private final String fMsg;
	private final String fEntity;

	private static final long serialVersionUID = 1L;
}
