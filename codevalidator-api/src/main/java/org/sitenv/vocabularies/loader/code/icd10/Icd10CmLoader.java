package org.sitenv.vocabularies.loader.code.icd10;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.code.CodeLoader;
import org.sitenv.vocabularies.loader.code.CodeLoaderManager;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;
import org.sitenv.vocabularies.model.impl.Icd9CmSgModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd10CmLoader  implements CodeLoader {

	private static Logger logger = Logger.getLogger(Icd10CmLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD10CM_CODE_NAME, Icd10CmLoader.class);
		logger.info("Loaded: " + VocabularyConstants.ICD10CM_CODE_NAME + "(" + VocabularyConstants.ICD10CM_CODE_SYSTEM + ")");
			
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

			VocabularyRepository.updateIndexProperties(dbConnection, Icd10CmModel.class, true);
		
			String insertQueryPrefix = "insert into " + Icd10CmModel.class.getSimpleName() + " (codeIndex, displayNameIndex, code, displayName) values ";
			
			StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
			insertQueryBuilder.ensureCapacity(1000);
			
			int totalCount = 0, pendingCount = 0;
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading ICD10CM File: " + file.getName());
	
					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						
						if (pendingCount++ > 0) {
							insertQueryBuilder.append(",");
						}
						
						insertQueryBuilder.append("(\"");
						insertQueryBuilder.append(OIOUtils.encode(available.substring(6, 13).trim().toUpperCase()));
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(OIOUtils.encode(available.substring(77).trim().toUpperCase()));
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(OIOUtils.encode(available.substring(6, 13).trim()));
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(OIOUtils.encode(available.substring(77).trim()));
						insertQueryBuilder.append("\")");
						
						if ((totalCount % 5000) == 0) {
							dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
							dbConnection.commit();
							
							insertQueryBuilder.clear();
							insertQueryBuilder.append(insertQueryPrefix);
							
							pendingCount = 0;
						}
					}
						
				}
			}
			
			if (pendingCount > 0) {
				dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
				dbConnection.commit();
			}
			
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
			
				dbConnection.declareIntent(null);
				
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
