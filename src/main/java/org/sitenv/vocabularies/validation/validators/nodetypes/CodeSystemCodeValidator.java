package org.sitenv.vocabularies.validation.validators.nodetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.sitenv.vocabularies.validation.repositories.CodeRepository;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

@Component(value = "CodeSystemCodeValidator")
public class CodeSystemCodeValidator extends NodeValidator {
    private static final Logger logger = Logger.getLogger(CodeSystemCodeValidator.class);
    private CodeRepository codeRepository;


    @Autowired
    public CodeSystemCodeValidator(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

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

        Set<String> allowedConfiguredCodeSystemNames = new HashSet<>(Arrays.asList(configuredValidator.getAllowedCodesystemNames().split(",")));

        NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedCode(nodeCode);
        nodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        nodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        nodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        nodeValidationResult.setConfiguredAllowableCodesystemNamesForNode(configuredValidator.getAllowedCodesystemNames());

        if(codeRepository.foundActiveCodeAndDisplayNameAndCodeSystemOIDInCodesystem(nodeCode, nodeDisplayName, nodeCodeSystem, allowedConfiguredCodeSystemNames)){
            nodeValidationResult.setValid(true);
        }else{
            if(codeRepository.foundCodesystems(allowedConfiguredCodeSystemNames)){
                nodeValidationResult.setCodeSystemFound(true);
                if(codeRepository.foundCodeInCodesystems(nodeCode, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeCodeFound(true);
                    if(!codeRepository.codeIsActive(nodeCode, allowedConfiguredCodeSystemNames)){
                        nodeValidationResult.setNodeCodeIsActive(false);
                    }
                }
                if(codeRepository.foundDisplayNameInCodesystems(nodeDisplayName, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeDisplayNameFound(true);
                }
                if(codeRepository.foundCodeSystemOIDInCodesystems(nodeCodeSystem, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeCodeSystemOIDFound(true);
                }
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
    }

    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel){
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isCodeSystemFound()) {
                if (!nodeValidationResult.isNodeCodeFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
                    String validationMessage = "Code: " + nodeValidationResult.getRequestedCode() + " , Code System: " + nodeValidationResult.getRequestedCodeSystem() + " are not found in the configured code system name(s) " + nodeValidationResult.getConfiguredAllowableCodesystemNamesForNode();
                    vocabularyValidationResult.setMessage(validationMessage);
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
                if (!nodeValidationResult.isNodeDisplayNameFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
                    String validationMessage = "Display Name " + nodeValidationResult.getRequestedDisplayName() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystem() + " in the configured code system name(s) " + nodeValidationResult.getConfiguredAllowableCodesystemNamesForNode();
                    vocabularyValidationResult.setMessage(validationMessage);
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
                if (!nodeValidationResult.isNodeCodeSystemOIDFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
                    String validationMessage = "Code system OID " + nodeValidationResult.getRequestedCodeSystem() + " does not exist in the configured code system name(s) " + nodeValidationResult.getConfiguredAllowableCodesystemNamesForNode();
                    vocabularyValidationResult.setMessage(validationMessage);
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
                if(!nodeValidationResult.isNodeCodeIsActive()){
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.SHOULD);
                    String validationMessage = "Code: " + nodeValidationResult.getRequestedCode() + " , is not an active code in Code System: " + nodeValidationResult.getRequestedCodeSystem();
                    vocabularyValidationResult.setMessage(validationMessage);
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
            }else{
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
        return vocabularyValidationResults;
    }
}