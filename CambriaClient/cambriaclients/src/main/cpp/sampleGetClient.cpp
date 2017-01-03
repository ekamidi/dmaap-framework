
#include <stdio.h>
#include "cambria.h"

int main ( int argc, const char* argv[] )
{
	const CAMBRIA_CLIENT cc = ::cambriaCreateClient ( "localhost", 8080, "topic", CAMBRIA_NATIVE_FORMAT );
	if ( !cc )
	{
		::printf ( "Couldn't create client.\n" );
		return 1;
	}

	int count = 0;
	while ( 1 )
	{
		cambriaGetResponse* response = ::cambriaGetMessages ( cc, 5000, 1024*1024 );
		if ( response && response->statusCode < 300 )
		{
			for ( int i=0; i<response->messageCount; i++ )
			{
				const char* msg = response->messageSet [ i ];
				::printf ( "%d: %s\n", count++,	 msg );
			}
			::cambriaDestroyGetResponse ( cc, response );
		}
		else if ( response )
		{
			::fprintf ( stderr, "%d %s", response->statusCode, response->statusMessage );
		}
		else
		{
			::fprintf ( stderr, "No response object.\n" );
		}
	}

	::cambriaDestroyClient ( cc );

	return 0;
}
