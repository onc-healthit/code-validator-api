package org.sitenv.vocabularies.servlet.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.data.OrientDbCredentials;
import org.sitenv.vocabularies.data.VocabularyDataStore;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.vocabularies.watchdog.RepositoryWatchdog;

import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class VocabularyValidationListener implements ServletContextListener {

	private RepositoryWatchdog watchdog = null;
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
		logger.debug("Stopping the watchdog...");
		if (watchdog != null) {
			watchdog.stop();
		}
		logger.debug("Watchdog stopped...");
		
		logger.debug("Stopping the Orient DB server...");
		VocabularyDataStore.getInstance().getOrientDbServer().shutdown();
		VocabularyDataStore.getInstance().setOrientDbServer(null);
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
				
				server.startup(new File(props.getProperty("vocabulary.orientDbConfigFile")));
				
				server.activate();
				
				VocabularyDataStore.getInstance().setOrientDbServer(server);
				logger.debug("Orient DB server initialized...");

				OrientDbCredentials primary = new OrientDbCredentials ();
				primary.setConnectionInfo("remote:localhost/primary");
				primary.setPassword("admin");
				primary.setUsername("admin");
				VocabularyDataStore.getInstance().setPrimaryNodeCredentials(primary);
				

				OrientDbCredentials secondary = new OrientDbCredentials ();
				secondary.setConnectionInfo("remote:localhost/secondary");
				secondary.setPassword("admin");
				secondary.setUsername("admin");
				VocabularyDataStore.getInstance().setSecondaryNodeCredentials(secondary);
				
			}
			catch (Exception e)
			{
				logger.error("Could not initialize the DataStore repository", e);
			}
			
			logger.debug("Initializing the validation engine...");
			watchdog = ValidationEngine.initialize(props.getProperty("vocabulary.localRepositoryDir"));
			logger.debug("Validation Engine initialized...");
			
			
			
		}
		catch (IOException e)
		{
			logger.error("Error initializing the Validation engine.", e);
		}
	}

	
	
}
