package org.sitenv.vocabularies.data;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.model.CodeModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;

public class VocabularyDataStore {
	
	private static Logger logger = Logger.getLogger(VocabularyDataStore.class);
	

	
	// Singleton self reference
	private static final VocabularyDataStore ACTIVE_INSTANCE = new VocabularyDataStore();
	
	private Map<String, Vocabulary> vocabularyMap;
	
	private OServer orientDbServer;
	private OrientDbCredentials primaryNodeCredentials;
	private OrientDbCredentials secondaryNodeCredentials;
	private boolean isPrimaryActive = false;  // default to false so we first write to the primary at startups
	
	private VocabularyDataStore () {}
	
	
	public static VocabularyDataStore getInstance() {
		return ACTIVE_INSTANCE;
	}


	public OServer getOrientDbServer() {
		return orientDbServer;
	}


	public void setOrientDbServer(OServer orientDbServer) {
		this.orientDbServer = orientDbServer;
	}


	public synchronized OrientDbCredentials getPrimaryNodeCredentials() {
		return primaryNodeCredentials;
	}


	public synchronized void setPrimaryNodeCredentials(OrientDbCredentials primaryNodeCredentials) {
		this.primaryNodeCredentials = primaryNodeCredentials;
	}


	public synchronized OrientDbCredentials getSecondaryNodeCredentials() {
		return secondaryNodeCredentials;
	}


	public synchronized void setSecondaryNodeCredentials(
			OrientDbCredentials secondaryNodeCredentials) {
		this.secondaryNodeCredentials = secondaryNodeCredentials;
	}
	
	public synchronized OObjectDatabaseTx getActiveDbConnection() {
		OrientDbCredentials creds;
		if (isPrimaryActive) {
			creds = primaryNodeCredentials;
		} else {
			creds = secondaryNodeCredentials;
		}
		
		OObjectDatabaseTx connection = OObjectDatabasePool.global().acquire(creds.getConnectionInfo(), creds.getUsername(), creds.getPassword());
		
		registerModels(connection);
		
		return connection;
	}
	
	public synchronized OObjectDatabaseTx getInactiveDbConnection() {
		OrientDbCredentials creds;
		if (isPrimaryActive) {
			creds = secondaryNodeCredentials;
		} else {
			creds = primaryNodeCredentials;
		}
		
		OObjectDatabaseTx connection = OObjectDatabasePool.global().acquire(creds.getConnectionInfo(), creds.getUsername(), creds.getPassword());
		
		registerModels(connection);
		
		return connection;	
	}
	
	public static void truncateModel(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz) 
	{
		dbConnection.command(new OCommandSQL("TRUNCATE CLASS " + clazz.getSimpleName())).execute();
		dbConnection.commit();
		
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
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
		this.isPrimaryActive = !this.isPrimaryActive;
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


	public Map<String, Vocabulary> getVocabularyMap() {
		return vocabularyMap;
	}


	public void setVocabularyMap(Map<String, Vocabulary> vocabularyMap) {
		this.vocabularyMap = vocabularyMap;
	}
	
	
}
