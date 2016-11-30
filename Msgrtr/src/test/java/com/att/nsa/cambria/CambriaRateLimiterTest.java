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
package com.att.nsa.cambria;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.apiServer.util.NsaTestClock;

public class CambriaRateLimiterTest 
{
	@Test
	public void testRateLimiter ()
	{
		/*final NsaTestClock clock = new NsaTestClock(1, false);

		final String topic = "topic";
		final String consumerGroup = "group";
		final String clientId = "id";

		final int window = 5;

		// rate limit: 1 empty call/min avg over 5 minutes, with 10ms delay
		final CambriaRateLimiter rater = new CambriaRateLimiter ( 1.0, window, 10 );
		try
		{
			// prime with a call to start rate window
			rater.onCall ( topic, consumerGroup, clientId );
			rater.onSend ( topic, consumerGroup, clientId, 1 );
			clock.addMs ( 1000*60*window );

			// rate should now be 0, with a good window
			for ( int i=0; i<4; i++ )
			{
				clock.addMs ( 1000*15 );
				rater.onCall ( topic, consumerGroup, clientId );
				rater.onSend ( topic, consumerGroup, clientId, 0 );
			}
			// rate is now 0.8 = 4 calls in last 5 minutes = 4/5 = 0.8

			clock.addMs ( 1000*15 );
			rater.onCall ( topic, consumerGroup, clientId );
			rater.onSend ( topic, consumerGroup, clientId, 0 );
				// rate = 1.0 = 5 calls in last 5 mins

			clock.addMs ( 1000 );
			rater.onCall ( topic, consumerGroup, clientId );
			rater.onSend ( topic, consumerGroup, clientId, 0 );
				// rate = 1.2 = 6 calls in last 5 mins, should fire

			fail ( "Should have thrown rate limit exception." );
		}
		catch ( CambriaApiException x )
		{
			// good
		}*/
	}
}
