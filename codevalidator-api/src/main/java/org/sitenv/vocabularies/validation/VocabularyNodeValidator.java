package org.sitenv.vocabularies.validation;

import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.dto.NodeValidationResult;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;

public interface VocabularyNodeValidator {
	NodeValidationResult validateNode(ConfiguredValidator configuredValidator, XPath xpath, Node node, int nodeIndex);
}
