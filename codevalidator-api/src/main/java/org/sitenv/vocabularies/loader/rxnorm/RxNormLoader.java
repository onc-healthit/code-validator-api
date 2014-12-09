package org.sitenv.vocabularies.loader.rxnorm;

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
import org.sitenv.vocabularies.model.impl.LoincModel;
import org.sitenv.vocabularies.model.impl.RxNormModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class RxNormLoader implements Loader {

	private static Logger logger = Logger.getLogger(RxNormLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.RXNORM_CODE_NAME, RxNormLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.RXNORM_CODE_NAME + "(" + VocabularyConstants.RXNORM_CODE_SYSTEM + ")");
	}

	public VocabularyModelDefinition load(File file) {
		VocabularyModelDefinition rxNorm = new VocabularyModelDefinition(RxNormModel.class, this.getCodeSystem());

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		
		logger.debug("Loading RXNORM File: " + file.getName());

		BufferedReader br = null;
		
		try {
			logger.info("Truncating RxNormModel Datastore...");
			
			VocabularyRepository.truncateModel(dbConnection, RxNormModel.class);
			
			logger.info(dbConnection.getName() + ".RxNormModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, RxNormModel.class));
			


			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				
				String[] line = available.split("\\|");
				
				RxNormModel model = dbConnection.newInstance(RxNormModel.class);;
				model.setCode(line[0].toUpperCase());
				model.setDisplayName(line[14].toUpperCase());
				
				dbConnection.save(model);


			}
			VocabularyRepository.updateIndexProperties(dbConnection, RxNormModel.class);
			
			
			logger.info("RxNormModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, RxNormModel.class));
			
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

		return rxNorm;
	}
	
	public String getCodeName() {
		return VocabularyConstants.RXNORM_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.RXNORM_CODE_SYSTEM;
	}
}
