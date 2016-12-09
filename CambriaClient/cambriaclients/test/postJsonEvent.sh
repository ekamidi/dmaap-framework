#!/bin/sh

#Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.

curl -i -X POST -d @$2 --header "Content-Type: application/json" $CAMBRIA_URI/events/$1
echo ""

