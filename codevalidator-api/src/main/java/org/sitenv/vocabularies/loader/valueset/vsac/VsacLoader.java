package org.sitenv.vocabularies.loader.valueset.vsac;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.sitenv.vocabularies.model.impl.VsacModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

public class VsacLoader extends VocabularyLoader<VsacModel> {
	public VsacLoader() {
		super(VsacModel.class, 5, 11);
	}

	@Override
	protected int loadFile(VocabularyRepository vocabRepo, OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields,
		Map<String, String> fields, File file) throws Exception {
		int fileCount = 0;
		HSSFWorkbook workbook = null;
		
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
			
			HSSFSheet sheet = workbook.getSheet("Code List");
			
			baseFields.clear();
			baseFields.put("valueSetId", sheet.getRow(2).getCell(1).getStringCellValue());
			baseFields.put("valueSetName", sheet.getRow(1).getCell(1).getStringCellValue());
			baseFields.put("valueSetVersion", sheet.getRow(4).getCell(1).getStringCellValue());
			baseFields.put("steward", sheet.getRow(5).getCell(1).getStringCellValue());
			baseFields.put("type", sheet.getRow(3).getCell(1).getStringCellValue());
			
			HSSFRow row;
			
			for (int rowIndex = 11; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				row = sheet.getRow(rowIndex);
				
				fields.clear();
				fields.put("code", row.getCell(0).getStringCellValue());
				fields.put("displayName", row.getCell(1).getStringCellValue());
				fields.put("tty", row.getCell(5).getStringCellValue());
				fields.put("codeSystemId", row.getCell(4).getStringCellValue());
				fields.put("codeSystemName", row.getCell(2).getStringCellValue());
				fields.put("codeSystemVersion", row.getCell(3).getStringCellValue());
				fields.putAll(baseFields);
				
				this.loadDocument(dbConnection, doc, fields);
				
				fileCount++;
			}
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		
		return fileCount;
	}
}
