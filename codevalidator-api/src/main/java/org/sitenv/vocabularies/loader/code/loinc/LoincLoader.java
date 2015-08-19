package org.sitenv.vocabularies.loader.code.loinc;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
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
import org.sitenv.vocabularies.model.impl.Icd10PcsModel;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;
import org.sitenv.vocabularies.model.impl.LoincModel;
import org.sitenv.vocabularies.model.impl.LoincModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class LoincLoader implements CodeLoader {

	private static Logger logger = Logger.getLogger(LoincLoader.class);
	

	static {
		CodeLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.LOINC_CODE_NAME, LoincLoader.class);
		logger.info("Loaded: " + VocabularyConstants.LOINC_CODE_NAME + "(" + VocabularyConstants.LOINC_CODE_SYSTEM + ")");
			
		VocabularyModelDefinition loinc = new VocabularyModelDefinition(LoincModel.class, VocabularyConstants.LOINC_CODE_SYSTEM);
			
		VocabularyRepository.getInstance().getVocabularyMap().put(VocabularyConstants.LOINC_CODE_SYSTEM, loinc);
		
	}
	
	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		BufferedReader br = null;
		
		try {
			
			logger.info("Truncating LoincModel Datastore...");
			VocabularyRepository.truncateModel(dbConnection, LoincModel.class);
			logger.info(dbConnection.getName() + ".LoincModel Datastore Truncated... records remaining: " + VocabularyRepository.getRecordCount(dbConnection, LoincModel.class));

			VocabularyRepository.updateIndexProperties(dbConnection, LoincModel.class, true);
			
			String insertQueryPrefix = "insert into " + LoincModel.class.getSimpleName() + " (codeIndex, displayNameIndex, code, displayName) values ";
			
			StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
			insertQueryBuilder.ensureCapacity(1000);
			
			int totalCount = 0, pendingCount = 0;
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading LOINC File: " + file.getName());
	
					int count = 0;

					br = new BufferedReader(new FileReader(file));
					String available;
					while ((available = br.readLine()) != null) {
						if (count++ == 0) {
							continue; // skip header row
						} else {

							String[] line = StringUtils.splitPreserveAllTokens(available, ",", 3);
							
							if (pendingCount++ > 0) {
								insertQueryBuilder.append(",");
							}
							
							insertQueryBuilder.append("(\"");
							insertQueryBuilder.append(OIOUtils.encode(StringUtils.strip(line[0], "\"").toUpperCase()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(StringUtils.strip(line[1], "\"").toUpperCase()));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(StringUtils.strip(line[0], "\"")));
							insertQueryBuilder.append("\",\"");
							insertQueryBuilder.append(OIOUtils.encode(StringUtils.strip(line[1], "\"")));
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
			
			logger.info("LoincModel Loading complete... records existing: " + VocabularyRepository.getRecordCount(dbConnection, LoincModel.class));
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
		return VocabularyConstants.LOINC_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.LOINC_CODE_SYSTEM;
	}
}
