/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.PrintStream;

import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class AuthCommand implements Command<CambriaCommandContext>
{
	@Override
	public void checkReady ( CambriaCommandContext context ) throws CommandNotReadyException
	{
	}

	@Override
	public void execute ( String[] parts, CambriaCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		if ( parts.length > 0 )
		{
			context.setAuth ( parts[0], parts[1] );
			out.println ( "Now authenticating with " + parts[0] );
		}
		else
		{
			context.clearAuth ();
			out.println ( "No longer authenticating." );
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "auth <apiKey> <apiSecret>" );
		out.println ( "\tuse these credentials on subsequent transactions" );
		out.println ( "noauth" );
		out.println ( "\tdo not use credentials on subsequent transactions" );
	}

	@Override
	public String[] getMatches ()
	{
		return new String[]
		{
			"auth (\\S*) (\\S*)",
			"noauth"
		};
	}
}
