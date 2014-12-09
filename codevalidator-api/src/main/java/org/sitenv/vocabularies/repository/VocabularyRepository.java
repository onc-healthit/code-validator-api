package org.sitenv.vocabularies.repository;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;

public class VocabularyRepository {
	
	private static Logger logger = Logger.getLogger(VocabularyRepository.class);
	

	
	// Singleton self reference
	private static final VocabularyRepository ACTIVE_INSTANCE = new VocabularyRepository();
	
	private Map<String, VocabularyModelDefinition> vocabularyMap;
	
	private OServer primaryOrientDbServer;
	private VocabularyRepositoryConnectionInfo primaryNodeCredentials;
	private OObjectDatabasePool primaryConnectionPool;
	private OObjectDatabasePool secondaryConnectionPool;
	private VocabularyRepositoryConnectionInfo secondaryNodeCredentials;
	private boolean isPrimaryActive = true;  // default to true server starts using the secondary, loads the primary and swaps to primary
	
	private VocabularyRepository () {}
	
	
	public static VocabularyRepository getInstance() {
		return ACTIVE_INSTANCE;
	}


	public OServer getOrientDbServer() {
		return primaryOrientDbServer;
	}


	public void setOrientDbServer(OServer orientDbServer) {
		this.primaryOrientDbServer = orientDbServer;
	}


	public synchronized VocabularyRepositoryConnectionInfo getPrimaryNodeCredentials() {
		return primaryNodeCredentials;
	}


	public synchronized void setPrimaryNodeCredentials(VocabularyRepositoryConnectionInfo primaryNodeCredentials) {
		this.primaryNodeCredentials = primaryNodeCredentials;
		
		this.primaryConnectionPool = new OObjectDatabasePool(primaryNodeCredentials.getConnectionInfo(), primaryNodeCredentials.getUsername(), primaryNodeCredentials.getPassword());
	}


	public synchronized VocabularyRepositoryConnectionInfo getSecondaryNodeCredentials() {
		return secondaryNodeCredentials;
	}


	public synchronized void setSecondaryNodeCredentials(
			VocabularyRepositoryConnectionInfo secondaryNodeCredentials) {
		this.secondaryNodeCredentials = secondaryNodeCredentials;
	
		this.secondaryConnectionPool = new OObjectDatabasePool(secondaryNodeCredentials.getConnectionInfo(), secondaryNodeCredentials.getUsername(), secondaryNodeCredentials.getPassword());
	}
	
	public OObjectDatabaseTx getActiveDbConnection() {
		
		if (isPrimaryActive)
		{
			logger.info("PRIMARY IS ACTIVE");
		}
		else
		{
			logger.info("SECONDARY IS ACTIVE");
		}
		
		OObjectDatabaseTx connection;
		
		if (isPrimaryActive) {
			connection = primaryConnectionPool.acquire();
		} else {
			connection = secondaryConnectionPool.acquire();
		}
		
		registerModels(connection);
		
		return connection;
	}
	
	public OObjectDatabaseTx getInactiveDbConnection() {
		
		if (isPrimaryActive)
		{
			logger.info("PRIMARY IS ACTIVE");
		}
		else
		{
			logger.info("SECONDARY IS ACTIVE");
		}
		
		OObjectDatabaseTx connection;
		
		if (!isPrimaryActive) {
			connection = primaryConnectionPool.acquire();
		} else {
			connection = secondaryConnectionPool.acquire();
		}
		
		registerModels(connection);
		
		return connection;	
	}
	
	public static void truncateModel(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz) 
	{
		dbConnection.command(new OCommandSQL("TRUNCATE CLASS " + clazz.getSimpleName())).execute();
		dbConnection.commit();
	}
	
	public static void updateIndexProperties(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz)
	{
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		
		if (!target.areIndexed("code"))
		{
			if (target.getProperty("code") == null)
			{
				target.createProperty("code", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".code", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "code");
			dbConnection.getMetadata().getSchema().save();
		}
		
		if (!target.areIndexed("displayName"))
		{
			if (target.getProperty("displayName") == null)
			{
				target.createProperty("displayName", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".displayName", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "displayName");
			dbConnection.getMetadata().getSchema().save();
		}
	}
	
	public static long getRecordCount (OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz)
	{
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		
		return target.count();
	}
	
	public synchronized void toggleActiveDatabase()
	{
		this.isPrimaryActive = !(this.isPrimaryActive);
		
		logger.info("TOGGLING ACTIVE DATABASE");
	}
	
	
	public <T extends CodeModel> List<T> fetchByCode(Class<T> clazz, String code)
	{
		
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where code = '" + code.toUpperCase() + "'");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			result = dbConnection.query(query);
		}
		catch (Exception e)
		{
			logger.error("Could not execute query against active database.", e);
		}
		finally
		{
			dbConnection.close();
		}
		return result;
	}
	
	public <T extends CodeModel> List<T> fetchByDisplayName(Class<T> clazz, String displayName)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where displayName = '" + displayName.toUpperCase() + "'");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			result = dbConnection.query(query);
		}
		catch (Exception e)
		{
			logger.error("Could not execute query against active database.", e);
		}
		finally
		{
			dbConnection.close();
		}
		return result;
	}
	
	
	public static void registerModels(OObjectDatabaseTx db) {
		db.getEntityManager().registerEntityClasses("org.sitenv.vocabularies.model.impl");
	}


	public Map<String, VocabularyModelDefinition> getVocabularyMap() {
		return vocabularyMap;
	}


	public void setVocabularyMap(Map<String, VocabularyModelDefinition> vocabularyMap) {
		this.vocabularyMap = vocabularyMap;
	}
	
	
}
