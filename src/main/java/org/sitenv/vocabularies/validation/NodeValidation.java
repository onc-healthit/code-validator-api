package org.sitenv.vocabularies.validation;

import java.util.List;

import javax.xml.xpath.XPath;

import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionFilter;

import com.ximpleware.VTDNav;

public interface NodeValidation {
	List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, VTDNav vn, int nodeIndex, ConfiguredExpressionFilter filter, String xpathExpression);
}
