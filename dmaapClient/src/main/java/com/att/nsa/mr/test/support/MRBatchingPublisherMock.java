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
package com.att.nsa.mr.test.support;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.response.MRPublisherResponse;

/**
 * A helper for unit testing systems that use a MRPublisher. When setting
 * up your test, inject an instance into MRClientFactory to have it return
 * the mock client.
 * 
 * @author author
 *
 */
public class MRBatchingPublisherMock implements MRBatchingPublisher
{
	public class Entry
	{
		public Entry ( String partition, String msg )
		{
			fPartition = partition;
			fMessage = msg;
		}

		@Override
		public String toString ()
		{
			return fMessage;
		}
		
		public final String fPartition;
		public final String fMessage;
	}

	public MRBatchingPublisherMock ()
	{
		fCaptures = new LinkedList<Entry> ();
	}

	public interface Listener
	{
		void onMessage ( Entry e );
	}
	public void addListener ( Listener listener )
	{
		fListeners.add ( listener );
	}
	
	public List<Entry> getCaptures ()
	{
		return getCaptures ( new MessageFilter () { @Override public boolean match ( String msg ) { return true; } } );
	}

	public interface MessageFilter
	{
		boolean match ( String msg );
	}

	public List<Entry> getCaptures ( MessageFilter filter )
	{
		final LinkedList<Entry> result = new LinkedList<Entry> ();
		for ( Entry capture : fCaptures )
		{
			if ( filter.match ( capture.fMessage ) )
			{
				result.add ( capture );
			}
		}
		return result;
	}

	public int received ()
	{
		return fCaptures.size();
	}

	public void reset ()
	{
		fCaptures.clear ();
	}

	@Override
	public int send ( String partition, String msg )
	{
		final Entry e = new Entry ( partition, msg ); 

		fCaptures.add ( e );
		for ( Listener l : fListeners )
		{
			l.onMessage ( e );
		}
		return 1;
	}

	@Override
	public int send ( message msg )
	{
		return send ( msg.fPartition, msg.fMsg );
	}
	@Override
	public int send ( String msg )
	{
		return 1;
		
	}

	@Override
	public int send ( Collection<message> msgs )
	{
		int sum = 0;
		for ( message m : msgs )
		{
			sum += send ( m );
		}
		return sum;
	}

	@Override
	public int getPendingMessageCount ()
	{
		return 0;
	}

	@Override
	public List<message> close ( long timeout, TimeUnit timeoutUnits )
	{
		return new LinkedList<message> ();
	}

	@Override
	public void close ()
	{
	}

	@Override
	public void setApiCredentials ( String apiKey, String apiSecret )
	{
	}

	@Override
	public void clearApiCredentials ()
	{
	}

	@Override
	public void logTo ( Logger log )
	{
	}

	private final LinkedList<Entry> fCaptures;
	private LinkedList<Listener> fListeners = new LinkedList<Listener> ();
	@Override
	public MRPublisherResponse sendBatchWithResponse() {
		// TODO Auto-generated method stub
		return null;
	}
}
