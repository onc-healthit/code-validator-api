package org.sitenv.vocabularies.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.model.ValueSetModel;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;

import com.orientechnologies.orient.core.index.OIndex;
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
	
	private Map<String, VocabularyModelDefinition> vocabularyMap = new TreeMap<String, VocabularyModelDefinition>(String.CASE_INSENSITIVE_ORDER);

	List<Class<? extends ValueSetModel>> valueSetModelClassList = new ArrayList<Class<? extends ValueSetModel>>();
	
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
	
	public static void updateIndexProperties(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz, boolean clear)
	{
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		
		if (clear) {
			for (OIndex<?> index : target.getInvolvedIndexes("codeIndex", "displayNameIndex")) {
				index.clear();
			}
		}
		
		if (!target.areIndexed("codeIndex"))
		{
			if (target.getProperty("codeIndex") == null)
			{
				target.createProperty("codeIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".codeIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "codeIndex");
			dbConnection.getMetadata().getSchema().save();
		}
		
		if (!target.areIndexed("displayNameIndex"))
		{
			if (target.getProperty("displayNameIndex") == null)
			{
				target.createProperty("displayNameIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".displayNameIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "displayNameIndex");
			dbConnection.getMetadata().getSchema().save();
		}
	}
	
	public static void updateValueSetIndexProperties(OObjectDatabaseTx dbConnection, Class<? extends ValueSetModel> clazz, boolean clear)
	{
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		
		if (clear) {
			for (OIndex<?> index : target.getInvolvedIndexes("codeIndex", "codeSystemIndex", "valueSetIndex", "valueSetNameIndex", "descriptionIndex")) {
				index.clear();
			}
		}
		
		if (!target.areIndexed("codeIndex"))
		{
			if (target.getProperty("codeIndex") == null)
			{
				target.createProperty("codeIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".codeIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "codeIndex");
			dbConnection.getMetadata().getSchema().save();
		}
		
		if (!target.areIndexed("codeSystemIndex"))
		{
			if (target.getProperty("codeSystemIndex") == null)
			{
				target.createProperty("codeSystemIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".codeSystemIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "codeSystemIndex");
			dbConnection.getMetadata().getSchema().save();
		}
		
		if (!target.areIndexed("valueSetIndex"))
		{
			if (target.getProperty("valueSetIndex") == null)
			{
				target.createProperty("valueSetIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".valueSetIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "valueSetIndex");
			dbConnection.getMetadata().getSchema().save();
		}
		
		if (!target.areIndexed("valueSetNameIndex"))
		{
			if (target.getProperty("valueSetNameIndex") == null)
			{
				target.createProperty("valueSetNameIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".valueSetNameIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "valueSetNameIndex");
			dbConnection.getMetadata().getSchema().save();
		}
		

		if (!target.areIndexed("descriptionIndex"))
		{
			if (target.getProperty("descriptionIndex") == null)
			{
				target.createProperty("descriptionIndex", OType.STRING);
			}
			target.createIndex(clazz.getSimpleName() + ".descriptionIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "descriptionIndex");
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
		
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where codeIndex = :code");
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
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where displayNameIndex = :displayName");
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
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where valueSetIndex = :valueSet AND codeIndex = :code");
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
	
	public <T extends ValueSetModel> List<T> fetchByValueSetAndDescription(Class<T> clazz, String valueSet, String description)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where valueSetIndex = :valueSet AND descriptionIndex = :description");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSet", valueSet.toUpperCase());
		params.put("description", description.toUpperCase());
		
		
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
	
	public <T extends ValueSetModel> Boolean valueSetExists(Class<T> clazz, String valueSet)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT DISTINCT(valueSet) FROM "+clazz.getSimpleName()+" where valueSetIndex = :valueSet");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSet", valueSet.toUpperCase());
		
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
		return (result != null && result.size() > 0);
	}
	
	public <T extends ValueSetModel> List<T> fetchByValueSetCodeSystemAndCode(Class<T> clazz, String valueSet, String codeSystem, String code)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where valueSetIndex = :valueSet AND codeSystemIndex = :codeSystem AND codeIndex = :code");
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
}
