/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.IOException;

import com.att.nsa.cambria.client.impl.CambriaClientVersionInfo;
import com.att.nsa.cmdtool.CommandLineTool;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaTool extends CommandLineTool<CambriaCommandContext>
{
	protected CambriaTool ()
	{
		super ( "Cambria Tool (" + CambriaClientVersionInfo.getVersion () + ")", "cambria> " );

		registerCommand ( new ApiKeyCommand () );
		registerCommand ( new AuthCommand () );
		registerCommand ( new ClusterCommand () );
		registerCommand ( new MessageCommand () );
		registerCommand ( new TopicCommand () );
		registerCommand ( new TraceCommand () );
	}

	public static void main ( String[] args ) throws IOException
	{
		final CambriaTool ct = new CambriaTool ();
		final CambriaCommandContext ccc = new CambriaCommandContext ();
		ct.runFromMain ( args, ccc );
	}
}
