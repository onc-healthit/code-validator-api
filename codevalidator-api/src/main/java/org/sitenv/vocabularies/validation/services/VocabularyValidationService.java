package org.sitenv.vocabularies.validation.services;

import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.dto.NodeValidationResult;
import org.sitenv.vocabularies.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.VocabularyNodeValidator;
import org.sitenv.vocabularies.validation.VocabularyValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Brian on 2/10/2016.
 */
@Service
@Transactional(readOnly=true)
public  class VocabularyValidationService {
    @Resource(name="vocabularyValidationConfigurations")
    List<ConfiguredExpression> vocabularyValidationConfigurations;
    @Resource(name="documentBuilder")
    DocumentBuilder documentBuilder;
    @Resource(name="xPathFactory")
    XPathFactory xPathFactory;
    @Autowired
    VocabularyValidatorFactory vocabularyValidatorFactory;

    public List<VocabularyValidationResult> validate(String uri) throws IOException, SAXException {
        Document doc = documentBuilder.parse(uri);
        return this.validate(doc);
    }

    public List<VocabularyValidationResult> validate(InputStream stream) throws IOException, SAXException {
        Document doc = documentBuilder.parse(stream);
        return this.validate(doc);
    }

    public List<VocabularyValidationResult> validate(Document doc) {
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        Map<String, ArrayList<VocabularyValidationResult>> vocabularyValidationResultMap = getInitializedResultMap();
        if (doc != null) {
            try {
                XPath xpath = xPathFactory.newXPath();
                for (ConfiguredExpression configuredExpression : vocabularyValidationConfigurations) {
                    String configuredXpathExpression = configuredExpression.getConfiguredXpathExpression();
                    NodeList nodes = findAllDocumentNodesByXpathExpression(xpath, configuredXpathExpression, doc);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        Iterator configIterator = configuredExpression.getConfiguredValidators().iterator();
                        while(configIterator.hasNext()){
                            ConfiguredValidator configuredValidator = (ConfiguredValidator) configIterator.next();
                            VocabularyNodeValidator vocabularyValidator = vocabularyValidatorFactory.getVocabularyValidator(configuredValidator.getName());
                            NodeValidationResult nodeValidationResult = vocabularyValidator.validateNode(configuredValidator, xpath, node, i);
                            if(!nodeValidationResult.isValid()) {
                                if (nodeValidationResult.isNodeValuesetsFound()) {
                                    if (!nodeValidationResult.isNodeCodeSystemFoundInConfiguredAllowableValueSets()) {
                                        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                                        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                                        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.ERRORS);
                                        vocabularyValidationResult.setMessage("Code System " + nodeValidationResult.getRequestedCodeSystem() + " does not exist in value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                                        vocabularyValidationResultMap.get(VocabularyValidationResultLevel.ERRORS.getResultType()).add(vocabularyValidationResult);
                                    } else {
                                        if (!nodeValidationResult.isNodeCodeFoundInCodeSystemForConfiguredAllowableValueSets()) {
                                            VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                                            vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                                            vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.ERRORS);
                                            vocabularyValidationResult.setMessage("Code " + nodeValidationResult.getRequestedCode() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                                            vocabularyValidationResultMap.get(VocabularyValidationResultLevel.ERRORS.getResultType()).add(vocabularyValidationResult);
                                        }
                                        if (!nodeValidationResult.isNodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets()) {
                                            VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                                            vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                                            vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.WARNINGS);
                                            vocabularyValidationResult.setMessage("Code System Name " + nodeValidationResult.getRequestedCodeSystemName() + " does not match expected name for the code system oid" + nodeValidationResult.getRequestedCodeSystem() + " in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                                            vocabularyValidationResultMap.get(VocabularyValidationResultLevel.WARNINGS.getResultType()).add(vocabularyValidationResult);
                                        }
                                        if (!nodeValidationResult.isNodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets()) {
                                            VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                                            vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                                            vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.WARNINGS);
                                            vocabularyValidationResult.setMessage("Display Name " + nodeValidationResult.getRequestedDisplayName() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                                            vocabularyValidationResultMap.get(VocabularyValidationResultLevel.WARNINGS.getResultType()).add(vocabularyValidationResult);
                                        }
                                    }
                                }else{
                                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.INFO);
                                    vocabularyValidationResult.setMessage("Value set code validation attempt for a value set that does not exist in service.");
                                    vocabularyValidationResultMap.get(VocabularyValidationResultLevel.INFO.getResultType()).add(vocabularyValidationResult);
                                }
                            }
                        }
                    }

                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        return convertMapToList(vocabularyValidationResultMap);
    }

    private Map<String, ArrayList<VocabularyValidationResult>> getInitializedResultMap() {
        Map<String, ArrayList<VocabularyValidationResult>> resultMap = new HashMap<>();
        resultMap.put("errors", new ArrayList<VocabularyValidationResult>());
        resultMap.put("warnings", new ArrayList<VocabularyValidationResult>());
        resultMap.put("info", new ArrayList<VocabularyValidationResult>());
        return resultMap;
    }

    private static NodeList findAllDocumentNodesByXpathExpression(XPath xpath, String configuredXpath, Document doc) throws XPathExpressionException {
        NodeList result = (NodeList) xpath.compile(configuredXpath).evaluate(doc, XPathConstants.NODESET);
        return result;
    }

    private  List<VocabularyValidationResult> convertMapToList(Map<String, ArrayList<VocabularyValidationResult>> resultMap) {
        List<VocabularyValidationResult> results = new ArrayList<>();
        for(ArrayList<VocabularyValidationResult> resultList : resultMap.values()){
            results.addAll(resultList);
        }
        return results;
    }
}
