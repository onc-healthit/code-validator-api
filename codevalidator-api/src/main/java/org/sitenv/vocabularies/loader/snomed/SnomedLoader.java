package org.sitenv.vocabularies.loader.snomed;

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
import org.sitenv.vocabularies.model.impl.RxNormModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class SnomedLoader implements Loader {

	private static Logger logger = Logger.getLogger(SnomedLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.SNOMEDCT_CODE_NAME, SnomedLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.SNOMEDCT_CODE_NAME + "(" + VocabularyConstants.SNOMEDCT_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file, OObjectDatabaseTx dbConnection) {
		Vocabulary snomed = new Vocabulary(SnomedModel.class, this.getCodeSystem());

		logger.debug("Loading SNOMED File: " + file.getName());

		BufferedReader br = null;
		
		try {
			logger.info("Truncating SnomedModel Datastore...");
			
			VocabularyDataStore.truncateModel(dbConnection, SnomedModel.class);
			
			logger.info("SnomedModel Datastore Truncated... records remaining: " + VocabularyDataStore.getRecordCount(dbConnection, SnomedModel.class));
			
			
			int count = 0;

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				if (count++ == 0) {
					continue; // skip header row
				} else {

					String[] line = available.split("\t");
					
					SnomedModel model = dbConnection.newInstance(SnomedModel.class);
					model.setCode(line[0].toUpperCase());
					model.setDisplayName(line[2].toUpperCase());
					
					dbConnection.save(model);
				}


			}
			VocabularyDataStore.updateIndexProperties(dbConnection, SnomedModel.class);
			
			
			logger.info("SnomedModel Loading complete... records existing: " + VocabularyDataStore.getRecordCount(dbConnection, SnomedModel.class));
			
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

		return snomed;
	}
	
	public String getCodeName() {
		return VocabularyConstants.SNOMEDCT_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.SNOMEDCT_CODE_SYSTEM;
	}
}
