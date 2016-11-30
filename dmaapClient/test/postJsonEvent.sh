#!/bin/sh

curl -i -X POST -d @$2 --header "Content-Type: application/json" $CAMBRIA_URI/events/$1
echo ""

