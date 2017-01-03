/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.data.json;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.nsa.data.SaStringHelper;

public class SaJsonUtil
{
	public interface arrayVisitor<T, E extends Exception>
	{
		void visit ( T t ) throws JSONException, E;
	};

	/**
	 * Visit each element of a JSON array. Safe to call with a null array.
	 * @param a
	 * @param v
	 * @throws JSONException
	 * @throws E
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Exception> void forEachElement ( JSONArray a, arrayVisitor<T,E> v ) throws JSONException, E
	{
		if ( a != null )
		{
			int len = a.length ();
			for ( int i=0; i<len; i++ )
			{
				if ( !a.isNull ( i ) )
				{
					final Object o = a.get ( i );
					v.visit ( (T) o );
				}
			}
		}
	}

	public interface EntryVisitor<V>
	{
		void visit ( String key, V value ) throws JSONException;
	};

	@SuppressWarnings("unchecked")
	public static <V> void forEachElement ( JSONObject o, EntryVisitor<V> v ) throws JSONException
	{
		if ( o == null ) return;

		final Iterator<?> it = o.keys ();
		while ( it.hasNext () )
		{
			final String key = it.next ().toString ();
			final V val = (V) o.get ( key );
			v.visit ( key, val );
		}
	}

	public interface matcher<T>
	{
		boolean matches ( T o );
	}

	@SuppressWarnings("unchecked")
	public static <T> T findInArray ( JSONArray a, matcher<T> m )
	{
		if ( a != null )
		{
			int len = a.length ();
			for ( int i=0; i<len; i++ )
			{
				if ( !a.isNull ( i ) )
				{
					final Object o = a.get ( i );
					if ( m.matches ( (T) o ) )
					{
						return (T)o;
					}
				}
			}
		}
		return null;
	}

	public static JSONArray listToJsonArray ( List<?> fields )
	{
		return collectionToJsonArray ( fields );
	}

	public static JSONArray setToJsonArray ( Set<?> fields )
	{
		return collectionToJsonArray ( fields );
	}

	public static JSONArray collectionToJsonArray ( Collection<?> fields )
	{
		final JSONArray a = new JSONArray ();
		if ( fields != null )
		{
			for ( Object o : fields )
			{
				a.put ( o );
			}
		}
		return a;
	}

	public static List<String> jsonArrayToList ( JSONArray a ) throws JSONException
	{
		final LinkedList<String> list = new LinkedList<String> ();
		if ( a != null )
		{
			for ( int i=0; i<a.length (); i++ )
			{
				list.add ( a.getString ( i ) );
			}
		}
		return list;
	}
	
	public static JSONObject mapToJsonObject ( Map<String,?> map ) throws JSONException
	{
		final JSONObject o = new JSONObject ();
		for ( Entry<String, ?> e : map.entrySet() )
		{
			o.put ( e.getKey(), e.getValue () );
		}
		return o;
	}

	/**
	 * Read a JSON object into a string/string map. The object can be null, in which case an empty
	 * map is returned.
	 * 
	 * @param obj
	 * @return a map
	 * @throws JSONException
	 */
	public static Map<String,String> jsonObjectToMap ( JSONObject obj ) throws JSONException
	{
		return jsonObjectToMap ( obj, new mapEntryFilter<String>()
			{
			@Override public String checkKey ( String key ) { return key; }
			@Override public String checkValue ( String value ) { return value; }
			});
	}

	public interface mapEntryFilter<T>
	{
		String checkKey ( String key );
		T checkValue ( String value );
	}
	public static <T> Map<String,T> jsonObjectToMap ( JSONObject obj, mapEntryFilter<T> mef ) throws JSONException
	{
		final HashMap<String,T> map = new HashMap<String,T> ();
		if ( obj != null )
		{
			final Iterator<?> it = obj.keys ();
			while ( it.hasNext () )
			{
				final Object nameObj = it.next ();
				final String name = mef.checkKey ( nameObj.toString () );
				final T val = mef.checkValue ( obj.get ( name ).toString () );
				map.put ( name, val );
			}
		}
		return map;
	}

	public static String jsonToXml ( JSONObject o )
	{
		return "<foo>FIXME</foo>";
	}

	/**
	 * Creates a string from a JSON object that's guaranteed to be the same for objects
	 * that are identical in fields and values. (This is not true of JSONObject.toString(),
	 * which can re-order fields.)
	 * @return a string
	 */
	public static String jsonObjectToString ( JSONObject o )
	{
		// the underlying JSON object isn't guaranteed to iterate keys in any particular
		// order and therefore the resulting string can change. Here, we roll our own
		// to guarantee that two equal JSON objects have the same signature string, which
		// gives us the same hashcode and equals result consistently.

		final LinkedList<String> keys = new LinkedList<String> ();
		for ( Object key : o.keySet () )
		{
			keys.add ( (String) key );
		}
		Collections.sort ( keys );
			// giving us a guaranteed order for the members...

		final StringBuffer sb = new StringBuffer ();
		boolean doneOne = false;
		sb.append ( "{" );
		for ( String key : keys )
		{
			if ( doneOne ) sb.append ( "," );
			sb.append ( JSONObject.quote ( key ) ).append ( ":" ).append ( jsonThingToString ( o.get(key) ) );
			doneOne = true;
		}
		sb.append ( "}" );
		return sb.toString();
	}

	private static String jsonThingToString ( Object o )
	{
		if ( o == null )
		{
			return "null";
		}

		if ( o instanceof JSONObject )
		{
			return jsonObjectToString ( (JSONObject) o );
		}

		if ( o instanceof JSONArray )
		{
			final JSONArray a = (JSONArray) o;
			final StringBuffer sb = new StringBuffer ();
			sb.append ( "[" );
			boolean doneOne = false;
			for ( int i=0; i<a.length(); i++ )
			{
				final Object val = a.get ( i );
				if ( doneOne ) sb.append ( "," );
				sb.append ( jsonThingToString ( val ) );
				doneOne = true;
			}
			sb.append ( "]" );
			return sb.toString ();
		}

		if ( o instanceof Number )
		{
			return JSONObject.numberToString ( (Number) o );
		}

		if ( o instanceof Boolean )
		{
			return o.toString ();
		}

		return JSONObject.quote ( o.toString () );
	}

	public static JSONArray removeEntries ( JSONArray listToJsonArray, final Set<String> values )
	{
		final JSONArray result = new JSONArray ();
		forEachElement ( listToJsonArray, new arrayVisitor<String,JSONException> ()
		{
			@Override
			public void visit ( String s ) throws JSONException
			{
				if ( !values.contains ( s ) )
				{
					result.put ( s );
				}
			}
		} );
		return result;
	}

	public static JSONObject clone ( JSONObject that )
	{
		if ( that == null ) return null;

		final JSONObject result = new JSONObject ();
		forEachElement ( that, new EntryVisitor<Object> ()
		{
			@Override
			public void visit ( String key, Object value ) throws JSONException
			{
				result.put ( key, value );
			}
		} );
		return result;
	}

	public static boolean isDotFieldName ( JSONObject container, String field )
	{
		// If the field name has a ".", we use embedded objects, unless
		// we have an existing field that matches the full name.
		final int dot = field.indexOf ( '.' );
		return ( !container.has ( field ) && dot >= 0 );
	}

	public interface ValueReference
	{
		void writeValue ( Object o );
		String getValue ( String defval );
		int getValue ( int defval );
		long getValue ( long defval );
		boolean getValue ( boolean defval );
		JSONObject getObject ();
		JSONArray getArray ();
		void remove ();
	}

	private static class ArrayValueReference implements ValueReference
	{
		private final int fIndex;
		private final JSONArray fArray;

		public ArrayValueReference ( JSONArray a, int index )
		{
			fArray = a;
			fIndex = index;
		}

		@Override public void writeValue ( Object o ) { fArray.put ( fIndex, o ); }
		@Override public void remove () { fArray.remove ( fIndex ); }

		@Override public String getValue ( String defval ) { return fArray.optString ( fIndex, defval ); }
		@Override public int getValue ( int defval ) { return fArray.optInt ( fIndex, defval ); }
		@Override public long getValue ( long defval ) { return fArray.optLong ( fIndex, defval ); }
		@Override public boolean getValue ( boolean defval ) { return fArray.optBoolean ( fIndex, defval ); }
		@Override public JSONObject getObject () { return fArray.optJSONObject ( fIndex ); }
		@Override public JSONArray getArray () { return fArray.optJSONArray ( fIndex ); }
	}
	
	private static class ObjectValueReference implements ValueReference
	{
		private final JSONObject fObj;
		private final String fField;

		public ObjectValueReference ( JSONObject o, String field )
		{
			fObj = o;
			fField = field;
		}

		@Override public void writeValue ( Object o ) { fObj.put ( fField, o ); }
		@Override public void remove () { fObj.remove ( fField ); }

		@Override public String getValue ( String defval ) { return fObj.optString ( fField, defval ); }
		@Override public int getValue ( int defval ) { return fObj.optInt ( fField, defval ); }
		@Override public long getValue ( long defval ) { return fObj.optLong ( fField, defval ); }
		@Override public boolean getValue ( boolean defval ) { return fObj.optBoolean ( fField, defval ); }
		@Override public JSONObject getObject () { return fObj.optJSONObject ( fField ); }
		@Override public JSONArray getArray () { return fObj.optJSONArray ( fField ); }
	}

	public static ValueReference getContainerNamed ( final JSONObject top, final String name, boolean createIntermediates )
	{
		if ( isDotFieldName ( top, name ) )
		{
			// the name has segments

			final String[] parts = SaStringHelper.splitOnFirst ( name, '.' );
			if ( parts == null ) return null;	// odd, since the name is supposed to be segmented

			final term t = new term ( parts[0] );
			if ( t.index > -1 )
			{
				// this is an array of objects

				JSONArray a = null;
				final Object item = top.opt ( t.label );
				if ( item == null )
				{
					if ( createIntermediates )
					{
						a = new JSONArray ();
						top.put ( t.label, a );
					}
				}
				else if ( item instanceof JSONArray )
				{
					a = (JSONArray) item;
				}

				// if we don't have an array, there's a name collision
				if ( a == null ) return null;

				// now find the object at the index
				Object oo = a.opt ( t.index );
				if ( oo == null && createIntermediates )
				{
					oo = new JSONObject ();
					a.put ( t.index, oo );
					return getContainerNamed ( (JSONObject)oo, parts[1], createIntermediates );
				}
				else if ( oo instanceof JSONObject )
				{
					// next segment goes into oo
					return getContainerNamed ( (JSONObject)oo, parts[1], createIntermediates );
				}
				else
				{
					// name conflict -- the thing exists but is not an array, or does not exist
					// and not asked to create it.
					return null;
				}
			}
			else
			{
				// this is an object
				Object oo = top.opt ( parts[0] );
				if ( oo == null && createIntermediates )
				{
					oo = new JSONObject ();
					top.put ( parts[0], oo );
					return getContainerNamed ( (JSONObject)oo, parts[1], createIntermediates );
				}
				else if ( oo instanceof JSONObject )
				{
					// next segment goes into oo
					return getContainerNamed ( (JSONObject)oo, parts[1], createIntermediates );
				}
				else
				{
					// name conflict -- the thing exists but is not an array, or does not exist
					// and not asked to create it.
					return null;
				}
			}
		}
		else
		{
			// there are no segments. it could still be an array...

			final term t = new term ( name );
			if ( t.index > -1 )
			{
				// this is an array.

				final Object item = top.opt ( t.label );
				if ( item instanceof JSONArray )
				{
					// good to go
					final JSONArray a = (JSONArray) item;
					return new ArrayValueReference ( a, t.index );
				}
				else if ( item == null && createIntermediates )
				{
					// create it
					final JSONArray a = new JSONArray ();
					top.put ( t.label, a );
					return new ArrayValueReference ( a, t.index );
				}
				else
				{
					// name conflict -- the thing exists but is not an array, or does not exist
					// and not asked to create it.
					return null;
				}
			}
			else
			{
				// this is a field
				return new ObjectValueReference ( top, name );
			}
		}
	}
	
	private static class term
	{
		public term ( String token )
		{
			String labelPart = token;
			int indexPart = -1;

			if ( token.endsWith ( "]" ) )
			{
				final String[] parts = SaStringHelper.splitOnFirst ( token, '[' );
				if ( parts != null )
				{
					try
					{
						final String indexText = parts[1].substring ( 0, parts[1].length ()-1 );
						indexPart = Integer.parseInt ( indexText );
						labelPart = parts[0];
					}
					catch ( NumberFormatException e )
					{
						log.warn ( "Couldn't parse array subscript from " + token );
					}
				}
			}

			label = labelPart;
			index = indexPart;
		}
		public final String label;
		public final int index;
	}

	private static final Logger log = LoggerFactory.getLogger ( SaJsonUtil.class );
}

