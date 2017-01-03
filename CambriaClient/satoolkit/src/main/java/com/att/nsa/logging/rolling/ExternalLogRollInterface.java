/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging.rolling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

// FIXME: this was build for a potential GFP-IP use, but has not been picked
// up as of 7 Dec 2015. Because it requires log4j in the build, we get 
// complaints from slf4j at app startup

/**
 * This class creates a signal handler that seeks a given Log4J file appender
 * and resets its output file. This allows us to control log rolling outside
 * of the Java process using tools like logrotate. This is desirable from
 * an OA&M consistency perspective.
 * 
 * Use of this class requires a Sun-based JVM.
 * 
 *
 */
@SuppressWarnings("restriction")
public class ExternalLogRollInterface implements SignalHandler
{
	/**
	 * Create a signal handler for log rolling on the given signal.
	 * @param signal HUP, INT, etc.
	 */
	public ExternalLogRollInterface ( String signal )
	{
		fSignal = signal;
	}

	/**
	 * Install the signal handler.
	 */
	public void install ()
	{
		Signal.handle ( new Signal ( fSignal ), this );
	}

	/**
	 * Rotate the log
	 */
	public void rotate ()
	{
/*
		final HashMap<String,Appender> apps = new HashMap<String,Appender> ();

		// iterate through all loggers to collect the appenders
		final org.apache.log4j.Logger root = LogManager.getRootLogger ();
		collectAppenders ( root.getAllAppenders (), apps );
		collectAppenders ( root.getLoggerRepository (), apps );
		final LoggerRepository lr = LogManager.getLoggerRepository ();
		collectAppenders ( lr, apps );

		// update each file appender
		for ( Entry<String,Appender> e : apps.entrySet () )
		{
			if ( e.getValue() instanceof FileAppender )
			{
				// the log rotator has already moved the current file. Any post-move
				// log writes are still going there (via inode). We just need to close
				// and re-open the same filename. The activateOptions call does this,
				// even if the filename didn't change.
				final FileAppender fa = (FileAppender) e.getValue ();
				fa.setAppend ( false );	// never append the files
				fa.activateOptions ();
			}
		}
*/
	}
	
	/**
	 * Provided as part of the signal handling interface. Calls rotate().
	 */
	@Override
	public void handle ( Signal arg0 )
	{
		final String signame = arg0.getName ();
		if ( signame.equals ( fSignal ) )
		{
			log.info ( "Received signal " + arg0 + "; rotating." );
			rotate ();
			log.info ( "Log rotation complete." );
		}
		else
		{
			log.info ( "Received signal " + arg0 + "; ignored." );
		}
	}

	private final String fSignal;
	private static final Logger log = LoggerFactory.getLogger ( ExternalLogRollInterface.class );
/*
	private void collectAppenders ( LoggerRepository lr, HashMap<String,Appender> apps )
	{
		final Enumeration<?> loggers = lr.getCurrentLoggers ();
		while ( loggers.hasMoreElements () )
		{
			final Object o = loggers.nextElement ();
			if ( o instanceof org.apache.log4j.Logger )
			{
				org.apache.log4j.Logger log = (org.apache.log4j.Logger) o;
				final Enumeration<?> appenders = log.getAllAppenders ();
				collectAppenders ( appenders, apps );
			}
		}
	}

	private void collectAppenders ( Enumeration<?> appenders, HashMap<String, Appender> apps )
	{
		while ( appenders.hasMoreElements () )
		{
			final Object a = appenders.nextElement ();
			if ( a instanceof Appender )
			{
				final Appender aa = (Appender) a;
				final String name = aa.getName ();
				apps.put ( name, aa );	// would overwrite, but that should be fine as names are 1:1 to appenders
			}
		}
	}
*/
}
