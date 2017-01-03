/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.test.support;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A helper for unit testing systems that use a CambriaPublisher. When setting
 * up your test, inject an instance into CambriaClientFactory to have it return
 * the mock client.
 * 
 *
 */
public class CambriaBatchingPublisherMock implements CambriaBatchingPublisher
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

	public CambriaBatchingPublisherMock ()
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
	public void setHttpBasicCredentials ( String username, String password )
	{
	}

	@Override
	public void clearHttpBasicCredentials ()
	{
	}

	@Override
	public void logTo ( Logger log )
	{
	}

	private final LinkedList<Entry> fCaptures;
	private LinkedList<Listener> fListeners = new LinkedList<Listener> ();
}
