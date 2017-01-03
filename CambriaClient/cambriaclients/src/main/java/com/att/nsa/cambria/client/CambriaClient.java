/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client;

import org.slf4j.Logger;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public interface CambriaClient
{
	/**
	 * An exception at the Cambria layer. This is used when the HTTP transport
	 * layer returns a success code but the transaction is not completed as expected.
	 */
	public class CambriaApiException extends Exception
	{
		public CambriaApiException ( String msg ) { super ( msg ); }
		public CambriaApiException ( String msg, Throwable t ) { super ( msg, t ); }
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Optionally set the Logger to use
	 * @param log
	 */
	void logTo ( Logger log );

	/**
	 * Set the API credentials for this client connection. Subsequent calls will
	 * include authentication headers.who i 
	 */
	void setApiCredentials ( String apiKey, String apiSecret );

	/**
	 * Remove API credentials, if any, on this connection. Subsequent calls will not include
	 * authentication headers.
	 */
	void clearApiCredentials ();

	/**
	 * Set the HTTP Basic credentials for this client connection. Subsequent calls will
	 * include authentication headers.
	 */
	void setHttpBasicCredentials ( String username, String password );

	/**
	 * Remove HTTP Basic credentials, if any, on this connection. Subsequent calls will not include
	 * authentication headers.
	 */
	void clearHttpBasicCredentials ();

	/**
	 * Close this connection. Some client interfaces have additional close capability.
	 */
	void close ();
}
