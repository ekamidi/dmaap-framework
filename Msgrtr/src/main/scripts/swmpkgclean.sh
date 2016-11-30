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

# SWM can only store a finite amount of packages in its repository, so this script deletes the oldest package.
# This script is run by Jenkins after the build is finished (post SWM upload).

SWM_COMPONENT="com.att.nsa:msgrtr"

SWM_PKGS=`/opt/app/swm/aftswmcli/bin/swmcli "component pkglist -c $SWM_COMPONENT -df -dh -dj -sui"`
SWM_PKGS_COUNT=`echo "$SWM_PKGS" | wc -l`
SWM_PKGS_OLDEST=`echo "$SWM_PKGS" | head -1`
SWM_PKGS_MAX_COUNT=2

if [ $SWM_PKGS_COUNT > $SWM_PKGS_MAX_COUNT ]
then
	SWM_PKG_OLDEST_VERSION=`echo $SWM_PKGS_OLDEST | awk '{print $2}'`

	# Delete the oldest package for this component from the SWM repository
	/opt/app/swm/aftswmcli/bin/swmcli "component pkgdelete -c $SWM_COMPONENT:$SWM_PKG_OLDEST_VERSION"
else
	echo "No need to clean up SWM, package count ($SWM_PKGS_COUNT) is below threshold ($SWM_PKGS_MAX_COUNT)"
fi
