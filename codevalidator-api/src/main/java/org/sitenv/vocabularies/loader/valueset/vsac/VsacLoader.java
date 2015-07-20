package org.sitenv.vocabularies.loader.valueset.vsac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoader;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoaderManager;
import org.sitenv.vocabularies.model.ValueSetModel;
import org.sitenv.vocabularies.model.ValueSetModelDefinition;
import org.sitenv.vocabularies.model.impl.VsacValueSetModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class VsacLoader implements ValueSetLoader {

	private static Logger logger = Logger.getLogger(VsacLoader.class);
	

	static {
		ValueSetLoaderManager.getInstance()
				.registerLoader(VocabularyConstants.VSAC_VALUESET_NAME, VsacLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.VSAC_VALUESET_NAME + " (value set)");
		
		
		if (VocabularyRepository.getInstance().getValueSetModelClassList() == null) 
		{
			VocabularyRepository.getInstance().setValueSetModelClassList(new ArrayList<Class<? extends ValueSetModel>>());
		}
		
		VocabularyRepository.getInstance().getValueSetModelClassList().add(VsacValueSetModel.class);
		
	}
	

	public void load(List<File> filesToLoad) {
		

		OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
		HSSFWorkbook workBook = null;
		
		try {
			
			logger.info("Truncating VsacValueSetModel Datastore...");
			VocabularyRepository.truncateValueSetModel(dbConnection, VsacValueSetModel.class);
			logger.info(dbConnection.getName() + ".VsacValueSetModel Datastore Truncated... records remaining: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));

		
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
						
						
					String valueSetName = sheet.getRow(1).getCell(1).getStringCellValue();
					String oid = sheet.getRow(2).getCell(1).getStringCellValue();
					String type = sheet.getRow(3).getCell(1).getStringCellValue();
					String version = sheet.getRow(4).getCell(1).getStringCellValue();
					String steward = sheet.getRow(5).getCell(1).getStringCellValue();
				
					
					for (int count = 11; count <= sheet.getLastRowNum(); count++)
					{
						String code = sheet.getRow(count).getCell(0).getStringCellValue();
						String description = sheet.getRow(count).getCell(1).getStringCellValue();
						String codeSystem = sheet.getRow(count).getCell(2).getStringCellValue();
						String codeSystemVersion = sheet.getRow(count).getCell(3).getStringCellValue();
						String codeSystemOid = sheet.getRow(count).getCell(4).getStringCellValue();
						String tty = sheet.getRow(count).getCell(5).getStringCellValue();
						
						//System.out.println(code+":"+description+":"+codeSystem+":"+codeSystemVersion+":"+codeSystemOid+":"+tty);
						
						VsacValueSetModel model = dbConnection.newInstance(VsacValueSetModel.class);
						model.setCode(code.toUpperCase());
						model.setCodeSystem(codeSystemOid.toUpperCase());
						model.setCodeSystemName(codeSystem.toUpperCase());
						model.setCodeSystemVersion(codeSystemVersion);
						model.setDescription(description);
						model.setDefinitionVersion(version);
						model.setSteward(steward);
						model.setTty(tty);
						model.setType(type);
						model.setValueSet(oid.toUpperCase());
						model.setValueSetName(valueSetName);
						
						dbConnection.save(model);
					}
					
					workBook.close();
					
						
				}
			}
			
				VocabularyRepository.updateValueSetIndexProperties(dbConnection, VsacValueSetModel.class);
			
			
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
				Runtime r = Runtime.getRuntime();
				r.gc();
			}

	}
	
	public String getValueSetAuthorName() {
		return VocabularyConstants.VSAC_VALUESET_NAME;
	}

}
