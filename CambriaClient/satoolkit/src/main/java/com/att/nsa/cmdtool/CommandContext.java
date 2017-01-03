/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

public interface CommandContext
{
	void requestShutdown ();
	boolean shouldContinue ();
}
