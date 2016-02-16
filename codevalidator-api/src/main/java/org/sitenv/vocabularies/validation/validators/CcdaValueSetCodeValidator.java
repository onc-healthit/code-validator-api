package org.sitenv.vocabularies.validation.validators;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.VocabularyNodeValidator;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.entities.VsacValueSet;
import org.sitenv.vocabularies.validation.repositories.VsacValuesSetRepository;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.enums.VocabularyValidationNodeAttributeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(value = "CcdaValueSetCodeValidator")
public class CcdaValueSetCodeValidator extends BaseValidator implements VocabularyNodeValidator {
    private static final Logger logger = Logger.getLogger(CcdaValueSetCodeValidator.class);
    private VsacValuesSetRepository vsacValuesSetRepository;

    @Autowired
    public CcdaValueSetCodeValidator(VsacValuesSetRepository vsacValuesSetRepository) {
        this.vsacValuesSetRepository = vsacValuesSetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
        List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedCodeSystemOids().split(",")));

        getNodeAttributesToBeValidated(xpath, node);

        NodeValidationResult vocabularyNodeValidationResult = new NodeValidationResult();
        vocabularyNodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        vocabularyNodeValidationResult.setRequestedCode(nodeCode);
        vocabularyNodeValidationResult.setRequestedCodeSystemName(nodeCodeSystemName);
        vocabularyNodeValidationResult.setRequestedCodeSystem(nodeCodeSystem);
        vocabularyNodeValidationResult.setRequestedDisplayName(nodeDisplayName);
        vocabularyNodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedCodeSystemOids());
        if(vsacValuesSetRepository.valuesetOidsExists(allowedConfiguredCodeSystemOids)){
            vocabularyNodeValidationResult.setNodeValuesetsFound(true);
            if (vsacValuesSetRepository.existsByCodeAndCodeSystemAndCodeSystemNameAndDisplayNameInValuesetOid(nodeCode, nodeCodeSystem, nodeCodeSystemName, nodeDisplayName, allowedConfiguredCodeSystemOids)) {
                vocabularyNodeValidationResult.setValid(true);
            } else {
                List<VsacValueSet> valuesetsFoundByNodeCodeSystemInConfiguredAllowableValuesetOids = vsacValuesSetRepository.findByCodeSystemAndValuesetOidIn(nodeCodeSystem, allowedConfiguredCodeSystemOids);
                if (!valuesetsFoundByNodeCodeSystemInConfiguredAllowableValuesetOids.isEmpty()) {
                    vocabularyNodeValidationResult.setNodeCodeSystemFoundInConfiguredAllowableValueSets(true);
                    boolean codeSystemCodeFound = false;
                    boolean codeSystemNameFound = false;
                    boolean codeSystemDisplayNameFound = false;
                    for (VsacValueSet valueSet : valuesetsFoundByNodeCodeSystemInConfiguredAllowableValuesetOids) {
                        if (!codeSystemCodeFound) {
                            if (nodeCode.equals(valueSet.getCode())) {
                                codeSystemCodeFound = true;
                                vocabularyNodeValidationResult.setNodeCodeFoundInCodeSystemForConfiguredAllowableValueSets(true);
                            }
                        }
                        if (!codeSystemNameFound) {
                            if (nodeCodeSystemName.equals(valueSet.getCodeSystemName())) {
                                codeSystemNameFound = true;
                                vocabularyNodeValidationResult.setNodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets(true);
                            }
                        }
                        if (!codeSystemDisplayNameFound) {
                            if (nodeDisplayName.equals(valueSet.getDisplayName())) {
                                codeSystemDisplayNameFound = true;
                                vocabularyNodeValidationResult.setNodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets(true);
                            }
                        }
                    }
                }
            }
        }
        return buildVocabularyValidationResults(vocabularyNodeValidationResult);
    }

    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult){
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isNodeValuesetsFound()) {
                if (!nodeValidationResult.isNodeCodeSystemFoundInConfiguredAllowableValueSets()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.ERRORS);
                    String validationMessage;
                    if(nodeValidationResult.getRequestedCodeSystem().isEmpty()){
                        validationMessage = getMissingNodeAttributeMessage(VocabularyValidationNodeAttributeType.CODESYSTEM);
                    }else{
                        validationMessage = "Code System " + nodeValidationResult.getRequestedCodeSystem() + " does not exist in value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode();
                    }
                    vocabularyValidationResult.setMessage(validationMessage);
                    vocabularyValidationResults.add(vocabularyValidationResult);
                } else {
                    if (!nodeValidationResult.isNodeCodeFoundInCodeSystemForConfiguredAllowableValueSets()) {
                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.ERRORS);
                        String validationMessage;
                        if(nodeValidationResult.getRequestedCode().isEmpty()){
                            validationMessage = getMissingNodeAttributeMessage(VocabularyValidationNodeAttributeType.CODE);
                        }else{
                            validationMessage = "Code " + nodeValidationResult.getRequestedCode() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode();
                        }
                        vocabularyValidationResult.setMessage(validationMessage);
                        vocabularyValidationResults.add(vocabularyValidationResult);
                    }
                    if (!nodeValidationResult.isNodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets()) {
                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.WARNINGS);
                        String validationMessage;
                        if(nodeValidationResult.getRequestedCodeSystemName().isEmpty()){
                            validationMessage = getMissingNodeAttributeMessage(VocabularyValidationNodeAttributeType.CODESYSTEMNAME);
                        }else{
                            validationMessage = "Code System Name " + nodeValidationResult.getRequestedCodeSystemName() + " does not match expected name for the code system oid " + nodeValidationResult.getRequestedCodeSystem() + " in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode();
                        }
                        vocabularyValidationResult.setMessage(validationMessage);
                        vocabularyValidationResults.add(vocabularyValidationResult);
                    }
                    if (!nodeValidationResult.isNodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets()) {
                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.WARNINGS);
                        String validationMessage;
                        if(nodeValidationResult.getRequestedDisplayName().isEmpty()){
                            validationMessage = getMissingNodeAttributeMessage(VocabularyValidationNodeAttributeType.DISPLAYNAME);
                        }else{
                            validationMessage = "Display Name " + nodeValidationResult.getRequestedDisplayName() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode();
                        }
                        vocabularyValidationResult.setMessage(validationMessage);
                        vocabularyValidationResults.add(vocabularyValidationResult);
                    }
                }
            }else{
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
        return vocabularyValidationResults;
    }
}