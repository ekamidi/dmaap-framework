/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaPublisher.message;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

@SuppressWarnings("deprecation")
public class MessageCommand implements Command<CambriaCommandContext>
{

	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"(post) (\\S*) (\\S*) (.*)",
			"(read) (\\S*) (\\S*) (\\S*)",
		};
	}

	@Override
	public void checkReady ( CambriaCommandContext context ) throws CommandNotReadyException
	{
		if ( !context.checkClusterReady () )
		{
			throw new CommandNotReadyException ( "Use 'cluster' to specify a cluster to use." );
		}
	}

	@Override
	public void execute ( String[] parts, CambriaCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		if ( parts[0].equalsIgnoreCase ( "read" ))
		{
			CambriaConsumer cc = null;
			try
			{
				cc = CambriaClientFactory.createConsumer ( ConnectionType.HTTP, context.getCluster (), parts[1], parts[2], parts[3],
					-1, -1, null, context.getApiKey(), context.getApiPwd() );
				context.applyTracer ( cc );

				for ( String msg : cc.fetch () )
				{
					out.println ( msg );
				}
			}
			catch ( IOException e )
			{
				out.println ( "Problem fetching messages: " + e.getMessage() );
			}
			catch ( GeneralSecurityException e )
			{
				out.println ( "Problem fetching messages: " + e.getMessage() );
			}
			finally
			{
				cc.close ();
			}
		}
		else
		{
			CambriaBatchingPublisher pub = null;
			try
			{
				pub = new PublisherBuilder ().
					usingHosts ( context.getCluster () ).
					onTopic ( parts[1] ).
					authenticatedBy ( context.getApiKey(), context.getApiPwd() ).
					build ()
				;
				pub.send ( parts[2], parts[3] );
			}
			catch ( IOException e )
			{
				out.println ( "Problem sending message: " + e.getMessage() );
			}
			catch ( GeneralSecurityException e )
			{
				out.println ( "Problem sending message: " + e.getMessage() );
			}
			finally
			{
				List<message> left = null;
				try
				{
					if ( pub != null ) 
					{
						left = pub.close ( 500, TimeUnit.MILLISECONDS );
					}
				}
				catch ( IOException e )
				{
					out.println ( "Problem sending message: " + e.getMessage() );
				}
				catch ( InterruptedException e )
				{
					out.println ( "Problem sending message: " + e.getMessage() );
				}
				if ( left != null && left.size () > 0 )
				{
					out.println ( left.size() + " messages not sent." );
				}
			}
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "post <topicName> <partition> <message>" );
		out.println ( "read <topicName> <consumerGroup> <consumerId>" );
	}

}
