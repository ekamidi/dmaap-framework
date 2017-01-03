/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.nsa.configs.confimpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;

public class CassandraConfigDb implements ConfigDb {
	
	private final Cluster cluster;
	private final Session session;
	private final ConcurrentHashMap<StatementName, PreparedStatement> preparedStatements;
	private final Object prepareStatementCreateLock;
	private final List<InetAddress> contactPoints;
	private final int port;
	
	private enum StatementName {
		GET_SETTING,
		GET_CHILDREN,
		PUT_SETTING,
		DELETE_SETTING
	}
	
	@SuppressWarnings("unused") //Hide the implicit constructor
	private CassandraConfigDb() {
		this.cluster = null;
		this.session = null;
		this.preparedStatements = null;
		this.prepareStatementCreateLock = null;
		this.port = -1;
		this.contactPoints = null;
	}
	
	public CassandraConfigDb(List<String> contactPoints, int port) {
		
		this.contactPoints = new ArrayList<InetAddress> (contactPoints.size());
		
		for (String contactPoint : contactPoints) {
			try {
				this.contactPoints.add(InetAddress.getByName(contactPoint));
			} catch (UnknownHostException e) {
                throw new IllegalArgumentException(e.getMessage());
			}
		}
		
		this.port = port;
		
		cluster = (new Cluster.Builder()).withPort (this.port)
				.addContactPoints(this.contactPoints)
				.withSocketOptions(new SocketOptions().setReadTimeoutMillis(60000).setKeepAlive(true).setReuseAddress(true))
				.withLoadBalancingPolicy(new RoundRobinPolicy())
				.withReconnectionPolicy(new ConstantReconnectionPolicy(500L))
				.withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE))
				.build ();
		
		session = cluster.newSession();
		preparedStatements = new ConcurrentHashMap<StatementName, PreparedStatement> ();
		prepareStatementCreateLock = new Object();
	}
	
	private void createKeyspaceIfNotExists() {
		session.execute("CREATE KEYSPACE IF NOT EXISTS fe3c WITH replication = {'class':'SimpleStrategy', 'replication_factor': '2'}");
	}
	
	private void createTableIfNotExists() {
		session.execute("CREATE TABLE IF NOT EXISTS fe3c.configuration (parent text, child text, value text, PRIMARY KEY(parent, child));");
	}
	
	private void prepareStatements() {
		
		createKeyspaceIfNotExists();
		createTableIfNotExists();
		
		preparedStatements.put(StatementName.GET_SETTING, session.prepare("SELECT value FROM fe3c.configuration WHERE parent = ?"));
		preparedStatements.put(StatementName.GET_CHILDREN, session.prepare("SELECT child, value FROM fe3c.configuration WHERE parent = ?"));
		preparedStatements.put(StatementName.PUT_SETTING, session.prepare("INSERT INTO fe3c.configuration (parent, child, value) VALUES (?, ?, ?)"));
		preparedStatements.put(StatementName.DELETE_SETTING, session.prepare("DELETE FROM fe3c.configuration WHERE parent = ? AND child = ?"));
	}
	
	private PreparedStatement getStatement ( StatementName name )
	{
		synchronized ( prepareStatementCreateLock )
		{
			if ( preparedStatements.isEmpty () )
			{
				prepareStatements ();
			}
		}
		return preparedStatements.get ( name );
	}
	
	@Override
	public ConfigPath getRoot() {
		return SimplePath.getRootPath ();
	}

	@Override
	public ConfigPath parse(String pathAsString) {
		return SimplePath.parse ( pathAsString );
	}

	@Override
	public boolean exists(ConfigPath path) throws ConfigDbException {
		final BoundStatement bStat = new BoundStatement(getStatement(StatementName.GET_SETTING));
		
		bStat.bind(path.toString());
		
		return session.execute(bStat).one() != null;
	}

	@Override
	public String load(ConfigPath key) throws ConfigDbException {

		final BoundStatement bStat = new BoundStatement(getStatement(StatementName.GET_SETTING));
		
		bStat.bind(key.toString());
		
		final Row result = session.execute(bStat).one();
		
		if (result == null) {
			return null;
		}
		
		return result.getString("value");
	}

	@Override
	public Set<ConfigPath> loadChildrenNames(ConfigPath key)
			throws ConfigDbException {

		final TreeSet<ConfigPath> set = new TreeSet<ConfigPath> ();
		final BoundStatement bStat = new BoundStatement(getStatement(StatementName.GET_CHILDREN));
		final ResultSet results = session.execute(bStat);
		
		for (Row result : results.all()) {
			set.add(SimplePath.parse(result.getString("child")));
		}
		
		return set;
	}

	@Override
	public Map<ConfigPath, String> loadChildrenOf(ConfigPath key)
			throws ConfigDbException {

		// no magic here...
		final HashMap<ConfigPath,String> map = new HashMap<ConfigPath,String> ();
		final BoundStatement bStat = new BoundStatement(getStatement(StatementName.GET_CHILDREN));
		final ResultSet results = session.execute(bStat);
		
		for (Row result : results.all()) {
			map.put(SimplePath.parse(result.getString("child")), result.getString("value"));
		}
		
		return map;
	}

	@Override
	public void store(ConfigPath key, String data) throws ConfigDbException {

		final BatchStatement batchStat = new BatchStatement();
		
		batchStat.add(getStatement(StatementName.PUT_SETTING).bind(key.toString(), "", data));
		ConfigPath parent;
		ConfigPath child = key;
		
		while ((parent = child.getParent()) != null) {
			batchStat.add(getStatement(StatementName.PUT_SETTING).bind(parent.toString(), child.toString(), data));
			child = parent;
		}
		
		session.execute(batchStat);
	}

	@Override
	public boolean clear(ConfigPath key) throws ConfigDbException {
		
		final BatchStatement batchStat = new BatchStatement();
		
		batchStat.add(getStatement(StatementName.DELETE_SETTING).bind(key.toString()));
		
		ConfigPath parent;
		ConfigPath child = key;
		
		while ((parent = child.getParent()) != null) {
			batchStat.add(getStatement(StatementName.DELETE_SETTING).bind(parent.toString(), child.toString()));
			child = parent;
		}
		
		session.execute(batchStat);
		
		return true;
	}

	@Override
	public long getLastModificationTime ( ConfigPath path ) throws ConfigDbException
	{
		// FIXME
		throw new RuntimeException ( "getLastModificationTime is not implemented for Cassandra" );
	}
}
