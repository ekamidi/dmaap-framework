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
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.MRClientBuilders.PublisherBuilder;
import com.att.nsa.mr.client.MRPublisher.message;

public class MessageCommand implements Command<MRCommandContext>
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
		if ( parts[0].equalsIgnoreCase ( "read" ))
		{
			final MRConsumer cc = MRClientFactory.createConsumer ( context.getCluster (), parts[1], parts[2], parts[3],
				-1, -1, null, context.getApiKey(), context.getApiPwd() );
			context.applyTracer ( cc );
			try
			{
				for ( String msg : cc.fetch () )
				{
					out.println ( msg );
				}
			}
			catch ( Exception e )
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
			final MRBatchingPublisher pub = new PublisherBuilder ().
				usingHosts ( context.getCluster () ).
				onTopic ( parts[1] ).
				authenticatedBy ( context.getApiKey(), context.getApiPwd() ).
				build ()
			;
			try
			{
				pub.send ( parts[2], parts[3] );
			}
			catch ( IOException e )
			{
				out.println ( "Problem sending message: " + e.getMessage() );
			}
			finally
			{
				List<message> left = null;
				try
				{
					left = pub.close ( 500, TimeUnit.MILLISECONDS );
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
