package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;

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
public abstract class Icd10BaseLoader extends IcdLoader {
    private static Logger logger = Logger.getLogger(Icd10BaseLoader.class);
    protected String oid;

    @Override
    public void load(List<File> filesToLoad, Connection connection) {
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);
            int totalCount = 0, pendingCount = 0;

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading ICD10CM File: " + file.getName());
                    String codeSystem = file.getParentFile().getName();
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String available;
                    while ((available = br.readLine()) != null) {
                        String code = buildDelimitedIcdCode(available.substring(6, 13));
                        String shortDisplayName = available.substring(17, 78);
                        String longDisplayName = available.substring(77);

                        buildCodeInsertQueryString(insertQueryBuilder, code, shortDisplayName, codeSystem, oid);
                        buildCodeInsertQueryString(insertQueryBuilder, code, longDisplayName, codeSystem, oid);

                        if ((++totalCount % BATCH_SIZE) == 0) {
                            insertCode(insertQueryBuilder.toString(), connection);
                            insertQueryBuilder.clear();
                            insertQueryBuilder.append(codeTableInsertSQLPrefix);
                            pendingCount = 0;
                        }
                    }

                }
            }
            if (pendingCount > 0) {
                insertCode(insertQueryBuilder.toString(), connection);
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
