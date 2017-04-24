package org.sitenv.vocabularies.validation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.NodeValidation;
import org.sitenv.vocabularies.validation.NodeValidatorFactory;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.pool.AutoPilotPool;
import org.sitenv.vocabularies.validation.pool.AutoPilotWrapper;
import org.sitenv.vocabularies.validation.utils.CCDADocumentNamespaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDNav;

@Component
public class ValidateWorker {

	private XPathFactory xPathFactory;

	public ValidateWorker() {
		this.xPathFactory = XPathFactory.newInstance();
	}

	@Resource(name = "vocabularyValidationConfigurations")
	List<ConfiguredExpression> vocabularyValidationConfigurations;

	@Resource(name = "vocabularyValidationConfigurationsForMu2")
	List<ConfiguredExpression> vocabularyValidationConfigurationsForMu2;

	@Autowired
	NodeValidatorFactory vocabularyValidatorFactory;

	@Async("taskExecutor")
	public Future<Map<String, List<VocabularyValidationResult>>> doWork(ValidateRequest req) {

		Map<String, List<VocabularyValidationResult>> vocabularyValidationResultMap = getInitializedResultMap();

		ConfiguredExpression configuredExpression = req.getConfiguredExpression();
		XPath xpath = null;
		String configuredXpathExpression = configuredExpression.getConfiguredXpathExpression();
		AutoPilotWrapper apw = null;
		try {
			apw = AutoPilotPool.newInstance().borrow(configuredXpathExpression);

			VTDNav vn = req.getNav().cloneNav();
			AutoPilot ap = apw.getAutoPilot();
			ap.bind(vn);

			int i = 0;
			while (ap.evalXPath() != -1) {
				i++;
				List<VocabularyValidationResult> vocabularyValidationResults = new ArrayList<>();
				boolean validNode = false;
				Iterator<ConfiguredValidator> configIterator = configuredExpression.getConfiguredValidators()
						.iterator();
				while (configIterator.hasNext() && !validNode) {
					ConfiguredValidator configuredValidator = configIterator.next();
					NodeValidation vocabularyValidator = vocabularyValidatorFactory
							.getVocabularyValidator(configuredValidator.getName());
					List<VocabularyValidationResult> tempResults = vocabularyValidator.validateNode(configuredValidator,
							xpath, vn, i, req.getFilter(), configuredXpathExpression);
					if (foundValidationError(tempResults)) {
						vocabularyValidationResults.addAll(tempResults);
					} else {
						vocabularyValidationResults.clear();
						vocabularyValidationResults.addAll(tempResults);
						validNode = true;
					}
				}

				for (VocabularyValidationResult vocabularyValidationResult : vocabularyValidationResults) {
					vocabularyValidationResult.getNodeValidationResult()
							.setConfiguredXpathExpression(configuredXpathExpression);
					vocabularyValidationResultMap
							.get(vocabularyValidationResult.getVocabularyValidationResultLevel().getResultType())
							.add(vocabularyValidationResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (apw != null) {
				AutoPilotPool.newInstance().returnObject(apw);
			}
		}

		return new AsyncResult<Map<String, List<VocabularyValidationResult>>>(vocabularyValidationResultMap);
	}

	private Map<String, List<VocabularyValidationResult>> getInitializedResultMap() {
		Map<String, List<VocabularyValidationResult>> resultMap = new HashMap<>();
		resultMap.put("errors", new ArrayList<VocabularyValidationResult>());
		resultMap.put("warnings", new ArrayList<VocabularyValidationResult>());
		resultMap.put("info", new ArrayList<VocabularyValidationResult>());
		return resultMap;
	}

	private boolean foundValidationError(List<VocabularyValidationResult> results) {
		for (VocabularyValidationResult result : results) {
			if (result.getVocabularyValidationResultLevel().equals(VocabularyValidationResultLevel.SHALL)) {
				return true;
			}
		}
		return false;
	}
}
