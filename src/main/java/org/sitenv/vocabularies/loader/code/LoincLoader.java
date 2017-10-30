package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.stereotype.Component;

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
@Component(value = "LOINC")
public class LoincLoader extends BaseCodeLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(LoincLoader.class);
    private static final String ACTIVE_CODE = "ACTIVE";

    @Override
    public void load(List<File> filesToLoad, Connection connection) {
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);
            int totalCount = 0, pendingCount = 0;

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading LOINC File: " + file.getName());
                    int count = 0;
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String available;
                    while ((available = br.readLine()) != null) {
                        if ((count++ == 0)) {
                            continue; // skip header row
                        } else {
                            String[] line = available.replaceAll("^\"", "").split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");

                            String code = StringUtils.strip(line[0], "\"");
                            String codeSystem = file.getParentFile().getName();
                            String oid = CodeSystemOIDs.LOINC.codesystemOID();
                            String longCommonName = StringUtils.strip(line[29], "\"");
                            String componentName = StringUtils.strip(line[1], "\"");
                            String status = StringUtils.strip(line[12], "\"");
                            String shortName = StringUtils.strip(line[23], "\"");
                            boolean isCodeActive = status.equals(ACTIVE_CODE);

                            buildCodeInsertQueryString(insertQueryBuilder, code, longCommonName, codeSystem, oid, isCodeActive);
                            buildCodeInsertQueryString(insertQueryBuilder, code, componentName, codeSystem, oid, isCodeActive);
                            buildCodeInsertQueryString(insertQueryBuilder, code, shortName, codeSystem, oid, isCodeActive);

                            if ((++totalCount % BATCH_SIZE) == 0) {
                                insertCode(insertQueryBuilder.toString(), connection);
                                insertQueryBuilder.clear();
                                insertQueryBuilder.append(codeTableInsertSQLPrefix);
                                pendingCount = 0;
                            }
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
