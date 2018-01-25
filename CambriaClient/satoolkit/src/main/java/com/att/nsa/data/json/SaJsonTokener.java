package com.att.nsa.data.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom JSON tokener for more explicit parse control.
 *
 */
public class SaJsonTokener extends JSONTokener
{
	public SaJsonTokener ( Reader reader )
	{
		super ( new CommentStrippingReader ( reader ) );
	}

	public SaJsonTokener ( String s )
	{
		super ( new CommentStrippingReader ( new StringReader ( s ) ) );
	}

	public SaJsonTokener ( InputStream is )
	{
		super ( new CommentStrippingReader ( new InputStreamReader ( is ) ) );
	}

	/**
	 * Perform a more strict parse that doesn't allow unquoted strings.
	 * @return this tokener
	 */
	public SaJsonTokener stricter ()
	{
		fNoUnquotedStrings = true;
		return this;
	}

	private boolean fNoUnquotedStrings = false;

	@Override
	public Object nextValue () throws JSONException
	{
		/*
		 * This implementation comes from JSONTokener from version 20131018.
		 * The difference between this and the base class is in the fNoUnquotedStrings
		 * block below. -pc569h 2016-11-12
		 */

        char c = this.nextClean();
        String string;

        switch (c) {
            case '"':
            case '\'':
                return this.nextString(c);
            case '{':
                this.back();
                return new JSONObject(this);
            case '[':
                this.back();
                return new JSONArray(this);
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuffer sb = new StringBuffer();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = this.next();
        }
        this.back();

        string = sb.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }

        final Object result = JSONObject.stringToValue(string);

		// at this point, the allowed values for string are null, true,
		// false, or numbers. If the caller has specified no unquoted strings,
		// and the value is not one of these, it's an error.
        if ( fNoUnquotedStrings && ( result instanceof String ) )
		{
			throw new JSONException ( "Unquoted string found with stricter parsing enabled." );
		}

		return result;
	}

	private enum ReadState
	{
		NORMAL,
		SINGLE_QUOTED_STRING,
		SINGLE_QUOTED_STRING_ESC,
		DOUBLE_QUOTED_STRING,
		DOUBLE_QUOTED_STRING_ESC,
		POSSIBLE_COMMENT,
		LINE_COMMENT,
		BLOCK_COMMENT,
		POSSIBLE_BLOCK_COMMENT_END,
		EOF
	}

	/**
	 * Originally published as part of Rathravane Silt (https://github.com/Rathravane/silt),
	 * which is Apache 2.0 licensed.
	 * 
	 * @author peter
	 *
	 */
	private static class CommentStrippingReader extends Reader
	{
		public CommentStrippingReader ( Reader baseReader )
		{
			fBase = new BufferedReader ( baseReader );
			fState = ReadState.NORMAL;
			fPendingOut = new ArrayList<Character> ();
		}

	    /**
	     * Reads characters into a portion of an array.  This method will block
	     * until some input is available, an I/O error occurs, or the end of the
	     * stream is reached.
	     *
	     * @param      cbuf  Destination buffer
	     * @param      off   Offset at which to start storing characters
	     * @param      len   Maximum number of characters to read
	     *
	     * @return     The number of characters read, or -1 if the end of the
	     *             stream has been reached
	     *
	     * @exception  IOException  If an I/O error occurs
	     */
		@Override
		public int read ( char[] cbuf, int off, int len ) throws IOException
		{
			// if all data has been delivered and the base stream says we're EOF, we're done
			if ( fPendingOut.size () == 0 && fState == ReadState.EOF ) return -1;

			// if the caller asked for 0 bytes, return that.
			if ( len < 1 ) return 0;

			// is there pending output?
			int trx = Math.min ( fPendingOut.size (), len );
			if ( trx > 0 )
			{
				for ( int i=0; i<trx; i++ )
				{
					cbuf [ off++ ] = fPendingOut.remove ( 0 );
				}
				return trx;
			}

			// there was no pending output. process the next line of base input
			process ();
			
			// now output as much as possible
			trx = Math.min ( fPendingOut.size (), len );
			if ( trx > 0 )
			{
				for ( int i=0; i<trx; i++ )
				{
					cbuf [ off++ ] = fPendingOut.remove ( 0 );
				}
				return trx;
			}

			return 0;
		}
		
		/**
		 * Closes the stream and releases any system resources associated with
		 * it.  Once the stream has been closed, further read(), ready(),
		 * mark(), reset(), or skip() invocations will throw an IOException.
		 * Closing a previously closed stream has no effect.
		 *
		 * @exception  IOException  If an I/O error occurs
		 */
		@Override
		public void close () throws IOException
		{
			fBase.close ();
		}

		private void process () throws IOException
		{
			final String rawLine = fBase.readLine ();
			if ( rawLine == null )
			{
				// we're out of input
				fState = ReadState.EOF;
				return;
			}

			// add a line ending
			final String line = rawLine + "\n";
			log.debug ( " IN:" + rawLine );
			
			// process the pending chars into the array
			for ( char currChar : line.toCharArray () )
			{
				switch ( fState )
				{
					case NORMAL:
					{
						switch ( currChar )
						{
							case '"':
							{
								fState = ReadState.DOUBLE_QUOTED_STRING;
								fPendingOut.add ( currChar );
							}
							break;

							case '\'':
							{
								fState = ReadState.SINGLE_QUOTED_STRING;
								fPendingOut.add ( currChar );
							}
							break;

							case '/':
							{
								fState = ReadState.POSSIBLE_COMMENT;
								// don't publish this
							}
							break;

							default:
							{
								fPendingOut.add ( currChar );
							}
						}
					}
					break;

					case SINGLE_QUOTED_STRING:
					{
						if ( currChar == '\\' )
						{
							fState = ReadState.SINGLE_QUOTED_STRING_ESC;
						}
						else if ( currChar == '\'' )
						{
							fState = ReadState.NORMAL;
						}
						fPendingOut.add ( currChar );
					}
					break;

					case SINGLE_QUOTED_STRING_ESC:
					{
						fState = ReadState.SINGLE_QUOTED_STRING;
						fPendingOut.add ( currChar );
					}
					break;

					case DOUBLE_QUOTED_STRING:
					{
						if ( currChar == '\\' )
						{
							fState = ReadState.DOUBLE_QUOTED_STRING_ESC;
						}
						else if ( currChar == '"' )
						{
							fState = ReadState.NORMAL;
						}
						fPendingOut.add ( currChar );
					}
					break;

					case DOUBLE_QUOTED_STRING_ESC:
					{
						fState = ReadState.DOUBLE_QUOTED_STRING;
						fPendingOut.add ( currChar );
					}
					break;

					case POSSIBLE_COMMENT:
					{
						if ( currChar == '*' )
						{
							fState = ReadState.BLOCK_COMMENT;
						}
						else if ( currChar == '/' )
						{
							fState = ReadState.LINE_COMMENT;
						}
						else
						{
							fState = ReadState.NORMAL;
							fPendingOut.add ( '/' );
							fPendingOut.add ( currChar );
						}
					}
					break;

					case LINE_COMMENT:
					{
						if ( currChar == '\n' )
						{
							fState = ReadState.NORMAL;
							fPendingOut.add ( currChar );
						}
						// else: stay in LINE_COMMENT, eat chars
					}
					break;

					case BLOCK_COMMENT:
					{
						if ( currChar == '*' )
						{
							fState = ReadState.POSSIBLE_BLOCK_COMMENT_END;
						}
						else if ( currChar == '\n' )
						{
							// emit it so the caller's line count stays correct
							fPendingOut.add ( currChar );
						}
						// else: stay in BLOCK_COMMENT, eat chars
					}
					break;

					case POSSIBLE_BLOCK_COMMENT_END:
					{
						if ( currChar == '/' )
						{
							fState = ReadState.NORMAL;
							fPendingOut.add ( ' ' );
						}
						else if ( currChar == '\n' )
						{
							// emit it so the caller's line count stays correct
							fPendingOut.add ( currChar );
							fState = ReadState.BLOCK_COMMENT;
						}
						else
						{
							fState = ReadState.BLOCK_COMMENT;
						}
					}
					break;

					case EOF:
					{
						// huh?
						throw new IllegalStateException ( "State EOF in process()" );
					}
				}
			}

			if ( log.isDebugEnabled () )
			{
				final StringBuilder sb = new StringBuilder ();
				for ( Character c : fPendingOut )
				{
					sb.append ( c );
				}
				log.debug ( "OUT:" + sb.toString () );
			}
		}


	    private final BufferedReader fBase;
		private ReadState fState;
		private ArrayList<Character> fPendingOut;
	}

	private static final Logger log = LoggerFactory.getLogger ( SaJsonTokener.class );
}