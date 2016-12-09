/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging.log4j;

public enum EcompFormats
{
	// specifically versioned formats -- DON'T change these
	ECOMP_AUDIT_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{" + EcompFields.kEndTimestamp + "}|%X{requestId}|%X{serviceInstanceId}|%-10t|%X{serverName}|%X{serviceName}|%X{partnerName}|%X{statusCode}|%X{responseCode}|%X{responseDescription}|%X{instanceUuid}|%p|%X{severity}|%X{serverIpAddress}|%X{" + EcompFields.kElapsedTimeMs + "}|%X{server}|%X{clientIpAddress}|%X{className}|%X{unused}|%X{processKey}|%X{customField1}|%X{customField2}|%X{customField3}|%X{customField4}|%m%n" ),
	ECOMP_METRIC_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{" + EcompFields.kEndTimestamp + "}|%X{requestId}|%X{serviceInstanceId}|%-10t|%X{serverName}|%X{serviceName}|%X{partnerName}|%X{targetEntity}|%X{targetServiceName}|%X{statusCode}|%X{responseCode}|%X{responseDescription}|%X{instanceUuid}|%p|%X{severity}|%X{serverIpAddress}|%X{" + EcompFields.kElapsedTimeMs + "}|%X{server}|%X{clientIpAddress}|%X{className}|%X{unused}|%X{processKey}|%X{targetVirtualEntity}|%X{customField1}|%X{customField2}|%X{customField3}|%X{customField4}|%m%n" ),
	ECOMP_ERROR_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{requestId}|%-10t|%X{serviceName}|%X{partnerName}|%X{targetEntity}|%X{targetServiceName}|%p|%X{errorCode}|%X{errorDescription}|%m%n" ),
	ECOMP_DEBUG_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{requestId}|%X{debugInfo}%n" ),

	// non-versioned formats -- change these to the current set as needed
	ECOMP_AUDIT ( ECOMP_AUDIT_1610.getConversionPattern () ),
	ECOMP_METRIC ( ECOMP_METRIC_1610.getConversionPattern () ),
	ECOMP_ERROR ( ECOMP_ERROR_1610.getConversionPattern () ),
	ECOMP_DEBUG ( ECOMP_DEBUG_1610.getConversionPattern () ),

	;

	public String getConversionPattern () { return fPattern; }
	private EcompFormats ( String pattern ) { fPattern = pattern; }
	private final String fPattern;
}
