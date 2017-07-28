package org.sitenv.vocabularies.loader.code;

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
import org.springframework.stereotype.Component;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "CPT")
public class CptLoader extends BaseCodeLoader {
    private static Logger logger = Logger.getLogger(CptLoader.class);
    private String oid;

    public CptLoader() {
        this.oid = CodeSystemOIDs.CPT4.codesystemOID();
    }

    @Override
    public long load(List<File> filesToLoad, DataSource ds) {
        long n = 0;
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);
            JdbcTemplate t  = new JdbcTemplate(ds);
            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading CPT File: " + file.getName());
                    String codeSystem = file.getParentFile().getName();
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.isEmpty()) {
                            String code = line.substring(0, 5);
                            String displayName = line.substring(line.indexOf(" "));
                            buildCodeInsertQueryString(insertQueryBuilder, code, displayName, codeSystem, oid);

                            n++;
                            t.update(codeTableInsertSQLPrefix,code.toUpperCase().trim(),displayName.toUpperCase().trim(),codeSystem,CodeSystemOIDs.CDT.codesystemOID());

                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
        finally {
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
