package org.sitenv.vocabularies.loader.valueset.vsac;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoader;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoaderManager;
import org.sitenv.vocabularies.model.ValueSetModel;
import org.sitenv.vocabularies.model.ValueSetModelDefinition;
import org.sitenv.vocabularies.model.impl.LoincModel;
import org.sitenv.vocabularies.model.impl.VsacValueSetModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class VsacLoader implements ValueSetLoader {

	private static Logger logger = Logger.getLogger(VsacLoader.class);
	

	static {
		ValueSetLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.VSAC_VALUESET_NAME, VsacLoader.class);
		logger.info("Loaded: " + VocabularyConstants.VSAC_VALUESET_NAME + " (value set)");
		
		VocabularyRepository.getInstance().getValueSetModelClassList().add(VsacValueSetModel.class);
		
	}
	

	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		HSSFWorkbook workBook = null;
		
		try {
			
			logger.info("Truncating VsacValueSetModel Datastore...");
			VocabularyRepository.truncateValueSetModel(dbConnection, VsacValueSetModel.class);
			logger.info(dbConnection.getName() + ".VsacValueSetModel Datastore Truncated... records remaining: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));

			VocabularyRepository.updateValueSetIndexProperties(dbConnection, VsacValueSetModel.class, true);
		
			String insertQueryPrefix = "insert into " + VsacValueSetModel.class.getSimpleName() +
				" (valueSetIndex, valueSetNameIndex, codeSystemIndex, codeIndex, descriptionIndex, code, codeSystem, codeSystemName, codeSystemVersion, description, definitionVersion, steward, tty, type, valueSet, valueSetName) values ";
			
			StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
			insertQueryBuilder.ensureCapacity(1000);
			
			int totalCount = 0, pendingCount = 0;
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
			for (File file : filesToLoad)
			{
				if (file.isFile() && !file.isHidden())
				{
					
					logger.debug("Loading Value Set File: " + file.getName());

					InputStream inputStream = null;

				
					inputStream = new FileInputStream(file);
				

					POIFSFileSystem fileSystem = null;

					
					fileSystem = new POIFSFileSystem(inputStream);

					workBook = new HSSFWorkbook(fileSystem);
					HSSFSheet sheet = workBook.getSheet("Code List");
						
						
					String valueSetName = ((String) OIOUtils.encode(sheet.getRow(1).getCell(1).getStringCellValue()));
					String oid = ((String) OIOUtils.encode(sheet.getRow(2).getCell(1).getStringCellValue()));
					String type = ((String) OIOUtils.encode(sheet.getRow(3).getCell(1).getStringCellValue()));
					String version = ((String) OIOUtils.encode(sheet.getRow(4).getCell(1).getStringCellValue()));
					String steward = ((String) OIOUtils.encode(sheet.getRow(5).getCell(1).getStringCellValue()));
					
					String valueSetIndex = oid.toUpperCase();
					String valueSetNameIndex = valueSetName.toUpperCase();
				
					
					for (int count = 11; count <= sheet.getLastRowNum(); count++)
					{
						if (pendingCount++ > 0) {
							insertQueryBuilder.append(",");
						}
						
						String code = (String) OIOUtils.encode(sheet.getRow(count).getCell(0).getStringCellValue());
						String description = (String) OIOUtils.encode(sheet.getRow(count).getCell(1).getStringCellValue());
						String codeSystemName = (String) OIOUtils.encode(sheet.getRow(count).getCell(2).getStringCellValue());
						String codeSystemVersion = (String) OIOUtils.encode(sheet.getRow(count).getCell(3).getStringCellValue());
						String codeSystem = (String) OIOUtils.encode(sheet.getRow(count).getCell(4).getStringCellValue());
						String tty = (String) OIOUtils.encode(sheet.getRow(count).getCell(5).getStringCellValue());
						
						insertQueryBuilder.append("(\"");
						
						insertQueryBuilder.append(valueSetIndex);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(valueSetNameIndex);
						insertQueryBuilder.append("\",\"");

						insertQueryBuilder.append(codeSystem.toUpperCase());
						insertQueryBuilder.append("\",\"");
						

						insertQueryBuilder.append(code.toUpperCase());
						insertQueryBuilder.append("\",\"");
						

						insertQueryBuilder.append(description.toUpperCase());
						insertQueryBuilder.append("\",\"");
						
						insertQueryBuilder.append(code);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(codeSystem);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(codeSystemName);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(codeSystemVersion);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(description);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(version);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(steward);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(tty);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(type);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(oid);
						insertQueryBuilder.append("\",\"");
						insertQueryBuilder.append(valueSetName);
						insertQueryBuilder.append("\")");
						
						if ((totalCount % 5000) == 0) {
							dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
							dbConnection.commit();
							
							insertQueryBuilder.clear();
							insertQueryBuilder.append(insertQueryPrefix);
							
							pendingCount = 0;
						}
					}
					
					workBook.close();
					
						
				}
			}
			
				if (pendingCount > 0) {
					dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
					dbConnection.commit();
				}
			
				logger.info("VsacValueSetModel Loading complete... records existing: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));
			} catch (FileNotFoundException e) {
				
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			} finally {
				if (workBook != null) {
					try {
						workBook.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				
				dbConnection.declareIntent(null);
				
				Runtime r = Runtime.getRuntime();
				r.gc();
			}

	}
	
	public String getValueSetAuthorName() {
		return VocabularyConstants.VSAC_VALUESET_NAME;
	}

}
