package org.sitenv.vocabularies.validation.validators.nodetypes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

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

@Component(value = "RequiredNodeValidator")
public class RequiredNodeValidator extends NodeValidator {
	
	private static Logger logger = Logger.getLogger(RequiredNodeValidator.class); 

	@Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath,
			Node node, int nodeIndex) {	
		boolean hasNode;
		try{
			XPathExpression exp = xpath.compile(configuredValidator.getRequiredNodeName());
			hasNode = (Boolean) exp.evaluate(node, XPathConstants.BOOLEAN);			
		} catch (XPathExpressionException e) {
			throw new RuntimeException("ERROR parsing document with given XPath expression: " + e.getMessage());
		}

		NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedNode(configuredValidator.getRequiredNodeName());
        nodeValidationResult.setConfiguredValidationMessage(configuredValidator.getValidationMessage());
        
        if(hasNode) {
        	nodeValidationResult.setValid(true);
        }
        
		return buildVocabularyValidationResults(nodeValidationResult,
				configuredValidator.getConfiguredValidationResultSeverityLevel());		
	}

	@Override
	protected List<VocabularyValidationResult> buildVocabularyValidationResults(
			NodeValidationResult nodeValidationResult,
			ConfiguredValidationResultSeverityLevel configuredValidationResultSeverityLevel) {
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
		if (!nodeValidationResult.isValid()) {
			VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
			vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
			vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel
					.valueOf(configuredValidationResultSeverityLevel.getCodeSeverityLevel()));

			String finalValidationMessage = "The node '" + nodeValidationResult.getRequestedNode()
					+ "' does not exist at the expected path "
					+ nodeValidationResult.getValidatedDocumentXpathExpression()
					+ " but is required as per the specification: "
					+ nodeValidationResult.getConfiguredValidationMessage();
                vocabularyValidationResult.setMessage(finalValidationMessage);
                vocabularyValidationResults.add(vocabularyValidationResult);

        }
		return vocabularyValidationResults;
	}	

}
