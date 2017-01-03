/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging.log4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;

import com.att.nsa.clock.SaClock;

/**
 * A Log4J Layout class that has pre-built ECOMP-compliant formats. To use,
 * <br/>
 * 		&lt;layout class="com.att.nsa.logging.log4j.EcompLayout"&gt;<br/>
 *			&lt;param<br/>
 *				name="ConversionPattern"<br/>
 *				value="ECOMP_AUDIT"<br/>
 *			/&lt;<br/>
 *		&lt;/layout&lt;<br/>
 * <br/>
 * Format choices are ECOMP_AUDIT, ECOMP_METRIC, ECOMP_ERROR, and ECOMP_DEBUG. You can
 * also append _1610 to each of these to get the 1610 version of the format. (Despite
 * being declared a standard, the formats have changed since 1507.)
 * 
 *
 */
public class EcompLayout extends org.apache.log4j.EnhancedPatternLayout
{
	public EcompLayout ()
	{
		super ();
	}

	public EcompLayout ( String pattern )
	{
		super ( pattern );
	}

	@Override
	public void setConversionPattern ( final String conversionPattern )
	{
		try
		{
			// first attempt to try the pattern value as an enum value
			super.setConversionPattern ( EcompFormats.valueOf ( conversionPattern ).getConversionPattern () );
		}
		catch ( IllegalArgumentException x )
		{
			// if it's not a known pattern, use it literally
			super.setConversionPattern ( conversionPattern );
		}
	}
	
	@Override
	public String format ( final LoggingEvent event )
	{
		// we don't actually do any formatting. we just populate the MDC with calculated values
		populateEcompAutomatics ( event );
		return super.format ( event );
	}

	protected void populateEcompAutomatics ( final LoggingEvent event )
	{
		final long beginTimestampMs = getMdcLong ( EcompFields.kBeginTimestampMs, -1 );
		if ( beginTimestampMs >= 0 )
		{
			final long now = SaClock.now ();

			putMdc ( EcompFields.kBeginTimestamp, timestampMsToDate ( beginTimestampMs ) );
			putMdc ( EcompFields.kEndTimestamp, timestampMsToDate ( now ) );
			putMdc ( EcompFields.kElapsedTimeMs, now - beginTimestampMs );
		}
		else
		{
			// the ECOMP standard doesn't specify how a timestamp gets in a log record
			// when outside the context of a transaction/request. Assume that since
			// kBeginTimestampMs is not set, we're outside a transaction and populate
			// just the beginning time. Obviously (at least to people not on the ECOMP
			// logging team?), we need a timestamp.
			putMdc ( EcompFields.kBeginTimestamp, timestampMsToDate ( SaClock.now () ) );
			putMdc ( EcompFields.kEndTimestamp, "" );
			putMdc ( EcompFields.kElapsedTimeMs, "" );
		}
	}

	static String timestampMsToDate ( long timestampMs )
	{
		// per 1610 doc, "2015-06-03T13:21:58.340+00:00"
		final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00";

		final SimpleDateFormat sdf = new SimpleDateFormat ( dateFormat );
		sdf.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		return sdf.format ( new Date ( timestampMs ) );
	}

	void putMdc ( String key, String val )
	{
		MDC.put ( key, val );
	}

	void putMdc ( String key, long val )
	{
		MDC.put ( key, Long.toString ( val ) );
	}

	long getMdcLong ( String key, long defval )
	{
		final Object o = MDC.get ( key );
		if ( o == null ) return defval;
		if ( o instanceof String )
		{
			try
			{
				return new Long ( o.toString () );
			}
			catch ( NumberFormatException x )
			{
				return defval;
			}
		}
		if ( o instanceof Long )
		{
			return (Long)o;
		}
		return defval;
	}
}
