package org.sitenv.vocabularies.servlet.listener;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerConfigurationLoaderXml;
import com.orientechnologies.orient.server.config.OServerStorageConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.vocabularies.repository.VocabularyRepository;
import org.sitenv.vocabularies.repository.VocabularyRepositoryConnectionInfo;

public class VocabularyValidationListener implements ServletContextListener {

	private static Logger logger = Logger.getLogger(VocabularyValidationListener.class);
	
	private static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
	
	protected Properties props;
	
	protected void loadProperties() throws IOException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
		
		if (in == null)
		{
			props = null;
			throw new FileNotFoundException("Environment Properties File not found in class path.");
		}
		else
		{
			props = new Properties();
			props.load(in);
		}
	}
	
	
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("Stopping the vocabulary watchdog...");
		if (ValidationEngine.getCodeWatchdogThread() != null) {
			ValidationEngine.getCodeWatchdogThread().stop();
		}
		logger.debug("Vocabulary watchdog stopped...");
		
		logger.debug("Stopping the value set watchdog...");
		if (ValidationEngine.getValueSetWatchdogThread() != null) {
			ValidationEngine.getValueSetWatchdogThread().stop();
		}
		logger.debug("Value set watchdog stopped...");
		
		logger.debug("Stopping the Orient DB server...");
		VocabularyRepository.getInstance().getOrientDbServer().shutdown();
		VocabularyRepository.getInstance().setOrientDbServer(null);
		logger.debug("Orient DB server stopped...");
	}

	
	public void contextInitialized(ServletContextEvent arg0) {
		try
		{
			if (props == null)
			{
				this.loadProperties();
			}
			
			try
			{
				logger.debug("Intializing the Orient DB server...");
				OServer server = OServerMain.create();
				
				String configFileName = props.getProperty("vocabulary.orientDbConfigFile");
				String primaryDbName = props.getProperty("vocabulary.primaryDbName");
				String secondaryDbName = props.getProperty("vocabulary.secondaryDbName");

				OServerConfiguration serverConfiguration = new OServerConfiguration(new OServerConfigurationLoaderXml(OServerConfiguration.class,
					new File(configFileName)));
				
				server.startup(serverConfiguration);
				
				server.activate();
				
				VocabularyRepository.getInstance().setOrientDbServer(server);
				logger.debug("Orient DB server initialized...");

				VocabularyRepositoryConnectionInfo primary = loadConnectionInfo(serverConfiguration, primaryDbName);
				if (primary != null)
				{
					VocabularyRepository.getInstance().setPrimaryNodeCredentials(primary);
				}
				else
				{
					throw new Exception("Could not load configuration for primary database node.");
				}
				
				VocabularyRepositoryConnectionInfo secondary = loadConnectionInfo(serverConfiguration, secondaryDbName);
				if (secondary != null)
				{
					VocabularyRepository.getInstance().setSecondaryNodeCredentials(secondary);
				}
				else
				{
					throw new Exception("Could not load configuration for secondary database node.");
				}
				
				VocabularyRepository.getInstance().initializeDbConnectionPools();
				
			}
			catch (Exception e)
			{
				logger.error("Could not initialize the DataStore repository", e);
			}
			
			String loadAtStartup = props.getProperty("vocabulary.loadVocabulariesAtStartup");
			boolean startupLoader = true;
			
			if (loadAtStartup != null)
			{
				startupLoader = Boolean.parseBoolean(loadAtStartup);
			}
			
			logger.debug("Initializing the validation engine...");
			ValidationEngine.initialize(props.getProperty("vocabulary.localCodeRepositoryDir"), props.getProperty("vocabulary.localValueSetRepositoryDir"), startupLoader);
			logger.debug("Validation Engine initialized...");
			
		}
		catch (IOException e)
		{
			logger.error("Error initializing the Validation engine.", e);
		}
	}
	
	private VocabularyRepositoryConnectionInfo loadConnectionInfo(OServerConfiguration serverConfiguration, String dbConnectionName) {
		if (serverConfiguration.storages == null) {
			return null;
		}
		
		for (OServerStorageConfiguration serverStorageConfiguration : serverConfiguration.storages) {
			if (serverStorageConfiguration.name.equals(dbConnectionName)) {
				VocabularyRepositoryConnectionInfo config = new VocabularyRepositoryConnectionInfo();
				config.setConnectionInfo("remote:localhost/" + dbConnectionName);
				config.setPassword(serverStorageConfiguration.userPassword);
				config.setUsername(serverStorageConfiguration.userName);

				return config;
			}
		}
		
		return null;
	}
}
