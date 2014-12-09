package org.sitenv.vocabularies.loader.icd10;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.Loader;
import org.sitenv.vocabularies.loader.LoaderManager;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd10CmLoader  implements Loader {

	private static Logger logger = Logger.getLogger(Icd10CmLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD10CM_CODE_NAME, Icd10CmLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD10CM_CODE_NAME + "(" + VocabularyConstants.ICD10CM_CODE_SYSTEM + ")");
	}

	public VocabularyModelDefinition load(File file) {
		VocabularyModelDefinition icd10Cm = new VocabularyModelDefinition(Icd10CmModel.class, this.getCodeSystem());

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		
		logger.debug("Loading ICD10CM File: " + file.getName());

		BufferedReader br = null;
		
		
		
		try {
			logger.info("Truncating Icd10CmModel Datastore...");
			
			VocabularyRepository.truncateModel(dbConnection, Icd10CmModel.class);
			
			logger.info(dbConnection.getName() + ".Icd10CmModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, Icd10CmModel.class));
			

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
			
			VocabularyRepository.updateIndexProperties(dbConnection, Icd10CmModel.class);
			
			
			logger.info("Icd10CmModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, Icd10CmModel.class));
		} catch (FileNotFoundException e) {
			// TODO: log4j
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Runtime r = Runtime.getRuntime();
			r.gc();
		}

		return icd10Cm;
	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD10CM_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD10CM_CODE_SYSTEM;
	}


}
