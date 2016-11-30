/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.nsa.mr.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRTopicManager;
import com.att.nsa.mr.client.MRTopicManager.TopicInfo;

public class TopicCommand implements Command<MRCommandContext>
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
	public void checkReady ( MRCommandContext context ) throws CommandNotReadyException
	{
		if ( !context.checkClusterReady () )
		{
			throw new CommandNotReadyException ( "Use 'cluster' to specify a cluster to use." );
		}
	}

	@Override
	public void execute ( String[] parts, MRCommandContext context, PrintStream out ) throws CommandNotReadyException
	{
		final MRTopicManager tm = MRClientFactory.createTopicManager ( context.getCluster(), context.getApiKey(), context.getApiPwd() );
		context.applyTracer ( tm );

		try
		{
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
		finally
		{
			tm.close ();
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
