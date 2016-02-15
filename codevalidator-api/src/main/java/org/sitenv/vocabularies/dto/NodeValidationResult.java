package org.sitenv.vocabularies.dto;

public class NodeValidationResult {
	private boolean isValid;
	private String validatedDocumentXpathExpression;
	private int baseNodeIndex;
	private String configuredXpathExpression;
	private int nodeIndex;

	private String requestedCode;
	private String requestedCodeSystem;
	private String requestedCodeSystemName;
	private String requestedDisplayName;
	private String configuredAllowableValuesetOidsForNode;

	private boolean nodeValuesetsFound;
	private boolean nodeCodeSystemFoundInConfiguredAllowableValueSets;

	private boolean nodeCodeFoundInCodeSystemForConfiguredAllowableValueSets;
	private boolean nodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets;
	private boolean nodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets;

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean valid) {
		isValid = valid;
	}

	public String getValidatedDocumentXpathExpression() {
		return validatedDocumentXpathExpression;
	}

	public void setValidatedDocumentXpathExpression(String validatedDocumentXpathExpression) {
		this.validatedDocumentXpathExpression = validatedDocumentXpathExpression;
	}

	public int getBaseNodeIndex() {
		return baseNodeIndex;
	}

	public void setBaseNodeIndex(int baseNodeIndex) {
		this.baseNodeIndex = baseNodeIndex;
	}

	public String getConfiguredXpathExpression() {
		return configuredXpathExpression;
	}

	public void setConfiguredXpathExpression(String configuredXpathExpression) {
		this.configuredXpathExpression = configuredXpathExpression;
	}

	public int getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public String getRequestedCode() {
		return requestedCode;
	}

	public void setRequestedCode(String requestedCode) {
		this.requestedCode = requestedCode;
	}

	public String getRequestedCodeSystem() {
		return requestedCodeSystem;
	}

	public void setRequestedCodeSystem(String requestedCodeSystem) {
		this.requestedCodeSystem = requestedCodeSystem;
	}

	public String getRequestedCodeSystemName() {
		return requestedCodeSystemName;
	}

	public void setRequestedCodeSystemName(String requestedCodeSystemName) {
		this.requestedCodeSystemName = requestedCodeSystemName;
	}

	public String getRequestedDisplayName() {
		return requestedDisplayName;
	}

	public void setRequestedDisplayName(String requestedDisplayName) {
		this.requestedDisplayName = requestedDisplayName;
	}

	public String getConfiguredAllowableValuesetOidsForNode() {
		return configuredAllowableValuesetOidsForNode;
	}

	public boolean isNodeCodeSystemFoundInConfiguredAllowableValueSets() {
		return nodeCodeSystemFoundInConfiguredAllowableValueSets;
	}

	public void setNodeCodeSystemFoundInConfiguredAllowableValueSets(boolean nodeCodeSystemFoundInConfiguredAllowableValueSets) {
		this.nodeCodeSystemFoundInConfiguredAllowableValueSets = nodeCodeSystemFoundInConfiguredAllowableValueSets;
	}

	public boolean isNodeCodeFoundInCodeSystemForConfiguredAllowableValueSets() {
		return nodeCodeFoundInCodeSystemForConfiguredAllowableValueSets;
	}

	public void setNodeCodeFoundInCodeSystemForConfiguredAllowableValueSets(boolean nodeCodeFoundInCodeSystemForConfiguredAllowableValueSets) {
		this.nodeCodeFoundInCodeSystemForConfiguredAllowableValueSets = nodeCodeFoundInCodeSystemForConfiguredAllowableValueSets;
	}

	public boolean isNodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets() {
		return nodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets;
	}

	public void setNodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets(boolean nodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets) {
		this.nodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets = nodeCodeSystemNameFoundInCodeSystemForConfiguredAllowableValueSets;
	}

	public boolean isNodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets() {
		return nodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets;
	}

	public void setNodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets(boolean nodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets) {
		this.nodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets = nodeDisplayNameFoundInCodeSystemForConfiguredAllowableValueSets;
	}

	public void setConfiguredAllowableValuesetOidsForNode(String configuredAllowableValuesetOidsForNode) {
		this.configuredAllowableValuesetOidsForNode = configuredAllowableValuesetOidsForNode;
	}

	public boolean isNodeValuesetsFound() {
		return nodeValuesetsFound;
	}

	public void setNodeValuesetsFound(boolean nodeValuesetsFound) {
		this.nodeValuesetsFound = nodeValuesetsFound;
	}
}
