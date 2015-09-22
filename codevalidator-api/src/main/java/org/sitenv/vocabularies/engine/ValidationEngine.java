package org.sitenv.vocabularies.engine;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.CodeSystemResult;
import org.sitenv.vocabularies.data.CodeValidationResult;
import org.sitenv.vocabularies.data.DisplayNameValidationResult;
import org.sitenv.vocabularies.data.ValueSetValidationResult;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.model.CodeModelDefinition;
import org.sitenv.vocabularies.model.ValueSetCodeModel;
import org.sitenv.vocabularies.model.ValueSetModelDefinition;
import org.sitenv.vocabularies.repository.VocabularyRepository;
import org.sitenv.vocabularies.watchdog.RepositoryWatchdog;

public abstract class ValidationEngine {
	
	private static Logger logger = Logger.getLogger(ValidationEngine.class);
	private static RepositoryWatchdog codeWatchdog = null;
	private static RepositoryWatchdog valueSetWatchdog = null;
	
	public static RepositoryWatchdog getCodeWatchdogThread()
	{
		return codeWatchdog;
	}
	
	public static RepositoryWatchdog getValueSetWatchdogThread()
	{
		return valueSetWatchdog;
	}
	
	public static boolean isCodeSystemLoaded(String codeSystem) {
		return VocabularyRepository.getInstance().getCodeModelDefinitions().containsKey(codeSystem);
	}
	
	public static boolean isValueSetLoaded(String valueSet)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		
		for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
		{
			if (ds.valueSetExists(valueSetModelDefinition.getModelClass(), valueSet))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static DisplayNameValidationResult validateCodeSystem(String codeSystemName, String displayName, String code) {
		String codeSystem = VocabularyConstants.CODE_SYSTEM_NAMES.getKey(codeSystemName);
		DisplayNameValidationResult result = null;
		
		if (codeSystem != null)
		{
			result = validateDisplayNameForCode(codeSystem, displayName, code);
		}
		
		return result;
	}
	
	public static DisplayNameValidationResult validateDisplayNameForCodeByCodeSystemName(String codeSystemName, String displayName, String code) {
		String codeSystem = VocabularyConstants.CODE_SYSTEM_NAMES.getKey(codeSystemName);
		DisplayNameValidationResult result = null;
		
		if (codeSystem != null)
		{
			result = validateDisplayNameForCode(codeSystem, displayName, code);
		}
		
		return result;
	}
	
	public static DisplayNameValidationResult validateDisplayNameForCode(String codeSystem, String displayName, String code) {
		VocabularyRepository ds = VocabularyRepository.getInstance();
		DisplayNameValidationResult result = null;
		
		if (codeSystem != null && code != null &&  ds != null) {
	
			
			result = new DisplayNameValidationResult();
			result.setCode(code);
			result.setAnticipatedDisplayName(displayName);
			result.setActualDisplayName(new ArrayList<String>());
			List<? extends CodeModel> results = getCode(codeSystem, code);
			
			result.setResult(false);
			
			for(CodeModel instance : results)
			{
				
				result.getActualDisplayName().add(instance.getDisplayName());
				if (instance.getDisplayName() != null && instance.getDisplayName().equalsIgnoreCase(displayName))
				{
					// we found a match for the code where the display name matches
					result.setResult(true);
				}
				
			}
			
		}
		
		return result;
	}
	
	public static boolean validateCodeByCodeSystemName(String codeSystemName, String code)
	{
		String codeSystem = VocabularyConstants.CODE_SYSTEM_NAMES.getKey(codeSystemName);
		
		if (codeSystem != null)
		{
			return validateCode(codeSystem, code);
		}
		
		return false;
	}

	public static boolean validateCode(String codeSystem, String code)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		
		if (codeSystem != null && code != null &&  ds != null) {
			
			List<? extends CodeModel> results = getCode(codeSystem, code);
			
			if (results != null && results.size() > 0)
			{
				return true; // instance of code found
			}
			
		}
		
		return false;
	}
	
	public static boolean validateDisplayNameByCodeSystemName(String codeSystemName, String displayName)
	{
		String codeSystem = VocabularyConstants.CODE_SYSTEM_NAMES.getKey(codeSystemName);
		
		if (codeSystem != null)
		{
			return validateDisplayName(codeSystem, displayName);
		}
		
		return false;
	}
	
	public static boolean validateDisplayName(String codeSystem, String displayName)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		
		if (codeSystem != null && displayName != null &&  ds != null) {
			
			List<? extends CodeModel> results = getDisplayName(codeSystem, displayName);
			
			
			if (results != null && results.size() > 0)
			{
				return true; // instance of code found
			}
			
		}
		
		return false;
	}
	
	public static CodeValidationResult validateCode(String codeSystem, String codeSystemName, String code, String displayName)
	{	
		CodeValidationResult result = new CodeValidationResult();
		
		result.setRequestedCode(code);
		result.setRequestedCodeSystemName(codeSystemName);
		result.setRequestedCodeSystemOid(codeSystem);
		result.setRequestedDisplayName(displayName);
		
		// Code System comparisons
		if (codeSystem == null)
		{
			codeSystem = VocabularyConstants.CODE_SYSTEM_NAMES.getKey(codeSystemName);
			result.getExpectedOidsForCodeSystemName().add(codeSystem);
		}
		else
		{
			if (codeSystemName != null)
			{
				if (codeSystem.equalsIgnoreCase(VocabularyConstants.CODE_SYSTEM_NAMES.getKey(codeSystemName)))
				{
					result.setCodeSystemAndNameMatch(true);
					result.getExpectedOidsForCodeSystemName().add(codeSystem);
					result.getExpectedCodeSystemNamesForOid().add(VocabularyConstants.CODE_SYSTEM_NAMES.get(codeSystem));
				}
			}
		}
		
		List<? extends CodeModel> codeModels = getCode(codeSystem, code);
		
		if (codeModels != null && codeModels.size() > 0)
		{
			result.setCodeExistsInCodeSystem(true);
			
			for (CodeModel model : codeModels)
			{
				result.getExpectedDisplayNamesForCode().add(model.getDisplayName());
				
				// case sensitive compare of displayName
				if (displayName != null && model.getDisplayName() != null && model.getDisplayName().equals(displayName))
				{
					result.setDisplayNameExistsForCode(true);
				}
			}
		}
		
		List<? extends CodeModel> displayNameModels = getCode(codeSystem, code);
		
		if (displayNameModels != null && displayNameModels.size() > 0)
		{
			result.setDisplayNameExistsInCodeSystem(true);
			
			for (CodeModel model : displayNameModels)
			{
				result.getExpectedCodesForDisplayName().add(model.getCode());
			}
		}
		
		
		return result;
		
	}
	
	public static ValueSetValidationResult validateValueSetCode (String valueSet, String codeSystem, String codeSystemName, String code, String description)
	{
		ValueSetValidationResult result = new ValueSetValidationResult();
		
		
		result.setRequestedCode(code);
		result.setRequestedCodeSystemName(codeSystemName);
		result.setRequestedCodeSystemOid(codeSystem);
		result.setRequestedDescription(description);
		result.setRequestedValueSetOid(valueSet);
		
		Set<String> valueSetNames = getValueSetNames(valueSet);
		
		result.getValueSetNames().addAll(valueSetNames);
		
		List<? extends ValueSetCodeModel> codeModels = getValueSetCode(valueSet, code);
		
		if (codeModels != null && codeModels.size() > 0)
		{
			result.setCodeExistsInValueSet(true);
			
			for (ValueSetCodeModel model : codeModels)
			{
				result.getExpectedDescriptionsForCode().add(model.getDisplayName());
				
				result.getExpectedCodeSystemsForCode().add(model.getCodeSystemId());
				
				// case sensitive compare of displayName
				if (description != null && model.getDisplayName() != null && model.getDisplayName().equals(description))
				{
					result.setDescriptionMatchesCode(true);
				}
				
				if (codeSystem != null && model.getCodeSystemId() != null && model.getCodeSystemId().equals(codeSystem))
				{
					result.setCodeExistsInCodeSystem(true);
				}
				
			}
		}
		
		List<? extends ValueSetCodeModel> descriptionModels = getValueSetDescription(valueSet, description);
		
		if (descriptionModels != null && descriptionModels.size() > 0)
		{
			result.setDescriptionExistsInValueSet(true);
			
			for (ValueSetCodeModel model : descriptionModels)
			{
				result.getExpectedCodesForDescription().add(model.getCode());
				
				if (codeSystem != null && model.getCodeSystemId() != null && model.getCodeSystemId().equalsIgnoreCase(codeSystem))
				{
					result.setDescriptionExistsInCodeSystem(true);
				}
			}
		}
		
		List<CodeSystemResult> codeSystemModels  = getValueSetCodeSystems(valueSet);
		
		if (codeSystemModels != null && codeSystemModels.size() > 0)
		{
			for (CodeSystemResult system : codeSystemModels)
			{
				result.getExpectedCodeSystemsForValueSet().add(system.getCodeSystem());
				
				if (codeSystem != null && system.getCodeSystem() != null && system.getCodeSystem().equalsIgnoreCase(codeSystem))
				{
					result.setCodeSystemExistsInValueSet(true);
					result.getExpectedCodeSystemNamesForOid().add(system.getCodeSystemName());
				}
				
				if (codeSystemName != null && system.getCodeSystemName() != null && system.getCodeSystemName().equalsIgnoreCase(codeSystemName))
				{
					
					result.getExpectedOidsForCodeSystemName().add(system.getCodeSystem());
					
					if (codeSystem != null && system.getCodeSystem() != null && system.getCodeSystem().equalsIgnoreCase(codeSystem))
					{
						result.setCodeSystemAndNameMatch(true);
					}
				}
			}
		}
		
		return result;
	}
	
	private static List<ValueSetCodeModel> getValueSetCode(String valueSet, String code)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		List<ValueSetCodeModel> result = null;
		
		if (valueSet != null && code != null &&  ds != null) {
			
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
			{
				List<? extends ValueSetCodeModel> modelList = ds.fetchByValueSetAndCode(valueSetModelDefinition.getModelClass(), valueSet, code);
				
				if (modelList != null)
				{
					if (result == null)
					{
						result = new ArrayList<ValueSetCodeModel>();
					}
					
					result.addAll(modelList);
				}
			}
					
		}
		
		return result;
	}
	
	private static Set<String> getValueSetNames(String valueSet)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		Set<String> result = null;
		
		if (valueSet != null &&  ds != null) {
			
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
			{
				Set<String> modelList = ds.fetchValueSetNamesByValueSet(valueSetModelDefinition.getModelClass(), valueSet);
				
				if (modelList != null)
				{
					if (result == null)
					{
						result = new TreeSet<String>();
					}
					
					result.addAll(modelList);
				}
			}
					
		}
		
		return result;
	}
	
	private static List<ValueSetCodeModel> getValueSetDescription(String valueSet, String description)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		List<ValueSetCodeModel> result = null;
		
		if (valueSet != null && description != null &&  ds != null) {
			
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
			{
				List<? extends ValueSetCodeModel> modelList = ds.fetchByValueSetAndDescription(valueSetModelDefinition.getModelClass(), valueSet, description);
				
				if (modelList != null)
				{
					if (result == null)
					{
						result = new ArrayList<ValueSetCodeModel>();
					}
					
					result.addAll(modelList);
				}
			}
					
		}
		
		return result;
	}
	
	private static List<CodeSystemResult> getValueSetCodeSystems(String valueSet)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		List<CodeSystemResult> result = null;
		
		if (valueSet != null &&  ds != null) {
			
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
			{
				List<CodeSystemResult> modelList = ds.fetchCodeSystemsByValueSet(valueSetModelDefinition.getModelClass(), valueSet);
				
				if (modelList != null)
				{
					if (result == null)
					{
						result = new ArrayList<CodeSystemResult>();
					}
					
					result.addAll(modelList);
				}
			}
					
		}
		
		return result;
	}
	
	private static List<? extends CodeModel> getCode(String codeSystem, String code)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		List<? extends CodeModel> results = null;
		
		if (codeSystem != null && code != null &&  ds != null) {
			CodeModelDefinition<?> vocab = ds.getCodeModelDefinitions().get(codeSystem);
			
			if (vocab != null) {
				results = ds.fetchByCode(vocab.getModelClass(), code);
			}
		}
		
		return results;
	}
	
	private static List<? extends CodeModel> getDisplayName(String codeSystem, String displayName)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		List<? extends CodeModel> results = null;
		
		if (codeSystem != null && displayName != null &&  ds != null) {
			CodeModelDefinition<?> vocab = ds.getCodeModelDefinitions().get(codeSystem);
			
			if (vocab != null) {
				results = ds.fetchByDisplayName(vocab.getModelClass(), displayName);
			}
		}
		
		return results;
	}
		
	
	public static boolean validateValueSetCode(String valueSet, String code)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		
		if (valueSet != null && code != null &&  ds != null) {
			
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
			{
				List<? extends ValueSetCodeModel> modelList = ds.fetchByValueSetAndCode(valueSetModelDefinition.getModelClass(), valueSet, code);
				
				if (modelList != null && modelList.size() > 0)
				{
					return true;
				}
			}
					
		}
		
		return false;
	}
	
	public static boolean validateValueSetCodeForCodeSystem(String valueSet, String code, String codeSystem)
	{
		VocabularyRepository ds = VocabularyRepository.getInstance();
		
		if (valueSet != null && code != null &&  ds != null) {
			
			
			for (ValueSetModelDefinition<?> valueSetModelDefinition : ds.getValueSetModelDefinitions().values())
			{
				List<? extends ValueSetCodeModel> modelList = ds.fetchByValueSetCodeSystemAndCode(valueSetModelDefinition.getModelClass(), valueSet, codeSystem,
					code);
				
				if (modelList != null && modelList.size() > 0)
				{
					return true;
				}
			}
					
		}
		
		return false;
	}
	
	public static synchronized void initialize(String codeDirectory, String valueSetDirectory, boolean loadAtStartup) throws IOException {
		boolean recursive = true;
		
		// Validation Engine should load using the primary database (existing). This will kick off the loading of the secondary database and swap configs
		// Once the secondary dB is loaded, the watchdog thread will be initialized to monitor future changes.
		// Putting this initialization code in a separate thread will dramatically speed up the tomcat launch time
		InitializerThread initializer = new InitializerThread();
		
		initializer.setCodeDirectory(codeDirectory);
		initializer.setValueSetDirectory(valueSetDirectory);
		initializer.setRecursive(recursive);
		initializer.setLoadAtStartup(loadAtStartup);
		
		initializer.start();
	}

	public static <T extends CodeModel, U extends VocabularyLoader<? extends T>> void loadVocabularyDirectory(String vocabDirPath,
		final Map<String, U> vocabLoaders) throws Exception {
		File vocabDir = new File(vocabDirPath);
		
		if (!vocabDir.isDirectory()) {
			throw new IOException("Vocabulary directory path is not a directory: " + vocabDirPath);
		}
		
		File[] vocabModelDirs = vocabDir.listFiles(new FileFilter() {
			public boolean accept(File vocabSubDir) {
				return vocabSubDir.isDirectory() && !vocabSubDir.isHidden() && vocabLoaders.containsKey(vocabSubDir.getName());
			}
		});
		
		if (ArrayUtils.isEmpty(vocabModelDirs)) {
			return;
		}

		Arrays.sort(vocabModelDirs);
		
		File[] vocabModelFiles;
		
		for (File vocabModelDir : vocabModelDirs) {
			vocabModelFiles = vocabModelDir.listFiles(new FileFilter() {
				public boolean accept(File vocabModelFile) {
					return vocabModelFile.isFile() && !vocabModelFile.isHidden();
				}
			});
			
			Arrays.sort(vocabModelFiles);
			
			vocabLoaders.get(vocabModelDir.getName()).load(vocabModelFiles);
		}
	}
	
	public static void loadValueSetDirectory(String directory) throws Exception {
		loadVocabularyDirectory(directory, VocabularyRepository.getInstance().getValueSetCodeLoaders());
	}

	public static void loadCodeDirectory(String directory) throws Exception {
		loadVocabularyDirectory(directory, VocabularyRepository.getInstance().getCodeLoaders());
	}
	
	private static class InitializerThread extends Thread {
		
		private String codeDirectory = null;
		private String valueSetDirectory = null;
		private boolean recursive = true;
		private boolean loadAtStartup = false;
		
		
		



		public String getCodeDirectory() {
			return codeDirectory;
		}



		public void setCodeDirectory(String codeDirectory) {
			this.codeDirectory = codeDirectory;
		}



		public String getValueSetDirectory() {
			return valueSetDirectory;
		}



		public void setValueSetDirectory(String valueSetDirectory) {
			this.valueSetDirectory = valueSetDirectory;
		}



		public boolean isRecursive() {
			return recursive;
		}



		public void setRecursive(boolean recursive) {
			this.recursive = recursive;
		}
		
		



		public boolean isLoadAtStartup() {
			return loadAtStartup;
		}



		public void setLoadAtStartup(boolean loadAtStartup) {
			this.loadAtStartup = loadAtStartup;
		}



		@SuppressWarnings("unchecked")
		public void run() {
			
			
			try 
			{
				if (loadAtStartup)
				{
					VocabularyRepository.getInstance().initializeDb(false);
					
					if (codeDirectory != null && !codeDirectory.trim().equals(""))
					{
						logger.info("Loading vocabularies at: " + codeDirectory + "...");
						loadCodeDirectory(codeDirectory);
						logger.info("Vocabularies loaded...");
					}
					
					if (valueSetDirectory != null && !valueSetDirectory.trim().equals(""))
					{
						logger.info("Loading value sets at: " + valueSetDirectory + "...");
						loadValueSetDirectory(valueSetDirectory);
						logger.info("Value Sets loaded...");
					}	
						
					logger.info("Activating new Vocabularies Map...");
					VocabularyRepository.getInstance().toggleActiveDatabase();
					logger.info("New vocabulary Map Activated...");
					
					VocabularyRepository.getInstance().initializeDb(false);
					
					if (codeDirectory != null && !codeDirectory.trim().equals(""))
					{
						logger.info("Loading vocabularies to new inactive repository at: " + codeDirectory + "...");
						loadCodeDirectory(codeDirectory);
						logger.info("Vocabularies loaded...");
					}
					
					if (valueSetDirectory != null && !valueSetDirectory.trim().equals(""))
					{
						logger.info("Loading value sets to new inactive repository at: " + valueSetDirectory + "...");
						loadValueSetDirectory(valueSetDirectory);
						logger.info("Value Sets loaded...");
					}
				}
				
				VocabularyRepository.getInstance().registerModels(false);
				VocabularyRepository.getInstance().registerModels(true);
				
				// recommendation from cwatson: load files back in the primary so both db's are 
				
				logger.info("Starting Vocabulary Watchdog...");
				ValidationEngine.codeWatchdog = new RepositoryWatchdog(this.getCodeDirectory(), this.isRecursive(), false);
				ValidationEngine.codeWatchdog.start();
				logger.info("Vocabulary Watchdog started...");
				
				logger.info("Starting Value Set Watchdog...");
				ValidationEngine.valueSetWatchdog = new RepositoryWatchdog(this.getValueSetDirectory(), this.isRecursive(), false);
				ValidationEngine.valueSetWatchdog.start();
				logger.info("Vocabulary ValueSet started...");
			}
			catch (Exception e)
			{
				logger.error("Failed to load configured vocabulary directory.", e);
			}
		}
		
	}

}
