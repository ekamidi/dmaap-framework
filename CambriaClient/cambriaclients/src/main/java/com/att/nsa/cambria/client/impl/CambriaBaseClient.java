/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.apiClient.http.CacheUse;
import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.cambria.client.CambriaClient;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaBaseClient extends HttpClient implements CambriaClient 
{
	protected CambriaBaseClient ( ConnectionType ct, Collection<String> hosts, int soTimeoutMs ) throws MalformedURLException, GeneralSecurityException
	{
		this ( ct, hosts, null, soTimeoutMs );
	}
	
	protected CambriaBaseClient ( ConnectionType ct, Collection<String> hosts, String clientSignature, int soTimeoutMs ) throws MalformedURLException, GeneralSecurityException
	{
		super ( ct, hosts, 
			ct == ConnectionType.HTTP ? CambriaConstants.kStdCambriaServicePort : CambriaConstants.kStdCambriaHttpsServicePort,
			clientSignature, CacheUse.NONE, 1, 1, TimeUnit.MILLISECONDS, HttpClient.kDefault_PoolMaxInTotal, HttpClient.kDefault_PoolMaxPerRoute, soTimeoutMs );

		fLog = LoggerFactory.getLogger ( this.getClass().getName () );
	}
	
	@Override
	public void close ()
	{
	}

	protected Set<String> jsonArrayToSet ( JSONArray a )
	{
		if ( a == null ) return null;

		final TreeSet<String> set = new TreeSet<String> ();
		for ( int i=0; i<a.length (); i++ )
		{
			set.add ( a.getString ( i ));
		}
		return set;
	}

	public void logTo ( Logger log )
	{
		fLog = log;
		replaceLogger ( log );
	}

	protected Logger getLog ()
	{
		return fLog;
	}
	
	private Logger fLog;
}
