/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaClient.CambriaApiException;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaIdentityManager;
import com.att.nsa.cambria.client.CambriaIdentityManager.ApiKey;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

@SuppressWarnings("deprecation")
public class ApiKeyCommand implements Command<CambriaCommandContext>
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
	public void checkReady ( CambriaCommandContext context ) throws CommandNotReadyException
	{
		if ( !context.checkClusterReady () )
		{
			throw new CommandNotReadyException ( "Use 'cluster' to specify a cluster to use." );
		}
	}

	@Override
	public void execute ( String[] parts, CambriaCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		CambriaIdentityManager tm = null;
		try
		{
			tm = CambriaClientFactory.createIdentityManager ( ConnectionType.HTTP,
				context.getCluster(), context.getApiKey(), context.getApiPwd() );
			context.applyTracer ( tm );
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
		catch ( CambriaApiException e )
		{
			out.println ( "API exception: " + e.getMessage () );
		}
		catch ( IOException e )
		{
			out.println ( "IO exception: " + e.getMessage () );
		}
		catch ( GeneralSecurityException e )
		{
			out.println ( "IO exception: " + e.getMessage () );
		}
		finally
		{
			if ( tm != null ) tm.close ();
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
