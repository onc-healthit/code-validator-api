package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "SNOMED-CT")
public class SnomedLoader extends BaseCodeLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(SnomedLoader.class);

    public long load(List<File> filesToLoad, DataSource datasource) {
        long n = 0;
        JdbcTemplate t = new JdbcTemplate(datasource);
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading SNOMED File: " + file.getName());
                    String codeSystem = file.getParentFile().getName();
                    int count = 0;
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String available;
                    while ((available = br.readLine()) != null) {
                        if ((count++ == 0)) {
                            continue; // skip header row
                        } else {
                            String[] line = StringUtils.splitPreserveAllTokens(available, "\t", 9);
                            String code = line[4];
                            String displayName = line[7];
                            n++;
                            buildCodeInsertQueryString(insertQueryBuilder, code.toUpperCase(), displayName.toUpperCase(), codeSystem, CodeSystemOIDs.SNOMEDCT.codesystemOID());
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
