package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.sitenv.vocabularies.loader.code.IcdLoader.buildDelimitedIcdCode;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "ICD9CM_DX")
public class Icd9CmDxLoader extends BaseCodeLoader {
    private static Logger logger = Logger.getLogger(Icd9CmDxLoader.class);
    private String oid;

    public Icd9CmDxLoader() {
        this.oid = CodeSystemOIDs.ICD9CMDX.codesystemOID();
    }

    @Override
    public void load(List<File> filesToLoad, Connection connection) {
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);
            int totalCount = 0, pendingCount = 0;

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading ICD9CM_DX File: " + file.getName());
                    String codeSystem = file.getParentFile().getName();
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.isEmpty()) {
                            String code = buildDelimitedIcdCode(line.substring(0, 5));
                            String displayName = line.substring(6);
                            buildCodeInsertQueryString(insertQueryBuilder, code, displayName, codeSystem, oid);

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
