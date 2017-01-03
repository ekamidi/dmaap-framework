/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.cambria.client.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.nsa.apiClient.credentials.ApiCredential;
import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;
import com.att.nsa.cambria.client.CambriaIdentityManager;
import com.att.nsa.cambria.client.CambriaTopicManager;

//Deprecated - API KEY Security model is deprecated and will be migrated to AAF security model

public class CambriaMetaClient extends CambriaBaseClient implements CambriaTopicManager, CambriaIdentityManager
{
	public CambriaMetaClient ( ConnectionType ct, Collection<String> baseUrls ) throws MalformedURLException, GeneralSecurityException
	{
		super ( ct, baseUrls, HttpClient.kDefault_SocketTimeoutMs );
	}

	@Override
	public Set<String> getTopics () throws IOException
	{
		final TreeSet<String> set = new TreeSet<String> ();
		try
		{
			final JSONObject topicSet = get ( "/topics" );
			final JSONArray a = topicSet.getJSONArray ( "topics" );
			for ( int i=0; i<a.length (); i++ )
			{
				set.add ( a.getString ( i ) );
			}
		}
		catch ( HttpObjectNotFoundException e )
		{
			getLog().warn ( "No /topics endpoint on service." );
		}
		catch ( JSONException e )
		{
			getLog().warn ( "Bad /topics result from service." );
		}
		catch ( HttpException e )
		{
			throw new IOException ( e );
		}
		return set;
	}

	@Override
	public TopicInfo getTopicMetadata ( String topic ) throws HttpObjectNotFoundException, IOException
	{
		try
		{
			final JSONObject topicData = get ( "/topics/" + CambriaConstants.escape ( topic ) );
			return new TopicInfo ()
			{
				@Override
				public String getOwner ()
				{
					return topicData.optString ( "owner", null );
				}

				@Override
				public String getDescription ()
				{
					return topicData.optString ( "description", null );
				}

				@Override
				public Set<String> getAllowedProducers ()
				{
					final JSONObject acl = topicData.optJSONObject ( "writerAcl" );
					if ( acl != null && acl.optBoolean ( "enabled", true ) )
					{
						return jsonArrayToSet ( acl.optJSONArray ( "users" ) );
					}
					return null;
				}

				@Override
				public Set<String> getAllowedConsumers ()
				{
					final JSONObject acl = topicData.optJSONObject ( "readerAcl" );
					if ( acl != null && acl.optBoolean ( "enabled", true ) )
					{
						return jsonArrayToSet ( acl.optJSONArray ( "users" ) );
					}
					return null;
				}
			};
		}
		catch ( JSONException e )
		{
			throw new IOException ( e );
		}
		catch ( HttpException e )
		{
			throw new IOException ( e );
		}
	}

	@Override
	public void createTopic ( String topicName, String topicDescription, int partitionCount, int replicationCount ) throws HttpException, IOException
	{
		final JSONObject o = new JSONObject ();
		o.put ( "topicName", topicName );
		o.put ( "topicDescription", topicDescription );
		o.put ( "partitionCount", partitionCount );
		o.put ( "replicationCount", replicationCount );
		post ( "/topics/create", o, false );
	}

	@Override
	public void deleteTopic ( String topic ) throws HttpException, IOException
	{
		delete ( "/topics/" + CambriaConstants.escape ( topic ) );
	}

	@Override
	public boolean isOpenForProducing ( String topic ) throws HttpObjectNotFoundException, IOException
	{
		return null == getAllowedProducers ( topic );
	}

	@Override
	public Set<String> getAllowedProducers ( String topic ) throws HttpObjectNotFoundException, IOException
	{
		return getTopicMetadata ( topic ).getAllowedProducers ();
	}

	@Override
	public void allowProducer ( String topic, String apiKey ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		put ( "/topics/" + CambriaConstants.escape ( topic ) + "/producers/" + CambriaConstants.escape ( apiKey ), new JSONObject() );
	}

	@Override
	public void revokeProducer ( String topic, String apiKey ) throws HttpException, IOException
	{
		delete ( "/topics/" + CambriaConstants.escape ( topic ) + "/producers/" + CambriaConstants.escape ( apiKey ) );
	}

	@Override
	public boolean isOpenForConsuming ( String topic ) throws HttpObjectNotFoundException, IOException
	{
		return null == getAllowedConsumers ( topic );
	}

	@Override
	public Set<String> getAllowedConsumers ( String topic ) throws HttpObjectNotFoundException, IOException
	{
		return getTopicMetadata ( topic ).getAllowedConsumers ();
	}

	@Override
	public void allowConsumer ( String topic, String apiKey ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		put ( "/topics/" + CambriaConstants.escape ( topic ) + "/consumers/" + CambriaConstants.escape ( apiKey ), new JSONObject() );
	}

	@Override
	public void revokeConsumer ( String topic, String apiKey ) throws HttpException, IOException
	{
		delete ( "/topics/" + CambriaConstants.escape ( topic ) + "/consumers/" + CambriaConstants.escape ( apiKey ) );
	}

	@Override
	public ApiCredential createApiKey ( String email, String description ) throws HttpException, CambriaApiException, IOException
	{
		try
		{
			final JSONObject o = new JSONObject ();
			o.put ( "email", email );
			o.put ( "description", description );
			final JSONObject reply = post ( "/apiKeys/create", o, true );
			return new ApiCredential ( reply.getString ( "key" ), reply.getString ( "secret" ) );
		}
		catch ( JSONException e )
		{
			// the response doesn't meet our expectation
			throw new CambriaApiException ( "The API key response is incomplete.", e );
		}
	}

	@Override
	public ApiKey getApiKey ( String apiKey ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		final JSONObject keyEntry = get ( "/apiKeys/" + CambriaConstants.escape ( apiKey ) );
		if ( keyEntry == null )
		{
			return null;
		}

		return new ApiKey ()
		{
			@Override
			public String getEmail ()
			{
				final JSONObject aux = keyEntry.optJSONObject ( "aux" );
				if ( aux != null )
				{
					return aux.optString ( "email" );
				}
				return null;
			}

			@Override
			public String getDescription ()
			{
				final JSONObject aux = keyEntry.optJSONObject ( "aux" );
				if ( aux != null )
				{
					return aux.optString ( "description" );
				}
				return null;
			}
		};
	}

	@Override
	public void updateCurrentApiKey ( String email, String description ) throws HttpObjectNotFoundException, HttpException, IOException
	{
		final JSONObject o = new JSONObject ();
		if ( email != null ) o.put ( "email", email );
		if ( description != null ) o.put ( "description", description );
		patch ( "/apiKeys/" + CambriaConstants.escape ( getCurrentApiKey() ), o );
	}

	@Override
	public void deleteCurrentApiKey () throws HttpException, IOException
	{
		delete ( "/apiKeys/" + CambriaConstants.escape ( getCurrentApiKey() ) );
	}
}
