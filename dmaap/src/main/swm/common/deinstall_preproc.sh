#!/bin/sh

. `dirname $0`/deinstall.env

LRMCLI=${INSTALL_ROOT}/opt/app/aft/scldlrm/bin/lrmcli
PATH=$PATH:`dirname $0`/utils; export PATH

runningCount=`${LRMCLI} -running | grep -w ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} | wc -l` || fail 300 "Unable to determine how many instances are running prior to notifying LRM of the upgrade"

if [ "${runningCount}" -eq 0 ]; then

${LRMCLI} -delete -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${AFT_SERVICE_ENV} || exit 101

	else 
		${LRMCLI} -shutdown -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${AFT_SERVICE_ENV} -ttw ${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS} || exit 100
		${LRMCLI} -delete -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${AFT_SERVICE_ENV} || exit 101
		
fi

rm -rf ${INSTALL_ROOT}/${ROOT_DIR}/logs || {
    echo "WARNING: Unable to purge logs directory during deinstall"
}

exit 0
