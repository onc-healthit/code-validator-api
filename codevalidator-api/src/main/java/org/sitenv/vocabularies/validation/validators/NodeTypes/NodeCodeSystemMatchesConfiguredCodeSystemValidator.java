package org.sitenv.vocabularies.validation.validators.NodeTypes;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.NodeValidator;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(value = "NodeCodeSystemMatchesConfiguredCodeSystemValidator")
public class NodeCodeSystemMatchesConfiguredCodeSystemValidator extends NodeValidator {
    private static final Logger logger = Logger.getLogger(NodeCodeSystemMatchesConfiguredCodeSystemValidator.class);

    @Override
    public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
        String nodeCode;
        String nodeCodeSystem;
        String nodeCodeSystemName;
        String nodeDisplayName;
        try{
            XPathExpression expCode = xpath.compile("@code");
            XPathExpression expCodeSystem = xpath.compile("@codeSystem");
            XPathExpression expCodeSystemName = xpath.compile("@codeSystemName");
            XPathExpression expDisplayName = xpath.compile("@displayName");
            nodeCode = ((String) expCode.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeCodeSystem = ((String) expCodeSystem.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeCodeSystemName = ((String) expCodeSystemName.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeDisplayName = ((String) expDisplayName.evaluate(node, XPathConstants.STRING)).toUpperCase();
        } catch (XPathExpressionException e) {
            throw new RuntimeException("ERROR getting node values " + e.getMessage());
        }

        List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedValuesetOids().split(",")));

        NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedCode(nodeCode);
        nodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        nodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        nodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        nodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedValuesetOids());

        for(String allowedConfiguredCodeSystemOid : allowedConfiguredCodeSystemOids){
            if (nodeCodeSystem.equalsIgnoreCase(allowedConfiguredCodeSystemOid)) {
                nodeValidationResult.setValid(true);
                return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
    }

    @Override
    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel) {
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
            vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
            vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.SHALL);
            String validationMessage = "Code system '" + nodeValidationResult.getRequestedCodeSystem()+ "' is not valid for the node found for (" + nodeValidationResult.getValidatedDocumentXpathExpression() + ")";
            vocabularyValidationResult.setMessage(validationMessage);
            vocabularyValidationResults.add(vocabularyValidationResult);
        }
        return vocabularyValidationResults;
    }
}