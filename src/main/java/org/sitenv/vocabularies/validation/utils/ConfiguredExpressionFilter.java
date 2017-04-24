package org.sitenv.vocabularies.validation.utils;

import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;

public interface ConfiguredExpressionFilter {

	ConfiguredExpression accept(ConfiguredExpression ce);
	boolean isEnabled(VocabularyValidationResultLevel lvl);
}
