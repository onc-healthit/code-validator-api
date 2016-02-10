package org.sitenv.vocabularies.loader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by Brian on 2/6/2016.
 */
public class VocabularyLoadRunner implements InitializingBean, DisposableBean {
    private VocabularyLoaderFactory vocabularyLoaderFactory;
    private static Logger logger = Logger.getLogger(VocabularyLoadRunner.class);
    private String codeDirectory = null;
    private String valueSetDirectory = null;
    private boolean recursive = true;
    private DataSource dataSource;

    public void loadValueSetDirectory(String directory, Connection connection) throws IOException {
        File dir = new File(directory);
        if (dir.isFile()) {
            logger.debug("Directory to Load is a file and not a directory");
            throw new IOException("Directory to Load is a file and not a directory");
        } else {
            File[] list = dir.listFiles();
            for (File file : list) {
                load(file, connection);
            }
        }
    }

    public void loadCodeDirectory(String directory, Connection connection) throws IOException {
        File dir = new File(directory);
        if (dir.isFile()) {
            throw new IOException("Directory to Load is a file and not a directory");
        } else {
            File[] list = dir.listFiles();
            for (File file : list) {
                load(file, connection);
            }
        }
    }

    private void load(File directory, Connection connection) throws IOException {
        if (directory.isDirectory() && !directory.isHidden()) {
            File[] filesToLoad = directory.listFiles();
            logger.debug("Building Loader for directory: " + directory.getName() + "...");
            VocabularyLoader loader = vocabularyLoaderFactory.getVocabularyLoader(directory.getName());
            if (loader != null && filesToLoad != null) {
                logger.debug("Loader built...");
                //logger.debug("Loading file: " + loadFile.getAbsolutePath() + "...");
                loader.load(Arrays.asList(filesToLoad), connection);
                logger.debug("File loaded...");
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
                loadCodeDirectory(codeDirectory, connection);
                logger.info("Vocabularies loaded...");
            }

            connection.commit();

            if (valueSetDirectory != null && !valueSetDirectory.trim().equals("")) {
                logger.info("Loading value sets at: " + valueSetDirectory + "...");
                loadValueSetDirectory(valueSetDirectory, connection);
                logger.info("Value Sets loaded...");
            }
//
//            logger.info("Activating new Vocabularies Map...");
//            VocabularyRepository.getInstance().toggleActiveDatabase();
//            logger.info("New vocabulary Map Activated...");
//
//            if (codeDirectory != null && !codeDirectory.trim().equals("")) {
//                logger.info("Loading vocabularies to new inactive repository at: " + codeDirectory + "...");
//                loadCodeDirectory(codeDirectory, connection);
//                logger.info("Vocabularies loaded...");
//            }
//
//            if (valueSetDirectory != null && !valueSetDirectory.trim().equals("")) {
//                logger.info("Loading value sets to new inactive repository at: " + valueSetDirectory + "...");
//                loadValueSetDirectory(valueSetDirectory);
//                logger.info("Value Sets loaded...");
//            }

//            // recommendation from cwatson: load files back in the primary so both db's are
//            logger.info("Starting Vocabulary Watchdog...");
//            ValidationEngine.codeWatchdog = new RepositoryWatchdog(this.getCodeDirectory(), this.isRecursive(), false);
//            ValidationEngine.codeWatchdog.start();
//            logger.info("Vocabulary Watchdog started...");
//
//            logger.info("Starting Value Set Watchdog...");
//            ValidationEngine.valueSetWatchdog = new RepositoryWatchdog(this.getValueSetDirectory(), this.isRecursive(), false);
//            ValidationEngine.valueSetWatchdog.start();
//            logger.info("Vocabulary VsacValueSet started...");
            connection.commit();
        } catch (Exception e) {
            logger.error("Failed to load configured vocabulary directory.", e);
        }finally {
            try {
                if(connection != null || !(connection.isClosed())) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
