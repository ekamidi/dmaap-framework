/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
/**
 * Publish log entries to Cambria, used by CambriaAppender.
 * Doesn't do any log4j logging to avoid cycles where something downstream logs,
 * which caused CambriaAppender.append() to get called recursively.
 * 
 * See CambriaAppender header for log4j configuration parameters.
 */
package com.att.nsa.cambria.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.att.nsa.cambria.client.CambriaPublisher.message;
import com.att.nsa.cambria.client.impl.CambriaSimplerBatchPublisher.TimestampedMessage;

import junit.framework.TestCase;

public class CambriaSimplerBatchPublisherTest extends TestCase
{
	@Test
	public void testNonUtf8Encodings () throws IOException
	{
		for ( TestItem ti : kTests )
		{
			final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			final TimestampedMessage m = new TimestampedMessage ( new message ( ti.fPartition, ti.fMsg ) );
			CambriaSimplerBatchPublisher.sendEncodedMessage ( baos, m );
			baos.close ();

			final byte[] bytes = baos.toByteArray ();
			assertEquals ( Arrays.toString ( ti.fOut ), Arrays.toString ( bytes ) );
		}
	}

	private static TestItem[] kTests = new TestItem[] {
		new TestItem ( "P", "M", new byte[] { '1', '.', '1', '.', 'P', 'M', '\n' } ),
		new TestItem ( null, "ABC", new byte[] { '0', '.', '3', '.', 'A', 'B', 'C', '\n' } ),
		new TestItem ( null, "\uffff", new byte[] { '0', '.', '3', '.', (byte) 0xef, (byte) 0xbf, (byte) 0xbf, '\n' } ),
	};

	private static class TestItem
	{
		public TestItem ( String partition, String msg, byte[] out ) { fPartition = partition; fMsg = msg; fOut = out; }
		public final String fPartition;
		public final String fMsg;
		public final byte[] fOut;
	}
}
