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
#include <ctime>
#include <string.h>
#include "cambria.h"

const char* kAlarm =
	"<EVENT>"
        "<AGENT_ADDR>12.123.70.213</AGENT_ADDR>"
        "<AGENT_RESOLVED>ptdor306me1.els-an.att.net</AGENT_RESOLVED>"
        "<TIME_RECEIVED>1364716208</TIME_RECEIVED>"
        "  <PROTOCOL_VERSION>V1</PROTOCOL_VERSION>"
        "  <ENTERPRISE_LEN>9</ENTERPRISE_LEN>"
        "  <ENTERPRISE>.1.3.6.1.4.1.9.9.187</ENTERPRISE>"
        "  <GENERIC>6</GENERIC>"
        "  <SPECIFIC>2</SPECIFIC>"
        "  <COMMAND>167</COMMAND>"
        "  <REQUEST_ID>0</REQUEST_ID>"
        "  <ERROR_STATUS>0</ERROR_STATUS>"
        "  <ERROR_INDEX>0</ERROR_INDEX>"
        "  <AGENT_TIME_UP>1554393204</AGENT_TIME_UP>"
        "  <COMMUNITY_LEN>10</COMMUNITY_LEN>"
        "  <COMMUNITY>nidVeskaf0</COMMUNITY>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.2.1.15.3.1.14.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>OCTET_STRING_HEX</VARBIND_TYPE>"
        "      <VARBIND_VALUE>02 02 </VARBIND_VALUE>"
        "    </VARBIND>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.2.1.15.3.1.2.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>INTEGER</VARBIND_TYPE>"
        "      <VARBIND_VALUE>1</VARBIND_VALUE>"
        "    </VARBIND>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.4.1.9.9.187.1.2.1.1.7.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>OCTET_STRING_ASCII</VARBIND_TYPE>"
        "      <VARBIND_VALUE>peer in wrong AS</VARBIND_VALUE>"
        "    </VARBIND>"
        "    <VARBIND>"
        "      <VARBIND_OID>.1.3.6.1.4.1.9.9.187.1.2.1.1.8.32.4.52.58</VARBIND_OID>"
        "      <VARBIND_TYPE>INTEGER</VARBIND_TYPE>"
        "      <VARBIND_VALUE>4</VARBIND_VALUE>"
        "    </VARBIND>"
      "</EVENT>";

int main ( int argc, const char* argv[] )
{
	char** msgs = new char* [ 100 ];
	for ( int i=0; i<100; i++ )
	{
		msgs[i] = new char [ ::strlen ( kAlarm + 1 ) ];
		::strcpy ( msgs[i], kAlarm );
	}

	std::time_t start = std::time ( NULL );
	for ( int i=0; i<5000; i++ )
	{
		::cambriaSimpleSendMultiple ( "localhost", 8080, "topic", "streamName", (const char**)msgs, 100 );
		if ( i % 50 == 0 )
		{
			std::time_t end = std::time ( NULL );
			double seconds = difftime ( end, start );
			::printf ( "%.f seconds for %u posts.\n", seconds, i*100 );
		}
	}
	std::time_t end = std::time ( NULL );
	double seconds = difftime ( end, start );
	::printf ( "%.f seconds for 1,000,000 posts.\n", seconds );

	return 0;
}
