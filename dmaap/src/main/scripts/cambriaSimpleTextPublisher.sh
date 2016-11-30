#!/bin/bash

#
#	act as a simple cambria publisher, requires wget
#
#	usage:
#		cambriaPublisher <broker> <topic>
#

while read LINE
do
	wget -q --header "Content-Type: text/plain" --post-data="$LINE" -O - $1/events/$2 >/dev/null
done 

