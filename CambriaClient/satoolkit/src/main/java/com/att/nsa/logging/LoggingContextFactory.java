/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.logging;

import com.att.nsa.logging.impl.Slf4jLoggingContext;

/**
 * A factory for setting up a LoggingContext
 * 
 *
 */
public class LoggingContextFactory
{
	public static class Builder
	{
		/**
		 * Inherit values from a base context
		 * @param lc
		 * @return this builder
		 */
		public Builder withBaseContext ( LoggingContext lc )
		{
			fBase = lc;
			return this;
		}

		/**
		 * Build the logging context.
		 * @return a new logging context
		 */
		public LoggingContext build ()
		{
			return new Slf4jLoggingContext ( fBase );
		}

		private LoggingContext fBase = null;
	}
}
