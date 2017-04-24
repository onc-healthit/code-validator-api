package org.sitenv.vocabularies.validation;

import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionFilter;
import org.w3c.dom.Node;

import com.ximpleware.VTDNav;

import javax.xml.xpath.XPath;
import java.util.List;

public interface NodeValidation {
	List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, VTDNav vn, int nodeIndex, ConfiguredExpressionFilter filter, String xpathExpression);
}
