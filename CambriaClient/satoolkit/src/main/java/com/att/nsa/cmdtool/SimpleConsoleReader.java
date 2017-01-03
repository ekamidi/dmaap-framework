/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple implementation of features from JLine, which wasn't working well on Windows/Cygwin
 *
 */
public class SimpleConsoleReader implements ConsoleReader
{
	public void setPrompt ( String p )
	{
		fPrompt = p;
	}

	public String readLine () throws IOException
	{
		System.out.print ( fPrompt );

		final BufferedReader br = new BufferedReader ( new InputStreamReader ( System.in ) );
		return br.readLine ();
	}

	private String fPrompt = "> ";
}
