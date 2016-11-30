#!/bin/sh
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

# switched this from CAMBRIA_API_HOME, which should be declared in the env.
# harmless to overwrite it here, but it's confusing to do so.
BASE_DIR=`dirname "$0"`/..

# use JAVA_HOME if provided
if [ -n "${CAMBRIA_JAVA_HOME}" ]; then
    JAVA=${CAMBRIA_JAVA_HOME}/bin/java
elif [ -n "${JAVA_HOME}" ]; then
    JAVA=${JAVA_HOME}/bin/java
else
    JAVA=java
fi

# use the logs dir set in environment, or the installation's logs dir if not set
if [ -z "$CAMBRIA_LOGS_HOME" ]; then
	CAMBRIA_LOGS_HOME=$BASE_DIR/logs
fi

mkdir -p ${CAMBRIA_LOGS_HOME}
# run java. The classpath is the etc dir for config files, and the lib dir
# for all the jars.
#
# don't pipe stdout/stderr to /dev/null here - some diagnostic info is available only there.
# also don't assume the run is in the background. the caller should take care of that.
#
$JAVA -cp ${BASE_DIR}/etc:${BASE_DIR}/lib/* com.att.nsa.cambria.CambriaApiServer $* >${CAMBRIA_LOGS_HOME}/console.log 2>&1
