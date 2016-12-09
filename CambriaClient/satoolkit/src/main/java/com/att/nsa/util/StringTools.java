/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * String parsing helpers (for lightweight parsing), originally from the
 * open source Rathravane Silt project (http://mvnrepository.com/artifact/com.rathravane/silt)
 *
 */
public class StringTools
{
	public static class ValueInfo
	{
		public ValueInfo ()
		{
			this ( null, -1 );
		}

		public ValueInfo ( String val, int next )
		{
			fValue = val;
			fNextFieldAt = next;
		}

		public final String fValue;
		public final int fNextFieldAt;
	}

	/**
	 * Is the given character part of the given set?
	 * @param c
	 * @param set
	 * @return -1 if not in the set, or the index into the set of the match
	 */
	public static int isOneOf ( char c, char[] set )
	{
		int result = -1;
		for ( result = 0; result < set.length && c != set[result]; result++ )
		{
		}
		return result >= set.length ? -1 : result;
	}

	/**
	 * Get the index of any character in the given set of chars in the given string
	 * @param s
	 * @param chars
	 * @return -1 if not in the set, or the index 
	 */
	public static int indexOfAnyOf ( String s, char[] chars )
	{
		return indexOfAnyOf ( s, chars, 0 );
	}

	/**
	 * Get the index of any character in the given set of chars in the given string
	 * starting at the given index
	 * @param s
	 * @param chars
	 * @param fromIndex
	 * @return -1 if not in the set, or the index 
	 */
	public static int indexOfAnyOf ( String s, char[] chars, int fromIndex )
	{
		int result = -1;
		for ( int i = fromIndex; i < s.length () && result == -1; i++ )
		{
			final int one = isOneOf ( s.charAt ( i ), chars );
			if ( -1 != one )
			{
				result = i;
			}
		}
		return result;
	}

	/**
	 * Get the leading value from the given string based on quoting characters and delimiters.
	 * @param from
	 * @param quoteChars
	 * @param delimChars
	 * @return
	 */
	public static ValueInfo getLeadingValue ( String from, char[] quoteChars, char[] delimChars )
	{
		ValueInfo vi = new ValueInfo ();
		if ( from.length () > 0 )
		{
			char current = from.charAt ( 0 );
			final int quoteId = isOneOf ( current, quoteChars );
			boolean quoted = ( quoteId != -1 );

			if ( quoted )
			{
				final char quoteChar = quoteChars[quoteId];

				// scan for close quote
				int foundEnd = -1;
				int quoteScanFrom = 1;
				while ( -1 == foundEnd )
				{
					int quote = from.indexOf ( quoteChar, quoteScanFrom );
					if ( quote == -1 )
					{
						// improper format!
						break;
					}
					else
					{
						// check if this is a double quote inside the string or
						// if this quote terminates the field
						if ( quote + 1 < from.length () && from.charAt ( quote + 1 ) == quoteChar )
						{
							quoteScanFrom = quote + 2;
						}
						else
						{
							foundEnd = quote;
						}
					}
				}
				if ( foundEnd > -1 )
				{
					StringBuffer fixedUp = new StringBuffer ();
					String val = from.substring ( 1, foundEnd );
					boolean lastWasQuote = false;
					for ( int i = 0; i < val.length (); i++ )
					{
						char c = val.charAt ( i );
						if ( c == quoteChar )
						{
							if ( !lastWasQuote )
							{
								fixedUp.append ( c );
							}
							// else: drop it
							lastWasQuote = !lastWasQuote;
						}
						else
						{
							fixedUp.append ( c );
							lastWasQuote = false;
						}
					}

					final int nextFieldAt = indexOfAnyOf ( from, delimChars, foundEnd + 1 );
					vi = new ValueInfo ( fixedUp.toString (), nextFieldAt != -1 ? nextFieldAt + 1 : nextFieldAt );
				}
			}
			else
			{
				// scan for delimiter
				int delim = indexOfAnyOf ( from, delimChars );
				if ( delim == -1 )
				{
					vi = new ValueInfo ( from, -1 );
				}
				else
				{
					if ( delim == 0 )
					{
						vi = new ValueInfo ( null, 1 );
					}
					else
					{
						vi = new ValueInfo ( from.substring ( 0, delim ), delim + 1 );
					}
				}
			}
		}
		return vi;
	}

	/**
	 * Test string equality; either argument may be null.
	 * @param a
	 * @param b
	 * @return true if the two strings are equal or both null
	 */
	public static boolean safeEquals ( String a, String b )
	{
		if ( a == null && b == null ) return true;
		if ( a != null && b != null )
		{
			return a.equals ( b );
		}
		return false;	// one is null
	}

	/**
	 * Compare to with strings that may be null
	 * @param a
	 * @param b
	 * @return the comparison result, with null less than any string
	 */
	public static int safeCompareTo ( String a, String b )
	{
		if ( a == null && b == null ) return 0;
		if ( a != null && b != null )
		{
			return a.compareTo ( b );
		}
		return a == null ? -1 : 1;
	}

	/**
	 * Return a string with the listed items separated by ", "
	 * @param items
	 * @return a string
	 */
	public static String listToString ( List<?> items )
	{
		return listToString ( items, ", " );
	}

	/**
	 * Return a string with the listed items separated by the given separator
	 * @param items
	 * @param separator
	 * @return a string
	 */
	public static String listToString ( List<?> items, String separator )
	{
		boolean doneOne = false;
		final StringBuffer sb = new StringBuffer ();
		for ( Object o : items )
		{
			if ( doneOne ) sb.append ( separator );
			doneOne = true;

			sb.append ( o.toString () );
		}
		return sb.toString ();
	}

	/**
	 * Return a string with the listed items separated by ", "
	 * @param items
	 * @return a string
	 */
	public static String setToString ( Set<?> items )
	{
		return setToString ( items, ", " );
	}

	/**
	 * Return a string with the listed items separated by the given separator
	 * @param items
	 * @param separator
	 * @return a string
	 */
	public static String setToString ( Set<?> items, String separator )
	{
		boolean doneOne = false;
		final StringBuffer sb = new StringBuffer ();
		for ( Object o : items )
		{
			if ( doneOne ) sb.append ( separator );
			doneOne = true;

			sb.append ( o.toString () );
		}
		return sb.toString ();
	}

	/**
	 * Build and return a set from an array.
	 * @param a
	 * @return a set of elements from the array
	 */
	public static <T> Set<T> arrayToSet ( T[] a )
	{
		final TreeSet<T> set = new TreeSet<T> ();
		for ( T t : a )
		{
			set.add ( t );
		}
		return set;
	}

	/**
	 * Get the text between two strings. If the strings aren't found, null is returned.
	 * @param sourceString
	 * @param leftSide
	 * @param rightSide
	 * @return the substring of the source string between the two given strings
	 */
	public static String getTextBetween ( String sourceString, String leftSide, String rightSide )
	{
		final TreeSet<String> right = new TreeSet<String> ();
		right.add ( rightSide );
		return getTextBetween ( sourceString, leftSide, right );
	}

	/**
	 * get the text between a left string and the first of a set of right strings.
	 * @param sourceString
	 * @param leftSide
	 * @param rightSides
	 * @return the substring of the source string or null
	 */
	public static String getTextBetween ( String sourceString, String leftSide, Set<String> rightSides )
	{
		String result = null;

		final int left = sourceString.indexOf ( leftSide );
		if ( left > -1 )
		{
			final int afterLeft = left + leftSide.length();
			
			for ( String rightSide : rightSides )
			{
				final int right = sourceString.indexOf ( rightSide, afterLeft );
				if ( right > -1 )
				{
					final String val = sourceString.substring ( afterLeft, right );
					if ( result == null || val.length () < result.length () )
					{
						result = val;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Split the string on the given character, returning an array of two strings. If the character
	 * isn't found, null is returned.
	 * @param sourceString
	 * @param c
	 * @return an array with two members, or null
	 */
	public static String[] splitOnFirst ( String sourceString, char c )
	{
		final int index = sourceString.indexOf ( c );
		if ( index > -1 )
		{
			return new String[] { sourceString.substring(0,index), sourceString.substring(index+1) };
		}
		return null;
	}

	public interface charFilter
	{
		boolean meetsFilter ( char c );
	}
	
	/**
	 * split a string on its whitespace into individual tokens
	 * @param line
	 * @return split array
	 */
	public static String[] splitLine ( final String line )
	{
		return splitLine ( line, new charFilter ()
		{
			@Override
			public boolean meetsFilter ( char c )
			{
				return Character.isWhitespace ( c );
			}
		} );
	}

	/**
	 * Split a string on commas into individual tokens.
	 * @param line
	 * @return split array
	 */
	public static String[] splitLineOnComma ( final String line )
	{
		return splitLine ( line, new charFilter ()
		{
			@Override
			public boolean meetsFilter ( char c )
			{
				return c == ',';
			}
		} );
	}

	public static String[] splitLine ( final String line, charFilter cf )
	{
		final LinkedList<String> tokens = new LinkedList<String> ();
		if ( line != null )
		{
			StringBuffer current = new StringBuffer ();
			boolean quoting = false;
			for ( int i=0; i<line.length (); i++ )
			{
				final char c = line.charAt ( i );
				if ( cf.meetsFilter ( c ) && !quoting )
				{
					if ( current.length () > 0 )
					{
						tokens.add ( current.toString ().trim () );
					}
					current = new StringBuffer ();
				}
				else if ( c == '"' )
				{
					// if we see "abc"def, that's "abc", then "def"
					if ( current.length () == 0 )
					{
						// starting quoted string. eat it, flip quote flag
						quoting = true;
					}
					else if ( !quoting )
					{
						// abc"def
						tokens.add ( current.toString () );
						current = new StringBuffer ();
						quoting = true;
					}
					else
					{
						// end quoted string
						tokens.add ( current.toString () );	// don't trim
						current = new StringBuffer ();
						quoting = false;
					}
				}
				else
				{
					current.append ( c );
				}
			}
			if ( current.length () > 0 )
			{
				tokens.add ( current.toString ().trim () );
			}
		}		
		return tokens.toArray ( new String[ tokens.size () ] );
	}

	/**
	 * Parse the string into an integer, returning defVal if there's a number format exception.
	 * @param s
	 * @param defVal
	 * @return the string as an int, or defval
	 */
	public static final int parseInt ( String s, int defVal )
	{
		int result = defVal;
		try
		{
			result = Integer.parseInt ( s );
		}
		catch ( NumberFormatException x )
		{
		}
		return result;
	}
}
