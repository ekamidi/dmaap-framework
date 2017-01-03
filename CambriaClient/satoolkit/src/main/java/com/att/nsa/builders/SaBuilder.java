/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.builders;

import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

import com.att.nsa.data.SaMultiMap;

/**
 * The builder contains catalogs for various types and creates instances based on JSON data.
 *
 * @param <E>
 */
public class SaBuilder
{
	public SaBuilder ()
	{
		fCatalogs = new SaMultiMap<Class<?>,SaBuilderCatalog<?>> ();
	}

	public SaBuilder ( SaBuilderCatalog<?>... catalogs )
	{
		fCatalogs = new SaMultiMap<Class<?>,SaBuilderCatalog<?>> ();
		for ( SaBuilderCatalog<?> c : catalogs )
		{
			addCatalog ( c );
		}
	}

	/**
	 * Add a catalog to this builder.
	 * @param c
	 */
	public void addCatalog ( SaBuilderCatalog<?> c )
	{
		fCatalogs.put ( c.getCatalogType (), c );
	}

	/**
	 * Get this builder's catalogs.
	 * @return a map from type to catalogs that build the type.
	 */
	public SaMultiMap<Class<?>, SaBuilderCatalog<?>> getCatalogs ()
	{
		SaMultiMap<Class<?>, SaBuilderCatalog<?>> map = new SaMultiMap<Class<?>, SaBuilderCatalog<?>> ( fCatalogs );
		return map;
	}

	/**
	 * Get the catalogs that build a given base type.
	 * @param baseType
	 * @return a set of catalog names.
	 */
	public <T> Set<String> getCatalogOf ( Class<T> baseType )
	{
		final TreeSet<String> result = new TreeSet<String> ();
		for ( SaBuilderCatalog<?> cat : fCatalogs.get ( baseType ) )
		{
			result.addAll ( cat.getCatalogTypes () );
		}
		return result;
	}

	/**
	 * Get the catalog that builds the baseType class of a given name.
	 * @param baseType
	 * @param name
	 * @return a catalog that builds the named class or null
	 */
	public <T> SaBuilderCatalog<?> getCatalogFor ( Class<T> baseType, String name )
	{
		for ( SaBuilderCatalog<?> pkg : fCatalogs.get ( baseType ) )
		{
			if ( pkg.creates ( name ) )
			{
				return pkg;
			}
		}
		return null;
	}

	/**
	 * Create an instance of the base type interface given a JSON object.
	 * @param baseType
	 * @param data
	 * @return the new instances
	 * @throws SaBuilderException
	 */
	@SuppressWarnings("unchecked")
	public <T> T create ( Class<T> baseType, JSONObject data ) throws SaBuilderException
	{
		if ( data != null )
		{
			final String extType = data.optString ( "class" );
			if ( extType == null )
			{
				throw new SaBuilderException ( "A configured object must have a value for 'class': " + data.toString(4) );
			}

			try
			{
				final SaBuilderCatalog<?> pkg = getCatalogFor ( baseType, extType );
				if ( pkg != null )
				{
					return (T) pkg.create ( extType, data, this );
				}

				throw new SaBuilderException ( "Couldn't instantiate class '" + extType + "'. No catalog defines it." );
			}
			catch ( SecurityException e )
			{
				throw new SaBuilderException ( "Couldn't instantiate class '" + extType + "'.", e );
			}
			catch ( IllegalArgumentException e )
			{
				throw new SaBuilderException ( "Couldn't instantiate class '" + extType + "'.", e );
			}
		}
		return null;
	}

	private final SaMultiMap<Class<?>, SaBuilderCatalog<?>> fCatalogs;
}
