package org.sitenv.vocabularies.validation.dto.enums;

public enum VocabularyValidationResultLevel {
    MAY ("info"),
    SHOULD ("warnings"),
    SHALL ("errors");

    private final String resultType;

    VocabularyValidationResultLevel(final String resultType) {
        this.resultType = resultType;
    }

    public String getResultType() {
        return resultType;
    }
}
