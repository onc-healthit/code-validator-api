package org.sitenv.vocabularies.loader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.validation.dao.CodeSystemCodeDAO;
import org.sitenv.vocabularies.validation.dao.ValueSetDAO;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Brian on 2/6/2016.
 */
@PropertySource("classpath:CodeValidator.properties")
public class VocabularyLoadRunner implements InitializingBean, DisposableBean {
    private VocabularyLoaderFactory vocabularyLoaderFactory;
    private static Logger logger = Logger.getLogger(VocabularyLoadRunner.class);
    private String codeDirectory = null;
    private String valueSetDirectory = null;
    private boolean recursive = true;
    private DataSource dataSource;

    @Value("${cleanUpDatabaseAfterLoadingHashSets}")
    private boolean cleanUpDatabaseAfterLoadingHashSets;
    
    @Autowired
    ValueSetDAO vsdao;

    @Autowired
    CodeSystemCodeDAO csdao;
    
    
    public void loadDirectory(String directory, DataSource datasource) throws IOException {
        File dir = new File(directory);
        if (dir.isFile()) {
            throw new IOException("Directory to Load is a file and not a directory");
        } else {
            File[] list = dir.listFiles();
            for (File file : list) {
                load(file, datasource);
            }
        }
    }
    

    private void load(File directory, DataSource datasource) throws IOException {
        if (directory.isDirectory() && !directory.isHidden()) {
            File[] filesToLoad = directory.listFiles();
            logger.debug("Building Loader for directory: " + directory.getName() + "...");
            VocabularyLoader loader = vocabularyLoaderFactory.getVocabularyLoader(directory.getName());
            if (loader != null && filesToLoad != null) {
                logger.debug("Loader built...");
                logger.info("Loading files in : " + directory.getName() + "...");
                long n = loader.load(Arrays.asList(filesToLoad), datasource);
                logger.info("File loaded...(" + n + ") entries.");
            } else {
                logger.debug("Building of Loader Failed.");
            }
        }
    }
    

    public String getCodeDirectory() {
        return codeDirectory;
    }

    public void setCodeDirectory(String codeDirectory) {
        this.codeDirectory = codeDirectory;
    }

    public String getValueSetDirectory() {
        return valueSetDirectory;
    }

    public void setValueSetDirectory(String valueSetDirectory) {
        this.valueSetDirectory = valueSetDirectory;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setVocabularyLoaderFactory(VocabularyLoaderFactory vocabularyLoaderFactory) {
        this.vocabularyLoaderFactory = vocabularyLoaderFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            if (codeDirectory != null && !codeDirectory.trim().equals("")) {
                logger.info("Loading vocabularies at: " + codeDirectory + "...");
              loadDirectory(codeDirectory, dataSource);
                logger.info("Vocabularies loaded...");
            }
            connection.commit();

            if (valueSetDirectory != null && !valueSetDirectory.trim().equals("")) {
                logger.info("Loading value sets at: " + valueSetDirectory + "...");
                loadDirectory(valueSetDirectory, dataSource);
                logger.info("Value Sets loaded...");
            }
            connection.commit();

            logger.info("!!!!*********** VOCABULARY DATABASE HAS FINISHED LOADING - SERVER WILL CONTINUE AND SHOULD BE DONE SHORTLY. ***********!!!!");

            vsdao.loadValueSets(dataSource);
            connection.commit();
            csdao.loadCodes(dataSource);
            connection.commit();
            
            logger.info("!!!!*********** INITIALIZED HASH SETS. ***********!!!!");

        } catch (Exception e) {
            logger.error("Failed to load configured vocabulary directory.", e);
        }finally {
            try {
				logger.info("!!!!*********** cleanUpDatabaseAfterLoadingHashSets falg set as  : "
						+ cleanUpDatabaseAfterLoadingHashSets);
				/*
				 * If the 'cleanUpDatabaseAfterLoadingHashSets' defined in
				 * CodeValidator.properties is set to true, then all the records
				 * from (Codes and ValueSets tables) the DB are deleted.
				 * 
				 * Note: If there are any methods that are defined in the
				 * Repository classes (CodeRepository, VsacValuesSetRepository
				 * etc.,) are not implemented in the DAO classes (ValueSetDAO,
				 * CodeSystemDAO) and the same methods are being used by the
				 * consumer/caller then the flag
				 * cleanUpDatabaseAfterLoadingHashSets should be set to false.
				 * Otherwise if this flag was set to true and when these methods
				 * are invoked, no records will be returned. Ex., In the current
				 * versionm, these three methods findByCodeAndCodeSystemIn;
				 * findByValuesetOidIn, findByCodeAndValuesetOidIn defined in
				 * CodeRepository and ValueSetRepository are not implemented in
				 * ValueSetDAO & CodeSystemDAO classes. Hence, if any
				 * client/consumer wants to leverage these helper methods,
				 * should set the flag to false.
				 * 
				 */
            	
            	if(cleanUpDatabaseAfterLoadingHashSets) {
            		perfromDBCleanup(connection);
            		logger.info("!!!!*********** DB Cleanup completed **************!!!!");
            	}
                if(connection != null && !(connection.isClosed())) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

	private void perfromDBCleanup(Connection connection) throws SQLException {
		csdao.cleanupDBAfterLoadingToHashSets(dataSource);
		connection.commit();
		
		vsdao.cleanupDBAfterLoadingToHashSets(dataSource);
		connection.commit();
	}

    @Override
    public void destroy() throws Exception {
        logger.info("Destroying Loader Bean. Loading is done.");
    }
}
