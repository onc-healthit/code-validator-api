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
@Component(value = "RXNORM")
public class RxNormLoader extends BaseCodeLoader implements VocabularyLoader {
    private static Logger logger = Logger.getLogger(RxNormLoader.class);

    public long load(List<File> filesToLoad, DataSource datasource) {
        long n = 0;
        JdbcTemplate t = new JdbcTemplate(datasource);
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            StrBuilder insertQueryBuilder = new StrBuilder(codeTableInsertSQLPrefix);
            int totalCount = 0, pendingCount = 0;

            for (File file : filesToLoad) {
                if (file.isFile() && !file.isHidden()) {
                    logger.debug("Loading RxNorm File: " + file.getName());
                    String codeSystem = file.getParentFile().getName();
                    fileReader = new FileReader(file);
                    br = new BufferedReader(fileReader);
                    String available;
                    while ((available = br.readLine()) != null) {
                        String[] line = StringUtils.splitPreserveAllTokens(available, "|", 16);
                        String code = line[0];
                        String displayName = line[14];
//                        if (pendingCount++ > 0) {
//                            insertQueryBuilder.append(",");
//                        }
//                        insertQueryBuilder.append("(");
//                        insertQueryBuilder.append("DEFAULT");
//                        insertQueryBuilder.append(",'");
//                        insertQueryBuilder.append(line[0]);
//                        insertQueryBuilder.append("','");
//                        insertQueryBuilder.append(line[14].toUpperCase().replaceAll("'", "''"));
//                        insertQueryBuilder.append("','");
//                        insertQueryBuilder.append(file.getParentFile().getName());
//                        insertQueryBuilder.append("','");
//                        insertQueryBuilder.append(CodeSystemOIDs.RXNORM.codesystemOID());
//                        insertQueryBuilder.append("')");

                        n++;
                        buildCodeInsertQueryString(insertQueryBuilder, code, displayName.toUpperCase(), codeSystem, CodeSystemOIDs.RXNORM.codesystemOID());
                        t.update(insertQueryBuilder.toString());
                        insertQueryBuilder.clear();
                        insertQueryBuilder.append(codeTableInsertSQLPrefix);
                        
//                        t.update(codeTableInsertSQLPrefix,code,displayName.toUpperCase(),codeSystem,CodeSystemOIDs.RXNORM.codesystemOID());

//                        if ((++totalCount % 5000) == 0) {
//                            doInsert(insertQueryBuilder.toString(), connection);
//                            insertQueryBuilder.clear();
//                            insertQueryBuilder.append(insertQueryPrefix);
//                            pendingCount = 0;
//                        }
                    }
                }
            }
//            if (pendingCount > 0) {
//                doInsert(insertQueryBuilder.toString(), connection);
//            }
        } catch (IOException e) {
            logger.error(e);
//        } catch (SQLException e) {
//            e.printStackTrace();
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
