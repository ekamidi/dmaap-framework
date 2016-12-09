/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Set;

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.apiClient.http.HttpClient.ConnectionType;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaTopicManager;
import com.att.nsa.cambria.client.CambriaTopicManager.TopicInfo;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

@SuppressWarnings("deprecation")
public class TopicCommand implements Command<CambriaCommandContext>
{

	@Override
	public String[] getMatches ()
	{
		return new String[]{
			"topic (list)",
			"topic (list) (\\S*)",
			"topic (create) (\\S*) (\\S*) (\\S*)",
			"topic (grant|revoke) (read|write) (\\S*) (\\S*)",
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
		CambriaTopicManager tm = null;

		try
		{
			tm = CambriaClientFactory.createTopicManager ( ConnectionType.HTTP, context.getCluster(), context.getApiKey(), context.getApiPwd() );
				context.applyTracer ( tm );
			if ( parts[0].equals ( "list" ) )
			{
				try
				{
					if ( parts.length == 1 )
					{
						for ( String topic : tm.getTopics () )
						{
							out.println ( topic );
						}
					}
					else
					{
						final TopicInfo ti = tm.getTopicMetadata ( parts[1] );

						final String owner = ti.getOwner ();
						out.println ( "      owner: " + ( owner == null ? "<none>" : owner ) );

						final String desc = ti.getDescription ();
						out.println ( "description: " + ( desc == null ? "<none>" : desc ) );

						final Set<String> prods = ti.getAllowedProducers ();
						if ( prods != null )
						{
							out.println ( "  write ACL: " );
							for ( String key : prods )
							{
								out.println ( "\t" + key );
							}
						}
						else
						{
							out.println ( "  write ACL: <not active>" );
						}

						final Set<String> cons = ti.getAllowedConsumers ();
						if ( cons != null )
						{
							out.println ( "   read ACL: " );
							for ( String key : cons )
							{
								out.println ( "\t" + key );
							}
						}
						else
						{
							out.println ( "   read ACL: <not active>" );
						}
					}
				}
				catch ( IOException x )
				{
					out.println ( "Problem with request: " + x.getMessage () );
				}
				catch ( HttpObjectNotFoundException e )
				{
					out.println ( "Not found: " + e.getMessage () );
				}
			}
			else if ( parts[0].equals ( "create" ) )
			{
				try
				{
					final int partitions = Integer.parseInt ( parts[2] );
					final int replicas = Integer.parseInt ( parts[3] );
					
					tm.createTopic ( parts[1], "", partitions, replicas );
				}
				catch ( HttpException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
				catch ( IOException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
				catch ( NumberFormatException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
			}
			else if ( parts[0].equals ( "grant" ) )
			{
				try
				{
					if ( parts[1].equals ( "write" ) ) 
					{
						tm.allowProducer ( parts[2], parts[3] );
					}
					else if ( parts[1].equals ( "read" ) )
					{
						tm.allowConsumer ( parts[2], parts[3] );
					}
				}
				catch ( HttpException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
				catch ( IOException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
			}
			else if ( parts[0].equals ( "revoke" ) )
			{
				try
				{
					if ( parts[1].equals ( "write" ) ) 
					{
						tm.revokeProducer ( parts[2], parts[3] );
					}
					else if ( parts[1].equals ( "read" ) )
					{
						tm.revokeConsumer ( parts[2], parts[3] );
					}
				}
				catch ( HttpException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
				catch ( IOException e )
				{
					out.println ( "Problem with request: " + e.getMessage () );
				}
			}
		}
		catch ( MalformedURLException e1 )
		{
			out.println ( "Problem with request: " + e1.getMessage () );
		}
		catch ( GeneralSecurityException e1 )
		{
			out.println ( "Problem with request: " + e1.getMessage () );
		}
		finally
		{
			if ( tm != null ) tm.close ();
		}
	}

	@Override
	public void displayHelp ( PrintStream out )
	{
		out.println ( "topic list" );
		out.println ( "topic list <topicName>" );
		out.println ( "topic create <topicName> <partitions> <replicas>" );
		out.println ( "topic grant write|read <topicName> <apiKey>" );
		out.println ( "topic revoke write|read <topicName> <apiKey>" );
	}

}
