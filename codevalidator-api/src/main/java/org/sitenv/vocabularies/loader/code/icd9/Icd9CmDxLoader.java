package org.sitenv.vocabularies.loader.code.icd9;

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
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd9CmDxLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(Icd9CmDxLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME, Icd9CmDxLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME + "(" + VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM + ")");
		
		if (VocabularyRepository.getInstance().getVocabularyMap() == null)
		{
			VocabularyRepository.getInstance().setVocabularyMap(new HashMap<String,VocabularyModelDefinition>());
		}
		VocabularyModelDefinition icd9CmDx = new VocabularyModelDefinition(Icd9CmDxModel.class, VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM);
			
		VocabularyRepository.getInstance().getVocabularyMap().put(VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM, icd9CmDx);
		
	}
	
	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating Icd9CmDxModel Datastore...");
			VocabularyRepository.truncateModel(dbConnection, Icd9CmDxModel.class);
			logger.info(dbConnection.getName() + ".Icd9CmDxModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, Icd9CmDxModel.class));

		
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading ICD9CM_DX File: " + file.getName());
	
					int count = 0;

					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						if (count++ == 0) {
							continue; // skip header row
						} else {

							String[] line = available.split("\t");
							
							Icd9CmDxModel model = dbConnection.newInstance(Icd9CmDxModel.class);;
							
							model.setCode(line[0].toUpperCase());
							model.setDisplayName(line[1].toUpperCase());
							
							dbConnection.save(model);
						}


					}
					
				}
			}
			
			VocabularyRepository.updateIndexProperties(dbConnection, Icd9CmDxModel.class);
			
			logger.info("Icd9CmDxModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, Icd9CmDxModel.class));
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
		return VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM;
	}

}
