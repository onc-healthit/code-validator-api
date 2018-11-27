import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sitenv.vocabularies.configuration.CodeValidatorApiConfiguration;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.configuration.ValidationConfigurationLoader;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.validation.NodeValidatorFactory;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

public class VocabularyValidationServiceTest {

	private static final boolean LOG_RESULTS_TO_CONSOLE = true;
	private CodeValidatorApiConfiguration codeValidatorConfig;
	private TestableVocabularyValidationService vocabularyValidationService;

	List<ConfiguredExpression> vocabularyValidationConfigurations;
	DocumentBuilder documentBuilder;
	XPathFactory xPathFactory;
	NodeValidatorFactory vocabularyValidatorFactory;
	ServletContext context;

	private static final int MISSING_UNIT_ATTRIBUTE = 0, HAS_UNIT_ATTRIBUTE = 1;
	private static URI[] CCDA_FILES = new URI[0];
	static {
		try {
			CCDA_FILES = new URI[] {
					VocabularyValidationServiceTest.class.getResource("/unitTest1_NoUnitExpectFail.xml").toURI(),
					VocabularyValidationServiceTest.class.getResource("/unitTest2_hasUnitExpectPass.xml").toURI() };
		} catch (URISyntaxException e) {
			if (LOG_RESULTS_TO_CONSOLE)
				e.printStackTrace();
		}
		
		BasicConfigurator.configure();
	}

	private static final String ASSERT_MSG_NO_VOCABULARY_ISSUE_BUT_SHOULD = "A vocabulary issue does not exist when it should";
	private static final String ASSERT_MSG_HAS_VOCABULARY_ISSUE_BUT_SHOULD_NOT = "A vocabulary issue exists but it should not exist";
	private static final String ASSERT_MSG_SEVERITY_OR_MESSAGE_DOES_NOT_MATCH_BUT_SHOULD = "Severity or message does not match but should";
	private static final String ASSERT_MSG_SEVERITY_WITH_MESSAGE_MATCHES_BUT_SHOULD_NOT = "The specified severity with message exists but should not";

	@Before
	public void initialize() {		
		codeValidatorConfig = new CodeValidatorApiConfiguration();
		intializeVocabularyValidationServiceFields();
	}
	
	private void intializeVocabularyValidationServiceFields() {
		vocabularyValidationConfigurations = new ArrayList<ConfiguredExpression>();
		try {
			documentBuilder = codeValidatorConfig.documentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		vocabularyValidatorFactory = codeValidatorConfig.vocabularyValidatorFactory();
		xPathFactory = codeValidatorConfig.xPathFactory();
		context = new MockServletContext();
	}

	private void setupInitParameters(boolean isFileBasedConfig) {
		if(isFileBasedConfig) {
			final String resourcePath = "src/test/resources";
	        context.setInitParameter("vocabulary.localCodeRepositoryDir", "C:/Programming/SITE/code_repository");
	        context.setInitParameter("vocabulary.localValueSetRepositoryDir", "C:/Programming/SITE/valueset_repository");
	        context.setInitParameter("referenceccda.isDynamicVocab", "true");
	        context.setInitParameter("referenceccda.configFolder", resourcePath);
	        context.setInitParameter("content.scenariosDir", "C:/Programming/SITE/scenarios");
		} else {
			context.setInitParameter("referenceccda.isDynamicVocab", "false");	
		}
	}
		
	private void programmaticallyConfigureRequiredNodeValidator(ConfiguredValidationResultSeverityLevel severity,
			String requiredNodeName, String validationMessage, String configuredXpathExpression) {
		ConfiguredValidator configuredValidator = new ConfiguredValidator();
		configuredValidator.setName("RequiredNodeValidator");
		configuredValidator.setConfiguredValidationResultSeverityLevel(severity);
		configuredValidator.setRequiredNodeName(requiredNodeName);
		configuredValidator.setValidationMessage(validationMessage);
		ConfiguredExpression configuredExpression = new ConfiguredExpression();
		configuredExpression
				.setConfiguredValidators(new ArrayList<ConfiguredValidator>(Arrays.asList(configuredValidator)));
		configuredExpression.setConfiguredXpathExpression(configuredXpathExpression);
		vocabularyValidationConfigurations = new ArrayList<ConfiguredExpression>();
		vocabularyValidationConfigurations.addAll(Arrays.asList(configuredExpression));
	}

	private void injectDependencies() {
		vocabularyValidationService = new TestableVocabularyValidationService();
		ReflectionTestUtils.setField(vocabularyValidationService, "vocabularyValidationConfigurations",
				vocabularyValidationConfigurations);
		ReflectionTestUtils.setField(vocabularyValidationService, "documentBuilder", documentBuilder);
		ReflectionTestUtils.setField(vocabularyValidationService, "xPathFactory", xPathFactory);
		ReflectionTestUtils.setField(vocabularyValidationService, "vocabularyValidatorFactory",
				vocabularyValidatorFactory);
		ReflectionTestUtils.setField(vocabularyValidationService, "context", context);
	}

	@Test
	public void requiredNodeValidatorMissingAttributeTest() {
		/* XML example - expect error for NO @unit
		<observation classCode="OBS" moodCode="EVN">
			...
			<value xsi:type="PQ" value="1.015"/>
			...
		</observation> */	
		String validationMessage = "If Observation/value is a physical quantity (xsi:type=\"PQ\"), "
				+ "the unit of measure SHALL be selected from ValueSet UnitsOfMeasureCaseSensitive 2.16.840.1.113883.1.11.12839 DYNAMIC "
				+ "(CONF:1198-31484).";
		String configuredXpathExpression = "//v3:observation/v3:templateId[@root='2.16.840.1.113883.10.20.22.4.2' and @extension='2015-08-01']"
				+ "/ancestor::v3:observation[1]/v3:value"
				+ "[@xsi:type='PQ' and not(@nullFlavor) and ancestor::v3:section[not(@nullFlavor)]]";
		programmaticallyConfigureRequiredNodeValidator(new ConfiguredValidationResultSeverityLevel("SHALL"), "@unit",
				validationMessage, configuredXpathExpression);
		injectDependencies();

		List<VocabularyValidationResult> results = testVocabularyValidator(CCDA_FILES[MISSING_UNIT_ATTRIBUTE]);

		Assert.assertTrue(ASSERT_MSG_NO_VOCABULARY_ISSUE_BUT_SHOULD, hasVocabularyIssue(results));
		String expectedMessage = "The node '@unit' does not exist at the expected path "
				+ "/ClinicalDocument[1]/component[1]/structuredBody[1]/component[10]/section[1]/entry[1]/organizer[1]/component[3]/observation[1]/value[1] "
				+ "but is required as per the specification: " + validationMessage;
		Assert.assertTrue(ASSERT_MSG_SEVERITY_OR_MESSAGE_DOES_NOT_MATCH_BUT_SHOULD,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHALL, expectedMessage));

		/* XML example - expect no error as has @unit
		<observation classCode="OBS" moodCode="EVN">
			...
			<value xsi:type="PQ" value="1.015" unit="someUnit"/>
			...
		</observation> */
		results = testVocabularyValidator(CCDA_FILES[HAS_UNIT_ATTRIBUTE]);
		
		Assert.assertFalse(ASSERT_MSG_HAS_VOCABULARY_ISSUE_BUT_SHOULD_NOT, hasVocabularyIssue(results));
		Assert.assertFalse(ASSERT_MSG_SEVERITY_WITH_MESSAGE_MATCHES_BUT_SHOULD_NOT,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHALL, expectedMessage));
	}
	
	@Test
	public void requiredNodeValidatorMissingAttributeFileBasedConfigTest() {		
		setupInitParameters(true);
		injectDependencies();
		
		/* XML example - expect error for NO @unit
		<observation classCode="OBS" moodCode="EVN">
			...
			<value xsi:type="PQ" value="1.015"/>
			...
		</observation> */
		String expectedMessage = "The node '@unit' does not exist at the expected path "
				+ "/ClinicalDocument[1]/component[1]/structuredBody[1]/component[10]/section[1]/entry[1]/organizer[1]/component[3]/observation[1]/value[1] "
				+ "but is required as per the specification: " 
				+ "If Observation/value is a physical quantity (xsi:type=\"PQ\"), the unit of measure SHALL be selected from "
				+ "ValueSet UnitsOfMeasureCaseSensitive 2.16.840.1.113883.1.11.12839 DYNAMIC (CONF:1198-31484).";
		List<VocabularyValidationResult> results = testVocabularyValidator(CCDA_FILES[MISSING_UNIT_ATTRIBUTE],
				"requiredNodeValidatorMissingAttributeConfig");

		Assert.assertTrue(ASSERT_MSG_NO_VOCABULARY_ISSUE_BUT_SHOULD, hasVocabularyIssue(results));		
		Assert.assertTrue(ASSERT_MSG_SEVERITY_OR_MESSAGE_DOES_NOT_MATCH_BUT_SHOULD,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHALL, expectedMessage));

		/* XML example - expect no error as has @unit
		<observation classCode="OBS" moodCode="EVN">
			...
			<value xsi:type="PQ" value="1.015" unit="someUnit"/>
			...
		</observation> */
		results = testVocabularyValidator(CCDA_FILES[HAS_UNIT_ATTRIBUTE], "requiredNodeValidatorMissingAttributeConfig");
		
		Assert.assertFalse(ASSERT_MSG_HAS_VOCABULARY_ISSUE_BUT_SHOULD_NOT, hasVocabularyIssue(results));
		Assert.assertFalse(ASSERT_MSG_SEVERITY_WITH_MESSAGE_MATCHES_BUT_SHOULD_NOT,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHALL, expectedMessage));
	}	

	@Test
	public void requiredNodeValidatorMissingElementTest() {
		/* XML example - expect warning for NO <prefix>
		<name>
			<given>James</given>
			<family>Madison</family>
		</name> */
		String validationMessage = "informant/relatedEntity/relatedPerson/name SHOULD contain a prefix element (not a real rule - just a test)";
		String configuredXpathExpression = "//v3:informant/v3:relatedEntity/v3:relatedPerson/v3:name";
		ConfiguredValidationResultSeverityLevel severity = new ConfiguredValidationResultSeverityLevel("SHOULD");
		String element = "v3:prefix";
		programmaticallyConfigureRequiredNodeValidator(severity, element, validationMessage, configuredXpathExpression);
		injectDependencies();

		List<VocabularyValidationResult> results = testVocabularyValidator(CCDA_FILES[MISSING_UNIT_ATTRIBUTE]);

		Assert.assertTrue(ASSERT_MSG_NO_VOCABULARY_ISSUE_BUT_SHOULD, hasVocabularyIssue(results));
		String expectedMessage = "The node 'v3:prefix' does not exist at the expected path "
				+ "/ClinicalDocument[1]/informant[2]/relatedEntity[1]/relatedPerson[1]/name[1] "
				+ "but is required as per the specification: " + validationMessage;
		Assert.assertTrue(ASSERT_MSG_SEVERITY_OR_MESSAGE_DOES_NOT_MATCH_BUT_SHOULD,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHOULD, expectedMessage));		

		/* XML example - expect no warning as has <prefix>
		<name>
			<prefix>Dr</prefix>
			<given>Albert</given>
			<family>Davis</family>
		</name> */
		validationMessage = "informationRecipient/intendedRecipient/informationRecipient/name SHOULD contain a prefix element (not a real rule - just a test)";
		configuredXpathExpression = "//v3:informationRecipient/v3:intendedRecipient/v3:informationRecipient/v3:name";
		programmaticallyConfigureRequiredNodeValidator(severity, element, validationMessage, configuredXpathExpression);
		injectDependencies();
		
		results = testVocabularyValidator(CCDA_FILES[MISSING_UNIT_ATTRIBUTE]);
		
		Assert.assertFalse(ASSERT_MSG_HAS_VOCABULARY_ISSUE_BUT_SHOULD_NOT, hasVocabularyIssue(results));
		expectedMessage = "The node 'v3:prefix' does not exist at the expected path "
				+ "/ClinicalDocument[1]/informationRecipient[1]/intendedRecipient[1]/informationRecipient[1]/name[1] "
				+ "but is required as per the specification: " + validationMessage;		
		Assert.assertFalse(ASSERT_MSG_SEVERITY_WITH_MESSAGE_MATCHES_BUT_SHOULD_NOT,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHOULD, expectedMessage));		
	}
	
	@Test
	public void requiredNodeValidatorMissingElementFileBasedConfigTest() {
		setupInitParameters(true);
		injectDependencies();
		
		/* XML example - expect warning for NO <prefix>
		<name>
			<given>James</given>
			<family>Madison</family>
		</name> */
		String expectedMessage = "The node 'v3:prefix' does not exist at the expected path "
				+ "/ClinicalDocument[1]/informant[2]/relatedEntity[1]/relatedPerson[1]/name[1] "
				+ "but is required as per the specification: " 
				+ "informant/relatedEntity/relatedPerson/name SHOULD contain a prefix element (not a real rule - just a test)";		
		List<VocabularyValidationResult> results = testVocabularyValidator(CCDA_FILES[MISSING_UNIT_ATTRIBUTE], "requiredNodeValidatorMissingElementConfig");

		Assert.assertTrue(ASSERT_MSG_NO_VOCABULARY_ISSUE_BUT_SHOULD, hasVocabularyIssue(results));
		Assert.assertTrue(ASSERT_MSG_SEVERITY_OR_MESSAGE_DOES_NOT_MATCH_BUT_SHOULD,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHOULD, expectedMessage));		

		/* XML example - expect no warning as has <prefix>
		<name>
			<prefix>Dr</prefix>
			<given>Albert</given>
			<family>Davis</family>
		</name> */
		expectedMessage = "The node 'v3:prefix' does not exist at the expected path "
				+ "/ClinicalDocument[1]/informationRecipient[1]/intendedRecipient[1]/informationRecipient[1]/name[1] "
				+ "but is required as per the specification: "
				+ "informationRecipient/intendedRecipient/informationRecipient/name SHOULD contain a prefix element (not a real rule - just a test)";
		results = testVocabularyValidator(CCDA_FILES[MISSING_UNIT_ATTRIBUTE], "requiredNodeValidatorMissingElementConfig");
		
		// ensure there are no more warnings than the previous tests result
		Assert.assertFalse(ASSERT_MSG_HAS_VOCABULARY_ISSUE_BUT_SHOULD_NOT, results.size() > 1);
		Assert.assertFalse(ASSERT_MSG_SEVERITY_WITH_MESSAGE_MATCHES_BUT_SHOULD_NOT,
				isResultMatchingExpectedResult(results, VocabularyValidationResultLevel.SHOULD, expectedMessage));		
	}	

	private List<VocabularyValidationResult> testVocabularyValidator(URI filePath) {
		return testVocabularyValidator(filePath, VocabularyConstants.Config.DEFAULT);
	}
	
	private List<VocabularyValidationResult> testVocabularyValidator(URI filePath, String vocabularyConfig) {
		List<VocabularyValidationResult> results = new ArrayList<>();
		try {
			results = vocabularyValidationService.validate(filePath.toString(), vocabularyConfig);
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

	private static boolean hasVocabularyIssue(List<VocabularyValidationResult> results) {
		return results != null && !results.isEmpty();
	}

	private static boolean isResultMatchingExpectedResult(List<VocabularyValidationResult> results,
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

	private static void println() {
		if (LOG_RESULTS_TO_CONSOLE)
			System.out.println();
	}

	private static void println(String message) {
		print(message);
		println();
	}

	private static void print(String message) {
		if (LOG_RESULTS_TO_CONSOLE)
			System.out.print(message);
	}

}
