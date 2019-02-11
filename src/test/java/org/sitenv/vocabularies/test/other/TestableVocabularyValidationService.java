package org.sitenv.vocabularies.test.other;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.NodeValidation;
import org.sitenv.vocabularies.validation.services.VocabularyValidationService;

public class TestableVocabularyValidationService extends VocabularyValidationService {

	@Override
	public NodeValidation selectVocabularyValidator(ConfiguredValidator configuredValidator) {
		NodeValidation vocabularyValidator = null;
		final String packagePath = "org.sitenv.vocabularies.validation.validators.nodetypes.";
		try {
			vocabularyValidator = (NodeValidation) Class.forName(packagePath + configuredValidator.getName())
					.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return vocabularyValidator;
	}

}
