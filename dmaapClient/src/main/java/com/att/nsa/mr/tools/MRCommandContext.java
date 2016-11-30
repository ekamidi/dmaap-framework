/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.nsa.mr.tools;

import java.util.Collection;
import java.util.LinkedList;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpTracer;
import com.att.nsa.cmdtool.CommandContext;
import com.att.nsa.mr.client.MRClient;

public class MRCommandContext implements CommandContext
{
	public MRCommandContext ()
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

	public void applyTracer ( MRClient cc )
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
