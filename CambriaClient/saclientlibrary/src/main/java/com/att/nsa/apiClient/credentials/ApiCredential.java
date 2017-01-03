/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.credentials;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApiCredential
{
	public ApiCredential ( String apiKey, String apiSecret )
	{
		fApiKey = apiKey;
		fApiSecret = apiSecret;
	}

	/**
	 * Get the API key on this credential
	 * @return the API key passed to the constructor
	 */
	public String getApiKey () { return fApiKey; }

	/**
	 * Get API secret on this credential.
	 * @return the API secret passed to the constructor
	 */
	public String getApiSecret () { return fApiSecret; }
	
	/**
	 * Create a set of authentication headers to add to a API request.
	 * @param timeInMs
	 * @return a map of headers to add to the request
	 */
	public Map<String,String> createAuthenticationHeaders ( long timeInMs )
	{
		final HashMap<String,String> result = new HashMap<String,String> ();

		final Date date = new Date ( timeInMs );
		final SimpleDateFormat sdf = new SimpleDateFormat ( kPreferredDateFormat );
		final String xDate = sdf.format ( date );

		result.put ( "X-CambriaDate", xDate );

		final String signature = Sha1HmacSigner.sign ( xDate, fApiSecret );

		final String auth = fApiKey + ":" + signature;
		result.put ( "X-CambriaAuth", auth );

		return result;
	}

	private final String fApiKey;
	private final String fApiSecret;

	public static final String kPreferredDateFormat = "EEE, dd MMM yyyy HH:mm:ss zzz";
}
