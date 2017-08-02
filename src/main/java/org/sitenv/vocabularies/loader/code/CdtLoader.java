package org.sitenv.vocabularies.loader.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component(value = "CDT")
public class CdtLoader extends BaseCodeLoader {
    private static Logger logger = Logger.getLogger(CdtLoader.class);

    public long load(List<File> filesToLoad, DataSource ds) {
        long n = 0;
        StrBuilder insertQueryBuilder = null;
        String insertQueryPrefix = codeTableInsertSQLPrefix;
        JdbcTemplate t  = new JdbcTemplate(ds);
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
                String codeSystem = file.getParentFile().getName();
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

                                n++;
                                buildCodeInsertQueryString(insertQueryBuilder, code.toUpperCase(), displayName.toUpperCase(), codeSystem, CodeSystemOIDs.CDT.codesystemOID());
                                t.update(insertQueryPrefix.toString());
                                insertQueryBuilder.clear();
                                insertQueryBuilder.append(codeTableInsertSQLPrefix);

                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error(e);
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
        return n;
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
