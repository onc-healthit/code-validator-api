package org.sitenv.vocabularies.validation.validators;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.VocabularyNodeValidator;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.repositories.VsacValuesSetRepository;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.enums.VocabularyValidationNodeAttributeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(value = "CcdaValueSetNodeWithOnlyCodeValidator")
public class CcdaValueSetNodeWithOnlyCodeValidator extends BaseValidator implements VocabularyNodeValidator {
	private static final Logger logger = Logger.getLogger(CcdaValueSetNodeWithOnlyCodeValidator.class);
	private VsacValuesSetRepository vsacValuesSetRepository;

	@Autowired
	public CcdaValueSetNodeWithOnlyCodeValidator(VsacValuesSetRepository vsacValuesSetRepository) {
		this.vsacValuesSetRepository = vsacValuesSetRepository;
	}

	@Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex) {
		List<String> allowedConfiguredCodeSystemOids = new ArrayList<>(Arrays.asList(configuredValidator.getAllowedCodeSystemOids().split(",")));

		getNodeAttributesToBeValidated(xpath, node);

		NodeValidationResult nodeValidationResult = new NodeValidationResult();
		nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.buildXpathFromNode(node));
		nodeValidationResult.setRequestedCode(nodeCode);
		nodeValidationResult.setConfiguredAllowableValuesetOidsForNode(configuredValidator.getAllowedCodeSystemOids());
		if(vsacValuesSetRepository.valuesetOidsExists(allowedConfiguredCodeSystemOids)){
			nodeValidationResult.setNodeValuesetsFound(true);
			if (vsacValuesSetRepository.existsByCodeInValuesetOid(nodeCode, allowedConfiguredCodeSystemOids)) {
				nodeValidationResult.setValid(true);
			}
		}
		return buildVocabularyValidationResults(nodeValidationResult);
	}

	@Override
	protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult) {
		List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
		if(!nodeValidationResult.isValid()) {
			if (nodeValidationResult.isNodeValuesetsFound()) {
				VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
				vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
				vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.ERRORS);
                String validationMessage;
                if(nodeValidationResult.getRequestedCode().isEmpty()){
                    validationMessage = getMissingNodeAttributeMessage(VocabularyValidationNodeAttributeType.CODE);
                }else{
                    validationMessage = "Code '" + nodeValidationResult.getRequestedCode()+ "' does not exist in the value set (" + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode() + ")";
                }
				vocabularyValidationResult.setMessage(validationMessage);
				vocabularyValidationResults.add(vocabularyValidationResult);
			}else{
				vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
			}
		}
		return vocabularyValidationResults;
	}

}
