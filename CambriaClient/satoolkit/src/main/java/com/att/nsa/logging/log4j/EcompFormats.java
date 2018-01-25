/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging.log4j;

public enum EcompFormats
{	
	// Specifically versioned formats -- DON'T change these once released. You
	// should add new ones. Leaving published formats intact guarantees that older code
	// can log according to the standard format it was tested with.
	ECOMP_AUDIT_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{" + EcompFields.kEndTimestamp + "}|%X{requestId}|%X{serviceInstanceId}|%-10t|%X{serverName}|%X{serviceName}|%X{partnerName}|%X{statusCode}|%X{responseCode}|%X{responseDescription}|%X{instanceUuid}|%p|%X{severity}|%X{serverIpAddress}|%X{" + EcompFields.kElapsedTimeMs + "}|%X{server}|%X{clientIpAddress}|%X{className}|%X{unused}|%X{processKey}|%X{customField1}|%X{customField2}|%X{customField3}|%X{customField4}|%m%n" ),
	ECOMP_METRIC_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{" + EcompFields.kEndTimestamp + "}|%X{requestId}|%X{serviceInstanceId}|%-10t|%X{serverName}|%X{serviceName}|%X{partnerName}|%X{targetEntity}|%X{targetServiceName}|%X{statusCode}|%X{responseCode}|%X{responseDescription}|%X{instanceUuid}|%p|%X{severity}|%X{serverIpAddress}|%X{" + EcompFields.kElapsedTimeMs + "}|%X{server}|%X{clientIpAddress}|%X{className}|%X{unused}|%X{processKey}|%X{targetVirtualEntity}|%X{customField1}|%X{customField2}|%X{customField3}|%X{customField4}|%m%n" ),
	ECOMP_ERROR_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{requestId}|%-10t|%X{serviceName}|%X{partnerName}|%X{targetEntity}|%X{targetServiceName}|%p|%X{errorCode}|%X{errorDescription}|%m%n" ),
	ECOMP_DEBUG_1610 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{requestId}|%X{debugInfo}%n" ),
	ECOMP_DEBUG_1610_1 ( "%X{" + EcompFields.kBeginTimestamp + "}|%X{requestId}|%m|" + EcompFields.kEcompDebugLineEnding + "%n" ),

	// non-versioned formats -- change these to the current set as needed
	// ECOMP_DEBUG_1610_1 fixes two things baout ECOMP_DEBUG_1610. First, it's assumed that the
	// log specification writers meant to use the log message at the end of the log line rather
	// than an MDC field that we called debugInfo. Second, we add the explicit debug log line terminator
	// that we overlooked earlier.

	
	// non-versioned formats -- change these to the current set as needed. For less picky software,
	// these will always log with the latest standard format.
	ECOMP_AUDIT ( ECOMP_AUDIT_1610.getConversionPattern () ),
	ECOMP_METRIC ( ECOMP_METRIC_1610.getConversionPattern () ),
	ECOMP_ERROR ( ECOMP_ERROR_1610.getConversionPattern () ),
	ECOMP_DEBUG ( ECOMP_DEBUG_1610_1.getConversionPattern () ),

	;

	public String getConversionPattern () { return fPattern; }
	private EcompFormats ( String pattern ) { fPattern = pattern; }
	private final String fPattern;}
