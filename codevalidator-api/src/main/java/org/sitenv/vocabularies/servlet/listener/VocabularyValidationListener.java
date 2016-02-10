package org.sitenv.vocabularies.servlet.listener;

public class VocabularyValidationListener {

//	private static Logger logger = Logger.getLogger(VocabularyValidationListener.class);
//	public static final XPathFactory XPATH = XPathFactory.newInstance();
//	private String configFileName;
//	private String primaryDbName;
//	private String secondaryDbName;
//	private String localCodeRepositoryDir;
//	private String localValueSetRepositoryDir;
//	private boolean propertiesLoaded;
//	private boolean startupLoader;
//
//	protected void loadProperties(ServletContextEvent servletContextEvent) throws IOException {
//		ServletContext ctx = servletContextEvent.getServletContext();
//		configFileName = ctx.getInitParameter("vocabulary.orientDbConfigFile");
//		primaryDbName = ctx.getInitParameter("vocabulary.primaryDbName");
//		secondaryDbName = ctx.getInitParameter("vocabulary.secondaryDbName");
//		localCodeRepositoryDir = ctx.getInitParameter("vocabulary.localCodeRepositoryDir");
//		localValueSetRepositoryDir = ctx.getInitParameter("vocabulary.localValueSetRepositoryDir");
//		startupLoader = Boolean.parseBoolean(ctx.getInitParameter("vocabulary.loadVocabulariesAtStartup"));
//		propertiesLoaded = true;
//
//	}
//
//
//	public void contextDestroyed(ServletContextEvent arg0) {
//		logger.debug("Stopping the vocabulary watchdog...");
//		if (ValidationEngine.getCodeWatchdogThread() != null) {
//			ValidationEngine.getCodeWatchdogThread().stop();
//		}
//		logger.debug("Vocabulary watchdog stopped...");
//
//		logger.debug("Stopping the value set watchdog...");
//		if (ValidationEngine.getValueSetWatchdogThread() != null) {
//			ValidationEngine.getValueSetWatchdogThread().stop();
//		}
//		logger.debug("Value set watchdog stopped...");
//
//		logger.debug("Stopping the Orient DB server...");
//		VocabularyRepository.getInstance().getOrientDbServer().shutdown();
//		VocabularyRepository.getInstance().setOrientDbServer(null);
//		logger.debug("Orient DB server stopped...");
//	}
//
//
//	public void contextInitialized(ServletContextEvent servletContextEvent) {
//		try
//		{
//			if (!propertiesLoaded)
//			{
//				this.loadProperties(servletContextEvent);
//			}
//
//			try
//			{
//				logger.debug("Intializing the Orient DB server...");
//				OServer server = OServerMain.create();
//				server.startup(new File(configFileName));
//				server.activate();
//
//				VocabularyRepository.getInstance().setOrientDbServer(server);
//				logger.debug("Orient DB server initialized...");
//
//				VocabularyRepositoryConnectionInfo primary = loadConnectionInfo(configFileName, primaryDbName);
//				if (primary != null)
//				{
//					VocabularyRepository.getInstance().setPrimaryNodeCredentials(primary);
//				}
//				else
//				{
//					throw new Exception("Could not load configuration for primary database node.");
//				}
//
//				VocabularyRepositoryConnectionInfo secondary = loadConnectionInfo(configFileName, secondaryDbName);
//				if (secondary != null)
//				{
//					VocabularyRepository.getInstance().setSecondaryNodeCredentials(secondary);
//				}
//				else
//				{
//					throw new Exception("Could not load configuration for secondary database node.");
//				}
//
//			}
//			catch (Exception e)
//			{
//				logger.error("Could not initialize the DataStore repository", e);
//			}
//
//			logger.debug("Initializing the validation engine...");
//			ValidationEngine.initialize(localCodeRepositoryDir, localValueSetRepositoryDir, startupLoader);
//			logger.debug("Validation Engine initialized...");
//
//		}
//		catch (IOException e)
//		{
//			logger.error("Error initializing the Validation engine.", e);
//		}
//	}
//
//	private VocabularyRepositoryConnectionInfo loadConnectionInfo(String configFile, String dbConnectionName)
//	{
//		try {
//
//			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = domFactory.newDocumentBuilder();
//
//			Document doc  = builder.parse(configFile);
//
//			if (doc != null)
//			{
//				XPath xpath = XPATH.newXPath();
//				XPathExpression expExpressionNode = xpath.compile("/orient-server/storages/storage[@name='"+ dbConnectionName +"']");
//				XPathExpression expUserPasswordAtt = xpath.compile("@userPassword");
//				XPathExpression expUserNameAtt = xpath.compile("@userName");
//
//				Node expressionNode = (Node)expExpressionNode.evaluate(doc, XPathConstants.NODE);
//
//				String userName = (String)expUserNameAtt.evaluate(expressionNode, XPathConstants.STRING);
//				String userPassword = (String)expUserPasswordAtt.evaluate(expressionNode, XPathConstants.STRING);
//
//				VocabularyRepositoryConnectionInfo config = new VocabularyRepositoryConnectionInfo ();
//				config.setConnectionInfo("remote:localhost/" + dbConnectionName);
//				config.setPassword(userPassword);
//				config.setUsername(userName);
//
//				return config;
//
//			}
//		}
//		catch (XPathExpressionException e)
//		{
//			logger.error(e);
//		}
//		catch (ParserConfigurationException e)
//		{
//			logger.error(e);
//		}
//		catch (SAXException e)
//		{
//			logger.error(e);
//		}
//		catch (IOException e)
//		{
//			logger.error(e);
//		}
//
//		return null;
//	}
//
//

}
