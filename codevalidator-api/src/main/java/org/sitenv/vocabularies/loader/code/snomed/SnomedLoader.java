package org.sitenv.vocabularies.loader.code.snomed;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.code.CodeLoader;
import org.sitenv.vocabularies.loader.code.CodeLoaderManager;
import org.sitenv.vocabularies.model.VocabularyModelDefinition;
import org.sitenv.vocabularies.model.impl.SnomedModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

public class SnomedLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(SnomedLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.SNOMEDCT_CODE_NAME, SnomedLoader.class);
		logger.info("Loaded: " + VocabularyConstants.SNOMEDCT_CODE_NAME + "(" + VocabularyConstants.SNOMEDCT_CODE_SYSTEM + ")");
		
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

			VocabularyRepository.updateIndexProperties(dbConnection, SnomedModel.class, true);
		
			String insertQueryPrefix = "insert into " + SnomedModel.class.getSimpleName() + " (codeIndex, displayNameIndex, code, displayName) values ";
			
			StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
			insertQueryBuilder.ensureCapacity(1000);
			
			int totalCount = 0, pendingCount = 0;
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
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

							String[] line = StringUtils.splitPreserveAllTokens(available, "\t", 9);
							
							if (pendingCount++ > 0) {
								insertQueryBuilder.append(",");
							}
							
							insertQueryBuilder.append("(\"");
							insertQueryBuilder.append(OIOUtils.encode(line[4].toUpperCase()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(line[7].toUpperCase()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(line[4]));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(line[7]));
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
			}
			
			if (pendingCount > 0) {
				dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
				dbConnection.commit();
			}
			
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
			
			dbConnection.declareIntent(null);
			
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
