/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

import java.io.IOException;

public class JLineConsoleReader implements ConsoleReader
{
	public JLineConsoleReader () throws IOException
	{
		fJline = new jline.console.ConsoleReader ();
	}

	@Override
	public void setPrompt ( String p )
	{
		fJline.setPrompt ( p );
	}

	@Override
	public String readLine () throws IOException
	{
		return fJline.readLine ();
	}

	private jline.console.ConsoleReader fJline;
}
