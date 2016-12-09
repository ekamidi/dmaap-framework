/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

import java.io.PrintStream;

public interface Command<T extends CommandContext>
{
	String[] getMatches ();

	void checkReady ( T context ) throws CommandNotReadyException;

	void execute ( String[] parts, T context, PrintStream out ) throws CommandNotReadyException;

	void displayHelp ( PrintStream out );
}
