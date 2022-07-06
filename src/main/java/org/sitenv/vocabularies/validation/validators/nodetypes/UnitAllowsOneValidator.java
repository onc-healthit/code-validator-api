package org.sitenv.vocabularies.validation.validators.nodetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component(value = "UnitAllowsOneValidator")
public class UnitAllowsOneValidator extends NodeValidator {
	private static final Logger logger = LoggerFactory.getLogger(UnitAllowsOneValidator.class);
	private VsacValuesSetRepository vsacValuesSetRepository;
	private static final String UNIT_EXCEPTION = "1";

	@Autowired
	public UnitAllowsOneValidator(VsacValuesSetRepository vsacValuesSetRepository) {
		this.vsacValuesSetRepository = vsacValuesSetRepository;
	}

	@Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
		String nodeUnit;		
		try{
			XPathExpression exp = xpath.compile("@unit");
			nodeUnit = ((String) exp.evaluate(node, XPathConstants.STRING)).toUpperCase();
		} catch (XPathExpressionException e) {
			throw new RuntimeException("ERROR getting node values " + e.getMessage());
		}

		List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedValuesetOids().split(",")));

		NodeValidationResult nodeValidationResult = new NodeValidationResult();
        nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
        nodeValidationResult.setRequestedUnit(nodeUnit);
        nodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedValuesetOids());
        if(!StringUtils.trimToEmpty(nodeValidationResult.getRequestedUnit()).equals(UNIT_EXCEPTION)) {
			logger.info("Unit is != " + UNIT_EXCEPTION + " as it is equal to " + "'"
					+ nodeValidationResult.getRequestedUnit() + "' instead"
					+ ": Running standard vocabulary validation on node");
			if(vsacValuesSetRepository.valuesetOidsExists(allowedConfiguredCodeSystemOids)){
	            nodeValidationResult.setNodeValuesetsFound(true);
				if (vsacValuesSetRepository.codeExistsInValueset(nodeUnit, allowedConfiguredCodeSystemOids)) {
	                nodeValidationResult.setValid(true);
				}
			}
        } else {
        	logger.info("Allowing 1 as a valid unit. One scenario for this exception is: "
        			+ "A measurement which does not have units can instead use a unit of '1' as per UCUM"); 
        	nodeValidationResult.setValid(true);
        }
		return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel());
	}
	
	@Override
	protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel) {
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isNodeValuesetsFound()) {
                VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
                vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
				if(nodeValidationResult.getRequestedUnit().indexOf('{') > -1){
					vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.SHOULD);
				}else{
					vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
				}
				String validationMessage = "Unit '" + nodeValidationResult.getRequestedUnit()
						+ "' does not exist in the value set ("
						+ nodeValidationResult.getConfiguredAllowableValuesetOidsForNode() + ")"
						+ " or is not equal to 1";
                vocabularyValidationResult.setMessage(validationMessage);
                vocabularyValidationResults.add(vocabularyValidationResult);
            }else{
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
		return vocabularyValidationResults;
	}	

}
