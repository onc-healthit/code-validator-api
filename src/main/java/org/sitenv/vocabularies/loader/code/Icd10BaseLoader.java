package org.sitenv.vocabularies.loader.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by Brian on 2/7/2016.
 */
public abstract class Icd10BaseLoader extends IcdLoader {
    private static Logger logger = Logger.getLogger(Icd10BaseLoader.class);
    protected String oid;

    public long load(List<File> filesToLoad, DataSource datasource) {
        long n = 0;
        JdbcTemplate t = new JdbcTemplate(datasource);
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
                        String shortDisplayName = available.substring(16, 77);
                        String longDisplayName = available.substring(77);

                        n++;
                        
                        buildCodeInsertQueryString(insertQueryBuilder, code, shortDisplayName, codeSystem, oid);
                        t.update(insertQueryBuilder.toString());
                        insertQueryBuilder.clear();
                        insertQueryBuilder.append(codeTableInsertSQLPrefix);
                        
                        buildCodeInsertQueryString(insertQueryBuilder, code, longDisplayName, codeSystem, oid);
                        t.update(insertQueryBuilder.toString());
                        insertQueryBuilder.clear();
                        insertQueryBuilder.append(codeTableInsertSQLPrefix);
                        
                    }

                }
            }
        } catch (IOException e) {
            logger.error(e);
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
        return n;
    }
}
