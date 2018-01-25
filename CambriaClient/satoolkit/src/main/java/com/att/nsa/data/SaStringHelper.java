/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SaStringHelper
{
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
	 * compare two lists
	 * @param a
	 * @param b
	 * @return the comparison result
	 */
	public static int compareLists ( List<String> a, List<String> b )
	{
		if ( a == b ) return 0;
		if ( a != null && b == null ) return -1;
		if ( a == null && b != null ) return 1;
		if ( a.size () != b.size () ) return -1;

		for ( int i=0; i<a.size(); i++ )
		{
			final String aval = a.get ( i );
			final String bval = b.get ( i );
			
			if ( aval == null && bval == null ) continue;
			if ( aval == null && bval != null ) return 1;
			if ( aval != null && bval == null ) return -1;

			final int c = aval.compareTo ( bval );
			if ( c != 0 ) return c;
		}

		return 0;
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
	
	/**
	 * Split a string on dots into individual tokens.
	 * @param line
	 * @return split array
	 */
	public static String[] splitStringOnDot ( final String line )
	{
		return splitLine ( line, new charFilter ()
		{
			@Override
			public boolean meetsFilter ( char c )
			{
				return c == '.';
			}
		} );
	}

	public static String joinStringWithDot ( String[] segments )
	{
		return joinStringWithDot ( segments, 0 );
	}

	public static String joinStringWithDot ( String[] segments, int offset )
	{
		return joinStringWithDot ( segments, offset, segments.length - offset );
	}

	public static String joinStringWithDot ( String[] segments, int offset, int length )
	{
		int count = 0;
		final StringBuilder sb = new StringBuilder ();
		for ( int i=offset; count<length; i++ )
		{
			if ( i > offset ) sb.append ( '.' );
			sb.append ( segments [ i ] );
			count++;
		}
		return sb.toString ();
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
