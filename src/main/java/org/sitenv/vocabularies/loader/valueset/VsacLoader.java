package org.sitenv.vocabularies.loader.valueset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

//import com.monitorjbl.xlsx.StreamingReader;

@Component(value = "VSAC")
public class VsacLoader extends BaseCodeLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(VsacLoader.class);
    private static final int MIN_EXPECTED_NUMBER_OF_CELLS_IN_ROW = 6;
    private static final int CODE_CELL_INDEX_IN_ROW = 0;
    private static final int BATCH_SIZE = 1000;
    private static final String HEADER_ROW_FINDER_KEY = "CODE";

    public long load(List<File> filesToLoad, DataSource datasource) {
        String insertQueryPrefix = "insert into VALUESETS (CODE, DISPLAYNAME, CODESYSTEMNAME, CODESYSTEMVERSION, CODESYSTEM, TTY, VALUESETNAME, VALUESETOID, VALUESETTYPE, VALUESETDEFINITIONVERSION, VALUESETSTEWARD) values (?,?,?,?,?,?,?,?,?,?,?)";
        long n = 0;
        JdbcTemplate templ = new JdbcTemplate(datasource);
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
                try {
                    logger.info("Loading Value Set File: " + file.getName());
//                    PreparedStatement preparedStatement = connection.prepareStatement(insertQueryPrefix);
                    InputStream inputStream = new FileInputStream(file);
//                    Workbook workBook = StreamingReader.builder().open(inputStream);
                    Workbook workBook = WorkbookFactory.create(inputStream);
                    for (int i = 1; i < workBook.getNumberOfSheets(); i++) {
                        boolean headerRowFound = false;
                        Sheet sheet = workBook.getSheetAt(i);
                        String valueSetName = "";
                        String valueSetOid = "";
                        String valueSetType = "";
                        String valueSetVersion = "";
                        String valueSetSteward = "";
                        int valuesetDataRowCount = 0;

                        for(Row row : sheet){
                            if(row.getRowNum() < 6) {
                                if (row.getRowNum() == 1) {
                                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                    valueSetName = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 2) {
                                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                    valueSetOid = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 3) {
                                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                    valueSetType = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 4) {
                                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                    valueSetVersion = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 5) {
                                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                    valueSetSteward = row.getCell(1).getStringCellValue().replaceAll("'", "''").toUpperCase().trim();
                                }
                            }

                            if(row.getRowNum() > 10){
                                if(row.getCell(0) != null) {
                                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
//                                	if (!StringUtils.isEmpty(row.getCell(0).getStringCellValue())) {
                                    if(headerRowFound && canProcessRow(row)){
//                                    preparedStatement.setString(1, row.getCell(0).getStringCellValue().replaceAll("'", "''").toUpperCase().trim());
//                                    preparedStatement.setString(2, row.getCell(1).getStringCellValue().replaceAll("'", "''").toUpperCase().trim());
//                                    preparedStatement.setString(3, row.getCell(2).getStringCellValue().toUpperCase().trim());
//                                    preparedStatement.setString(4, row.getCell(3).getStringCellValue().trim());
//                                    preparedStatement.setString(5, row.getCell(4).getStringCellValue().toUpperCase().trim());
//                                    preparedStatement.setString(6, row.getCell(5).getStringCellValue().toUpperCase().trim());
//                                    preparedStatement.setString(7, valueSetName);
//                                    preparedStatement.setString(8, valueSetOid);
//                                    preparedStatement.setString(9, valueSetType);
//                                    preparedStatement.setString(10, valueSetVersion);
//                                    preparedStatement.setString(11, valueSetSteward);
//                                    preparedStatement.addBatch();
                                        n++;
                                        row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                                        row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                                        row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                                        row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                                        row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                                        templ.update(insertQueryPrefix, row.getCell(0).getStringCellValue().toUpperCase().trim(),
                                                row.getCell(1).getStringCellValue().toUpperCase().trim(),
                                                row.getCell(2).getStringCellValue().toUpperCase().trim(),
                                                row.getCell(3).getStringCellValue().trim(),
                                                row.getCell(4).getStringCellValue().toUpperCase().trim(),
                                                row.getCell(5).getStringCellValue().toUpperCase().trim(),
                                                valueSetName,
                                                valueSetOid,
                                                valueSetType,
                                                valueSetVersion,
                                                valueSetSteward);
                                    }
                                }
//                                if(row.getRowNum() % 1000 == 0){
//                                    preparedStatement.executeBatch();
//                                    connection.commit();
//                                    preparedStatement.clearBatch();
//                                	  valuesetDataRowCount++;

//		                                if(valuesetDataRowCount % BATCH_SIZE == 0){
//		                                	preparedStatement.executeBatch();
//		                                	connection.commit();
//		                                	preparedStatement.clearBatch();
//		                                }
//                                }
                            }

                            if(!headerRowFound){
                                if(hasValueInCell(row, 0) && row.getCell(0).getStringCellValue().toUpperCase().trim().equals(HEADER_ROW_FINDER_KEY)){
                                    headerRowFound = true;
                                }
//                        preparedStatement.executeBatch();
//                        connection.commit();
                            }

                        }
                    }
                    workBook.close();
                } catch (InvalidFormatException e) {
                    logger.error("ERROR loading valueset. " + e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error("ERROR loading valueset. " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
        return n;
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

    private boolean hasValueInCell(Row row, int cellNum) {
        return row.getCell(cellNum) != null && !row.getCell(cellNum).getStringCellValue().isEmpty();
    }
}
