package org.sitenv.vocabularies.validation.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.configuration.ConfiguredValidator;
import org.sitenv.vocabularies.validation.NodeValidation;
import org.sitenv.vocabularies.validation.NodeValidatorFactory;
import org.sitenv.vocabularies.validation.dto.VocabularyValidationResult;
import org.sitenv.vocabularies.validation.dto.enums.VocabularyValidationResultLevel;
import org.sitenv.vocabularies.validation.utils.CCDADocumentNamespaces;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionLevelFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

/**
 * Created by Brian on 2/10/2016.
 */
@Service
public class VocabularyValidationService {

	private static final Logger logger = Logger.getLogger(VocabularyValidationService.class);

	@Resource(name = "vocabularyValidationConfigurations")
	List<ConfiguredExpression> vocabularyValidationConfigurations;

	/*
	 * Following resource is defined to handle MU2 document validation to
	 * support CCDA R1.1 document templates This variable holds only MU2
	 * specific validation expressions
	 */
	// ------------------------- INTERNAL CODE CHANGE START
	// --------------------------
	@Resource(name = "vocabularyValidationConfigurationsForMu2")
	List<ConfiguredExpression> vocabularyValidationConfigurationsForMu2;
	// ------------------------- INTERNAL CODE CHANGE END
	// --------------------------

	// ------------------------- INTERNAL CODE CHANGE START
	// --------------------------
	// @Resource(name="documentBuilder")
	// DocumentBuilder documentBuilder;
	@Resource(name = "documentBuilderFactory")
	DocumentBuilderFactory documentBuilderFactory;

	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		return documentBuilderFactory.newDocumentBuilder();
	}

	@Autowired
	NodeValidatorFactory vocabularyValidatorFactory;

	@Autowired
	ValidateWorker worker;

	// ------------------------- INTERNAL CODE CHANGE END
	// --------------------------

	@Resource(name = "xPathFactory")
	XPathFactory xPathFactory;

	// @Autowired
	// VocabularyValidatorFactory vocabularyValidatorFactory;

	// ------------------------- INTERNAL CODE CHANGE START
	// --------------------------
	public List<VocabularyValidationResult> validate(String uri, String severityLevel)
			throws IOException, SAXException, ParserConfigurationException {
		// Document doc = documentBuilder.parse(uri);
		Document doc = getDocumentBuilder().parse(uri);
		return this.validate(doc, uri, severityLevel);
	}
	// ------------------------- INTERNAL CODE CHANGE END
	// --------------------------

	// ------------------------- INTERNAL CODE CHANGE START
	// --------------------------
	public List<VocabularyValidationResult> validate(InputStream stream, String severityLevel)
			throws IOException, SAXException, ParserConfigurationException {
		// Document doc = documentBuilder.parse(stream);
		String xml = IOUtils.toString(stream);
		stream.reset();
		Document doc = getDocumentBuilder().parse(stream);
		return this.validate(doc, xml, severityLevel);
	}
	// ------------------------- INTERNAL CODE CHANGE END
	// --------------------------

	public List<VocabularyValidationResult> validate(Document doc, String xmlDoc, String severityLevel) {
		Map<String, ArrayList<VocabularyValidationResult>> vocabularyValidationResultMap = getInitializedResultMap();
		if (doc != null) {
			String configuredXpathExpression = "";
			try {
				XPath xpath = getNewXpath(doc);

				/*
				 * Updated for loop to call getVocabValidationConfigurations
				 * method to validate respective document template specific
				 * expressions. getVocabValidationConfigurations() method
				 * returns the expression list based on the document templates
				 * (CCDA R1.1, CCDA R2.1)
				 * 
				 */
				// ------------------------- INTERNAL CODE CHANGE START --------------------------
				// for (ConfiguredExpression configuredExpression :
				// vocabularyValidationConfigurations) {
								
				List<ConfiguredExpression> docValidations = getVocabValidationConfigurations(doc, xpath);
				List<ConfiguredExpression> filteredList = new ArrayList<ConfiguredExpression>(docValidations.size());
				ConfiguredExpressionLevelFilter filter = new ConfiguredExpressionLevelFilter(severityLevel);
				for (ConfiguredExpression configuredExpression : docValidations) {
					ConfiguredExpression e = filter.accept(configuredExpression);
					if (e != null) {
						filteredList.add(e);
					}
				}

				logger.info("Document qualifying expressions:" + docValidations.size() + " filtered to level(" + severityLevel + ") leaves:" + filteredList.size());

				try {	
					VTDGen vg = new VTDGen();
					vg.setDoc(xmlDoc.getBytes("UTF-8"));
					vg.parse(true);
					VTDNav nav = vg.getNav();
																	
					List<Future<Map<String, List<VocabularyValidationResult>>>> futures = new ArrayList<Future<Map<String, List<VocabularyValidationResult>>>>();
					for (ConfiguredExpression configuredExpression : filteredList) {
						ValidateRequest r = new ValidateRequest(configuredExpression, filter, logger, nav);
						futures.add(worker.doWork(r));
					}
					for (Future<Map<String, List<VocabularyValidationResult>>> future : futures) {
						try {
							Map<String, List<VocabularyValidationResult>> r = future.get();
							for (String key : r.keySet()) {
								vocabularyValidationResultMap.get(key).addAll(r.get(key));
							}
						} catch (Exception e) {
							logger.error("Error gettting future result.",e);
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					logger.error("Error submitting futures.",e);
					e.printStackTrace();
				}
			} catch (Exception e) {
				logger.error("Error validating document for the following configured expression:" + configuredXpathExpression,e);
				System.err.println("ERROR VALIDATING DOCUMENT FOR THE FOLLOWING CONFIGURED EXPRESSION: "
						+ configuredXpathExpression);
				e.printStackTrace();
			}

		}
		return convertMapToList(vocabularyValidationResultMap);
	}

	private Map<String, ArrayList<VocabularyValidationResult>> getInitializedResultMap() {
		Map<String, ArrayList<VocabularyValidationResult>> resultMap = new HashMap<>();
		resultMap.put("errors", new ArrayList<VocabularyValidationResult>());
		resultMap.put("warnings", new ArrayList<VocabularyValidationResult>());
		resultMap.put("info", new ArrayList<VocabularyValidationResult>());
		return resultMap;
	}

	private XPath getNewXpath(final Document doc) {
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				String nameSpace;
				if (CCDADocumentNamespaces.sdtc.name().equals(prefix)) {
					nameSpace = CCDADocumentNamespaces.sdtc.getNamespace();
				} else {
					nameSpace = CCDADocumentNamespaces.defaultNameSpaceForCcda.getNamespace();
				}
				return nameSpace;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				return null;
			}

			@Override
			public Iterator getPrefixes(String namespaceURI) {
				return null;
			}
		});
		return xpath;
	}


	private List<VocabularyValidationResult> convertMapToList(
			Map<String, ArrayList<VocabularyValidationResult>> resultMap) {
		List<VocabularyValidationResult> results = new ArrayList<>();
		for (ArrayList<VocabularyValidationResult> resultList : resultMap.values()) {
			results.addAll(resultList);
		}
		return results;
	}

	/*
	 * Following method is added to differentiate weather CCDA document template
	 * is R 1.1 or R2.1 Load the xpath configuration list based on the document
	 * version (MU2 , MU3)
	 */
	// ------------------------- INTERNAL CODE CHANGE START --------------------------

	private List<ConfiguredExpression> getVocabValidationConfigurations(Document doc, XPath xpath)
			throws XPathExpressionException {

		Number num = (Number) xpath
				.compile("count(/v3:ClinicalDocument/v3:templateId[@root='2.16.840.1.113883.10.20.22.1.1']/@extension)")
				.evaluate(doc, XPathConstants.NUMBER);

		List<ConfiguredExpression> ret = null;
		if (num.intValue() == 0) {// MU2 CCDA R1.1 document
			ret = vocabularyValidationConfigurationsForMu2;
			if (ret.size() == 0) {
				logger.warn("Validating an MU2 CCDA R1.1 document, but found no configured vocabularyValidations!!!");
			}
		} else { // MU3 CCDA R2.1 document
			ret = vocabularyValidationConfigurations;
			if (ret.size() == 0) {
				logger.warn("Validating an MU3 CCDA R2.1 document, but found no configured vocabularyValidations!!!");
			}
		}
		return ret;
	}

	// ------------------------- INTERNAL CODE CHANGE END --------------------------

}
