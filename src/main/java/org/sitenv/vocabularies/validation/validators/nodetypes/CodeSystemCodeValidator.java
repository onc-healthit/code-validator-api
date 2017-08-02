package org.sitenv.vocabularies.validation.validators.nodetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dao.CodeSystemCodeDAO;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
//import org.sitenv.vocabularies.validation.repositories.CodeRepository;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionFilter;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ximpleware.VTDNav;

@Component(value = "CodeSystemCodeValidator")
public class CodeSystemCodeValidator extends NodeValidator {
    private static final Logger logger = Logger.getLogger(CodeSystemCodeValidator.class);

    @Autowired
    CodeSystemCodeDAO codeRepository;

    @Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath,
			VTDNav vn, int nodeIndex, ConfiguredExpressionFilter filter, String xpathExpression) {
        String nodeCode;
        String nodeCodeSystem;
        String nodeCodeSystemName;
        String nodeDisplayName;
        try {
			nodeCode = vn.getAttrVal("code")!=-1 ? vn.toString(vn.getAttrVal("code")).toUpperCase() : "";
			nodeCodeSystem = vn.getAttrVal("codeSystem")!=-1 ? vn.toString(vn.getAttrVal("codeSystem")).toUpperCase() : "";
			nodeCodeSystemName = vn.getAttrVal("codeSystemName")!=-1 ? vn.toString(vn.getAttrVal("codeSystemName")).toUpperCase() : "";
			nodeDisplayName = vn.getAttrVal("displayName")!=-1 ? vn.toString(vn.getAttrVal("displayName")).toUpperCase() : "";
        } catch (Exception e) {
			throw new RuntimeException("ERROR getting node values " + e.getMessage());
	
        }
        
        List<String> allowedConfiguredCodeSystemNames = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedCodesystemNames().split(",")));

        NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setErrorOffset(vn.getTokenOffset(vn.getCurrentIndex()));
        nodeValidationResult.setValidatedDocumentXpathExpression(xpathExpression);
        nodeValidationResult.setRequestedCode(nodeCode);
        nodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        nodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        nodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        nodeValidationResult.setConfiguredAllowableCodesystemNamesForNode(configuredValidator.getAllowedCodesystemNames());
        nodeValidationResult.setRuleID(configuredValidator.getId());

        if(codeRepository.foundCodeAndDisplayNameAndCodeSystemOIDInCodesystem(nodeCode, nodeDisplayName, nodeCodeSystem, allowedConfiguredCodeSystemNames)){
            nodeValidationResult.setValid(true);
        }else{
            if(codeRepository.foundCodesystems(allowedConfiguredCodeSystemNames)){
                nodeValidationResult.setCodeSystemFound(true);
                if(codeRepository.foundCodeInCodesystems(nodeCode, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeCodeFound(true);
                }
                if(codeRepository.foundDisplayNameInCodesystems(nodeDisplayName, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeDisplayNameFound(true);
                }
                if(codeRepository.foundCodeSystemOIDInCodesystems(nodeCodeSystem, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeCodeSystemOIDFound(true);
                }
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel(), filter, vn);
    }

    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel, ConfiguredExpressionFilter filter, VTDNav nav){
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isCodeSystemFound()) {
                    if (!nodeValidationResult.isNodeCodeFound()) {
                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
    					nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
                        String validationMessage = "Code " + nodeValidationResult.getRequestedCode() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystem() + " in the configured code system name(s) " + nodeValidationResult.getConfiguredAllowableCodesystemNamesForNode();
                        vocabularyValidationResult.setMessage(validationMessage);
                        vocabularyValidationResults.add(vocabularyValidationResult);
                    }
                    if (filter.isEnabled(VocabularyValidationResultLevel.MAY)) {
	                    if (!nodeValidationResult.isNodeDisplayNameFound()) {
	                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
        					nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
	                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
	                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
	                        String validationMessage = "Display Name " + nodeValidationResult.getRequestedDisplayName() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystem() + " in the configured code system name(s) " + nodeValidationResult.getConfiguredAllowableCodesystemNamesForNode();
	                        vocabularyValidationResult.setMessage(validationMessage);
	                        vocabularyValidationResults.add(vocabularyValidationResult);
	                    }
	                }
            }else {
				nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
        return vocabularyValidationResults;
    }
}