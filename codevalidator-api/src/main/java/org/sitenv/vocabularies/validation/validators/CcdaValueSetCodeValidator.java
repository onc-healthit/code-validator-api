package org.sitenv.vocabularies.validation.validators;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.VocabularyNodeValidator;
import org.sitenv.vocabularies.validation.entities.VsacValueSet;
import org.sitenv.vocabularies.validation.repositories.VsacValuesSetRepository;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(value = "CcdaValueSetCodeValidator")
public class CcdaValueSetCodeValidator implements VocabularyNodeValidator {
    private static final Logger logger = Logger.getLogger(CcdaValueSetCodeValidator.class);
    private VsacValuesSetRepository vsacValuesSetRepository;
    private String nodeCode;
    private String nodeCodeSystem;
    private String nodeCodeSystemName;
    private String nodeDisplayName;

    @Autowired
    public CcdaValueSetCodeValidator(VsacValuesSetRepository vsacValuesSetRepository) {
        this.vsacValuesSetRepository = vsacValuesSetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public NodeValidationResult validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
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
        return vocabularyNodeValidationResult;
    }

    private void getNodeAttributesToBeValidated(XPath xpath, Node node) {
        try {
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
    }
}