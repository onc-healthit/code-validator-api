package org.sitenv.vocabularies.servlet.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.vocabularies.repository.VocabularyRepository;
import org.sitenv.vocabularies.repository.VocabularyRepositoryConnectionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class VocabularyValidationListener implements ServletContextListener {

	private static Logger logger = Logger.getLogger(VocabularyValidationListener.class);
	public static final XPathFactory XPATH = XPathFactory.newInstance();
	
	
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
				
				
				server.startup(new File(configFileName));
				
				server.activate();
				
				VocabularyRepository.getInstance().setOrientDbServer(server);
				logger.debug("Orient DB server initialized...");

				VocabularyRepositoryConnectionInfo primary = loadConnectionInfo(configFileName, primaryDbName);
				if (primary != null)
				{
					VocabularyRepository.getInstance().setPrimaryNodeCredentials(primary);
				}
				else
				{
					throw new Exception("Could not load configuration for primary database node.");
				}
				
				VocabularyRepositoryConnectionInfo secondary = loadConnectionInfo(configFileName, secondaryDbName);
				if (secondary != null)
				{
					VocabularyRepository.getInstance().setSecondaryNodeCredentials(secondary);
				}
				else
				{
					throw new Exception("Could not load configuration for secondary database node.");
				}
				
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
	
	private VocabularyRepositoryConnectionInfo loadConnectionInfo(String configFile, String dbConnectionName) 
	{
		try {
			
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			
			Document doc  = builder.parse(configFile);
			
			if (doc != null)
			{
				XPath xpath = XPATH.newXPath();
				XPathExpression expExpressionNode = xpath.compile("/orient-server/storages/storage[@name='"+ dbConnectionName +"']");
				XPathExpression expUserPasswordAtt = xpath.compile("@userPassword");
				XPathExpression expUserNameAtt = xpath.compile("@userName");
				
				Node expressionNode = (Node)expExpressionNode.evaluate(doc, XPathConstants.NODE);
				
				String userName = (String)expUserNameAtt.evaluate(expressionNode, XPathConstants.STRING);
				String userPassword = (String)expUserPasswordAtt.evaluate(expressionNode, XPathConstants.STRING);
				
				VocabularyRepositoryConnectionInfo config = new VocabularyRepositoryConnectionInfo ();
				config.setConnectionInfo("remote:localhost/" + dbConnectionName);
				config.setPassword(userPassword);
				config.setUsername(userName);
				
				return config;
				
			}
		} 
		catch (XPathExpressionException e) 
		{
			logger.error(e);
		} 
		catch (ParserConfigurationException e) 
		{
			logger.error(e);
		} 
		catch (SAXException e)
		{
			logger.error(e);
		}
		catch (IOException e) 
		{
			logger.error(e);
		}
		
		return null;
	}

	
	
}
