/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.att.nsa.apiClient.http.HttpTracer;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class TraceCommand implements Command<CambriaCommandContext>
{
	@Override
	public void checkReady ( CambriaCommandContext context ) throws CommandNotReadyException
	{
	}

	@Override
	public void execute ( String[] parts, CambriaCommandContext context, final PrintStream out ) throws CommandNotReadyException
	{
		if ( parts[0].equalsIgnoreCase ( "on" ))
		{
			context.useTracer ( new HttpTracer ()
			{
				@Override
				public void outbound ( URI uri, Map<String, List<String>> headers, String method, byte[] entity )
				{
					out.println ( kLineBreak );
					out.println ( ">>> " + method + " " + uri.toString() );
					for ( Map.Entry<String,List<String>> e : headers.entrySet () )
					{
						final StringBuffer vals = new StringBuffer ();
						for ( String val : e.getValue () )
						{
							if ( vals.length () > 0 ) vals.append ( ", " );
							vals.append ( val );
						}
						out.println ( ">>> " + e.getKey () + ": " + vals.toString() );
					}
					if ( entity != null )
					{
						out.println ();
						out.println ( new String ( entity ) );
					}
					out.println ( kLineBreak );
				}

				@Override
				public void inbound ( Map<String, List<String>> headers, int statusCode, String responseLine, byte[] entity )
				{
					out.println ( kLineBreak );
					out.println ( "<<< " + responseLine );
					for ( Map.Entry<String,List<String>> e : headers.entrySet () )
					{
						final StringBuffer vals = new StringBuffer ();
						for ( String val : e.getValue () )
						{
							if ( vals.length () > 0 ) vals.append ( ", " );
							vals.append ( val );
						}
						out.println ( "<<< " + e.getKey () + ": " + vals.toString() );
					}
					if ( entity != null )
					{
						out.println ();
						out.println ( new String ( entity ) );
					}
					out.println ( kLineBreak );
				}
			} );
		}
		else
		{
			context.noTracer ();
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "trace on|off" );
		out.println ( "\tWhen trace is on, HTTP interaction is printed to the console." );
	}

	@Override
	public String[] getMatches ()
	{
		return new String[]
		{
			"trace (on)",
			"trace (off)"
		};
	}

	private static final String kLineBreak = "======================================================================";
}
