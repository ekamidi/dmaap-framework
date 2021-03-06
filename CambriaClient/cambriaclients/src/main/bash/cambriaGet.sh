#!/bin/bash
#*******************************************************************************
# Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
#*******************************************************************************

# format://
#	cambriaGet.sh <apiPath> 

if [ $# -gt 2 ]; then
	echo "usage: cambriaGet.sh <apiPath>"
	exit
fi
if [ $# -lt 1 ]; then
	echo "usage: cambriaGet.sh <apiPath>"
	exit
fi

API=$1

# the date needs to be in one of the formats cambria accepts
case "$(uname -s)" in

	Darwin)
		# "EEE MMM dd HH:mm:ss z yyyy"
		DATE=`date`
		;;

	 Linux)
		# "EEE MMM dd HH:mm:ss z yyyy"
		DATE=`date`
	 	;;

	 CYGWIN*|MINGW32*|MSYS*)
		DATE=`date --rfc-2822`
		;;

	*)
		DATE=`date`
		;;
esac


URI="http://$CAMBRIA_SERVER/$API"

if [ -z "$CAMBRIA_APIKEY" ]; then
	echo "no auth"
	curl -i -X GET $AUTHPART $URI
else
	echo "auth in use"
	SIGNATURE=`echo -n "$DATE" | openssl sha1 -hmac $CAMBRIA_APISECRET -binary | openssl base64`
	curl -i -X GET -H "X-CambriaAuth: $CAMBRIA_APIKEY:$SIGNATURE" -H "X-CambriaDate: $DATE" $URI
fi

