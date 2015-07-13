package org.sitenv.vocabularies.loader.code.rxnorm;

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
import org.sitenv.vocabularies.model.impl.RxNormModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class RxNormLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(RxNormLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.RXNORM_CODE_NAME, RxNormLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.RXNORM_CODE_NAME + "(" + VocabularyConstants.RXNORM_CODE_SYSTEM + ")");
		

		if (VocabularyRepository.getInstance().getVocabularyMap() == null)
		{
			VocabularyRepository.getInstance().setVocabularyMap(new HashMap<String,VocabularyModelDefinition>());
		}
		
		VocabularyModelDefinition rxNorm = new VocabularyModelDefinition(RxNormModel.class, VocabularyConstants.RXNORM_CODE_SYSTEM);
			

		VocabularyRepository.getInstance().getVocabularyMap().put(VocabularyConstants.RXNORM_CODE_SYSTEM, rxNorm);
		
	}

	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating RxNormModel Datastore...");
			VocabularyRepository.truncateModel(dbConnection, RxNormModel.class);
			logger.info(dbConnection.getName() + ".RxNormModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, RxNormModel.class));

		
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading RXNORM File: " + file.getName());
	
					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						
						String[] line = available.split("\\|");
						
						RxNormModel model = dbConnection.newInstance(RxNormModel.class);;
						model.setCode(line[0].toUpperCase());
						model.setDisplayName(line[14].toUpperCase());
						
						dbConnection.save(model);


					}
					
				}
			}
			
			VocabularyRepository.updateIndexProperties(dbConnection, RxNormModel.class);
			
			logger.info("RxNormModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, RxNormModel.class));
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
		return VocabularyConstants.RXNORM_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.RXNORM_CODE_SYSTEM;
	}
}
