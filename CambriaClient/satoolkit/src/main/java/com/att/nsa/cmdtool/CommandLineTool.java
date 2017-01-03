/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineTool<T extends CommandContext>
{
	protected CommandLineTool ( String title, String prompt )
	{
		fTitle = title;
		fPrompt = prompt;
		fCommands = new HashMap<String,Command<T>> ();
		fCommandList = new LinkedList<Command<T>> ();
	
		registerCommand ( new Quit() );
		registerCommand ( new Help() );
	}

	public void runFromMain ( String[] args, T context ) throws IOException
	{
		System.out.println ( fTitle );
		System.out.println ( "" );

		final ConsoleReader cr = buildReader ();
		cr.setPrompt ( fPrompt );

		String line = null;
		while ( context.shouldContinue () && ( line = cr.readLine () ) != null )
		{
			final String[] parts = split ( line );
			if ( parts.length > 0 )
			{
				final String matchKey = getMatchableLine ( parts );
				boolean found = false;
				for ( String key : fCommands.keySet () )
				{
					// FIXME: this is a pretty slow way to go
					final Pattern p = Pattern.compile ( key );
					final Matcher m = p.matcher ( matchKey );
					if ( m.matches () )
					{
						final int varCount = m.groupCount ();
						final String[] vars = new String[ varCount ];
						for ( int i=1; i<=varCount; i++ )
						{
							vars[i-1] = m.group ( i );
						}
						final Command<T> c = fCommands.get ( key );
						try
						{
							c.checkReady ( context );
							c.execute ( vars, context, System.out );
						}
						catch ( CommandNotReadyException e )
						{
							System.out.println ( e.getMessage () );
						}
						
						// command matched, stop trying
						found = true;
						break;
					}
				}
				
				if ( !found )
				{
					System.out.println ( "Unrecognized command. Try 'help'." );
				}
			}

			System.out.println ();
		}
	}

	private ConsoleReader buildReader () throws IOException
	{
		final boolean useJline =  Boolean.parseBoolean ( System.getProperty ( "jline", "true" ) );
		return useJline ? new JLineConsoleReader () : new SimpleConsoleReader ();
	}

	private static String getMatchableLine ( String[] line )
	{
		final StringBuffer sb = new StringBuffer ();
		for ( String part : line )
		{
			if ( sb.length() > 0 ) sb.append ( " " );
			sb.append ( part );
		}
		return sb.toString ();
	}

	private static String[] split ( String line )
	{
		final ArrayList<String> goodParts = new ArrayList<String> ();
		for ( String part : line.split ( "\\s+" ) )
		{
			final String trimmed = part.trim ();
			if ( trimmed.length () > 0 ) goodParts.add ( trimmed );
		}
		return goodParts.toArray ( new String[goodParts.size()] );
	}
	
	protected void registerCommand ( Command<T> c )
	{
		fCommandList.add ( c );
		for ( String cmdLineRegex : c.getMatches () )
		{
			fCommands.put ( cmdLineRegex, c );
		}
	}

	private final String fTitle;
	private final String fPrompt;
	private final HashMap<String,Command<T>> fCommands;
	private final LinkedList<Command<T>> fCommandList;

	private class Help implements Command<T>
	{
		@Override
		public void execute ( String[] parts, T context, PrintStream out )
		{
			for ( Command<T> c : fCommandList )
			{
				c.displayHelp ( out );
				out.println ();
			}
		}

		@Override
		public void checkReady ( T context ) throws CommandNotReadyException
		{
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "help" );
			out.println ( "\tDisplay this list" );
		}

		@Override
		public String[] getMatches ()
		{
			return new String[]
			{
				"help",
			};
		}
	}

	private class Quit implements Command<T>
	{
		@Override
		public void execute ( String[] parts, T context, PrintStream out )
		{
			context.requestShutdown ();
		}

		@Override
		public void checkReady ( T context ) throws CommandNotReadyException
		{
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "quit" );
			out.println ( "\tquit the program" );
		}

		@Override
		public String[] getMatches ()
		{
			return new String[]
			{
				"quit",
			};
		}
	}
}
