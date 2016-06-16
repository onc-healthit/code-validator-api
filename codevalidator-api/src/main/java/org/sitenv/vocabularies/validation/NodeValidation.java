package org.sitenv.vocabularies.validation;

import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import java.util.List;

public interface NodeValidation {
	List<VocabularyValidationResult> validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex);
}
