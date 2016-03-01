package org.sitenv.vocabularies.validation.dto;

import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;

/**
 * Created by Brian on 2/14/2016.
 */
public class VocabularyValidationResult {
    private NodeValidationResult nodeValidationResult;
    private String message;
    private VocabularyValidationResultLevel vocabularyValidationResultLevel;

    public NodeValidationResult getNodeValidationResult() {
        return nodeValidationResult;
    }

    public void setNodeValidationResult(NodeValidationResult nodeValidationResult) {
        this.nodeValidationResult = nodeValidationResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VocabularyValidationResultLevel getVocabularyValidationResultLevel() {
        return vocabularyValidationResultLevel;
    }

    public void setVocabularyValidationResultLevel(VocabularyValidationResultLevel vocabularyValidationResultLevel) {
        this.vocabularyValidationResultLevel = vocabularyValidationResultLevel;
    }
}
