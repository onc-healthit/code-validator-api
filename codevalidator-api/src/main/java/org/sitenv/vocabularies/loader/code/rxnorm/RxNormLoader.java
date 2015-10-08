package org.sitenv.vocabularies.loader.code.rxnorm;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.code.CodeLoader;
import org.sitenv.vocabularies.loader.code.CodeLoaderManager;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;
import org.sitenv.vocabularies.model.impl.LoincModel;
import org.sitenv.vocabularies.model.impl.RxNormModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class RxNormLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(RxNormLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.RXNORM_CODE_NAME, RxNormLoader.class);
		logger.info("Loaded: " + VocabularyConstants.RXNORM_CODE_NAME + "(" + VocabularyConstants.RXNORM_CODE_SYSTEM + ")");
		
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

			VocabularyRepository.updateIndexProperties(dbConnection, RxNormModel.class, true);
		
			String insertQueryPrefix = "insert into " + RxNormModel.class.getSimpleName() + " (codeIndex, displayNameIndex, code, displayName) values ";
			
			StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
			insertQueryBuilder.ensureCapacity(1000);
			
			int totalCount = 0, pendingCount = 0;
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading RXNORM File: " + file.getName());
	
					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						
						String[] line = StringUtils.splitPreserveAllTokens(available, "|", 16);
						
						if (pendingCount++ > 0) {
							insertQueryBuilder.append(",");
						}
						
						insertQueryBuilder.append("(\"");
						insertQueryBuilder.append(OIOUtils.encode(line[0]));
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(OIOUtils.encode(line[14]));
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(OIOUtils.encode(line[0]));
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(OIOUtils.encode(line[14]));
						insertQueryBuilder.append("\")");
						
						if ((++totalCount % 5000) == 0) {
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
			
			dbConnection.commit();
			
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
			
			dbConnection.declareIntent(null);
			
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
