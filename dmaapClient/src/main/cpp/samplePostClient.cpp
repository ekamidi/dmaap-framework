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

#include <stdio.h>
#include "cambria.h"

void handleResponse ( const CAMBRIA_CLIENT cc, const cambriaSendResponse* response )
{
	if ( response )
	{
		::printf ( "\t%d %s\n", response->statusCode, ( response->statusMessage ? response->statusMessage : "" ) );
		::printf ( "\t%s\n", response->responseBody ? response->responseBody : "" );

		// destroy the response (or it'll leak)
		::cambriaDestroySendResponse ( cc, response );
	}
	else
	{
		::fprintf ( stderr, "No response object.\n" );
	}
}

int main ( int argc, const char* argv[] )
{
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	// you can send single message in one call...
	::printf ( "Sending single message...\n" );
	int sent = ::cambriaSimpleSend ( "localhost", 8080, "topic", "streamName",
		"{ \"field\":\"this is a JSON formatted alarm\" }" );
	::printf ( "\t%d sent\n\n", sent );

	// you can also send multiple messages in one call with cambriaSimpleSendMultiple.
	// the message argument becomes an array of strings, and you pass an array
	// count too.
	const char* msgs[] =
	{
		"{\"format\":\"json\"}",
		"<format>xml</format>",
		"or whatever. they're just strings."
	};
	sent = ::cambriaSimpleSendMultiple ( "localhost", 8080, "topic", "streamName", msgs, 3 );
	::printf ( "\t%d sent\n\n", sent );

	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	// you can also create a client instance to keep around and make multiple
	// send requests to. Chunked sending isn't supported right now, so each
	// call to cambriaSendMessage results in a full socket open / post / close
	// cycle, but hopefully we can improve this with chunking so that subsequent
	// sends just push the message into the socket.

	// create a client
	const CAMBRIA_CLIENT cc = ::cambriaCreateClient ( "localhost", 8080, "topic", CAMBRIA_NATIVE_FORMAT );
	if ( !cc )
	{
		::printf ( "Couldn't create client.\n" );
		return 1;
	}

	////////////////////////////////////////////////////////////////////////////
	// send a single message
	::printf ( "Sending single message...\n" );
	const cambriaSendResponse* response = ::cambriaSendMessage ( cc, "streamName", "{\"foo\":\"bar\"}" );
	handleResponse ( cc, response );

	////////////////////////////////////////////////////////////////////////////
	// send a few messages at once
	const char* msgs2[] =
	{
		"{\"foo\":\"bar\"}",
		"{\"bar\":\"baz\"}",
		"{\"zoo\":\"zee\"}",
		"{\"foo\":\"bar\"}",
		"{\"foo\":\"bar\"}",
		"{\"foo\":\"bar\"}",
	};
	unsigned int count = sizeof(msgs2)/sizeof(const char*);

	::printf ( "Sending %d messages...\n", count );
	response = ::cambriaSendMessages ( cc, "streamName", msgs2, count );
	handleResponse ( cc, response );

	////////////////////////////////////////////////////////////////////////////
	// destroy the client (or it'll leak)
	::cambriaDestroyClient ( cc );

	return 0;
}
