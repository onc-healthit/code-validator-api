package org.sitenv.vocabularies.validation.validators;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.VocabularyNodeValidator;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.repositories.CodeRepository;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(value = "CcdaCodeSystemCodeValidator")
public class CcdaCodeSystemCodeValidator extends BaseValidator implements VocabularyNodeValidator {
    private static final Logger logger = Logger.getLogger(CcdaCodeSystemCodeValidator.class);
    private CodeRepository codeRepository;


    @Autowired
    public CcdaCodeSystemCodeValidator(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Override
    public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
        List<String> allowedConfiguredCodeSystemNames = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedCodesystemNames().split(",")));

        initializeValuesFromNodeAttributesToBeValidated(xpath, node);

        NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedCode(nodeCode);
        nodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        nodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        nodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        nodeValidationResult.setConfiguredAllowableCodesystemNamesForNode(configuredValidator.getAllowedCodesystemNames());

        if(codeRepository.foundCodeAndDisplayNameInCodesystem(nodeCode, nodeDisplayName, allowedConfiguredCodeSystemNames)){
            nodeValidationResult.setValid(true);
        }else{
            if(codeRepository.foundCodesystems(allowedConfiguredCodeSystemNames)){
                nodeValidationResult.setNodeCodeSystemFound(true);
                if(codeRepository.foundCodeInCodesystems(nodeCode, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeCodeFound(true);
                }
                if(codeRepository.foundDisplayNameInCodesystems(nodeDisplayName, allowedConfiguredCodeSystemNames)){
                    nodeValidationResult.setNodeDisplayNameFound(true);
                }
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
    }

    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel){
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isNodeCodeSystemFound()) {
                    if (!nodeValidationResult.isNodeCodeFound()) {
                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
                        String validationMessage = "Code " + nodeValidationResult.getRequestedCode() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystem() + " in the configured code system name(s) " + nodeValidationResult.getConfiguredAllowableCodesystemNamesForNode();
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
            }else{
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
        return vocabularyValidationResults;
    }
}