package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sitenv.vocabularies.loader.BaseVocabularyLoader;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component(value = "CDT")
public class CdtLoader extends BaseVocabularyLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(CdtLoader.class);

    public void load(List<File> filesToLoad, Connection connection) {
        StrBuilder insertQueryBuilder = null;
        String insertQueryPrefix = codeTableInsertSQLPrefix;
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
                InputStream inputStream = null;
                XSSFWorkbook workBook = null;
                try {
                    logger.debug("Loading CDT File: " + file.getName());
                    inputStream = new FileInputStream(file);
                    workBook = new XSSFWorkbook(inputStream);

                    for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
                        insertQueryBuilder = new StrBuilder(insertQueryPrefix);
                        XSSFSheet sheet = workBook.getSheetAt(i);

                        for (int count = 26; count < sheet.getLastRowNum()+1; count++) {
                            if (!isRowEmpty(sheet.getRow(count))) {
                                String code;
                                String displayName;

                                XSSFCell codeCell = sheet.getRow(count).getCell(0);
                                XSSFCell descriptionCell = sheet.getRow(count).getCell(2);

                                codeCell.setCellType(Cell.CELL_TYPE_STRING);
                                descriptionCell.setCellType(Cell.CELL_TYPE_STRING);


                                code = codeCell.getStringCellValue();
                                displayName = descriptionCell.getStringCellValue();

                                if(count > 26){
                                    insertQueryBuilder.append(",");
                                }
                                insertQueryBuilder.append("(");
                                insertQueryBuilder.append("DEFAULT");
                                insertQueryBuilder.append(",'");
                                insertQueryBuilder.append(code.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(displayName.replaceAll("'", "''").toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(file.getParentFile().getName());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(CodeSystemOIDs.CDT.codesystemOID());
                                insertQueryBuilder.append("')");
                            }
                        }
                        doInsert(insertQueryBuilder.toString(), connection);
                    }
                } catch (IOException e) {
                    logger.error(e);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(workBook != null) {
                            workBook.close();
                        }
                        if(inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean isRowEmpty(Row row) {
        if(row != null){
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                    return false;
            }
        }
        return true;
    }
}
