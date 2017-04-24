package org.sitenv.vocabularies.validation.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;

public class ConfiguredExpressionLevelFilter implements ConfiguredExpressionFilter {

	private String level = "info";
	private int iLevel = -1;

	private int INFO_LEVEL = 1;
	private int WARN_LEVEL = 2;
	private int ERROR_LEVEL = 3;

	public ConfiguredExpressionLevelFilter(String level) {
		if (level != null) {
			this.level = level.toLowerCase();
		}
		if (this.level.contains("info")) {
			iLevel = INFO_LEVEL;
		} else if (this.level.contains("warning")) {
			iLevel = WARN_LEVEL;
		} else if (this.level.contains("error")) {
			iLevel = ERROR_LEVEL;
		}
	}

	public boolean isEnabled(VocabularyValidationResultLevel lvl) {
		if (lvl == null) {
			return true;
		}
		// SHALL is always enabled.
		if (lvl == VocabularyValidationResultLevel.SHALL) {
			return true;
		}
		if (lvl == VocabularyValidationResultLevel.SHOULD) {
			return iLevel <= WARN_LEVEL;
		}
		if (lvl == VocabularyValidationResultLevel.MAY) {
			return iLevel <= INFO_LEVEL;
		}
		// SHALL is always enabled.
		return true;
	}

	@Override
	public ConfiguredExpression accept(ConfiguredExpression ce) {

		// If we're asking for INFO level, all expressions qualify, no need to
		// check.
		if (iLevel == INFO_LEVEL) {
			return ce;
		}

		// MAY ("info"),
		// SHOULD ("warnings"),
		// SHALL ("errors");

		boolean anyValidatorQualifies = false;
		Iterator<ConfiguredValidator> i = ce.getConfiguredValidators().iterator();
		while (i.hasNext() && !anyValidatorQualifies) {
			ConfiguredValidator v = i.next();
			int ruleLevel = INFO_LEVEL;
			ConfiguredValidationResultSeverityLevel vlvl = v.getConfiguredValidationResultSeverityLevel();
			if (vlvl == null) {
				ruleLevel = ERROR_LEVEL;
			} else {

				if (vlvl.getCodeSeverityLevel().equals(VocabularyValidationResultLevel.MAY.name())) {
					ruleLevel = INFO_LEVEL;
				}
				if (vlvl.getCodeSeverityLevel().equals(VocabularyValidationResultLevel.SHOULD.name())) {
					ruleLevel = WARN_LEVEL;
				}
				if (vlvl.getCodeSeverityLevel().equals(VocabularyValidationResultLevel.SHALL.name())) {
					// We always need to check SHALL rules.
					ruleLevel = ERROR_LEVEL;
				}
			}
			anyValidatorQualifies = (ruleLevel >= iLevel);
		}

		if (!anyValidatorQualifies) {
			return null;
		}

		// Need to make a deep copy of the original expression with only the
		// qualifying validator(s)
		ConfiguredExpression f = new ConfiguredExpression();
		f.setConfiguredXpathExpression(ce.getConfiguredXpathExpression());
		List<ConfiguredValidator> validators = new ArrayList<ConfiguredValidator>(ce.getConfiguredValidators().size());
		f.setConfiguredValidators(validators);
		for (ConfiguredValidator configuredValidator : ce.getConfiguredValidators()) {
			String codeSeverityLevel = VocabularyValidationResultLevel.SHALL.name();
			ConfiguredValidationResultSeverityLevel lvl = configuredValidator
					.getConfiguredValidationResultSeverityLevel();
			if (lvl != null) {
				codeSeverityLevel = lvl.getCodeSeverityLevel();
			}
			if (codeSeverityLevel.equals(VocabularyValidationResultLevel.MAY.name())) {
				if (iLevel <= INFO_LEVEL) {
					validators.add(configuredValidator);
				}
			}
			if (codeSeverityLevel.equals(VocabularyValidationResultLevel.SHOULD.name())) {
				if (iLevel <= WARN_LEVEL) {
					validators.add(configuredValidator);
				}
			}
			if (codeSeverityLevel.equals(VocabularyValidationResultLevel.SHALL.name())) {
				if (iLevel <= ERROR_LEVEL) {
					validators.add(configuredValidator);
				}
			}
		}

		if (validators.size() == 0) {
			return null;
		}

		return f;
	}

}
