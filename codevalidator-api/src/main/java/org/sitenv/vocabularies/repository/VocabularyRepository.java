package org.sitenv.vocabularies.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.model.ValueSetModel;
import org.sitenv.vocabularies.model.ValueSetModelDefinition;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;

public class VocabularyRepository {
	
	private static Logger logger = Logger.getLogger(VocabularyRepository.class);
	

	
	// Singleton self reference
	private static final VocabularyRepository ACTIVE_INSTANCE = new VocabularyRepository();
	
	private Map<String, VocabularyModelDefinition> vocabularyMap;

	List<Class<? extends ValueSetModel>> valueSetModelClassList = null;
	
	private OServer primaryOrientDbServer;
	private VocabularyRepositoryConnectionInfo primaryNodeCredentials;
	private OObjectDatabasePool primaryConnectionPool;
	private OObjectDatabasePool secondaryConnectionPool;
	private VocabularyRepositoryConnectionInfo secondaryNodeCredentials;
	private boolean isPrimaryActive = true; 
	
	private VocabularyRepository () {}
	
	
	public static VocabularyRepository getInstance() {
		return ACTIVE_INSTANCE;
	}

	
	


	public List<Class<? extends ValueSetModel>> getValueSetModelClassList() {
		return valueSetModelClassList;
	}


	public void setValueSetModelClassList(
			List<Class<? extends ValueSetModel>> valueSetModelClassList) {
		this.valueSetModelClassList = valueSetModelClassList;
	}


	public OServer getOrientDbServer() {
		return primaryOrientDbServer;
	}


	public void setOrientDbServer(OServer orientDbServer) {
		this.primaryOrientDbServer = orientDbServer;
	}


	public VocabularyRepositoryConnectionInfo getPrimaryNodeCredentials() {
		return primaryNodeCredentials;
	}


	public void setPrimaryNodeCredentials(VocabularyRepositoryConnectionInfo primaryNodeCredentials) {
		this.primaryNodeCredentials = primaryNodeCredentials;
		
		this.primaryConnectionPool = new OObjectDatabasePool(primaryNodeCredentials.getConnectionInfo(), primaryNodeCredentials.getUsername(), primaryNodeCredentials.getPassword());
	}


	public VocabularyRepositoryConnectionInfo getSecondaryNodeCredentials() {
		return secondaryNodeCredentials;
	}


	public void setSecondaryNodeCredentials(
			VocabularyRepositoryConnectionInfo secondaryNodeCredentials) {
		this.secondaryNodeCredentials = secondaryNodeCredentials;
	
		this.secondaryConnectionPool = new OObjectDatabasePool(secondaryNodeCredentials.getConnectionInfo(), secondaryNodeCredentials.getUsername(), secondaryNodeCredentials.getPassword());
	}
	
	public OObjectDatabaseTx getActiveDbConnection() {
		
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
		try {
		dbConnection.command(new OCommandSQL("TRUNCATE CLASS " + clazz.getSimpleName())).execute();
		dbConnection.commit();
		} catch (OCommandSQLParsingException e) {
			logger.error("Could not truncate the class " + clazz.getSimpleName() + ".  Perhaps it doesn't exist in " + dbConnection.getName());
		}
	}
	
	public static void truncateValueSetModel(OObjectDatabaseTx dbConnection, Class<? extends ValueSetModel> clazz) 
	{
		try {
		dbConnection.command(new OCommandSQL("TRUNCATE CLASS " + clazz.getSimpleName())).execute();
		dbConnection.commit();
		} catch (OCommandSQLParsingException e) {
			logger.error("Could not truncate the class " + clazz.getSimpleName() + ".  Perhaps it doesn't exist in " + dbConnection.getName());
		}
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
	
	public static void updateValueSetIndexProperties(OObjectDatabaseTx dbConnection, Class<? extends ValueSetModel> clazz)
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
		
		if (!target.areIndexed("codeSystem"))
		{
			if (target.getProperty("codeSystem") == null)
			{
				target.createProperty("codeSystem", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".codeSystem", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "codeSystem");
			dbConnection.getMetadata().getSchema().save();
		}
		
		if (!target.areIndexed("valueSet"))
		{
			if (target.getProperty("valueSet") == null)
			{
				target.createProperty("valueSet", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".valueSet", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "valueSet");
			dbConnection.getMetadata().getSchema().save();
		}
	}
	
	public static long getRecordCount (OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz)
	{
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		
		return target.count();
	}
	
	public static long getValueSetRecordCount (OObjectDatabaseTx dbConnection, Class<? extends ValueSetModel> clazz)
	{
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		
		return target.count();
	}
	
	public void toggleActiveDatabase()
	{
		this.isPrimaryActive = !(this.isPrimaryActive);
		
		logger.info("TOGGLING ACTIVE DATABASE");
	}
	
	
	public <T extends CodeModel> List<T> fetchByCode(Class<T> clazz, String code)
	{
		
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where code = :code");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("code", code.toUpperCase());
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			result = dbConnection.command(query).execute(params);
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
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where displayName = :displayName");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("displayName", displayName.toUpperCase());
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			result = dbConnection.command(query).execute(params);
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
	
	public <T extends ValueSetModel> List<T> fetchByValueSetAndCode(Class<T> clazz, String valueSet, String code)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM "+clazz.getSimpleName()+" where valueSet = :valueSet AND code = :code");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSet", valueSet.toUpperCase());
		params.put("code", code.toUpperCase());
		
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			result = dbConnection.command(query).execute(params);
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
	
	public <T extends ValueSetModel> List<T> fetchByValueSetCodeSystemAndCode(Class<T> clazz, String valueSet, String codeSystem, String code)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where valueSet = :valueSet AND codeSystem = :codeSystem AND code = :code");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSet", valueSet.toUpperCase());
		params.put("codeSystem", codeSystem.toUpperCase());
		params.put("code", code.toUpperCase());
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			result = dbConnection.command(query).execute(params);
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
