package org.sitenv.vocabularies.loader.valueset;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sitenv.vocabularies.loader.BaseVocabularyLoader;
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
public class VsacLoader extends BaseVocabularyLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(VsacLoader.class);

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

                        Sheet sheet = workBook.getSheetAt(i);
                        String valueSetName = "";
                        String valueSetOid = "";
                        String valueSetType = "";
                        String valueSetVersion = "";
                        String valueSetSteward = "";

                        for(Row row : sheet){
                            if(row.getRowNum() < 6) {
                                if (row.getRowNum() == 1) {
                                    valueSetName = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 2) {
                                    valueSetOid = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 3) {
                                    valueSetType = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 4) {
                                    valueSetVersion = row.getCell(1).getStringCellValue().toUpperCase().trim();
                                }
                                if (row.getRowNum() == 5) {
                                    valueSetSteward = row.getCell(1).getStringCellValue().replaceAll("'", "''").toUpperCase().trim();
                                }
                            }

                            if(row.getRowNum() > 10){
                                if(row.getCell(0) != null) {
                                    preparedStatement.setString(1, row.getCell(0).getStringCellValue().replaceAll("'", "''").toUpperCase().trim());
                                    preparedStatement.setString(2, row.getCell(1).getStringCellValue().replaceAll("'", "''").toUpperCase().trim());
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
                                }
                                if(row.getRowNum() % 1000 == 0){
                                    preparedStatement.executeBatch();
                                    connection.commit();
                                    preparedStatement.clearBatch();
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
}
