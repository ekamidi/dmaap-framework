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

import java.io.IOException;
import java.io.PrintStream;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRIdentityManager;
import com.att.nsa.mr.client.MRClient.MRApiException;
import com.att.nsa.mr.client.MRIdentityManager.ApiKey;

public class ApiKeyCommand implements Command<MRCommandContext>
{

	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"key (create|update) (\\S*) (\\S*)",
			"key (list) (\\S*)",
			"key (revoke)",
		};
	}

	@Override
	public void checkReady ( MRCommandContext context ) throws CommandNotReadyException
	{
		if ( !context.checkClusterReady () )
		{
			throw new CommandNotReadyException ( "Use 'cluster' to specify a cluster to use." );
		}
	}

	@Override
	public void execute ( String[] parts, MRCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		final MRIdentityManager tm = MRClientFactory.createIdentityManager ( context.getCluster(), context.getApiKey(), context.getApiPwd() );
		context.applyTracer ( tm );

		try
		{
			if ( parts[0].equals ( "list" ) )
			{
				final ApiKey key = tm.getApiKey ( parts[1] );
				if ( key != null )
				{
					out.println ( "email: " + key.getEmail () );
					out.println ( "description: " + key.getDescription () );
				}
				else
				{
					out.println ( "No key returned" );
				}
			}
			else if ( parts[0].equals ( "create" ) )
			{
				final ApiCredential ac = tm.createApiKey ( parts[1], parts[2] );
				if ( ac != null )
				{
					out.println ( "   key: " + ac.getApiKey () );
					out.println ( "secret: " + ac.getApiSecret () );
				}
				else
				{
					out.println ( "No credential returned?" );
				}
			}
			else if ( parts[0].equals ( "update" ) )
			{
				tm.updateCurrentApiKey ( parts[1], parts[2] );
				out.println ( "Updated" );
			}
			else if ( parts[0].equals ( "revoke" ) )
			{
				tm.deleteCurrentApiKey ();
				out.println ( "Updated" );
			}
		}
		catch ( HttpObjectNotFoundException e )
		{
			out.println ( "Object not found: " + e.getMessage () );
		}
		catch ( HttpException e )
		{
			out.println ( "HTTP exception: " + e.getMessage () );
		}
		catch ( MRApiException e )
		{
			out.println ( "API exception: " + e.getMessage () );
		}
		catch ( IOException e )
		{
			out.println ( "IO exception: " + e.getMessage () );
		}
		finally
		{
			tm.close ();
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "key create <email> <description>" );
		out.println ( "key update <email> <description>" );
		out.println ( "key list <key>" );
		out.println ( "key revoke" );
	}
}
