/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.util.Collection;
import java.util.LinkedList;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpTracer;
import com.att.nsa.cambria.client.CambriaClient;
import com.att.nsa.cmdtool.CommandContext;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaCommandContext implements CommandContext
{
	public CambriaCommandContext ()
	{
		fApiKey = null;
		fApiPwd = null;

		fCluster = new LinkedList<String> ();
		fCluster.add ( "localhost" );
	}

	@Override
	public void requestShutdown ()
	{
		fShutdown = true;
	}

	@Override
	public boolean shouldContinue ()
	{
		return !fShutdown;
	}

	public void setAuth ( String key, String pwd ) { fApiKey = key; fApiPwd = pwd; }
	public void clearAuth () { setAuth(null,null); }
	
	public boolean checkClusterReady ()
	{
		return ( fCluster.size () != 0 );
	}

	public Collection<String> getCluster ()
	{
		return new LinkedList<String> ( fCluster );
	}

	public void clearCluster ()
	{
		fCluster.clear ();
	}

	public void addClusterHost ( String host )
	{
		fCluster.add ( host );
	}

	public String getApiKey () { return fApiKey; }
	public String getApiPwd () { return fApiPwd; }

	public void useTracer ( HttpTracer t )
	{
		fTracer = t;
	}
	public void noTracer () { fTracer = null; }

	public void applyTracer ( CambriaClient cc )
	{
		if ( cc instanceof HttpClient && fTracer != null )
		{
			((HttpClient)cc).installTracer ( fTracer );
		}
	}

	private boolean fShutdown;
	private String fApiKey;
	private String fApiPwd;
	private final LinkedList<String> fCluster;
	private HttpTracer fTracer = null;
}
