package org.sitenv.vocabularies.loader.code.snomed;

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
import org.sitenv.vocabularies.model.impl.SnomedModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class SnomedLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(SnomedLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.SNOMEDCT_CODE_NAME, SnomedLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.SNOMEDCT_CODE_NAME + "(" + VocabularyConstants.SNOMEDCT_CODE_SYSTEM + ")");
		

		if (VocabularyRepository.getInstance().getVocabularyMap() == null)
		{
			VocabularyRepository.getInstance().setVocabularyMap(new HashMap<String,VocabularyModelDefinition>());
		}
		
		VocabularyModelDefinition snomed = new VocabularyModelDefinition(SnomedModel.class, VocabularyConstants.SNOMEDCT_CODE_SYSTEM);
			

		VocabularyRepository.getInstance().getVocabularyMap().put(VocabularyConstants.SNOMEDCT_CODE_SYSTEM, snomed);
		
	}
	
	
public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating SnomedModel Datastore...");
			VocabularyRepository.truncateModel(dbConnection, SnomedModel.class);
			logger.info(dbConnection.getName() + ".SnomedModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, SnomedModel.class));

		
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading SNOMED File: " + file.getName());
	
					int count = 0;

					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						if (count++ == 0) {
							continue; // skip header row
						} else {

							String[] line = available.split("\t");
							
							SnomedModel model = dbConnection.newInstance(SnomedModel.class);
							model.setCode(line[4].toUpperCase());
							model.setDisplayName(line[2].toUpperCase());
							
							dbConnection.save(model);
						}


					}
					
				}
			}
			
			VocabularyRepository.updateIndexProperties(dbConnection, SnomedModel.class);
			
			logger.info("SnomedModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, SnomedModel.class));
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
		return VocabularyConstants.SNOMEDCT_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.SNOMEDCT_CODE_SYSTEM;
	}
}
