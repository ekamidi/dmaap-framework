#!/bin/sh

# switched this from CAMBRIA_API_HOME, which should be declared in the env.
# harmless to overwrite it here, but it's confusing to do so.
BASE_DIR=`dirname "$0"`/..

# determin a path separator that works for this platform
PATHSEP=":"
case "$(uname -s)" in

	Darwin)
		;;

	 Linux)
	 	;;

	 CYGWIN*|MINGW32*|MSYS*)
		PATHSEP=";"
		;;

	*)
		;;
esac

# use JAVA_HOME if provided
if [ -n "${CAMBRIA_JAVA_HOME}" ]; then
    JAVA=${CAMBRIA_JAVA_HOME}/bin/java
elif [ -n "${JAVA_HOME}" ]; then
    JAVA=${JAVA_HOME}/bin/java
else
    JAVA=java
fi

$JAVA -cp ${BASE_DIR}/etc${PATHSEP}${BASE_DIR}/lib/* com.att.nsa.cambria.tools.ConfigTool $*
