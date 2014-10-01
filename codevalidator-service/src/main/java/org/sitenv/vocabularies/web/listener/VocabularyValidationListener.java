package org.sitenv.vocabularies.web.listener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.vocabularies.watchdog.RepositoryWatchdog;

public class VocabularyValidationListener implements ServletContextListener {

	private RepositoryWatchdog watchdog = null;
	private static Logger logger = Logger.getLogger(VocabularyValidationListener.class);
	
	public static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
	
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
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("Stopping the watchdog...");
		if (watchdog != null) {
			watchdog.stop();
		}
		logger.debug("Watchdog stopped...");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try
		{
			if (props == null)
			{
				this.loadProperties();
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
