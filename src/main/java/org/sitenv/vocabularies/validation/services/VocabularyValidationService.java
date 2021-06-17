package org.sitenv.vocabularies.validation.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.CodeValidatorApiConfiguration;
import org.sitenv.vocabularies.configuration.Configurations;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.configuration.ValidationConfigurationLoader;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.constants.VocabularyConstants.LogSeverity;
import org.sitenv.vocabularies.constants.VocabularyConstants.SeverityLevel;
import org.sitenv.vocabularies.validation.NodeValidation;
import org.sitenv.vocabularies.validation.NodeValidatorFactory;
import org.sitenv.vocabularies.validation.dto.GlobalCodeValidatorResults;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.utils.CCDADocumentNamespaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by Brian on 2/10/2016.
 */
@Service
@Scope(value = "prototype",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VocabularyValidationService {
    @Resource(name="vocabularyValidationConfigurations")
    List<ConfiguredExpression> vocabularyValidationConfigurations;
    @Resource(name="documentBuilder")
    DocumentBuilder documentBuilder;
    @Resource(name="xPathFactory")
    XPathFactory xPathFactory;
    @Autowired
    NodeValidatorFactory vocabularyValidatorFactory;
    @Autowired
    private ServletContext context;
    @Resource(name="globalCodeValidatorResults")
    GlobalCodeValidatorResults globalCodeValidatorResults;
    
    private static Logger logger = Logger.getLogger(VocabularyValidationService.class);
    private static final boolean FULL_LOG = false;
    
    public List<VocabularyValidationResult> validate(String uri) throws IOException, SAXException {
    	return this.validate(uri, VocabularyConstants.Config.DEFAULT);
    }
    
    public List<VocabularyValidationResult> validate(String uri, String vocabularyConfig) throws IOException, SAXException {
    	return validate(uri, vocabularyConfig, SeverityLevel.INFO);
    }

	public List<VocabularyValidationResult> validate(String uri, String vocabularyConfig, SeverityLevel severityLevel)
			throws IOException, SAXException {
        Document doc = documentBuilder.parse(uri);
        return this.validate(doc, vocabularyConfig, severityLevel);
    }

    public List<VocabularyValidationResult> validate(InputStream stream) throws IOException, SAXException {
    	return this.validate(stream, VocabularyConstants.Config.DEFAULT);
    }
    
	public List<VocabularyValidationResult> validate(InputStream stream, String vocabularyConfig)
			throws IOException, SAXException {
		return validate(stream, vocabularyConfig, SeverityLevel.INFO);
    }
	
	public List<VocabularyValidationResult> validate(InputStream stream, String vocabularyConfig, SeverityLevel severityLevel)
			throws IOException, SAXException {
        Document doc = documentBuilder.parse(stream);
        return this.validate(doc, vocabularyConfig, severityLevel);
    }

	public List<VocabularyValidationResult> validate(Document doc) {
		return this.validate(doc, VocabularyConstants.Config.DEFAULT);
	}
	
	public List<VocabularyValidationResult> validate(Document doc, String vocabularyConfig) {
		return this.validate(doc, vocabularyConfig, SeverityLevel.INFO);
	}
	
    public List<VocabularyValidationResult> validate(Document doc, String vocabularyConfig, SeverityLevel severityLevel) {
        Map<String, ArrayList<VocabularyValidationResult>> vocabularyValidationResultMap = getInitializedResultMap();
        if (doc != null) {
            String configuredXpathExpression = "";
            try {
                XPath xpath = getNewXpath(doc);                         
                
                if (Boolean.parseBoolean(context.getInitParameter("referenceccda.isDynamicVocab"))) {
                	String suffix = "based on vocabularyConfig input: " + vocabularyConfig;
	                if (useDynamicVocab(vocabularyConfig)) {	                
	                	logger.info("useDynamicConfig was successful " + suffix + " but may have used default (see prior log).");
	                } else {
	                	logger.error("useDynamicConfig (including attempt to load default dynamically) failed " + suffix);
	                }	                
                } else {
                	logger.info("Property referenceccda.isDynamicVocab is false; "
                			+ "using preloaded default config for this and all future validations.");
                }
                
                if (vocabularyValidationConfigurations != null) {
                	validate(vocabularyValidationResultMap, configuredXpathExpression, xpath, doc, severityLevel);
                } else {
                	logger.error("Vocabulary validation was not run as vocabularyValidationConfigurations is null");
                }
            } catch (XPathExpressionException e) {
                System.err.println("ERROR VALIDATING DOCUMENT FOR THE FOLLOWING CONFIGURED EXPRESSION: " + configuredXpathExpression);
            }
        }
        return convertMapToList(vocabularyValidationResultMap, severityLevel);
    }
    
	private void limitConfiguredExpressionsBySeverity(SeverityLevel severityLevelLimit) {
		// This improves performance since it is run before the expressions are processed
		logger.info("limiting configured expressions by severity level: " + severityLevelLimit.name());
		for (ConfiguredExpression configuredExpression : vocabularyValidationConfigurations) {
			Iterator<ConfiguredValidator> validatorIter = configuredExpression.getConfiguredValidators().iterator();
			while (validatorIter.hasNext()) {		
				ConfiguredValidator configuredValidator = validatorIter.next();
				// NodeCodeSystemMatchesConfiguredCodeSystemValidator defaults to ERROR severity dynamically
				if (!configuredValidator.getName().equalsIgnoreCase("NodeCodeSystemMatchesConfiguredCodeSystemValidator")) {
					SeverityLevel configuredSeverityLevelConversion = configuredValidator
							.getConfiguredValidationResultSeverityLevel().getSeverityLevelConversion();
					switch (severityLevelLimit) {
					case INFO:
						// no changes required as we process everything
						break;
					case WARNING:
						if (configuredSeverityLevelConversion == SeverityLevel.INFO) {
							// remove may/info configurations so we only process
							// warnings and errors
							validatorIter.remove();
						}
						break;
					case ERROR:
						if (configuredSeverityLevelConversion == SeverityLevel.INFO
								|| configuredSeverityLevelConversion == SeverityLevel.WARNING) {
							// remove may/info and should/warning configurations so
							// we only process warnings and errors						
							validatorIter.remove();
						}
						break;
					}					
				}
			}			
		}
		
		Iterator<ConfiguredExpression> expressionIter = vocabularyValidationConfigurations.iterator();
		while (expressionIter.hasNext()) {
			ConfiguredExpression configuredExpression = expressionIter.next();
			if (configuredExpression.getConfiguredValidators().isEmpty()) {
				expressionIter.remove();
			}
		}
		
	}
	
	private int determineConfigurationsErrorCount() {
		int errorCount = 0;
		for (ConfiguredExpression expression : vocabularyValidationConfigurations) {
			for (ConfiguredValidator validator : expression.getConfiguredValidators()) {
				// NodeCodeSystemMatchesConfiguredCodeSystemValidator dynamically resolves to
				// SHALL/does not have codeSeverityLevel in the config / may be null
				if (validator.getName().equalsIgnoreCase("NodeCodeSystemMatchesConfiguredCodeSystemValidator")) {
					errorCount++;
				} else {
					if (validator.getConfiguredValidationResultSeverityLevel() != null
							&& validator.getConfiguredValidationResultSeverityLevel().getCodeSeverityLevel() != null) {
						SeverityLevel configuredSeverityLevelConversion = validator
								.getConfiguredValidationResultSeverityLevel().getSeverityLevelConversion();
						if (configuredSeverityLevelConversion == SeverityLevel.ERROR) {
							errorCount++;
						}
					}
				}
			}
		}
		return errorCount;
	}
	
	private void validate(Map<String, ArrayList<VocabularyValidationResult>> vocabularyValidationResultMap,
			String configuredXpathExpression, XPath xpath, Document doc, SeverityLevel severityLevel)
			throws XPathExpressionException {
		if(severityLevel != SeverityLevel.INFO) {
			limitConfiguredExpressionsBySeverity(severityLevel);
		}
		
		globalCodeValidatorResults.setVocabularyValidationConfigurationsCount(
				vocabularyValidationConfigurations != null ? vocabularyValidationConfigurations.size() : 0);
		globalCodeValidatorResults.setVocabularyValidationConfigurationsErrorCount(
				vocabularyValidationConfigurations != null ? determineConfigurationsErrorCount() : 0);		
				
        for (ConfiguredExpression configuredExpression : vocabularyValidationConfigurations) {
            configuredXpathExpression = configuredExpression.getConfiguredXpathExpression();
            NodeList nodes = findAllDocumentNodesByXpathExpression(xpath, configuredXpathExpression, doc);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
                boolean validNode = false;
                Iterator configIterator = configuredExpression.getConfiguredValidators().iterator();
                while(configIterator.hasNext() && !validNode){
                    ConfiguredValidator configuredValidator = (ConfiguredValidator) configIterator.next();
                    NodeValidation vocabularyValidator = selectVocabularyValidator(configuredValidator);
					List<VocabularyValidationResult> tempResults = vocabularyValidator.validateNode(configuredValidator,
							xpath, node, i);
					if (foundValidationError(tempResults)) {
						vocabularyValidationResults.addAll(tempResults);
					} else {
						vocabularyValidationResults.clear();
						vocabularyValidationResults.addAll(tempResults);
						validNode = true;
					}
                }

				for (VocabularyValidationResult vocabularyValidationResult : vocabularyValidationResults) {
					vocabularyValidationResult.getNodeValidationResult()
							.setConfiguredXpathExpression(configuredXpathExpression);
					vocabularyValidationResultMap
							.get(vocabularyValidationResult.getVocabularyValidationResultLevel().getResultType())
							.add(vocabularyValidationResult);
				}

            }

        }
    }
	
	public GlobalCodeValidatorResults getGlobalCodeValidatorResults() {
		return globalCodeValidatorResults;
	}
	
	protected NodeValidation selectVocabularyValidator(ConfiguredValidator configuredValidator) {
		return vocabularyValidatorFactory.getVocabularyValidator(configuredValidator.getName());
	}
    
    private boolean useDynamicVocab(String vocabularyConfig) {
    	logger.info("Attempting to overwrite pre-loaded vocabulary configuration dynamically "
    			+ "with the following provided custom file: " + vocabularyConfig);
    	final String dynamicConfigsFolderPath = context.getInitParameter("referenceccda.configFolder");
        final String dynamicConfigsFilePath = ValidationConfigurationLoader.createFullFilePath(dynamicConfigsFolderPath, vocabularyConfig);
        boolean isValidVocabularyConfigFilePath = isValidVocabularyConfigPath(dynamicConfigsFilePath);		
		if (isValidVocabularyConfigFilePath) {
			logger.info("Using dynamic vocab as file path is valid");
			return useDynamicVocabImpl(dynamicConfigsFilePath);
		}		
		logger.info("Using default vocab file path since dynamic file path is invalid");
		return useDynamicVocabImpl(null);
    }
    
    private boolean isValidVocabularyConfigPath(String filePath) {
		if (filePath != null && !filePath.isEmpty()) {
			File file = new File(filePath);
			if (file.exists() && file.isFile()) {
				return true;
			}
			return dynamicVocabErrorReporter("filePath: '" + filePath + "' does not exist or is not a file: ", LogSeverity.ERROR);
		}
		if(filePath == null) {
			return dynamicVocabErrorReporter("filePath is null.", LogSeverity.ERROR);
		}
		return dynamicVocabErrorReporter("filePath is empty.", LogSeverity.ERROR);
	}

	private boolean useDynamicVocabImpl(String filePath) {    	
    	ValidationConfigurationLoader validationConfigurationLoader = new ValidationConfigurationLoader();        	        	
    	if (filePath != null) {
    		logger.info("Using dynamic folder based vocabulary configuration with path " + filePath);
    		validationConfigurationLoader.setValidationConfigurationFilePath(filePath);
    	} else {
        	logger.info("Attempting to use default vocabulary configuration due to issue with dynamic config");
            final String defaultConfigsFilePath = context.getInitParameter("referenceccda.configFile");
            if (isValidVocabularyConfigPath(defaultConfigsFilePath)) {
                logger.info("File with path extracted from properties and being set: " + defaultConfigsFilePath);
        		validationConfigurationLoader.setValidationConfigurationFilePath(defaultConfigsFilePath);                	
            } else {
            	return dynamicVocabErrorReporter("Error: There is no valid vocabulary path in the supplied in referenceccdaservice.xml"
            			+ ". The dynamic path is " + (filePath == null ? "null" : filePath) + " and the default path is "
            			+ (defaultConfigsFilePath == null ? "null" : defaultConfigsFilePath) + ". No vocabulary will be used.",
            			LogSeverity.ERROR);
            }
    	}
    	
        validationConfigurationLoader.setUnmarshaller(CodeValidatorApiConfiguration.castorMarshaller());
        
		final String storedPath = validationConfigurationLoader.getValidationConfigurationFilePath();
		if (isValidVocabularyConfigPath(storedPath)) {
        	logger.info("Setting configurations with " + storedPath);
			try {
				validationConfigurationLoader.afterPropertiesSet();
			} catch (Exception e) {
				logger.error("Error setting configurations with validationConfigurationLoader.afterPropertiesSet()");
				e.printStackTrace();
				return false;
			}
			final Configurations configurations = validationConfigurationLoader.getConfigurations();
			if (configurations != null) {
				return overwriteVocabularyValidationConfigurations(validationConfigurationLoader);
			}
			return dynamicVocabErrorReporter("validationConfigurationLoader.getConfigurations() is null.", LogSeverity.ERROR);		        			
		}
		return false;
    }
    
    private boolean overwriteVocabularyValidationConfigurations(ValidationConfigurationLoader validationConfigurationLoader) {
    	if (validationConfigurationLoader.getConfigurations().getExpressions() != null) {
	        List<ConfiguredExpression> tempVocabularyValidationExpressions =  
	        		CodeValidatorApiConfiguration.vocabularyValidationConfigurations(validationConfigurationLoader);
	        if (tempVocabularyValidationExpressions != null && !tempVocabularyValidationExpressions.isEmpty()) {
	        	logger.info("overwriteVocabularyValidationConfigurations() in progress: "
	        			+ "List of tempVocabularyValidationExpressions are neither null nor empty.");
	        	this.vocabularyValidationConfigurations = new ArrayList<ConfiguredExpression>(tempVocabularyValidationExpressions);
	        	if (FULL_LOG) {
		        	logger.info("Configured Expressions:");
		        	for (ConfiguredExpression expression : vocabularyValidationConfigurations) {
		        		logger.info(expression.toString());
		        	}
	        	}
	        	return true;
	        } else {
	        	if(tempVocabularyValidationExpressions == null) {
	        		return dynamicVocabErrorReporter("tempVocabularyValidationExpressions is null.", LogSeverity.ERROR);
	        	}
	        	return dynamicVocabErrorReporter("tempVocabularyValidationExpressions is empty.", LogSeverity.WARN);
	        }
    	}
    	return dynamicVocabErrorReporter("validationConfigurationLoader.getConfigurations().getExpressions() is null", 
    			LogSeverity.ERROR);
    }
    
    private static boolean dynamicVocabErrorReporter(String message, LogSeverity severity) {
        final String errorSuffix = " Cannot update config dynamically.";
        final String finalMessage = message + errorSuffix;
    	switch (severity) {
		case ERROR:
			logger.error(finalMessage);
			break;
		case WARN:
			logger.warn(finalMessage);
			break;
		case INFO:
			logger.info(finalMessage);
			break;
		}
    	return false;
    }        

    private Map<String, ArrayList<VocabularyValidationResult>> getInitializedResultMap() {
        Map<String, ArrayList<VocabularyValidationResult>> resultMap = new HashMap<>();
        resultMap.put("errors", new ArrayList<VocabularyValidationResult>());
        resultMap.put("warnings", new ArrayList<VocabularyValidationResult>());
        resultMap.put("info", new ArrayList<VocabularyValidationResult>());
        return resultMap;
    }

    private XPath getNewXpath(final Document doc){
        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                String nameSpace;
                if(CCDADocumentNamespaces.sdtc.name().equals(prefix)) {
                    nameSpace = CCDADocumentNamespaces.sdtc.getNamespace();
                } else if(CCDADocumentNamespaces.xsi.name().equals(prefix)) {
                	nameSpace = CCDADocumentNamespaces.xsi.getNamespace();
                } else {
                    nameSpace = CCDADocumentNamespaces.defaultNameSpaceForCcda.getNamespace();
                }
                return nameSpace;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        return xpath;
    }

    private static NodeList findAllDocumentNodesByXpathExpression(XPath xpath, String configuredXpath, Document doc) throws XPathExpressionException {
        NodeList result = (NodeList) xpath.compile(configuredXpath).evaluate(doc, XPathConstants.NODESET);
        return result;
    }

    private boolean foundValidationError(List<VocabularyValidationResult> results){
        for(VocabularyValidationResult result : results){
            if(result.getVocabularyValidationResultLevel().equals(VocabularyValidationResultLevel.SHALL)){
                return true;
            }
        }
        return false;
    }

    private List<VocabularyValidationResult> convertMapToList(Map<String, ArrayList<VocabularyValidationResult>> resultMap, 
    		SeverityLevel severityLevel) {
        List<VocabularyValidationResult> results = new ArrayList<>();
        for(ArrayList<VocabularyValidationResult> resultList : resultMap.values()){
            results.addAll(resultList);
        }
        limitSeverityForDynamicallySetValidators(results, severityLevel);
        return results;
    }
    
	private void limitSeverityForDynamicallySetValidators(List<VocabularyValidationResult> results,
			SeverityLevel severityLevelLimit) {
		// This cannot improve performance since it is run after the expressions are processed
		// This is an exception to cleanup after dynamically set configurations which require processing to determine their severity
		Iterator<VocabularyValidationResult> resultsIter = results.iterator();
		while (resultsIter.hasNext()) {
			VocabularyValidationResult result = resultsIter.next();
			VocabularyValidationResultLevel resultLevel = result.getVocabularyValidationResultLevel();
			switch (severityLevelLimit) {
			case INFO:
				break;
			case WARNING:
				if (resultLevel == VocabularyValidationResultLevel.MAY) {
					resultsIter.remove();
				}
				break;
			case ERROR:
				if (resultLevel == VocabularyValidationResultLevel.MAY
						|| resultLevel == VocabularyValidationResultLevel.SHOULD) {
					resultsIter.remove();
				}
				break;
			}
		}
	}	
	
}
