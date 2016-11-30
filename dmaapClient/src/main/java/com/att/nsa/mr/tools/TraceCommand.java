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

import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.att.nsa.apiClient.http.HttpTracer;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

public class TraceCommand implements Command<MRCommandContext>
{
	@Override
	public void checkReady ( MRCommandContext context ) throws CommandNotReadyException
	{
	}

	@Override
	public void execute ( String[] parts, MRCommandContext context, final PrintStream out ) throws CommandNotReadyException
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
