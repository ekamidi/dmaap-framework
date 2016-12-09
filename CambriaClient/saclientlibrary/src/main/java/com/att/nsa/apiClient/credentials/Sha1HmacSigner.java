/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.apiClient.credentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Sha1HmacSigner
{
	private static final String kHmacSha1Algo = "HmacSHA1";

	public static String sign ( String message, String key )
	{
		try
		{
			final SecretKey secretKey = new SecretKeySpec ( key.getBytes (), kHmacSha1Algo );
			final Mac mac = Mac.getInstance ( kHmacSha1Algo );
			mac.init ( secretKey );
			final byte[] rawHmac = mac.doFinal ( message.getBytes () );
			return base64Encode ( rawHmac );
		}
		catch ( InvalidKeyException e )
		{
			throw new RuntimeException ( e );
		}
		catch ( NoSuchAlgorithmException e )
		{
			throw new RuntimeException ( e );
		}
		catch ( IllegalStateException e )
		{
			throw new RuntimeException ( e );
		}
	}


	public static String base64Encode ( byte[] in )
	{
		try
		{
			final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			final rrcBase64OutputStream os = new rrcBase64OutputStream ( baos );
			os.write ( in );
			os.close ();
			final byte[] textBytes = baos.toByteArray ();
			return new String ( textBytes );
		}
		catch ( IOException e )
		{
			throw new RuntimeException ( "Error writing bytes to byte stream. " + e.getMessage () );
		}
	}

	private static class rrcBase64OutputStream extends OutputStream
	{
		public rrcBase64OutputStream ( OutputStream downstream )
		{
			fDownstream = downstream;
			fPendings = new LinkedList<Byte> ();
			fWrittenToLine = 0;
		}
	
		@Override
		public void write ( int b )
			throws IOException
		{
			fPendings.add ( (byte) b );
			writePendings ( false );
		}
	
		@Override
		public void close () throws IOException
		{
			writePendings ( true );
			fDownstream.close ();
		}
	
		private int writeNow ()
		{
			int result = 0;
			int pending = fPendings.size ();
			if ( pending > kBufferSize )
			{
				result = kBufferSize;
			}
			return result;
		}
		
		private void writePendings ( boolean pad ) throws IOException
		{
			int thisWrite = fPendings.size ();
			if ( !pad )
			{
				thisWrite = writeNow ();
			}
	
			if ( thisWrite > 0 )
			{
				byte[] bb = new byte [ thisWrite ];
				for ( int i=0; i<thisWrite; i++ )
				{
					bb[i] = fPendings.remove ();
				}
		
				char[] cc = encode ( bb );
				for ( char c : cc )
				{
					fDownstream.write ( c );
					if ( ++fWrittenToLine == kMaxLine )
					{
						fDownstream.write ( Base64Constants.kNewline );
						fWrittenToLine = 0;
					}
				}
			}
		}
	
		private char[] encode ( byte[] in )
		{
			int iLen = in.length;
			int oDataLen = ( iLen * 4 + 2 ) / 3; // output length without padding
			int oLen = ( ( iLen + 2 ) / 3 ) * 4; // output length including padding
			char[] out = new char [oLen];
			int ip = 0;
			int op = 0;
			while ( ip < iLen )
			{
				int i0 = in[ip++] & 0xff;
				int i1 = ip < iLen ? in[ip++] & 0xff : 0;
				int i2 = ip < iLen ? in[ip++] & 0xff : 0;
				int o0 = i0 >>> 2;
				int o1 = ( ( i0 & 3 ) << 4 ) | ( i1 >>> 4 );
				int o2 = ( ( i1 & 0xf ) << 2 ) | ( i2 >>> 6 );
				int o3 = i2 & 0x3F;
				out[op++] = Base64Constants.nibblesToB64[o0];
				out[op++] = Base64Constants.nibblesToB64[o1];
				out[op] = op < oDataLen ? Base64Constants.nibblesToB64[o2] : '=';
				op++;
				out[op] = op < oDataLen ? Base64Constants.nibblesToB64[o3] : '=';
				op++;
			}
			return out;
		}
	
		private static final int kMaxLine = 80;
		private static final int kBufferSize = 3*64;	// multiple of 3 for no padding
		
		private OutputStream fDownstream;
		private int fWrittenToLine;
		private final LinkedList<Byte> fPendings;
	}

}
