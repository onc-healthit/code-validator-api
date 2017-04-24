package org.sitenv.vocabularies.validation.validators.nodetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dao.ValueSetDAO;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionFilter;
import org.sitenv.vocabularies.validation.utils.XpathUtils;
import org.sitenv.vocabularies.validation.validators.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ximpleware.VTDNav;

@Component(value = "ValueSetCodeValidator")
public class ValueSetCodeValidator extends NodeValidator {
    private static final Logger logger = Logger.getLogger(ValueSetCodeValidator.class);
//    private VsacValuesSetRepository vsacValuesSetRepository;

	@Autowired
	ValueSetDAO vsacValuesSetRepository;

//    @Autowired
//    public ValueSetCodeValidator(VsacValuesSetRepository vsacValuesSetRepository) {
//        this.vsacValuesSetRepository = vsacValuesSetRepository;
//    }

    @Override
	public List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath,
			VTDNav vn, int nodeIndex, ConfiguredExpressionFilter filter, String xpathExpression) {
        String nodeCode;
        String nodeCodeSystem;
        String nodeCodeSystemName;
        String nodeDisplayName;
		try{
			nodeCode = vn.getAttrVal("code")!=-1 ? vn.toString(vn.getAttrVal("code")).toUpperCase() : "";
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

        if(vsacValuesSetRepository.valuesetOidsExists(allowedConfiguredCodeSystemOids)){
            nodeValidationResult.setNodeValuesetsFound(true);
            if (vsacValuesSetRepository.existsByCodeAndCodeSystemAndCodeSystemNameAndDisplayNameInValuesetOid(nodeCode, nodeCodeSystem, nodeCodeSystemName, nodeDisplayName, allowedConfiguredCodeSystemOids)) {
                nodeValidationResult.setValid(true);
            } else {
                if (vsacValuesSetRepository.codeSystemExistsInValueset(nodeCodeSystem, allowedConfiguredCodeSystemOids)) {
                    nodeValidationResult.setNodeCodeSystemOIDFound(true);
                }
                if(vsacValuesSetRepository.codeExistsInValueset(nodeCode, allowedConfiguredCodeSystemOids)){
                    nodeValidationResult.setNodeCodeFound(true);
                }
                if(nodeDisplayName.isEmpty() || vsacValuesSetRepository.displayNameExistsInValueset(nodeDisplayName, allowedConfiguredCodeSystemOids)){
                    nodeValidationResult.setNodeDisplayNameFound(true);
                }
                if(nodeCodeSystemName.isEmpty() || vsacValuesSetRepository.codeSystemNameExistsInValueset(nodeCodeSystemName, allowedConfiguredCodeSystemOids)){
                    nodeValidationResult.setNodeCodeSystemNameFound(true);
                }
            }
        }
        return buildVocabularyValidationResults(nodeValidationResult, configuredValidator.getConfiguredValidationResultSeverityLevel(), filter, vn);
    }
    
    protected List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredNodeAttributeSeverityLevel, ConfiguredExpressionFilter filter, VTDNav nav){
        List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
        if(!nodeValidationResult.isValid()) {
            if (nodeValidationResult.isNodeValuesetsFound()) {
                if (!nodeValidationResult.isNodeCodeFound()) {
                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
					nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.valueOf(configuredNodeAttributeSeverityLevel.getCodeSeverityLevel()));
                    vocabularyValidationResult.setMessage("Code " + nodeValidationResult.getRequestedCode() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
                    vocabularyValidationResults.add(vocabularyValidationResult);
                }
                if (filter==null || filter.isEnabled(VocabularyValidationResultLevel.MAY)) {
	                if (!nodeValidationResult.isNodeCodeSystemOIDFound()) {
	                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
    					nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
	                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
	                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
	                    vocabularyValidationResult.setMessage("Code System " + nodeValidationResult.getRequestedCodeSystem() + " does not exist in value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
	                    vocabularyValidationResults.add(vocabularyValidationResult);
	                }
	
	                if (!nodeValidationResult.isNodeCodeSystemNameFound()) {
	                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
    					nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
	                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
	                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
	                    vocabularyValidationResult.setMessage("Code System Name " + nodeValidationResult.getRequestedCodeSystemName() + " does not match expected name for the code system oid " + nodeValidationResult.getRequestedCodeSystem() + " in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
	                    vocabularyValidationResults.add(vocabularyValidationResult);
	                }
	
	                if (!nodeValidationResult.isNodeDisplayNameFound()) {
	                    VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
    					nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
	                    vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
	                    vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
	                    vocabularyValidationResult.setMessage("Display Name " + nodeValidationResult.getRequestedDisplayName() + " does not exist in the code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ") in the value set(s) " + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode());
	                    vocabularyValidationResults.add(vocabularyValidationResult);
	                }
                }
            }else{
				nodeValidationResult.setValidatedDocumentXpathExpression(XpathUtils.getVTDXPath(nav));
                vocabularyValidationResults.add(valuesetNotLoadedResult(nodeValidationResult));
            }
        }
        return vocabularyValidationResults;
    }
}