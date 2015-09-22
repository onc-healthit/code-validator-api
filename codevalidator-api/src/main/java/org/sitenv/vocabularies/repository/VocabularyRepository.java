package org.sitenv.vocabularies.repository;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.CodeSystemResult;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.sitenv.vocabularies.loader.code.icd10.Icd10CmLoader;
import org.sitenv.vocabularies.loader.code.icd10.Icd10PcsLoader;
import org.sitenv.vocabularies.loader.code.icd9.Icd9CmDxLoader;
import org.sitenv.vocabularies.loader.code.icd9.Icd9CmSgLoader;
import org.sitenv.vocabularies.loader.code.loinc.LoincLoader;
import org.sitenv.vocabularies.loader.code.rxnorm.RxNormLoader;
import org.sitenv.vocabularies.loader.code.snomed.SnomedLoader;
import org.sitenv.vocabularies.loader.valueset.phvs.PhinVadsLoader;
import org.sitenv.vocabularies.loader.valueset.vsac.VsacLoader;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.model.CodeModelDefinition;
import org.sitenv.vocabularies.model.ValueSetCodeModel;
import org.sitenv.vocabularies.model.ValueSetModelDefinition;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;
import org.sitenv.vocabularies.model.impl.Icd10PcsModel;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;
import org.sitenv.vocabularies.model.impl.Icd9CmSgModel;
import org.sitenv.vocabularies.model.impl.LoincModel;
import org.sitenv.vocabularies.model.impl.PhinVadsModel;
import org.sitenv.vocabularies.model.impl.RxNormModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;
import org.sitenv.vocabularies.model.impl.VsacModel;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class VocabularyRepository {
	private static Logger logger = Logger.getLogger(VocabularyRepository.class);
	
	// Singleton self reference
	private static final VocabularyRepository ACTIVE_INSTANCE = new VocabularyRepository();
	
	private OServer primaryOrientDbServer;
	private VocabularyRepositoryConnectionInfo primaryNodeCredentials;
	private VocabularyRepositoryConnectionInfo secondaryNodeCredentials;
	private boolean isPrimaryActive = true;
	private OPartitionedDatabasePoolFactory connectionPoolFactory = new OPartitionedDatabasePoolFactory(2);
	private OPartitionedDatabasePool primaryConnectionPool;
	private OPartitionedDatabasePool secondaryConnectionPool;
	
	@SuppressWarnings("serial")
	private Map<String, CodeModelDefinition<?>> codeModelDefinitions = new LinkedHashMap<String, CodeModelDefinition<?>>(){{
		this.put(VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_ID, new CodeModelDefinition<Icd9CmDxModel>(Icd9CmDxModel.class,
			VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_ID, VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_NAME,
			VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_TYPE));
		
		this.put(VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_ID, new CodeModelDefinition<Icd9CmSgModel>(Icd9CmSgModel.class,
			VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_ID, VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_NAME,
			VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_TYPE));
		
		this.put(VocabularyConstants.ICD10CM_CODE_SYSTEM_ID, new CodeModelDefinition<Icd10CmModel>(Icd10CmModel.class,
			VocabularyConstants.ICD10CM_CODE_SYSTEM_ID, VocabularyConstants.ICD10CM_CODE_SYSTEM_NAME, VocabularyConstants.ICD10CM_CODE_SYSTEM_TYPE));
		
		this.put(VocabularyConstants.ICD10PCS_CODE_SYSTEM_ID, new CodeModelDefinition<Icd10PcsModel>(Icd10PcsModel.class,
			VocabularyConstants.ICD10PCS_CODE_SYSTEM_ID, VocabularyConstants.ICD10PCS_CODE_SYSTEM_NAME, VocabularyConstants.ICD10PCS_CODE_SYSTEM_TYPE));
		
		this.put(VocabularyConstants.LOINC_CODE_SYSTEM_ID, new CodeModelDefinition<LoincModel>(LoincModel.class, VocabularyConstants.LOINC_CODE_SYSTEM_ID,
			VocabularyConstants.LOINC_CODE_SYSTEM_NAME, VocabularyConstants.LOINC_CODE_SYSTEM_TYPE));
		
		this.put(VocabularyConstants.RXNORM_CODE_SYSTEM_ID, new CodeModelDefinition<RxNormModel>(RxNormModel.class, VocabularyConstants.RXNORM_CODE_SYSTEM_ID,
			VocabularyConstants.RXNORM_CODE_SYSTEM_NAME, VocabularyConstants.RXNORM_CODE_SYSTEM_TYPE));
		
		this.put(VocabularyConstants.SNOMEDCT_CODE_SYSTEM_ID, new CodeModelDefinition<SnomedModel>(SnomedModel.class,
			VocabularyConstants.SNOMEDCT_CODE_SYSTEM_ID, VocabularyConstants.SNOMEDCT_CODE_SYSTEM_NAME, VocabularyConstants.SNOMEDCT_CODE_SYSTEM_TYPE));
	}};
	
	@SuppressWarnings("serial")
	private Map<String, ValueSetModelDefinition<?>> valueSetModelDefinitions = new LinkedHashMap<String, ValueSetModelDefinition<?>>(){{
		this.put(VocabularyConstants.PHIN_VADS_VALUE_SET_TYPE, new ValueSetModelDefinition<PhinVadsModel>(PhinVadsModel.class,
			VocabularyConstants.PHIN_VADS_VALUE_SET_TYPE));
		
		this.put(VocabularyConstants.VSAC_VALUE_SET_TYPE, new ValueSetModelDefinition<VsacModel>(VsacModel.class, VocabularyConstants.VSAC_VALUE_SET_TYPE));
	}};
	
	@SuppressWarnings("serial")
	private Map<String, VocabularyLoader<?>> codeLoaders = new LinkedCaseInsensitiveMap<VocabularyLoader<?>>(){{
		this.put(VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_TYPE, new Icd9CmDxLoader());
		this.put(VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_TYPE, new Icd9CmSgLoader());
		this.put(VocabularyConstants.ICD10CM_CODE_SYSTEM_TYPE, new Icd10CmLoader());
		this.put(VocabularyConstants.ICD10PCS_CODE_SYSTEM_TYPE, new Icd10PcsLoader());
		this.put(VocabularyConstants.LOINC_CODE_SYSTEM_TYPE, new LoincLoader());
		this.put(VocabularyConstants.RXNORM_CODE_SYSTEM_TYPE, new RxNormLoader());
		this.put(VocabularyConstants.SNOMEDCT_CODE_SYSTEM_TYPE, new SnomedLoader());
	}};
	
	@SuppressWarnings("serial")
	private Map<String, VocabularyLoader<? extends ValueSetCodeModel>> valueSetCodeLoaders =
		new LinkedCaseInsensitiveMap<VocabularyLoader<? extends ValueSetCodeModel>>(){{
		this.put(VocabularyConstants.PHIN_VADS_VALUE_SET_TYPE, new PhinVadsLoader());
		this.put(VocabularyConstants.VSAC_VALUE_SET_TYPE, new VsacLoader());
	}};
	
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
	
	public boolean isPrimaryActive() {
		return this.isPrimaryActive;
	}
	
	public OPartitionedDatabasePoolFactory getConnectionPoolFactory() {
		return this.connectionPoolFactory;
	}

	public void setConnectionPoolFactory(OPartitionedDatabasePoolFactory connectionPoolFactory) {
		this.connectionPoolFactory = connectionPoolFactory;
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

	public void setSecondaryNodeCredentials(VocabularyRepositoryConnectionInfo secondaryNodeCredentials) {
		this.secondaryNodeCredentials = secondaryNodeCredentials;
	}
	
	public OObjectDatabaseTx getActiveDbConnection() {
		return this.getDbConnection(true);
	}
	
	public OObjectDatabaseTx getInactiveDbConnection() {
		return this.getDbConnection(false);
	}
	
	public OObjectDatabaseTx getDbConnection(boolean active) {
		return new OObjectDatabaseTx(this.getDbConnectionPool(active).acquire());
	}
	
	public OPartitionedDatabasePool getDbConnectionPool(boolean active) {
		return (active ? (this.isPrimaryActive ? this.primaryConnectionPool : this.secondaryConnectionPool) :
			(!this.isPrimaryActive ? this.primaryConnectionPool : this.secondaryConnectionPool));
	}
	
	public void initializeDbConnectionPools() {
		this.connectionPoolFactory.setMaxPoolSize(100);
		
		this.primaryConnectionPool = this.connectionPoolFactory.get(this.primaryNodeCredentials.getConnectionInfo(),
			this.primaryNodeCredentials.getUsername(), this.primaryNodeCredentials.getPassword());
		
		this.secondaryConnectionPool = this.connectionPoolFactory.get(this.secondaryNodeCredentials.getConnectionInfo(),
			this.secondaryNodeCredentials.getUsername(), this.secondaryNodeCredentials.getPassword());
	}
	
	public void initializeDb(boolean active) {
		OObjectDatabaseTx dbConnection = null;
		
		try {
			dbConnection = this.getDbConnection(active);
			
			OEntityManager entityManager = dbConnection.getEntityManager();
			OMetadata metadata = dbConnection.getMetadata();
			OIndexManager indexManager = metadata.getIndexManager();
			OSchema schema = metadata.getSchema();
			OClass modelDbClass = this.initializeModel(true, entityManager, indexManager, schema, CodeModel.class);
			
			buildDbIndex(true, indexManager, modelDbClass, INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "code", "codeSystemId");
			
			modelDbClass = this.initializeModel(true, entityManager, indexManager, schema, ValueSetCodeModel.class);
			
			buildDbIndex(true, indexManager, modelDbClass, INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "code", "codeSystemId");
			buildDbIndex(true, indexManager, modelDbClass, INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "code", "valueSetId");
			buildDbIndex(true, indexManager, modelDbClass, INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "code", "codeSystemId", "valueSetId");
		} finally {
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	public <T extends CodeModel> OClass initializeModel(boolean clear, OObjectDatabaseTx dbConnection, Class<T> modelClass) {
		OMetadata metadata = dbConnection.getMetadata();
		
		return this.initializeModel(clear, dbConnection.getEntityManager(), metadata.getIndexManager(), metadata.getSchema(), modelClass);
	}
	
	public <T extends CodeModel> OClass initializeModel(boolean clear, OEntityManager entityManager, OIndexManager indexManager, OSchema schema,
		Class<T> modelClass) {
		boolean valueSetModel = ValueSetCodeModel.class.isAssignableFrom(modelClass);
		OClass modelDbClass = buildDbClass(clear, entityManager, schema, modelClass, schema.getClass((valueSetModel ? ValueSetCodeModel.class :
			CodeModel.class)));
		
		buildDbProperty(clear, indexManager, modelDbClass, "code", OType.STRING, true, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		buildDbProperty(clear, indexManager, modelDbClass, "displayName", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		buildDbProperty(clear, indexManager, modelDbClass, "tty", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		buildDbProperty(clear, indexManager, modelDbClass, "codeSystemId", OType.STRING, true, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		buildDbProperty(clear, indexManager, modelDbClass, "codeSystemName", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		buildDbProperty(clear, indexManager, modelDbClass, "codeSystemVersion", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		
		if (valueSetModel) {
			buildDbProperty(clear, indexManager, modelDbClass, "valueSetId", OType.STRING, true, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
			buildDbProperty(clear, indexManager, modelDbClass, "valueSetName", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
			buildDbProperty(clear, indexManager, modelDbClass, "valueSetVersion", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
			buildDbProperty(clear, indexManager, modelDbClass, "steward", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
			buildDbProperty(clear, indexManager, modelDbClass, "type", OType.STRING, false, INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
		}
		
		return modelDbClass;
	}
	
	public void registerModels(boolean active) {
		OObjectDatabaseTx dbConnection = null;
		OEntityManager entityManager;
		OSchema schema;
		
		try {
			dbConnection = this.getDbConnection(active);
			entityManager = dbConnection.getEntityManager();
			schema = dbConnection.getMetadata().getSchema();
			
			for (CodeModelDefinition<?> codeModelDefinition : this.codeModelDefinitions.values()) {
				registerEntityClass(entityManager, schema, codeModelDefinition.getModelClass());
			}
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : this.valueSetModelDefinitions.values()) {
				registerEntityClass(entityManager, schema, valueSetModelDefinition.getModelClass());
			}
		} finally {
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	public static OProperty buildDbProperty(boolean clear, OIndexManager indexManager, OClass dbClass, String propName, OType propType, boolean required,
		INDEX_TYPE indexType) {
		OProperty dbProp = (dbClass.existsProperty(propName) ? dbClass.getProperty(propName) : dbClass.createProperty(propName, propType));
		
		if (required) {
			dbProp.setMandatory(true);
			dbProp.setNotNull(true);
		}
		
		if (indexType != null) {
			buildDbIndex(clear, indexManager, dbClass, indexType, propName);
		}

		return dbProp;
	}
	
	public static OIndex<?> buildDbIndex(boolean clear, OIndexManager indexManager, OClass dbClass, INDEX_TYPE indexType, String ... fieldNames) {
		StrBuilder indexNameBuilder = new StrBuilder(dbClass.getName());
		indexNameBuilder.append(".");
		indexNameBuilder.append(fieldNames[0]);
		
		for (int a = 1; a < fieldNames.length; a++) {
			indexNameBuilder.append("And");
			indexNameBuilder.append(StringUtils.capitalize(fieldNames[a]));
		}
		
		String indexName = indexNameBuilder.build();
		OIndex<?> index;
		
		if (indexManager.existsIndex(indexName)) {
			index = indexManager.getIndex(indexName);
			
			if (clear) {
				index.clear();
			}
		} else {
			index = dbClass.createIndex(indexName, indexType, fieldNames);
		}
		
		return index;
	}
	
	public static OClass buildDbClass(boolean truncate, OEntityManager entityManager, OSchema schema, Class<?> clazz, OClass ... superClasses) {
		String className = clazz.getSimpleName();
		OClass dbClass;
		
		if (schema.existsClass(className)) {
			dbClass = schema.getClass(className);
			
			if (truncate) {
				try {
					dbClass.truncate();
					
					logger.debug(String.format("Database class (name=%s) truncated.", className));
				} catch (IOException e) {
					logger.error("Could not truncate database class: %s" + clazz.getSimpleName(), e);
				}
			}
		} else {
			int classMods = clazz.getModifiers();

			dbClass = ((Modifier.isInterface(classMods) || Modifier.isAbstract(classMods)) ? schema.createAbstractClass(className, superClasses) :
				schema.createClass(className, superClasses));
			dbClass.setStrictMode(true);
		}
		
		registerEntityClass(entityManager, schema, clazz);
		
		return dbClass;
	}
	
	public static void registerEntityClass(OEntityManager entityManager, OSchema schema, Class<?> clazz) {
		String className = clazz.getSimpleName();
		
		if (schema.existsClass(className) && (entityManager.getEntityClass(className) == null)) {
			entityManager.registerEntityClass(clazz);
		}
	}
	
	public static long getRecordCount(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz)
	{
		return getRecordCount(dbConnection, clazz, true);
	}
	
	public static long getRecordCount(OObjectDatabaseTx dbConnection, Class<? extends CodeModel> clazz, boolean polymorphic)
	{
		OClass target = dbConnection.getMetadata().getSchema().getClass(clazz.getSimpleName());
		
		return ((target != null) ? target.count(polymorphic) : -1);
	}
	
	public void toggleActiveDatabase()
	{
		this.isPrimaryActive = !(this.isPrimaryActive);
		
		logger.info(String.format("Database toggled - %s is now active.", (this.isPrimaryActive ? "primary" : "secondary")));
	}
	
	public <T extends CodeModel> List<T> fetchByCode(Class<T> clazz, String code)
	{
		
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() + " where code = :code");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("code", code);
		
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
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return result;
	}
	
	public <T extends CodeModel> List<T> fetchByDisplayName(Class<T> clazz, String displayName)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where displayName = :displayName");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("displayName", displayName);
		
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
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return result;
	}
	
	public <T extends ValueSetCodeModel> List<CodeSystemResult> fetchCodeSystemsByValueSet(Class<T> clazz, String valueSet)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT codeSystemId, codeSystemName FROM " + clazz.getSimpleName() +
				" where valueSetId = :valueSetId GROUP BY codeSystemId, codeSystemName");
		OObjectDatabaseTx dbConnection = null;
		List<ODocument> results = null;
		List<CodeSystemResult> codeSystems = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSetId", valueSet);
		
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			results = dbConnection.command(query).execute(params);
			
			if (results != null)
			{
				codeSystems = new ArrayList<CodeSystemResult>();
				for (ODocument result : results)
				{
					String codeSystem = result.field("codeSystemId");
					String codeSystemName = result.field("codeSystemName");
					
					CodeSystemResult csResult = new CodeSystemResult();
					csResult.setCodeSystem(codeSystem);
					csResult.setCodeSystemName(codeSystemName);
					
					codeSystems.add(csResult);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Could not execute query against active database.", e);
		}
		finally
		{
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		
		return codeSystems;
	}
	
	public <T extends ValueSetCodeModel> Set<String> fetchValueSetNamesByValueSet(Class<T> clazz, String valueSet)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT DISTINCT(valueSetName) AS valueSetName FROM " + clazz.getSimpleName() +
				" where valueSetId = :valueSetId");
		OObjectDatabaseTx dbConnection = null;
		List<ODocument> results = null;
		Set<String> valueSetNames = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSetId", valueSet);
		
		
		try 
		{
			dbConnection = this.getActiveDbConnection();
			results = dbConnection.command(query).execute(params);
			
			if (results != null)
			{
				valueSetNames = new TreeSet<String>();
				for (ODocument result : results)
				{
					//logger.debug("fieldNames " + result.fieldNames()[0] + " " + result.fieldNames()[1]);
					String valueSetName = result.field("valueSetName");
					
					valueSetNames.add(valueSetName);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Could not execute query against active database.", e);
		}
		finally
		{
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		
		return valueSetNames;
	}
	
	public <T extends ValueSetCodeModel> List<T> fetchByValueSetAndCode(Class<T> clazz, String valueSet, String code)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where valueSetId = :valueSetId AND code = :code");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new LinkedHashMap<String,Object> ();
		params.put("valueSetId", valueSet);
		params.put("code", code);
		
		
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
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return result;
	}
	
	public <T extends ValueSetCodeModel> List<T> fetchByValueSetAndDescription(Class<T> clazz, String valueSet, String description)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where valueSetId = :valueSetId AND displayName = :displayName");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new LinkedHashMap<String,Object> ();
		params.put("valueSetId", valueSet);
		params.put("displayName", description);
		
		
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
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return result;
	}
	
	public <T extends ValueSetCodeModel> boolean valueSetExists(Class<T> clazz, String valueSet)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT DISTINCT(valueSetId) AS valueSetId FROM "+clazz.getSimpleName()+" where valueSetId = :valueSetId");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new HashMap<String,Object> ();
		params.put("valueSetId", valueSet);
		
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
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return (result != null && result.size() > 0);
	}
	
	public <T extends ValueSetCodeModel> List<T> fetchByValueSetCodeSystemAndCode(Class<T> clazz, String valueSet, String codeSystem, String code)
	{
		OSQLSynchQuery <T> query = new OSQLSynchQuery<T>("SELECT * FROM " + clazz.getSimpleName() +
			" where valueSetId = :valueSetId AND codeSystemId = :codeSystemId AND code = :code");
		OObjectDatabaseTx dbConnection = null;
		List<T> result = null;
		
		Map<String, Object> params = new LinkedHashMap<String,Object> ();
		params.put("valueSetId", valueSet);
		params.put("codeSystemId", codeSystem);
		params.put("code", code);
		
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
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return result;
	}
	
	public Map<String, CodeModelDefinition<?>> getCodeModelDefinitions() {
		return this.codeModelDefinitions;
	}

	public Map<String, ValueSetModelDefinition<?>> getValueSetModelDefinitions() {
		return this.valueSetModelDefinitions;
	}

	public Map<String, VocabularyLoader<?>> getCodeLoaders() {
		return this.codeLoaders;
	}

	public void setCodeLoaders(Map<String, VocabularyLoader<?>> codeLoaders) {
		this.codeLoaders = codeLoaders;
	}

	public Map<String, VocabularyLoader<? extends ValueSetCodeModel>> getValueSetCodeLoaders() {
		return this.valueSetCodeLoaders;
	}

	public void setValueSetCodeLoaders(
		Map<String, VocabularyLoader<? extends ValueSetCodeModel>> valueSetCodeLoaders) {
		this.valueSetCodeLoaders = valueSetCodeLoaders;
	}
}
