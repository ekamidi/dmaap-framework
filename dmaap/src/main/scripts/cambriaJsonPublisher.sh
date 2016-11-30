#!/bin/bash

#
#	act as a simple cambria publisher, requires wget
#
#	usage:
#		cambriaPublisher <broker> <topic>
#

KEY=$3
if [ "$3" == "" ]
then
	KEY=`hostname -f`
fi

while read LINE
do
	wget -q --header "Content-Type: application/json" --post-data="{ \"cambria.partition\":\"$KEY\", \"msg\":\"$LINE\" }" -O - $1/events/$2 >/dev/null
done 

