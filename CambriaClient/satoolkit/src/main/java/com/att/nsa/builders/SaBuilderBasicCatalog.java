/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.builders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

/**
 * A typical catalog implementation
 *
 * @param <T>
 */
public class SaBuilderBasicCatalog<T> implements SaBuilderCatalog<T>
{
	public SaBuilderBasicCatalog ( Class<?> baseClass )
	{
		this ( baseClass, null );
	}

	public SaBuilderBasicCatalog ( Class<?> baseClass, Class<? extends T>[] classes )
	{
		fBase = baseClass;
		fClasses = new HashMap<String,Class<? extends T>> ();

		if ( classes != null )
		{
			for ( Class<? extends T> c : classes )
			{
				add ( c );
			}
		}
	}

	@Override
	public Class<?> getCatalogType ()
	{
		return fBase;
	}

	@Override
	public Set<String> getCatalogTypes ()
	{
		return new TreeSet<String> ( fClasses.keySet () );
	}

	public void add ( Class<? extends T> c )
	{
		fClasses.put ( c.getSimpleName(), c );
	}

	@Override
	public boolean creates ( String name )
	{
		return getClassByName(name) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final T create ( String className, JSONObject config, SaBuilder cc ) throws SaBuilderException
	{
		try
		{
			Class<?> tt = getClassByName ( className );
			if ( tt == null )
			{
				throw new SaBuilderException ( "Class [" + className + "] is not in this catalog." );
			}

			T result = null;

			// if the target class is a configurable class, construct and call "readFrom"
			if ( SaBuilderJsonConfiguredItem.class.isAssignableFrom ( tt ) )
			{
				result = noArgConstruct ( tt );
				if ( result != null )
				{
					final SaBuilderJsonConfiguredItem ci = (SaBuilderJsonConfiguredItem) result;
					ci.readFrom ( cc, config );
					return result;
				}
			}
			
			// try the static construct method
			try
			{
				final Method m = tt.getMethod ( "construct", new Class<?>[] { JSONObject.class, SaBuilder.class } );
				result = (T) m.invoke ( null, new Object[] { config, cc } );
				if ( result != null )
				{
					return result;
				}
			}
			catch ( NoSuchMethodException x )
			{
				result = null;
			}

			// now try a JSONObject constructor
			result = jsonObjectArgConstruct ( tt, config );
			if ( result != null )
			{
				return result;
			}

			// finally a no-arg constructor
			result = noArgConstruct ( tt );
			if ( result != null )
			{
				return result;
			}

			throw new SaBuilderException ( "Couldn't instantiate " + className + ". It's not a valid catalog class. Check its constructors." );
		}
		catch ( IllegalAccessException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + className + "'.", e );
		}
		catch ( SecurityException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + className + "'.", e );
		}
		catch ( IllegalArgumentException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + className + "'.", e );
		}
		catch ( InvocationTargetException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + className + "'.", e );
		}
	}

	private T jsonObjectArgConstruct ( Class<?> tt, JSONObject o ) throws SaBuilderException
	{
		try
		{
			try
			{
				@SuppressWarnings("unchecked")
				final Constructor<? extends T> ccc = (Constructor<? extends T>) tt.getConstructor ( new Class<?>[] { JSONObject.class } );
				return ccc.newInstance ( new Object[] { o } );
			}
			catch ( NoSuchMethodException e )
			{
				// ignore
			}
			return null;
		}
		catch ( InstantiationException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + tt.getName() + "'.", e );
		}
		catch ( IllegalAccessException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + tt.getName() + "'.", e );
		}
		catch ( SecurityException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + tt.getName() + "'.", e );
		}
		catch ( IllegalArgumentException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + tt.getName() + "'.", e );
		}
		catch ( InvocationTargetException e )
		{
			throw new SaBuilderException ( "Couldn't instantiate class '" + tt.getName() + "'.", e );
		}
	}

	@SuppressWarnings("unchecked")
	private T noArgConstruct ( Class<?> tt ) throws SaBuilderException
	{
		try
		{
			return (T) tt.newInstance ();
		}
		catch ( InstantiationException e )
		{
			return null;
		}
		catch ( IllegalAccessException e )
		{
			return null;
		}
	}

	private final Class<?> fBase;
	private final HashMap<String,Class<? extends T>> fClasses;

	private Class<?> getClassByName ( String name )
	{
		final String[] parts = name.split ( "\\." );
		final Class<?> cc = fClasses.get ( parts[parts.length-1] );
		if ( cc != null && ( cc.getSimpleName().equals(name) || name.equals(cc.getName ()) ) )
		{
			return cc;
		}
		return null;
	}
}
