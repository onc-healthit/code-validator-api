package org.sitenv.vocabularies.loader.icd9;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.Vocabulary;
import org.sitenv.vocabularies.data.VocabularyDataStore;
import org.sitenv.vocabularies.loader.Loader;
import org.sitenv.vocabularies.loader.LoaderManager;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;
import org.sitenv.vocabularies.model.impl.Icd9CmSgModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd9CmSgLoader implements Loader {

	private static Logger logger = Logger.getLogger(Icd9CmSgLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD9CM_PROCEDURE_CODE_NAME, Icd9CmSgLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD9CM_PROCEDURE_CODE_NAME + "(" + VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file, OObjectDatabaseTx dbConnection) {
		Vocabulary icd9CmSg = new Vocabulary(Icd9CmSgModel.class, this.getCodeSystem());

		logger.debug("Loading ICD9CM_SG File: " + file.getName());

		BufferedReader br = null;
		
		try {
			logger.info("Truncating Icd9CmSgModel Datastore...");
			
			VocabularyDataStore.truncateModel(dbConnection, Icd9CmSgModel.class);
			
			logger.info("Icd9CmSgModel Datastore Truncated... records remaining: " + VocabularyDataStore.getRecordCount(dbConnection, Icd9CmSgModel.class));
			
			
			int count = 0;

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				if (count++ == 0) {
					continue; // skip header row
				} else {

					String[] line = available.split("\t");
					
					Icd9CmSgModel model = dbConnection.newInstance(Icd9CmSgModel.class);;
					model.setCode(line[0].toUpperCase());
					model.setDisplayName(line[1].toUpperCase());
					
					dbConnection.save(model);
				}


			}
			

			VocabularyDataStore.updateIndexProperties(dbConnection, Icd9CmSgModel.class);
			
			
			logger.info("Icd9CmSgModel Loading complete... records existing: " + VocabularyDataStore.getRecordCount(dbConnection, Icd9CmSgModel.class));
			
			
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

		return icd9CmSg;
	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD9CM_PROCEDURE_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM;
	}

}
