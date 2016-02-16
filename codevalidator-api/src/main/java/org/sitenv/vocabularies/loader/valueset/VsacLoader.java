package org.sitenv.vocabularies.loader.valueset;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

@Component(value = "VSAC")
public class VsacLoader extends BaseVocabularyLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(VsacLoader.class);

    public void load(List<File> filesToLoad, Connection connection) {
        StrBuilder insertQueryBuilder = null;
        String insertQueryPrefix = "insert into VALUESETS (ID, CODE, DISPLAYNAME, CODESYSTEMNAME, CODESYSTEMVERSION, CODESYSTEM, TTY, VALUESETNAME, VALUESETOID, VALUESETTYPE, VALUESETDEFINITIONVERSION, VALUESETSTEWARD) values ";
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
                InputStream inputStream = null;
                POIFSFileSystem fileSystem = null;
                HSSFWorkbook workBook = null;
                try {
                    logger.debug("Loading Value Set File: " + file.getName());
                    inputStream = new FileInputStream(file);
                    fileSystem = new POIFSFileSystem(inputStream);
                    workBook = new HSSFWorkbook(fileSystem);

                    for (int i = 1; i < workBook.getNumberOfSheets(); i++) {
                        insertQueryBuilder = new StrBuilder(insertQueryPrefix);
                        HSSFSheet sheet = workBook.getSheetAt(i);
                        String valueSetName = sheet.getRow(1).getCell(1).getStringCellValue();
                        String valueSetOid = sheet.getRow(2).getCell(1).getStringCellValue();
                        String valueSetType = sheet.getRow(3).getCell(1).getStringCellValue();
                        String valueSetVersion;
                        Cell versionCell = sheet.getRow(4).getCell(1);
                        versionCell.setCellType(Cell.CELL_TYPE_STRING);
                        valueSetVersion = versionCell.getStringCellValue();
                        String valueSetSteward = sheet.getRow(5).getCell(1).getStringCellValue();

                        for (int count = 11; count < sheet.getLastRowNum()+1; count++) {
                            if (!isRowEmpty(sheet.getRow(count))) {
                                String code;
                                String displayName;
                                String codeSystemName;
                                String codeSystemVersion;
                                String codeSystem;
                                String tty;

                                Cell codeCell = sheet.getRow(count).getCell(0);
                                Cell descriptionCell = sheet.getRow(count).getCell(1);
                                Cell codeSystemCell = sheet.getRow(count).getCell(2);
                                Cell codeSystemVersionCell = sheet.getRow(count).getCell(3);
                                Cell codeSystemOidCell = sheet.getRow(count).getCell(4);
                                Cell ttyCell = sheet.getRow(count).getCell(5);

                                codeCell.setCellType(Cell.CELL_TYPE_STRING);
                                descriptionCell.setCellType(Cell.CELL_TYPE_STRING);
                                codeSystemCell.setCellType(Cell.CELL_TYPE_STRING);
                                codeSystemVersionCell.setCellType(Cell.CELL_TYPE_STRING);
                                codeSystemOidCell.setCellType(Cell.CELL_TYPE_STRING);
                                ttyCell.setCellType(Cell.CELL_TYPE_STRING);

                                code = codeCell.getStringCellValue();
                                displayName = descriptionCell.getStringCellValue();
                                codeSystemName = codeSystemCell.getStringCellValue();
                                codeSystemVersion = codeSystemVersionCell.getStringCellValue();
                                codeSystem = codeSystemOidCell.getStringCellValue();
                                tty = ttyCell.getStringCellValue();

                                if(count > 11){
                                    insertQueryBuilder.append(",");
                                }
                                insertQueryBuilder.append("(");
                                insertQueryBuilder.append("DEFAULT");
                                insertQueryBuilder.append(",'");
                                insertQueryBuilder.append(code.replaceAll("'", "''").toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(displayName.replaceAll("'", "''").toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(codeSystemName.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(codeSystemVersion.trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(codeSystem.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(tty.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(valueSetName.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(valueSetOid.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(valueSetType.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(valueSetVersion.toUpperCase().trim());
                                insertQueryBuilder.append("','");
                                insertQueryBuilder.append(valueSetSteward.replaceAll("'", "''").toUpperCase().trim());
                                insertQueryBuilder.append("')");
                            }
                        }
                        doInsert(insertQueryBuilder.toString(), connection);
                    }
                } catch(SQLException e){
                    logger.error("SQL ERROR loading valueset. " + e.getLocalizedMessage());
                } catch (IOException e) {
                    logger.error("IO ERROR loading valueset. " + e.getLocalizedMessage());
                } finally {
                    try {
                        workBook.close();
                        inputStream.close();
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
