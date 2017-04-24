package org.sitenv.vocabularies.validation.validators.nodetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.NodeValidation;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionFilter;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.NodeValidator;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ximpleware.VTDNav;

@Component(value = "NodeCodeSystemMatchesConfiguredCodeSystemValidator")
public class NodeCodeSystemMatchesConfiguredCodeSystemValidator extends NodeValidator {
    private static final Logger logger = Logger.getLogger(NodeCodeSystemMatchesConfiguredCodeSystemValidator.class);

    @Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath,
			VTDNav vn, int nodeIndex, ConfiguredExpressionFilter filter, String xpathExpression) {
        String nodeCode;
        String nodeCodeSystem;
        String nodeCodeSystemName;
        String nodeDisplayName;
		try{
			nodeCode =  vn.getAttrVal("code")!=-1 ? vn.toString(vn.getAttrVal("code")).toUpperCase() : "";
			nodeCodeSystem = vn.getAttrVal("codeSystem")!=-1 ? vn.toString(vn.getAttrVal("codeSystem")).toUpperCase() : "";
			nodeCodeSystemName = vn.getAttrVal("codeSystemName")!=-1 ? vn.toString(vn.getAttrVal("codeSystemName")).toUpperCase() : "";
			nodeDisplayName = vn.getAttrVal("displayName")!=-1 ? vn.toString(vn.getAttrVal("displayName")).toUpperCase() : "";
		} catch (Exception e) {
			throw new RuntimeException("ERROR getting node values " + e.getMessage());
		}

        List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedValuesetOids().split(",")));

        NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setErrorOffset(vn.getTokenOffset(vn.getCurrentIndex()));
        nodeValidationResult.setValidatedDocumentXpathExpression(xpathExpression);
        nodeValidationResult.setRequestedCode(nodeCode);
        nodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        nodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        nodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        nodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedValuesetOids());
        nodeValidationResult.setRuleID(configuredValidator.getId());

        for(String allowedConfiguredCodeSystemOid : allowedConfiguredCodeSystemOids){
            if (nodeCodeSystem.equalsIgnoreCase(allowedConfiguredCodeSystemOid)) {
                nodeValidationResult.setValid(true);
                return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel(), filter, vn);
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel(), filter, vn);
    }

    @Override
    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel, ConfiguredExpressionFilter filter, VTDNav nav) {
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
			nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
            vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
            vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.SHALL);
            String validationMessage = "Code system '" + nodeValidationResult.getRequestedCodeSystem()+ "' is not valid for the node found for (" + nodeValidationResult.getValidatedDocumentXpathExpression() + ")";
            vocabularyValidationResult.setMessage(validationMessage);
            vocabularyValidationResults.add(vocabularyValidationResult);
        }
        return vocabularyValidationResults;
    }
}