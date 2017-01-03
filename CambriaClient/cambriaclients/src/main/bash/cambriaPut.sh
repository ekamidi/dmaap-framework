#!/bin/bash
#*******************************************************************************
# Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
#*******************************************************************************

# format:
#	cambriaPut.sh <apiPath> [<file>]

if [ $# -gt 2 ]; then
	echo "usage: cambriaPut.sh <apiPath> [<file>]"
	exit
fi
if [ $# -lt 1 ]; then
	echo "usage: cambriaPut.sh <apiPath> [<file>]"
	exit
fi

API=$1
FILE=
if [ $# -gt 1 ]; then
	FILE="-d @$2"
fi

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
	curl -i -X PUT $FILE -H "Content-Type: application/json" $AUTHPART $URI
else
	echo "auth in use"
	SIGNATURE=`echo -n "$DATE" | openssl sha1 -hmac $CAMBRIA_APISECRET -binary | openssl base64`
	curl -i -X PUT $FILE -H "Content-Type: application/json" -H "X-CambriaAuth: $CAMBRIA_APIKEY:$SIGNATURE" -H "X-CambriaDate: $DATE" $URI
fi

