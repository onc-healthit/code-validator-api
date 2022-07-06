package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(LoincLoader.class);
    private static final String ACTIVE_CODE = "ACTIVE";
    private static final String STATUS_ROW_HEADER_VALUE = "STATUS";
    private static final int CODE_INDEX = 0;
    private static final int COMPONENT_INDEX = 1;
    private static final int LATEST_LOINC_CSV_STATUS_INDEX = 11;
    private static final int LATEST_LOINC_CSV_SHORT_NAME_INDEX = 22;
    private static final int LATEST_LOINC_CSV_LONG_COMMON_NAME_INDEX = 28;
    private static final int FORMER_LOINC_CSV_STATUS_INDEX = LATEST_LOINC_CSV_STATUS_INDEX + 1;
    private static final int FORMER_LOINC_CSV_SHORT_NAME_INDEX = LATEST_LOINC_CSV_SHORT_NAME_INDEX + 1;
    private static final int FORMER_LOINC_CSV_LONG_COMMON_NAME_INDEX = LATEST_LOINC_CSV_LONG_COMMON_NAME_INDEX + 1;    

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
                    // Default to legacy LOINC CSV indexes
                    int statusIndex = FORMER_LOINC_CSV_STATUS_INDEX;
                    int shortNameIndex = FORMER_LOINC_CSV_SHORT_NAME_INDEX;
                    int longCommonNameIndex = FORMER_LOINC_CSV_LONG_COMMON_NAME_INDEX;
                    while ((available = br.readLine()) != null) {
                        String[] line = available.replaceAll("^\"", "").split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
                    	String status = StringUtils.strip(line[LATEST_LOINC_CSV_STATUS_INDEX], "\"");                     	
                        if ((count++ == 0)) {
                        	// Analyze relevant data from header row to determine LOINC CSV version and update indexes if latest
                            if (status.equalsIgnoreCase(STATUS_ROW_HEADER_VALUE)) {
                                logger.info("Loading latest LOINC CSV / updating indexes");
                                statusIndex = LATEST_LOINC_CSV_STATUS_INDEX;
                                shortNameIndex = LATEST_LOINC_CSV_SHORT_NAME_INDEX;
                                longCommonNameIndex = LATEST_LOINC_CSV_LONG_COMMON_NAME_INDEX;
                            } else {
                            	logger.info("Loading Legacy LOINC CSV / using default indexes");
                            }
                        } else {
                            String codeSystem = file.getParentFile().getName();
                            String oid = CodeSystemOIDs.LOINC.codesystemOID();
                            
                            // Indexes OK for both versions
                            String code = StringUtils.strip(line[CODE_INDEX], "\"");
                            String componentName = StringUtils.strip(line[COMPONENT_INDEX], "\"");                                                     
                            // Indexes depend on version
                            status = StringUtils.strip(line[statusIndex], "\"");
                            String shortName = StringUtils.strip(line[shortNameIndex], "\"");
                            String longCommonName = StringUtils.strip(line[longCommonNameIndex], "\"");
                            
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
            logger.error(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    fileReader.close();
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

}
