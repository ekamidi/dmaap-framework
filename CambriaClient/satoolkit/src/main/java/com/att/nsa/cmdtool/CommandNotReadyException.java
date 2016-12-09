/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

public class CommandNotReadyException extends Exception
{
	public CommandNotReadyException (  ) { super("The command is not available now."); }
	public CommandNotReadyException ( String msg ) { super(msg); }
	private static final long serialVersionUID = 1L;
}