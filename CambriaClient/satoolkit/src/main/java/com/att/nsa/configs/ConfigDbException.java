/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs;

public class ConfigDbException extends Exception
{
	public ConfigDbException ( String msg ) { super(msg); }
	public ConfigDbException ( String msg, Throwable t ) { super(msg,t); }
	public ConfigDbException ( Throwable t ) { super(t); }
	private static final long serialVersionUID = 1L;
}
