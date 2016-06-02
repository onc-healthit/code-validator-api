package org.sitenv.vocabularies.validation.validators.NodeTypes;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.repositories.VsacValuesSetRepository;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(value = "ClassCodeValidator")
public class ClassCodeValidator extends NodeValidator {
	private static final Logger logger = Logger.getLogger(ClassCodeValidator.class);
	private VsacValuesSetRepository vsacValuesSetRepository;

	@Autowired
	public ClassCodeValidator(VsacValuesSetRepository vsacValuesSetRepository) {
		this.vsacValuesSetRepository = vsacValuesSetRepository;
	}

	@Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
		String classCode;
		try{
			XPathExpression exp = xpath.compile("@classCode");
			classCode = ((String) exp.evaluate(node, XPathConstants.STRING)).toUpperCase();
		} catch (XPathExpressionException e) {
			throw new RuntimeException("ERROR getting node values " + e.getMessage());
		}

		List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedValuesetOids().split(",")));

		NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedClassCode(classCode);
        nodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedValuesetOids());
		if(vsacValuesSetRepository.valuesetOidsExists(allowedConfiguredCodeSystemOids)){
            nodeValidationResult.setNodeValuesetsFound(true);
			if (vsacValuesSetRepository.codeExistsInValueset(classCode, allowedConfiguredCodeSystemOids)) {
                nodeValidationResult.setValid(true);
			}
		}
		return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
	}

	@Override
	protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel) {
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isNodeValuesetsFound()) {
                VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
                String validationMessage = "Class Code '" + nodeValidationResult.getRequestedClassCode() + "' does not exist in the value set (" + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode() + ")";
                vocabularyValidationResult.setMessage(validationMessage);
                vocabularyValidationResults.add(vocabularyValidationResult);
            }else{
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
		return vocabularyValidationResults;
	}
}
