#!/bin/bash

#
#	act as a simple cambria publisher, requires wget
#
#	usage:
#		cambriaPublisher <broker> <topic>
#

DATE=`date`
SIGNATURE=`echo -n "$DATE" | openssl sha1 -hmac $CAMBRIA_APISECRET -binary | openssl base64`

while read LINE
do
	wget -q --header "Content-Type: text/plain" --header "X-CambriaAuth: $CAMBRIA_APIKEY:$SIGNATURE" --header "X-CambriaDate: $DATE" --post-data="$LINE" -O - $1/events/$2 >/dev/null
done 

