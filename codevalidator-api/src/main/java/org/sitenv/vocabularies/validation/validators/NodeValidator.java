package org.sitenv.vocabularies.validation.validators;

import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.validation.NodeValidation;
import org.sitenv.vocabularies.validation.dto.NodeValidationResult;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;

import java.util.List;

/**
 * Created by Brian on 2/15/2016.
 */
public abstract class NodeValidator implements NodeValidation {

    protected VocabularyValidationResult valuesetNotLoadedResult(NodeValidationResult nodeValidationResult){
        VocabularyValidationResult vocabularyValidationResult = new VocabularyValidationResult();
        vocabularyValidationResult.setNodeValidationResult(nodeValidationResult);
        vocabularyValidationResult.setVocabularyValidationResultLevel(VocabularyValidationResultLevel.MAY);
        vocabularyValidationResult.setMessage("Value set code validation attempt for value set(s) ('" + nodeValidationResult.getConfiguredAllowableValuesetOidsForNode() + ") that do not exist in service for code system " + nodeValidationResult.getRequestedCodeSystemName() + " (" + nodeValidationResult.getRequestedCodeSystem() + ")");
        return vocabularyValidationResult;
    }

    protected abstract List<VocabularyValidationResult> buildVocabularyValidationResults(NodeValidationResult nodeValidationResult, ConfiguredValidationResultSeverityLevel configuredValidationResultSeverityLevel);
}
