package org.sitenv.vocabularies.validation.dto.enums;

public enum VocabularyValidationResultLevel {
    INFO ("info"),
    WARNINGS ("warnings"),
    ERRORS ("errors");

    private final String resultType;

    VocabularyValidationResultLevel(final String resultType) {
        this.resultType = resultType;
    }

    public String getResultType() {
        return resultType;
    }
}
