#!/bin/sh

curl -i -X POST -d @$2 --header "Content-Type: application/cambria" $CAMBRIA_URI/events/$1

echo ""

