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

@Component(value = "ValueSetCodeValidator")
public class ValueSetCodeValidator extends NodeValidator {
    private static final Logger logger = Logger.getLogger(ValueSetCodeValidator.class);
    private VsacValuesSetRepository vsacValuesSetRepository;


    @Autowired
    public ValueSetCodeValidator(VsacValuesSetRepository vsacValuesSetRepository) {
        this.vsacValuesSetRepository = vsacValuesSetRepository;
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

        List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedValuesetOids().split(",")));

        NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedCode(nodeCode);
        nodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        nodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        nodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        nodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedValuesetOids());

        if(vsacValuesSetRepository.valuesetOidsExists(allowedConfiguredCodeSystemOids)){
            nodeValidationResult.setNodeValuesetsFound(true);
            if (vsacValuesSetRepository.existsByCodeAndCodeSystemAndCodeSystemNameAndDisplayNameInValuesetOid(nodeCode, nodeCodeSystem, nodeCodeSystemName, nodeDisplayName, allowedConfiguredCodeSystemOids)) {
                nodeValidationResult.setValid(true);
            } else {
                if (vsacValuesSetRepository.codeSystemExistsInValueset(nodeCodeSystem, allowedConfiguredCodeSystemOids)) {
                    nodeValidationResult.setNodeCodeSystemFound(true);
                    if(vsacValuesSetRepository.codeExistsByCodeSystemInValuesetOid(nodeCode, nodeCodeSystem, allowedConfiguredCodeSystemOids)){
                        nodeValidationResult.setNodeCodeFound(true);
                        if(nodeDisplayName.isEmpty() || vsacValuesSetRepository.displayNameExistsForCodeByCodeSystemInValuesetOid(nodeDisplayName, nodeCode, nodeCodeSystem, allowedConfiguredCodeSystemOids)){
                            nodeValidationResult.setNodeDisplayNameFound(true);
                        }
                        if(nodeCodeSystemName.isEmpty() || vsacValuesSetRepository.codeSystemNameExistsForCodeByCodeSystemInValuesetOid(nodeCodeSystemName, nodeCode, nodeCodeSystem, allowedConfiguredCodeSystemOids)){
                            nodeValidationResult.setNodeCodeSystemNameFound(true);
                        }
                    }
                }
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
    }

    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel){
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isNodeValuesetsFound()) {
                if (!nodeValidationResult.isNodeCodeFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
                    vocabularyValidationResult.setMessage("Code " + nodeValidationResult.getRequestedCode() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
                if (!nodeValidationResult.isNodeCodeSystemFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
                    vocabularyValidationResult.setMessage("Code System " + nodeValidationResult.getRequestedCodeSystem() + " does not exist in value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }

                if (!nodeValidationResult.isNodeCodeSystemNameFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
                    vocabularyValidationResult.setMessage("Code System Name " + nodeValidationResult.getRequestedCodeSystemName() + " does not match expected name for the code system oid " + nodeValidationResult.getRequestedCodeSystem() + " in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }

                if (!nodeValidationResult.isNodeDisplayNameFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
                    vocabularyValidationResult.setMessage("Display Name " + nodeValidationResult.getRequestedDisplayName() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
            }else{
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
        return vocabularyValidationResults;
    }
}