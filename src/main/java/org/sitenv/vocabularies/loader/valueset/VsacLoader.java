package org.sitenv.vocabularies.loader.valueset;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
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
                    InputStream inputStream = new FileInputStream(file);
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQueryPrefix);
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
                                    valueSetName = row.getCell(1).getStringCellValue();
                                }
                                if (row.getRowNum() == 2) {
                                    valueSetOid = row.getCell(1).getStringCellValue();
                                }
                                if (row.getRowNum() == 3) {
                                    valueSetType = row.getCell(1).getStringCellValue();
                                }
                                if (row.getRowNum() == 4) {
                                    valueSetVersion = row.getCell(1).getStringCellValue();
                                }
                                if (row.getRowNum() == 5) {
                                    valueSetSteward = row.getCell(1).getStringCellValue();
                                }
                            }

                            if(row.getRowNum() > 10){
                                String code = "";
                                String displayName = "";
                                String codeSystemName = "";
                                String codeSystemVersion = "";
                                String codeSystem = "";
                                String tty = "";
                                for(Cell cell : row){
                                    if(row.getCell(cell.getColumnIndex()) != null){
                                        if(cell.getColumnIndex() == 0){
                                            code = row.getCell(0).getStringCellValue();
                                        }
                                        if(cell.getColumnIndex() == 1){
                                            displayName = row.getCell(1).getStringCellValue();
                                        }
                                        if(cell.getColumnIndex() == 2){
                                            codeSystemName = row.getCell(2).getStringCellValue();
                                        }
                                        if(cell.getColumnIndex() == 3){
                                            codeSystemVersion = row.getCell(3).getStringCellValue();
                                        }
                                        if(cell.getColumnIndex() == 4){
                                            codeSystem = row.getCell(4).getStringCellValue();
                                        }
                                        if(cell.getColumnIndex() == 5){
                                            tty = row.getCell(5).getStringCellValue();
                                        }
                                    }
                                }

                                preparedStatement.setString(1, code);
                                preparedStatement.setString(2, displayName);
                                preparedStatement.setString(3, codeSystemName);
                                preparedStatement.setString(4, codeSystemVersion);
                                preparedStatement.setString(5, codeSystem);
                                preparedStatement.setString(6, tty);
                                preparedStatement.setString(7, valueSetName);
                                preparedStatement.setString(8, valueSetOid);
                                preparedStatement.setString(9,valueSetType);
                                preparedStatement.setString(10, valueSetVersion);
                                preparedStatement.setString(11, valueSetSteward);
                                preparedStatement.addBatch();

                                if(row.getRowNum() % 1000 == 0){
                                    preparedStatement.executeBatch();
                                    connection.commit();
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
