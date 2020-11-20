package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component(value = "CDT")
    public class CdtLoader extends BaseCodeLoader {
    private static Logger logger = Logger.getLogger(CdtLoader.class);
    private final String INVALID_CODE_ENTRY = "99";
    private static final String HEADER_ROW_FINDER_KEY = "CODE";
    private static final int MIN_EXPECTED_NUMBER_OF_CELLS_IN_ROW = 6;
    private static final int CODE_CELL_INDEX_IN_ROW = 0;

    @Override
    public void load(List<File> filesToLoad, Connection connection) {
        StrBuilder insertQueryBuilder;
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
                String codeSystem = file.getParentFile().getName();
                InputStream inputStream = null;
                XSSFWorkbook workBook = null;
                try {
                    logger.debug("Loading CDT File: " + file.getName());
                    inputStream = new FileInputStream(file);
                    workBook = new XSSFWorkbook(inputStream);
                    for (int i = 1; i < workBook.getNumberOfSheets(); i++) {
                        insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);
                        boolean headerRowFound = false;
                        Sheet sheet = workBook.getSheetAt(i);
                        String code;
                        String displayName;

                        for(Row row : sheet){
                            if(headerRowFound && canProcessRow(row)){
                                code = row.getCell(0).getStringCellValue().toUpperCase().trim();
                                displayName = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                buildCodeInsertQueryString(insertQueryBuilder, code, displayName, codeSystem, CodeSystemOIDs.CDT.codesystemOID(), true);
                            }

                            if(!headerRowFound){
                                if(hasValueInCell(row, 0) && row.getCell(0).getStringCellValue().toUpperCase().trim().equals(HEADER_ROW_FINDER_KEY)){
                                    headerRowFound = true;
                                }
                            }
                        }

                        insertCode(insertQueryBuilder.toString(), connection);
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

    private boolean hasValueInCell(Row row, int cellNum) {
        return row.getCell(cellNum) != null && !row.getCell(cellNum).getStringCellValue().isEmpty();
    }

    private boolean canProcessRow(Row row) {
        return hasCodevalueInFirstCell(row) && hasExpectedNumberOfCellsInRow(row);
    }

    private boolean hasCodevalueInFirstCell(Row row){
        return hasValueInCell(row, CODE_CELL_INDEX_IN_ROW);
    }

    private boolean hasExpectedNumberOfCellsInRow(Row row) {
        return row.getLastCellNum() >= MIN_EXPECTED_NUMBER_OF_CELLS_IN_ROW;
    }
}
