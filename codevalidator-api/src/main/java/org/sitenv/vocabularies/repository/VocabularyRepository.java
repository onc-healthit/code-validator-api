package org.sitenv.vocabularies.repository;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.data.CodeSystemResult;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.model.ValueSetModel;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;

import java.util.*;

public class VocabularyRepository {
	private static Logger logger = Logger.getLogger(VocabularyRepository.class);

	// Singleton self reference
	private static final VocabularyRepository ACTIVE_INSTANCE = new VocabularyRepository();
	private Map<String, VocabularyModelDefinition> vocabularyMap = new TreeMap<String, VocabularyModelDefinition>(String.CASE_INSENSITIVE_ORDER);
	List<Class<? extends ValueSetModel>> valueSetModelClassList = new ArrayList<Class<? extends ValueSetModel>>();
	private OServer primaryOrientDbServer;
	private VocabularyRepositoryConnectionInfo primaryNodeCredentials;
	private VocabularyRepositoryConnectionInfo secondaryNodeCredentials;
	private boolean isPrimaryActive = true; 
	private OCommandSQL cmd = new OCommandSQL();
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
		
	}

	public VocabularyRepositoryConnectionInfo getSecondaryNodeCredentials() {
		return secondaryNodeCredentials;
	}

	public void setSecondaryNodeCredentials(
			VocabularyRepositoryConnectionInfo secondaryNodeCredentials) {
		this.secondaryNodeCredentials = secondaryNodeCredentials;
	}
	
	public OObjectDatabaseTx getActiveDbConnection() {
		OObjectDatabaseTx connection;
		if (isPrimaryActive) {
			//connection = primaryConnectionPool.acquire();
			connection = OObjectDatabasePool.global().acquire(primaryNodeCredentials.getConnectionInfo(), primaryNodeCredentials.getUsername(), primaryNodeCredentials.getPassword());
		} else {
			//connection = secondaryConnectionPool.acquire();
			connection = OObjectDatabasePool.global().acquire(secondaryNodeCredentials.getConnectionInfo(), secondaryNodeCredentials.getUsername(), secondaryNodeCredentials.getPassword());
		}
		registerModels(connection);
		return connection;
	}
	
	public OObjectDatabaseTx getInactiveDbConnection() {
		OObjectDatabaseTx connection;
		if (!isPrimaryActive) {
			//connection = primaryConnectionPool.acquire();
			connection = OObjectDatabasePool.global().acquire(primaryNodeCredentials.getConnectionInfo(), primaryNodeCredentials.getUsername(), primaryNodeCredentials.getPassword());
		} else {
			//connection = secondaryConnectionPool.acquire();
			connection = OObjectDatabasePool.global().acquire(secondaryNodeCredentials.getConnectionInfo(), secondaryNodeCredentials.getUsername(), secondaryNodeCredentials.getPassword());
		}
		registerModels(connection);
		return connection;	
	}
	
	public static void truncateModel(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz) {
		try {
		dbConnection.command(new OCommandSQL("TRUNCATE CLASS " + clazz.getSimpleName())).execute();
		dbConnection.commit();
		} catch (OCommandSQLParsingException e) {
			logger.error("Could not truncate the class " + clazz.getSimpleName() + ".  Perhaps it doesn't exist in " + dbConnection.getName());
		}
	}
	
	public static void truncateValueSetModel(OObjectDatabaseTx dbConnection, Class<? extends ValueSetModel> clazz) {
		try {
		dbConnection.command(new OCommandSQL("TRUNCATE CLASS " + clazz.getSimpleName())).execute();
		dbConnection.commit();
		} catch (OCommandSQLParsingException e) {
			logger.error("Could not truncate the class " + clazz.getSimpleName() + ".  Perhaps it doesn't exist in " + dbConnection.getName());
		}
	}
	
	public static void updateIndexProperties(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz, boolean clear) {
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
	
	public static long getRecordCount (OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz) {
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		return target.count();
	}
	
	public static long getValueSetRecordCount (OObjectDatabaseTx dbConnection, Class<? extends ValueSetModel> clazz) {
		OClass target = dbConnection.getMetadata().getSchema().getOrCreateClass(clazz.getSimpleName());
		return target.count();
	}
	
	public void toggleActiveDatabase() {
		this.isPrimaryActive = !(this.isPrimaryActive);
		logger.info("TOGGLING ACTIVE DATABASE");
	}

	public <T extends CodeModel> List<T> fetchByCode(Class<T> clazz, String code, OObjectDatabaseTx dbConnection) {
		String query = "SELECT * FROM " + clazz.getSimpleName() + " where codeIndex = ?";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<T> result = null;
		try {
			result = dbConnection.command(cmd).execute(code.toUpperCase());
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return result;
	}
	
	public <T extends CodeModel> List<T> fetchByDisplayName(Class<T> clazz, String displayName, OObjectDatabaseTx dbConnection) {
		String query = "SELECT * FROM " + clazz.getSimpleName() + " where displayNameIndex = ?";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<T> result = null;
		try {
			result = dbConnection.command(cmd).execute(displayName.toUpperCase());
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return result;
	}
	
	public <T extends ValueSetModel> List<CodeSystemResult> fetchCodeSystemsByValueSet(Class<T> clazz, String valueSet, OObjectDatabaseTx dbConnection) {
		String query = "SELECT codeSystem, codeSystemName FROM " + clazz.getSimpleName() + " where valueSetIndex = ? GROUP BY codeSystem, codeSystemName";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<ODocument> results;
		List<CodeSystemResult> codeSystems = null;
		try {
			results = dbConnection.command(cmd).execute(valueSet.toUpperCase());
			if (results != null) {
				codeSystems = new ArrayList();
				for (ODocument result : results) {
					String codeSystem = result.field("codeSystem");
					String codeSystemName = result.field("codeSystemName");
					
					CodeSystemResult csResult = new CodeSystemResult();
					csResult.setCodeSystem(codeSystem);
					csResult.setCodeSystemName(codeSystemName);
					codeSystems.add(csResult);
				}
			}
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return codeSystems;
	}
	
	public <T extends ValueSetModel> Set<String> fetchValueSetNamesByValueSet(Class<T> clazz, String valueSet,  OObjectDatabaseTx dbConnection) {
		System.out.println("DEBUG ---> GONNA fetchValueSetNameByValueset : " + valueSet);
		String query = "SELECT DISTINCT(valueSetName) AS valueSetName FROM " + clazz.getSimpleName() + " where valueSetIndex = ?";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<ODocument> results = null;
		Set<String> valueSetNames = null;
		try {
			results = dbConnection.command(cmd).execute(valueSet.toUpperCase());
			if (results != null) {
				valueSetNames = new TreeSet<String>();
				for (ODocument result : results) {
					//logger.debug("fieldNames " + result.fieldNames()[0] + " " + result.fieldNames()[1]);
					String valueSetName = result.field("valueSetName");
					valueSetNames.add(valueSetName);
				}
			}
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return valueSetNames;
	}
	
	public <T extends ValueSetModel> List<T> fetchByValueSetAndCode(Class<T> clazz, String valueSet, String code, OObjectDatabaseTx dbConnection) {
		System.out.println("DEBUG ---> GONNA fetchByValueSetAndCode : " + valueSet + " : " + code);
		String query = "SELECT * FROM " + clazz.getSimpleName() + " where valueSetIndex = ? AND codeIndex = ?";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<T> result = null;
		try {
			result = dbConnection.command(cmd).execute(valueSet.toUpperCase(), code.toUpperCase());
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return result;
	}
	
	public <T extends ValueSetModel> List<T> fetchByValueSetAndDescription(Class<T> clazz, String valueSet, String description, OObjectDatabaseTx dbConnection) {
		System.out.println("DEBUG ---> GONNA fetchByValueSetAndDescription : " + valueSet + " : " + description);
		String query = "SELECT * FROM " + clazz.getSimpleName() + " where valueSetIndex = ? AND descriptionIndex = ?";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<T> result = null;
		try {
			result = dbConnection.command(cmd).execute(valueSet.toUpperCase(), description.toUpperCase());
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return result;
	}
	
	public <T extends ValueSetModel> Boolean valueSetExists(Class<T> clazz, String valueSet, OObjectDatabaseTx dbConnection) {
		System.out.println("DEBUG ---> GONNA valueSetExists : " + valueSet);
		String query = "SELECT DISTINCT(valueSet) AS valueSet FROM " +clazz.getSimpleName()+ " where valueSetIndex = ?";
		cmd.setUseCache(true);
		cmd.setText(query);
		List<T> result = null;
		try {
			result = dbConnection.command(cmd).execute(valueSet.toUpperCase());
		} catch (Exception e) {
			logger.error("Could not execute query against active database.", e);
		}
		return (result != null && result.size() > 0);
	}
	
	public static void registerModels(OObjectDatabaseTx db) {
		db.getEntityManager().registerEntityClasses("org.sitenv.vocabularies.model.impl");
	}

	public Map<String, VocabularyModelDefinition> getVocabularyMap() {
		return vocabularyMap;
	}
}
