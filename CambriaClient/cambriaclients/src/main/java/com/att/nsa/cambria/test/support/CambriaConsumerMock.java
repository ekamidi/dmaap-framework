/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.test.support;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import com.att.nsa.cambria.client.CambriaConsumer;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

/**
 * A helper for unit testing systems that use a CambriaConsumer. When setting
 * up your test, inject an instance into CambriaClientFactory to have it return
 * the mock client.
 * 
 *
 */
public class CambriaConsumerMock implements CambriaConsumer
{
	public class Entry
	{
		public Entry ( long waitMs, int statusCode, List<String> msgs )
		{
			fWaitMs = waitMs;
			fStatusCode = statusCode;
			fStatusMsg = null;
			fMsgs = new LinkedList<String> ( msgs );
		}

		public Entry ( long waitMs, int statusCode, String statusMsg )
		{
			fWaitMs = waitMs;
			fStatusCode = statusCode;
			fStatusMsg = statusMsg;
			fMsgs = null;
		}

		public LinkedList<String> run () throws IOException
		{
			try
			{
				Thread.sleep ( fWaitMs );
				if ( fStatusCode >= 200 && fStatusCode <= 299 )
				{
					return fMsgs;
				}
				throw new IOException ( "" + fStatusCode + " " + fStatusMsg );
			}
			catch ( InterruptedException e )
			{
				throw new IOException ( e );
			}
		}

		private final long fWaitMs;
		private final int fStatusCode;
		private final String fStatusMsg;
		private final LinkedList<String> fMsgs;
	}

	public CambriaConsumerMock ()
	{
		fReplies = new LinkedList<Entry> ();
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

	public synchronized void add ( Entry e )
	{
		fReplies.add ( e );
	}

	public void addImmediateMsg ( String msg )
	{
		addDelayedMsg ( 0, msg );
	}

	public void addDelayedMsg ( long delay, String msg )
	{
		final LinkedList<String> list = new LinkedList<String> ();
		list.add ( msg );
		add ( new Entry ( delay, 200, list ) );
	}

	public void addImmediateMsgGroup ( List<String> msgs )
	{
		addDelayedMsgGroup ( 0, msgs );
	}

	public void addDelayedMsgGroup ( long delay, List<String> msgs )
	{
		final LinkedList<String> list = new LinkedList<String> ( msgs );
		add ( new Entry ( delay, 200, list ) );
	}

	public void addImmediateError ( int statusCode, String statusText )
	{
		add ( new Entry ( 0, statusCode, statusText ) );
	}

	@Override
	public Iterable<String> fetch () throws IOException
	{
		return fetch ( -1, -1 );
	}

	@Override
	public Iterable<String> fetch ( int timeoutMs, int limit ) throws IOException
	{
		return fReplies.size () > 0 ? fReplies.removeFirst ().run() : new LinkedList<String>();
	}

	@Override
	public void logTo ( Logger log )
	{
	}

	private final LinkedList<Entry> fReplies;
}
