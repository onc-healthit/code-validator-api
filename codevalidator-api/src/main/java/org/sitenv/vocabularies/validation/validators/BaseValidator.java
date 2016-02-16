package org.sitenv.vocabularies.validation.validators;

import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.validators.enums.VocabularyValidationNodeAttributeType;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * Created by Brian on 2/15/2016.
 */
public abstract class BaseValidator {
    protected String nodeCode;
    protected String nodeCodeSystem;
    protected String nodeCodeSystemName;
    protected String nodeDisplayName;
    protected String nodeUnit;

    protected void getNodeAttributesToBeValidated(XPath xpath, Node node) {
        try {
            XPathExpression expCode = xpath.compile("@code");
            XPathExpression expCodeSystem = xpath.compile("@codeSystem");
            XPathExpression expCodeSystemName = xpath.compile("@codeSystemName");
            XPathExpression expDisplayName = xpath.compile("@displayName");
            XPathExpression expUnit = xpath.compile("@unit");

            nodeCode = ((String) expCode.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeCodeSystem = ((String) expCodeSystem.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeCodeSystemName = ((String) expCodeSystemName.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeDisplayName = ((String) expDisplayName.evaluate(node, XPathConstants.STRING)).toUpperCase();
            nodeUnit = ((String) expUnit.evaluate(node, XPathConstants.STRING)).toUpperCase();
        } catch (XPathExpressionException e) {
            throw new RuntimeException("ERROR getting node values " + e.getMessage());
        }
    }

    protected VocabularyValidationResult valuesetNotLoadedResult(NodeValidationResult nodeValidationResult){
        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.INFO);
        vocabularyValidationResult.setMessage("Value set code validation attempt for value set(s) ('" + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode() + ") that do not exist in service for code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ")");
        return vocabularyValidationResult;
    }

    protected String getMissingNodeAttributeMessage(VocabularyValidationNodeAttributeType vocabularyValidationNodeAttributeType){
        return vocabularyValidationNodeAttributeType.getVocabularyValidationNodeAttributeType() + " is missing or is empty for the node being validated";
    }

    protected abstract List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult);
}
