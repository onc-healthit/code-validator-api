package org.sitenv.vocabularies.test.other;

import static org.sitenv.vocabularies.test.other.ValidationLogger.println;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.sitenv.vocabularies.configuration.CodeValidatorApiConfiguration;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.constants.VocabularyConstants.SeverityLevel;
import org.sitenv.vocabularies.validation.NodeValidatorFactory;
import org.sitenv.vocabularies.validation.dto.GlobalCodeValidatorResults;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.services.VocabularyValidationService;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

public class VocabularyValidationTester {

	private CodeValidatorApiConfiguration codeValidatorConfig;
	private TestableVocabularyValidationService vocabularyValidationService;

	List<ConfiguredExpression> vocabularyValidationConfigurations;
	private DocumentBuilderFactory documentBuilderFactory;
	XPathFactory xPathFactory;
	NodeValidatorFactory vocabularyValidatorFactory;
	ServletContext context;
	GlobalCodeValidatorResults globalCodeValidatorResults;

	@Before
	public void initialize() {
		codeValidatorConfig = new CodeValidatorApiConfiguration();
		intializeVocabularyValidationServiceFields();
	}

	private void intializeVocabularyValidationServiceFields() {
		vocabularyValidationConfigurations = new ArrayList<ConfiguredExpression>();
		try {
			documentBuilderFactory = codeValidatorConfig.documentBuilderFactory();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		vocabularyValidatorFactory = codeValidatorConfig.vocabularyValidatorFactory();
		xPathFactory = codeValidatorConfig.xPathFactory();
		context = new MockServletContext();
		globalCodeValidatorResults = new GlobalCodeValidatorResults();
	}

	public void setupInitParameters(boolean isFileBasedConfig) {
		if (isFileBasedConfig) {
			println("Setting up for file based config...");
			final String resourcePath = "src/test/resources";
			context.setInitParameter("vocabulary.localCodeRepositoryDir", "C:/Programming/SITE/code_repository");
			context.setInitParameter("vocabulary.localValueSetRepositoryDir",
					"C:/Programming/SITE/valueset_repository");
			context.setInitParameter("referenceccda.isDynamicVocab", "true");
			context.setInitParameter("referenceccda.configFolder", resourcePath);
			context.setInitParameter("content.scenariosDir", "C:/Programming/SITE/scenarios");
		} else {
			println("Setting up for programmable config...");
			context.setInitParameter("referenceccda.isDynamicVocab", "false");
		}
	}
	
	public static ConfiguredExpression createConfiguredExpression(String validatorName, ConfiguredValidationResultSeverityLevel severity,
			String requiredNodeName, String validationMessage, String configuredXpathExpression) {
		ConfiguredValidator configuredValidator = new ConfiguredValidator();
		configuredValidator.setName(validatorName);
		configuredValidator.setConfiguredValidationResultSeverityLevel(severity);
		configuredValidator.setRequiredNodeName(requiredNodeName);
		configuredValidator.setValidationMessage(validationMessage);
		ConfiguredExpression configuredExpression = new ConfiguredExpression();
		configuredExpression
				.setConfiguredValidators(new ArrayList<ConfiguredValidator>(Arrays.asList(configuredValidator)));
		configuredExpression.setConfiguredXpathExpression(configuredXpathExpression);
		return configuredExpression;
	}

	public void programmaticallyConfigureRequiredNodeValidator(ConfiguredValidationResultSeverityLevel severity,
			String requiredNodeName, String validationMessage, String configuredXpathExpression) {		
		ConfiguredExpression configuredExpression = createConfiguredExpression("RequiredNodeValidator", severity,
				requiredNodeName, validationMessage, configuredXpathExpression);		
		vocabularyValidationConfigurations = new ArrayList<ConfiguredExpression>();
		vocabularyValidationConfigurations.addAll(Arrays.asList(configuredExpression));
	}
	
	public void addConfiguredExpressionsToVocabularyValidationConfigurations (List<ConfiguredExpression> configuredExpressions) {
		vocabularyValidationConfigurations = new ArrayList<ConfiguredExpression>();
		vocabularyValidationConfigurations.addAll(configuredExpressions);
	}

	public void injectDependencies() {
		vocabularyValidationService = new TestableVocabularyValidationService();
		ReflectionTestUtils.setField(vocabularyValidationService, "vocabularyValidationConfigurations",
				vocabularyValidationConfigurations);
		ReflectionTestUtils.setField(vocabularyValidationService, "documentBuilderFactory", documentBuilderFactory);
		ReflectionTestUtils.setField(vocabularyValidationService, "xPathFactory", xPathFactory);
		ReflectionTestUtils.setField(vocabularyValidationService, "vocabularyValidatorFactory",
				vocabularyValidatorFactory);
		ReflectionTestUtils.setField(vocabularyValidationService, "context", context);
		ReflectionTestUtils.setField(vocabularyValidationService, "globalCodeValidatorResults",
				globalCodeValidatorResults);
	}

	public List<VocabularyValidationResult> testVocabularyValidator(URI filePath) {
		return testVocabularyValidator(filePath, VocabularyConstants.Config.DEFAULT);
	}

	public List<VocabularyValidationResult> testVocabularyValidator(URI filePath, String vocabularyConfig) {
		return testVocabularyValidator(filePath, vocabularyConfig, SeverityLevel.INFO);
	}

	public List<VocabularyValidationResult> testVocabularyValidator(URI filePath, String vocabularyConfig,
			SeverityLevel severityLevel) {
		List<VocabularyValidationResult> results = new ArrayList<>();
		if (vocabularyConfig.endsWith(".xml")) {
			vocabularyConfig = vocabularyConfig.replace(".xml", "");
		}
		try {
			results = vocabularyValidationService.validate(filePath.toString(), vocabularyConfig, severityLevel);
			println("results.size(): " + results.size());
			for (VocabularyValidationResult result : results) {
				println(result.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return results;
	}

	public static boolean hasVocabularyIssue(List<VocabularyValidationResult> results) {
		return results != null && !results.isEmpty();
	}

	public static boolean isResultMatchingExpectedResult(List<VocabularyValidationResult> results,
			VocabularyValidationResultLevel expectedVocabularyValidationResultLevel, String expectedMessage) {
		boolean isMatching = false;
		if (!hasVocabularyIssue(results)) {
			return false;
		}
		for (VocabularyValidationResult result : results) {
			if (result.getVocabularyValidationResultLevel().equals(expectedVocabularyValidationResultLevel)) {
				if (result.getMessage().equals(expectedMessage)) {
					return true;
				}
			}

		}
		return isMatching;
	}

	public GlobalCodeValidatorResults getGlobalCodeValidatorResults() {
		return globalCodeValidatorResults;
	}

	/**
	 * 
	 * @return a testable version of VocabularyValidationService. *Note*: This
	 *         is NULL without initialize(), setupInitParameters(x),
	 *         injectDependencies(); i.e. it must be setup the same as any other
	 *         test
	 */
	public VocabularyValidationService getVocabularyValidationService() {
		return vocabularyValidationService;
	}

}
