package org.sitenv.vocabularies.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.DisplayNameValidationResult;
import org.sitenv.vocabularies.data.Vocabulary;
import org.sitenv.vocabularies.data.VocabularyDataStore;
import org.sitenv.vocabularies.loader.Loader;
import org.sitenv.vocabularies.loader.LoaderManager;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.watchdog.RepositoryWatchdog;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public abstract class ValidationEngine {
	
	private static Logger logger = Logger.getLogger(ValidationEngine.class);
	
	public static boolean isCodeSystemLoaded(String codeSystem) {
		VocabularyDataStore ds = VocabularyDataStore.getInstance();
		Vocabulary vocabulary = null;
		if (codeSystem != null) {
			Map<String, Vocabulary> vocabMap = ds.getVocabularyMap();
			
			if (vocabMap != null) {
				vocabulary = vocabMap.get(codeSystem.toUpperCase());
			}
		}
		
		return (vocabulary != null);
	}
	
	public static DisplayNameValidationResult validateCodeSystem(String codeSystemName, String displayName, String code) {
		String codeSystem = VocabularyConstants.CODE_SYSTEM_MAP.get(codeSystemName.toUpperCase());
		DisplayNameValidationResult result = null;
		
		if (codeSystem != null)
		{
			result = validateDisplayNameForCode(codeSystem, displayName, code);
		}
		
		return result;
	}
	
	public static DisplayNameValidationResult validateDisplayNameForCodeByCodeSystemName(String codeSystemName, String displayName, String code) {
		String codeSystem = VocabularyConstants.CODE_SYSTEM_MAP.get(codeSystemName.toUpperCase());
		DisplayNameValidationResult result = null;
		
		if (codeSystem != null)
		{
			result = validateDisplayNameForCode(codeSystem, displayName, code);
		}
		
		return result;
	}
	
	public static DisplayNameValidationResult validateDisplayNameForCode(String codeSystem, String displayName, String code) {
		VocabularyDataStore ds = VocabularyDataStore.getInstance();
		
		if (codeSystem != null && code != null &&  ds != null && ds.getVocabularyMap() != null) {
			Map<String, Vocabulary> vocabMap = ds.getVocabularyMap();
			
			Vocabulary vocab = vocabMap.get(codeSystem.toUpperCase());
			
			List<? extends CodeModel> results = ds.fetchByCode(vocab.getClazz(), code);
			
			for(CodeModel instance : results)
			{
				DisplayNameValidationResult result = new DisplayNameValidationResult();
				result.setCode(code);
				result.setActualDisplayName(instance.getDisplayName());
				result.setAnticipatedDisplayName(displayName);
				if (instance.getDisplayName() != null && instance.getDisplayName().equalsIgnoreCase(displayName))
				{
					result.setResult(true);
				}
				else
				{
					result.setResult(false);
				}
				
				return result;
				
			}
			
		}
		
		return null;
	}
	
	public static boolean validateCodeByCodeSystemName(String codeSystemName, String code)
	{
		String codeSystem = VocabularyConstants.CODE_SYSTEM_MAP.get(codeSystemName.toUpperCase());
		
		if (codeSystem != null)
		{
			return validateCode(codeSystem, code);
		}
		
		return false;
	}

	public static synchronized boolean validateCode(String codeSystem, String code)
	{
		VocabularyDataStore ds = VocabularyDataStore.getInstance();
		
		if (codeSystem != null && code != null &&  ds != null && ds.getVocabularyMap() != null) {
			Map<String, Vocabulary> vocabMap = ds.getVocabularyMap();
			
			Vocabulary vocab = vocabMap.get(codeSystem.toUpperCase());
			
			List<? extends CodeModel> results = ds.fetchByCode(vocab.getClazz(), code);
			
			if (results != null && results.size() > 0)
			{
				return true; // instance of code found
			}
			
		}
		
		return false;
	}
	
	public static boolean validateDisplayNameByCodeSystemName(String codeSystemName, String displayName)
	{
		String codeSystem = VocabularyConstants.CODE_SYSTEM_MAP.get(codeSystemName.toUpperCase());
		
		if (codeSystem != null)
		{
			return validateDisplayName(codeSystem, displayName);
		}
		
		return false;
	}
	
	public static synchronized boolean validateDisplayName(String codeSystem, String displayName)
	{
		VocabularyDataStore ds = VocabularyDataStore.getInstance();
		
		if (codeSystem != null && displayName != null &&  ds != null && ds.getVocabularyMap() != null) {
			Map<String, Vocabulary> vocabMap = ds.getVocabularyMap();
			
			Vocabulary vocab = vocabMap.get(codeSystem.toUpperCase());
			
			List<? extends CodeModel> results = ds.fetchByDisplayName(vocab.getClazz(), displayName);
			
			if (results != null && results.size() > 0)
			{
				return true; // instance of code found
			}
			
		}
		
		return false;
	}
	
	public static RepositoryWatchdog initialize(String directory) throws IOException {
		boolean recursive = true;

		logger.info("Registering Loaders...");
		// register Loaders
		registerLoaders();
		logger.info("Loaders Registered...");
		
		
		// Get inactive repository (hopefully this opens a new connection)
		OObjectDatabaseTx dbConnection = VocabularyDataStore.getInstance().getInactiveDbConnection();
				
		
		logger.info("Starting Watchdog...");
		RepositoryWatchdog watchdog = new RepositoryWatchdog(directory, recursive);
		watchdog.start();
		logger.info("Watchdog started...");
		
		
		
		try 
		{
			logger.info("Loading vocabularies at: " + directory + "...");
			loadDirectory(directory, dbConnection);
			logger.info("Vocabularies loaded...");
		}
		catch (Exception e)
		{
			logger.error("Failed to load configured vocabulary directory.", e);
		}
		finally
		{
			dbConnection.close();
		}
		
		// TODO: Perform Validation/Verification, if needed
		
		logger.info("Activating new Vocabularies Map...");
		
		VocabularyDataStore.getInstance().toggleActiveDatabase();
		
		Runtime.getRuntime().gc();
		logger.info("New vocabulary Map Activated...");
		
		
		return watchdog;
	}
	
	private static void registerLoaders() {
		try {
			Class.forName("org.sitenv.vocabularies.loader.snomed.SnomedLoader");
			Class.forName("org.sitenv.vocabularies.loader.loinc.LoincLoader");
			Class.forName("org.sitenv.vocabularies.loader.rxnorm.RxNormLoader");
			Class.forName("org.sitenv.vocabularies.loader.icd9.Icd9CmDxLoader");
			Class.forName("org.sitenv.vocabularies.loader.icd9.Icd9CmSgLoader");
			Class.forName("org.sitenv.vocabularies.loader.icd10.Icd10CmLoader");
			Class.forName("org.sitenv.vocabularies.loader.icd10.Icd10PcsLoader");
		} catch (ClassNotFoundException e) {
			// TODO: log4j
			logger.error("Error Initializing Loaders", e);
		}
	}
	
	public static void loadDirectory(String directory, OObjectDatabaseTx dbConnection) throws IOException
	{
		File dir = new File(directory);
		
		if (dir.isFile())
		{
			logger.debug("Directory to Load is a file and not a directory");
			throw new IOException("Directory to Load is a file and not a directory");
		}
		else
		{
			
			File[] list = dir.listFiles();
			
			VocabularyDataStore.getInstance().setVocabularyMap(new HashMap<String,Vocabulary>());
			
			for (File file : list)
			{
				loadFiles(file, dbConnection);
			}
		}
	}
	
	private static void loadFiles(File directory, OObjectDatabaseTx dbConnection) throws IOException
	{
		if (directory.isDirectory() && !directory.isHidden()) 
		{
			File[] filesToLoad = directory.listFiles();
			String codeSystem = null;
			
			for (File loadFile : filesToLoad)
			{
				if (loadFile.isFile() && !loadFile.isHidden())
				{
					
					

					
					logger.debug("Building Loader for directory: " + directory.getName() + "...");
					Loader loader = LoaderManager.getInstance().buildLoader(directory.getName());
					if (loader != null) {
						logger.debug("Loader built...");
					
						codeSystem = loader.getCodeSystem();
					
						logger.debug("Loading file: " + loadFile.getAbsolutePath() + "...");
						Vocabulary vocab = loader.load(loadFile, dbConnection);
						
						// TODO: Make this a passed in parameter:
						
						VocabularyDataStore.getInstance().getVocabularyMap().put(codeSystem.toUpperCase(), vocab);
						
						logger.debug("File loaded...");
					}
					else 
					{
						logger.debug("Building of Loader Failed.");
					}
					
				}
			}
			
		}
		
		

	}

}
