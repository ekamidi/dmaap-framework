#!/bin/bash
#*******************************************************************************
# BSD License
#  
# Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
#  
# Redistribution and use in source and binary forms, with or without modification, are permitted
# provided that the following conditions are met:
#  
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions
#    and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of
#    conditions and the following disclaimer in the documentation and/or other materials provided
#    with the distribution.
# 3. All advertising materials mentioning features or use of this software must display the
#    following acknowledgement:  This product includes software developed by the AT&T.
# 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
#    promote products derived from this software without specific prior written permission.
#  
# THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
# SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
# OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
# ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
# DAMAGE.
#*******************************************************************************

# format:
#	cambriaPut.sh <apiPath> [<file>]

if [ $# -gt 2 ]; then
	echo "usage: cambriaPost.sh <apiPath> [<file>]"
	exit
fi
if [ $# -lt 1 ]; then
	echo "usage: cambriaPost.sh <apiPath> [<file>]"
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
	curl -i -X POST $FILE -H "Content-Type: application/json" $AUTHPART $URI
else
	echo "auth in use"
	SIGNATURE=`echo -n "$DATE" | openssl sha1 -hmac $CAMBRIA_APISECRET -binary | openssl base64`
	curl -i -X POST $FILE -H "Content-Type: application/json" -H "X-CambriaAuth: $CAMBRIA_APIKEY:$SIGNATURE" -H "X-CambriaDate: $DATE" $URI
fi

