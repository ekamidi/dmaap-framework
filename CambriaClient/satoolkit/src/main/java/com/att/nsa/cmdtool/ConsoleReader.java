/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cmdtool;

import java.io.IOException;

/**
 * A simple implementation of features from JLine, which wasn't working well on Windows/Cygwin
 *
 */
public interface ConsoleReader
{
	void setPrompt ( String p );
	String readLine () throws IOException;
}
