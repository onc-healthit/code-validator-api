package org.sitenv.vocabularies.loader.code;

import static org.sitenv.vocabularies.loader.code.IcdLoader.buildDelimitedIcdCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by Brian on 2/7/2016.
 */
public abstract class Icd9BaseLoader extends BaseCodeLoader {
    private static Logger logger = Logger.getLogger(Icd9BaseLoader.class);
    protected String oid;

    @Override
    public long load(List<File> filesToLoad, DataSource datasource) {
        long n = 0;
        BufferedReader br = null;
        FileReader fileReader = null;
        JdbcTemplate t = new JdbcTemplate(datasource);
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading ICD9 File: " + file.getName());
                    String codeSystem = file.getParentFile().getName();
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.isEmpty()) {
                            String code = buildDelimitedIcdCode(line.substring(0, 5));
                            String displayName = line.substring(6);
                            buildCodeInsertQueryString(insertQueryBuilder, code, displayName, codeSystem, oid);
                            n++;
                            t.update(insertQueryBuilder.toString());
                            insertQueryBuilder.clear();
                            insertQueryBuilder.append(codeTableInsertSQLPrefix);

                        }
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
