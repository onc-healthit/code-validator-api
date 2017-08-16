package org.sitenv.vocabularies.loader.valueset;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component(value = "VSAC")
public class VsacLoader extends BaseCodeLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(VsacLoader.class);
    private static final int MIN_EXPECTED_NUMBER_OF_CELLS_IN_ROW = 6;
    private static final int CODE_CELL_INDEX_IN_ROW = 0;
    private static final int BATCH_SIZE = 1000;
    private static final String HEADER_ROW_FINDER_KEY = "CODE"; 

    public void load(List<File> filesToLoad, Connection connection) {
        String insertQueryPrefix = "insert into VALUESETS (ID, CODE, DISPLAYNAME, CODESYSTEMNAME, CODESYSTEMVERSION, CODESYSTEM, TTY, VALUESETNAME, VALUESETOID, VALUESETTYPE, VALUESETDEFINITIONVERSION, VALUESETSTEWARD) values (DEFAULT ,?,?,?,?,?,?,?,?,?,?,?)";
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
                try {
                    logger.info("Loading Value Set File: " + file.getName());
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQueryPrefix);
                    InputStream inputStream = new FileInputStream(file);
                    Workbook workBook = StreamingReader.builder().open(inputStream);
                    for (int i = 1; i < workBook.getNumberOfSheets(); i++) {
                        boolean headerRowFound = false;
                        Sheet sheet = workBook.getSheetAt(i);
                        String valueSetName = "";
                        String valueSetOid = "";
                        String valueSetType = "";
                        String valueSetVersion = "";
                        String valueSetSteward = "";
                        int valuesetDataRowCount = 0;
                        String rowlabel = "";

                        for(Row row : sheet){
                            if ((!headerRowFound) && hasValueInCell(row, 0) && (row.getRowNum()<10)) {
                            	rowlabel = row.getCell(0).getStringCellValue().trim();
								// Switching to use labels in the first column to look for meta info 
                                if (rowlabel.equalsIgnoreCase("VALUE SET NAME")) {
                                    valueSetName = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (rowlabel.equalsIgnoreCase("OID")) {
                                    valueSetOid = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (rowlabel.equalsIgnoreCase("TYPE")) {
                                    valueSetType = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (rowlabel.equalsIgnoreCase("DEFINITION VERSION")) {
                                    valueSetVersion = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (rowlabel.equalsIgnoreCase("STEWARD")) {
                                    valueSetSteward = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                            }

                            if(headerRowFound && canProcessRow(row)){
                                preparedStatement.setString(1, row.getCell(0).getStringCellValue().toUpperCase().trim());
                                preparedStatement.setString(2, row.getCell(1).getStringCellValue().toUpperCase().trim());
                                preparedStatement.setString(3, row.getCell(2).getStringCellValue().toUpperCase().trim());
                                preparedStatement.setString(4, row.getCell(3).getStringCellValue().trim());
                                preparedStatement.setString(5, row.getCell(4).getStringCellValue().toUpperCase().trim());
                                preparedStatement.setString(6, row.getCell(5).getStringCellValue().toUpperCase().trim());
                                preparedStatement.setString(7, valueSetName);
                                preparedStatement.setString(8, valueSetOid);
                                preparedStatement.setString(9, valueSetType);
                                preparedStatement.setString(10, valueSetVersion);
                                preparedStatement.setString(11, valueSetSteward);
                                preparedStatement.addBatch();
                                valuesetDataRowCount++;

                                if(valuesetDataRowCount % BATCH_SIZE == 0){
                                    preparedStatement.executeBatch();
                                    connection.commit();
                                    preparedStatement.clearBatch();
                                }
                            }

                            if(!headerRowFound){
                                if(hasValueInCell(row, 0) && row.getCell(0).getStringCellValue().toUpperCase().trim().equals(HEADER_ROW_FINDER_KEY)){
                                    headerRowFound = true;
                                }
                            }
                        }

                        preparedStatement.executeBatch();
                        connection.commit();
                    }
                    workBook.close();
                } catch (IOException | SQLException e) {
                    logger.error("ERROR loading valueset. " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
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
