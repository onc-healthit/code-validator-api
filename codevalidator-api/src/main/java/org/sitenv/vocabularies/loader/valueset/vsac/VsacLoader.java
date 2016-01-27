package org.sitenv.vocabularies.loader.valueset.vsac;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoader;
import org.sitenv.vocabularies.loader.valueset.ValueSetLoaderManager;
import org.sitenv.vocabularies.model.impl.VsacValueSetModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class VsacLoader implements ValueSetLoader {

    private static Logger logger = Logger.getLogger(VsacLoader.class);


    static {
        ValueSetLoaderManager.getInstance()
                .registerLoader(VocabularyConstants.VSAC_VALUESET_NAME, VsacLoader.class);
        logger.info("Loaded: " + VocabularyConstants.VSAC_VALUESET_NAME + " (value set)");
        VocabularyRepository.getInstance().getValueSetModelClassList().add(VsacValueSetModel.class);
    }


    public void load(List<File> filesToLoad) {
        OObjectDatabaseTx dbConnection = VocabularyRepository.getInstance().getInactiveDbConnection();
        HSSFWorkbook workBook = null;

        try {
            logger.info("Truncating VsacValueSetModel Datastore...");
            VocabularyRepository.truncateValueSetModel(dbConnection, VsacValueSetModel.class);
            logger.info(dbConnection.getName() + ".VsacValueSetModel Datastore Truncated... records remaining: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));

            VocabularyRepository.updateValueSetIndexProperties(dbConnection, VsacValueSetModel.class, true);

            String insertQueryPrefix = "insert into " + VsacValueSetModel.class.getSimpleName() +
                    " (valueSetIndex, valueSetNameIndex, codeSystemIndex, codeIndex, descriptionIndex, code, codeSystem, codeSystemName, codeSystemVersion, description, definitionVersion, steward, tty, type, valueSet, valueSetName) values ";
            StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
            insertQueryBuilder.ensureCapacity(1000);
            int totalCount = 0, pendingCount = 0;

            dbConnection.declareIntent(new OIntentMassiveInsert());

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading Value Set File: " + file.getName());

                    InputStream inputStream = null;
                    inputStream = new FileInputStream(file);
                    POIFSFileSystem fileSystem = null;
                    fileSystem = new POIFSFileSystem(inputStream);
                    workBook = new HSSFWorkbook(fileSystem);
                    for(int i = 1; i < workBook.getNumberOfSheets(); i++){
                        HSSFSheet sheet = workBook.getSheetAt(i);
                        String valueSetName = ((String) OIOUtils.encode(sheet.getRow(1).getCell(1).getStringCellValue()));
                        String oid = ((String) OIOUtils.encode(sheet.getRow(2).getCell(1).getStringCellValue()));
                        String type = ((String) OIOUtils.encode(sheet.getRow(3).getCell(1).getStringCellValue()));
                        String version;
                        Cell versionCell = sheet.getRow(4).getCell(1);
                        versionCell.setCellType(Cell.CELL_TYPE_STRING);
                        version = (String) OIOUtils.encode(versionCell.getStringCellValue());
                        String steward = ((String) OIOUtils.encode(sheet.getRow(5).getCell(1).getStringCellValue()));
                        String valueSetIndex = oid.toUpperCase();
                        String valueSetNameIndex = valueSetName.toUpperCase();

                        for (int count = 11; count <= sheet.getLastRowNum(); count++) {
                            if(!isRowEmpty(sheet.getRow(count))){
                                if (pendingCount++ > 0) {
                                    insertQueryBuilder.append(",");
                                }

                                String code;
                                String description;
                                String codeSystemName;
                                String codeSystemVersion;
                                String codeSystem;
                                String tty;

                                Cell codeCell = sheet.getRow(count).getCell(0);
                                Cell descriptionCell = sheet.getRow(count).getCell(1);
                                Cell codeSystemNameCell = sheet.getRow(count).getCell(2);
                                Cell codeSystemVersionCell = sheet.getRow(count).getCell(3);
                                Cell codeSystemCell = sheet.getRow(count).getCell(4);
                                Cell ttyCell = sheet.getRow(count).getCell(5);

                                codeCell.setCellType(Cell.CELL_TYPE_STRING);
                                descriptionCell.setCellType(Cell.CELL_TYPE_STRING);
                                codeSystemNameCell.setCellType(Cell.CELL_TYPE_STRING);
                                codeSystemVersionCell.setCellType(Cell.CELL_TYPE_STRING);
                                codeSystemCell.setCellType(Cell.CELL_TYPE_STRING);
                                ttyCell.setCellType(Cell.CELL_TYPE_STRING);

                                code = (String) OIOUtils.encode(codeCell.getStringCellValue());
                                description = (String) OIOUtils.encode(descriptionCell.getStringCellValue());
                                codeSystemName = (String) OIOUtils.encode(codeSystemNameCell.getStringCellValue());
                                codeSystemVersion = (String) OIOUtils.encode(codeSystemVersionCell.getStringCellValue());
                                codeSystem = (String) OIOUtils.encode(codeSystemCell.getStringCellValue());
                                tty = (String) OIOUtils.encode(ttyCell.getStringCellValue());

                                insertQueryBuilder.append("(\"");
                                insertQueryBuilder.append(valueSetIndex.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(valueSetNameIndex.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(codeSystem.toUpperCase().trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(code.toUpperCase().trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(description.toUpperCase().trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(code.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(codeSystem.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(codeSystemName.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(codeSystemVersion.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(description.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(version.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(steward.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(tty.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(type.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(oid.trim());
                                insertQueryBuilder.append("\",\"");
                                insertQueryBuilder.append(valueSetName.trim());
                                insertQueryBuilder.append("\")");

                                if ((++totalCount % 5000) == 0) {
                                    dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
                                    dbConnection.commit();

                                    insertQueryBuilder.clear();
                                    insertQueryBuilder.append(insertQueryPrefix);

                                    pendingCount = 0;
                                }

                            }
                        }
                    }
                    workBook.close();
                }
            }
            if (pendingCount > 0) {
                dbConnection.command(new OCommandSQL(insertQueryBuilder.toString())).execute();
                dbConnection.commit();
            }

            logger.info("VsacValueSetModel Loading complete... records existing: " + VocabularyRepository.getValueSetRecordCount(dbConnection, VsacValueSetModel.class));
        } catch (IOException e) {
            logger.error(e);
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }

            dbConnection.declareIntent(null);

            Runtime r = Runtime.getRuntime();
            r.gc();
        }
    }

    public String getValueSetAuthorName() {
        return VocabularyConstants.VSAC_VALUESET_NAME;
    }

    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }
        return true;
    }

}
