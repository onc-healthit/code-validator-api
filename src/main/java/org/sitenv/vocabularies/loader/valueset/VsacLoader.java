package org.sitenv.vocabularies.loader.valueset;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.sitenv.vocabularies.loader.BaseVocabularyLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component(value = "VSAC")
public class VsacLoader extends BaseVocabularyLoader {
    private static Logger logger = Logger.getLogger(VsacLoader.class);

    public void load(List<File> filesToLoad, Connection connection) {
        StrBuilder insertQueryBuilder = new StrBuilder();
        String insertQueryPrefix = "insert into VALUESETS (ID, CODE, DISPLAYNAME, CODESYSTEMNAME, CODESYSTEMVERSION, CODESYSTEM, TTY, VALUESETNAME, VALUESETOID, VALUESETTYPE, VALUESETDEFINITIONVERSION, VALUESETSTEWARD) values ";
        for (File file : filesToLoad) {
            if (file.isFile() && !file.isHidden()) {
               Workbook workBook;
                try {
                    logger.info("Loading Value Set File: " + file.getName());
                    workBook = WorkbookFactory.create(file);

                    for (int i = 1; i < workBook.getNumberOfSheets(); i++) {
                        insertQueryBuilder.append(insertQueryPrefix);
                        Sheet sheet = workBook.getSheetAt(i);
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
                        insertQueryBuilder.clear();
                        workBook.close();
                    }
                } catch(SQLException e){
                    logger.error("SQL ERROR loading valueset into database. " + e.getLocalizedMessage());
                } catch (IOException e) {
                    logger.error("IO ERROR loading valueset. " + e.getLocalizedMessage());
                } catch (InvalidFormatException e) {
                    logger.error("Error reading file. " + e.getLocalizedMessage());
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
