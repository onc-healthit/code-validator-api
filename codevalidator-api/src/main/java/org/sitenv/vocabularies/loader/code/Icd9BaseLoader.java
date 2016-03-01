package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.VocabularyLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Brian on 2/7/2016.
 */
public class Icd9BaseLoader extends IcdLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(Icd9BaseLoader.class);

    @Override
    public void load(List<File> filesToLoad, Connection connection) {
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            String insertQueryPrefix = "insert into CODES (ID, CODE, DISPLAYNAME, CODESYSTEM) values ";
            StrBuilder insertQueryBuilder = new StrBuilder(insertQueryPrefix);
            int totalCount = 0, pendingCount = 0;

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading ICD9CM_DX File: " + file.getName());
                    int count = 0;
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String line;
                    while ((line = br.readLine()) != null) {
                        if ((count++ == 0) || line.isEmpty()) {
                            continue; // skip header row
                        } else {
                            if (pendingCount++ > 0) {
                                insertQueryBuilder.append(",");
                            }
                            insertQueryBuilder.append("(");
                            insertQueryBuilder.append("DEFAULT");
                            insertQueryBuilder.append(",'");
                            insertQueryBuilder.append(buildDelimitedIcdCode(line.substring(0, 5).trim()).toUpperCase());
                            insertQueryBuilder.append("','");
                            insertQueryBuilder.append(line.substring(6).trim().toUpperCase().replaceAll("'", "''"));
                            insertQueryBuilder.append("','");
                            insertQueryBuilder.append(file.getParentFile().getName());
                            insertQueryBuilder.append("')");

                            if ((++totalCount % 5000) == 0) {
                                doInsert(insertQueryBuilder.toString(), connection);
                                insertQueryBuilder.clear();
                                insertQueryBuilder.append(insertQueryPrefix);
                                pendingCount = 0;
                            }
                        }
                    }
                }
            }
            if (pendingCount > 0) {
                doInsert(insertQueryBuilder.toString(), connection);
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    fileReader.close();
                    br.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
}
