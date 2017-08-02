package com.cerner.hs.ccda.validator.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidationResultSeverityLevel;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionLevelFilter;

import static org.junit.Assert.assertTrue;

public class ConfiguredExpressionLevelFilterTest {

	@Test
	public void testInfoLevel() {
		ConfiguredExpressionLevelFilter f = new ConfiguredExpressionLevelFilter("info");
		ConfiguredExpression e = new ConfiguredExpression();
		e.setConfiguredXpathExpression("new xpath expression");
		List<ConfiguredValidator> vs = new ArrayList<ConfiguredValidator>();
		e.setConfiguredValidators(vs);
		ConfiguredValidator v = new ConfiguredValidator();
		vs.add(v);
		ConfiguredValidationResultSeverityLevel lvl = new ConfiguredValidationResultSeverityLevel();
		lvl.setCodeSeverityLevel(VocabularyValidationResultLevel.SHOULD.toString());
		v.setConfiguredValidationResultSeverityLevel(lvl);

		ConfiguredExpression r = f.accept(e);
		assertTrue("Since level is info, return same object", r == e);
	}

	@Test
	public void testWarningLevel() {
		ConfiguredExpressionLevelFilter f = new ConfiguredExpressionLevelFilter("error");
		ConfiguredExpression e = new ConfiguredExpression();
		e.setConfiguredXpathExpression("new xpath expression");
		List<ConfiguredValidator> vs = new ArrayList<ConfiguredValidator>();
		e.setConfiguredValidators(vs);

		ConfiguredValidator v1 = new ConfiguredValidator();
		vs.add(v1);
		ConfiguredValidationResultSeverityLevel lvl1 = new ConfiguredValidationResultSeverityLevel();
		lvl1.setCodeSeverityLevel(VocabularyValidationResultLevel.MAY.toString());
		v1.setConfiguredValidationResultSeverityLevel(lvl1);

		ConfiguredValidator v2 = new ConfiguredValidator();
		vs.add(v2);
		ConfiguredValidationResultSeverityLevel lvl2 = new ConfiguredValidationResultSeverityLevel();
		lvl2.setCodeSeverityLevel(VocabularyValidationResultLevel.SHALL.toString());
		v2.setConfiguredValidationResultSeverityLevel(lvl2);

		ConfiguredExpression r = f.accept(e);
		assertTrue("No validators qualify", r.getConfiguredValidators().size() == 1);
	}
}
