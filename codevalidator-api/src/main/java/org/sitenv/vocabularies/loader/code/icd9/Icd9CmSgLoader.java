package org.sitenv.vocabularies.loader.code.icd9;

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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.code.CodeLoader;
import org.sitenv.vocabularies.loader.code.CodeLoaderManager;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;
import org.sitenv.vocabularies.model.impl.Icd9CmSgModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd9CmSgLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(Icd9CmSgLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD9CM_PROCEDURE_CODE_NAME, Icd9CmSgLoader.class);
		logger.info("Loaded: " + VocabularyConstants.ICD9CM_PROCEDURE_CODE_NAME + "(" + VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM + ")");
			
		VocabularyModelDefinition icd9CmSg = new VocabularyModelDefinition(Icd9CmSgModel.class, VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM);
			
		VocabularyRepository.getInstance().getVocabularyMap().put(VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM, icd9CmSg);
		
	}
	
	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating Icd9CmSgModel Datastore...");
			VocabularyRepository.truncateModel(dbConnection, Icd9CmSgModel.class);
			logger.info(dbConnection.getName() + ".Icd9CmSgModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, Icd9CmSgModel.class));

			VocabularyRepository.updateIndexProperties(dbConnection, Icd9CmSgModel.class, true);
		
			String insertQueryPrefix = "insert into " + Icd9CmSgModel.class.getSimpleName() + " (codeIndex, displayNameIndex, code, displayName) values ";
			
			StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
			insertQueryBuilder.ensureCapacity(1000);
			
			int totalCount = 0, pendingCount = 0;
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading ICD9CM_SG File: " + file.getName());
	
					int count = 0;

					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						if ((count++ == 0) || (available.isEmpty())) {
							continue; // skip header row
						} else {

							String[] line = StringUtils.splitPreserveAllTokens(available, "\t", 2);
							
							if (pendingCount++ > 0) {
								insertQueryBuilder.append(",");
							}
							
							insertQueryBuilder.append("(\"");
							insertQueryBuilder.append(OIOUtils.encode(line[0].trim().toUpperCase()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(line[1].trim().toUpperCase()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(line[0].trim()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(line[1].trim()));
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
			}
			
			if (pendingCount > 0) {
				dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
				dbConnection.commit();
			}
			
			logger.info("Icd9CmSgModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, Icd9CmSgModel.class));
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
		return VocabularyConstants.ICD9CM_PROCEDURE_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM;
	}

}
