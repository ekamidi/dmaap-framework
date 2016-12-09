/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.builders;

/**
 * An exception thrown by the builder classes.
 */
public class SaBuilderException extends Exception
{
	public SaBuilderException ( String msg ) { super(msg); }
	public SaBuilderException ( String msg, Throwable t ) { super(msg,t); }
	public SaBuilderException ( Throwable t ) { super(t); }

	public void setReferencePoint ( String pt )
	{
		fWhere = pt;
	}
	
	@Override
	public String getMessage ()
	{
		final String base = super.getMessage ();
		if ( fWhere != null )
		{
			return base + "; " + fWhere;
		}
		return base;
	}

	private String fWhere = null;
	
	private static final long serialVersionUID = 1L;
}
