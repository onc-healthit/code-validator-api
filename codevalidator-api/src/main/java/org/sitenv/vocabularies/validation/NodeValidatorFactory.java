package org.sitenv.vocabularies.validation;

/**
 * Created by Brian on 2/7/2016.
 */
public interface NodeValidatorFactory {
    NodeValidation getVocabularyValidator(String validatorType);
}
