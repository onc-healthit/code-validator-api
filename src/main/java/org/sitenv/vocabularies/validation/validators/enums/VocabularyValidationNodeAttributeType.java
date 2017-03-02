package org.sitenv.vocabularies.validation.validators.enums;

public enum VocabularyValidationNodeAttributeType {
    CODESYSTEM ("Code System"),
    CODE ("Code"),
    CODESYSTEMNAME ("Code System Name"),
    DISPLAYNAME ("Display Name"),
    UNIT("Unit");

    private final String vocabularyValidationNodeAttributeType;

    VocabularyValidationNodeAttributeType(final String vocabularyValidationNodeAttributeType) {
        this.vocabularyValidationNodeAttributeType = vocabularyValidationNodeAttributeType;
    }

    public String getVocabularyValidationNodeAttributeType() {
        return vocabularyValidationNodeAttributeType;
    }
}
