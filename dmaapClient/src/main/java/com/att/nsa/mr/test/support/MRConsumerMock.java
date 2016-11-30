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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.response.MRConsumerResponse;

/**
 * A helper for unit testing systems that use a MRConsumer. When setting
 * up your test, inject an instance into MRClientFactory to have it return
 * the mock client.
 * 
 * @author author
 *
 */
public class MRConsumerMock implements MRConsumer
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

	public MRConsumerMock ()
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

	@Override
	public MRConsumerResponse fetchWithReturnConsumerResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MRConsumerResponse fetchWithReturnConsumerResponse(int timeoutMs,
			int limit) {
		// TODO Auto-generated method stub
		return null;
	}
}
