package org.sitenv.vocabularies.loader.valueset.vsac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.code.icd10.Icd10CmLoader;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoader;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoaderManager;
import org.sitenv.vocabularies.model.ValueSetModelDefinition;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;
import org.sitenv.vocabularies.model.impl.VsacValueSetModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class VsacLoader implements ValueSetLoader {

	private static Logger logger = Logger.getLogger(Icd10CmLoader.class);
	

	static {
		ValueSetLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.VSAC_VALUESET_NAME, VsacLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.VSAC_VALUESET_NAME + " (value set)");
		
		if (VocabularyRepository.getInstance().getValueSetMap() == null)
		{
			VocabularyRepository.getInstance().setValueSetMap(new HashMap<String,ValueSetModelDefinition>());
		}
			
		ValueSetModelDefinition vsacValueSet = new ValueSetModelDefinition(VsacValueSetModel.class, VocabularyConstants.VSAC_VALUESET_NAME);
			
		VocabularyRepository.getInstance().getValueSetMap().put(VocabularyConstants.VSAC_VALUESET_NAME, vsacValueSet);
		
	}

	public void load(List<File> filesToLoad) {
		

		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating VsacValueSetModel Datastore...");
			VocabularyRepository.truncateValueSetModel(dbConnection, VsacValueSetModel.class);
			logger.info(dbConnection.getName() + ".VsacValueSetModel Datastore Truncated... records remaining: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));

		
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading Value Set File: " + file.getName());
	
					
					// parse value set file:
					
					br = new BufferedReader(new FileReader(file));
					/*
					String available;
					while ((available = br.readLine()) != null) {
						
						String code = available.substring(6, 13).trim();
						String displayName = available.substring(77).trim();
					
						VsacValueSetModel model = dbConnection.newInstance(VsacValueSetModel.class);;
						model.setCode(code);
						model.setDisplayName(displayName);
						
						dbConnection.save(model);
					}
					*/
						
				}
			}
			
			VocabularyRepository.updateValueSetIndexProperties(dbConnection, VsacValueSetModel.class);
			
			
			logger.info("VsacValueSetModel Loading complete... records existing: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));
			} catch (FileNotFoundException e) {
				// TODO: log4j
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
	
	public String getValueSetAuthorName() {
		return VocabularyConstants.VSAC_VALUESET_NAME;
	}

}
