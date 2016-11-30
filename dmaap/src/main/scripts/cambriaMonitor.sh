#!/bin/bash

#
#	act as a simple cambria consumer, requires wget
#
#	usage:
#		cambriaMonitor <broker> <topic> <group> <id> <timeout>
#

while :
do
	wget -q -O - $1/events/$2/$3/$4?timeout=$5\&pretty=1
	if [ $? -ne 0 ]
	then
		sleep 10
	fi
	echo
done

