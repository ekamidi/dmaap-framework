/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.PrintStream;

import com.att.nsa.cambria.client.impl.CambriaConsumerImpl;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class ClusterCommand implements Command<CambriaCommandContext>
{

	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"cluster",
			"cluster (\\S*)?",
		};
	}

	@Override
	public void checkReady ( CambriaCommandContext context ) throws CommandNotReadyException
	{
	}

	@Override
	public void execute ( String[] parts, CambriaCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		if ( parts.length == 0 )
		{
			for ( String host : context.getCluster () )
			{
				out.println ( host );
			}
		}
		else
		{
			context.clearCluster ();
			for ( String part : parts )
			{
				String[] hosts = part.trim().split ( "\\s+" );
				for ( String host : hosts )
				{
					for ( String splitHost : CambriaConsumerImpl.stringToList(host) )
					{
						context.addClusterHost ( splitHost );
					}
				}
			}
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "cluster host1 host2 ..." );
	}

}
