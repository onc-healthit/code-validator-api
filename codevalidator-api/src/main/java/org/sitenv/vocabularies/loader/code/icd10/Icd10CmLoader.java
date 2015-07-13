package org.sitenv.vocabularies.loader.code.icd10;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.code.CodeLoader;
import org.sitenv.vocabularies.loader.code.CodeLoaderManager;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd10CmLoader  implements CodeLoader {

	private static Logger logger = Logger.getLogger(Icd10CmLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD10CM_CODE_NAME, Icd10CmLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD10CM_CODE_NAME + "(" + VocabularyConstants.ICD10CM_CODE_SYSTEM + ")");
		
		if (VocabularyRepository.getInstance().getVocabularyMap() == null)
		{
			VocabularyRepository.getInstance().setVocabularyMap(new HashMap<String,VocabularyModelDefinition>());
		}
			
		VocabularyModelDefinition icd10Cm = new VocabularyModelDefinition(Icd10CmModel.class, VocabularyConstants.ICD10CM_CODE_SYSTEM);
			
		VocabularyRepository.getInstance().getVocabularyMap().put(VocabularyConstants.ICD10CM_CODE_SYSTEM, icd10Cm);
		
	}

	
	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating Icd10CmModel Datastore...");
			VocabularyRepository.truncateModel(dbConnection, Icd10CmModel.class);
			logger.info(dbConnection.getName() + ".Icd10CmModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, Icd10CmModel.class));

		
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading ICD10CM File: " + file.getName());
	
					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						
						String code = available.substring(6, 13).trim();
						String displayName = available.substring(77).trim();
					
						Icd10CmModel model = dbConnection.newInstance(Icd10CmModel.class);;
						model.setCode(code);
						model.setDisplayName(displayName);
						
						dbConnection.save(model);
					}
						
				}
			}
			
			VocabularyRepository.updateIndexProperties(dbConnection, Icd10CmModel.class);
			
			
			logger.info("Icd10CmModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, Icd10CmModel.class));
			} catch (FileNotFoundException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				
				Runtime r = Runtime.getRuntime();
				r.gc();
			}
		

	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD10CM_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD10CM_CODE_SYSTEM;
	}

}
