package org.sitenv.vocabularies.loader.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.BaseCodeLoader;
import org.sitenv.vocabularies.loader.VocabularyLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "LOINC")
public class LoincLoader extends BaseCodeLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(LoincLoader.class);

    public long load(List<File> filesToLoad, DataSource datasource) {
        long n = 0;
        JdbcTemplate t = new JdbcTemplate(datasource);
        BufferedReader br = null;
        FileReader fileReader = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);

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
                            String shortName = StringUtils.strip(line[23], "\"");
                            n++;
                            
                            buildCodeInsertQueryString(insertQueryBuilder, code, longCommonName.toUpperCase(), codeSystem, oid);
                            t.update(insertQueryBuilder.toString());
                            insertQueryBuilder.clear();
                            insertQueryBuilder.append(codeTableInsertSQLPrefix);
                            
                            buildCodeInsertQueryString(insertQueryBuilder, code, componentName.toUpperCase(), codeSystem, oid);
                            t.update(insertQueryBuilder.toString());
                            insertQueryBuilder.clear();
                            insertQueryBuilder.append(codeTableInsertSQLPrefix);

                            
                            buildCodeInsertQueryString(insertQueryBuilder, code, shortName.toUpperCase(), codeSystem, oid);
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
