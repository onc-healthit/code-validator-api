package org.sitenv.vocabularies.validation.dto;

public class NodeValidationResult {
	private boolean isValid;
	private String validatedDocumentXpathExpression;
	private int baseNodeIndex;
	private String configuredXpathExpression;
	private int nodeIndex;
	public long getErrorOffset() {
		return errorOffset;
	}

	public void setErrorOffset(long errorOffset) {
		this.errorOffset = errorOffset;
	}

	private long errorOffset = 0;
	
	private String requestedCode;
	private String requestedCodeSystem;
	private String requestedCodeSystemName;
	private String requestedDisplayName;
    private String requestedUnit;
	private String requestedClassCode;
	private String requestedText;
	private String configuredAllowableValuesetOidsForNode;
	private String configuredAllowableCodesystemNamesForNode;
	private String ruleID;

	private boolean nodeCodeSystemOIDFound;
	private boolean nodeValuesetsFound;
	private boolean codeSystemFound;
	private boolean nodeCodeFound;
	private boolean nodeCodeSystemNameFound;
	private boolean nodeDisplayNameFound;

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

	public void setConfiguredAllowableValuesetOidsForNode(String configuredAllowableValuesetOidsForNode) {
		this.configuredAllowableValuesetOidsForNode = configuredAllowableValuesetOidsForNode;
	}

	public boolean isNodeValuesetsFound() {
		return nodeValuesetsFound;
	}

	public void setNodeValuesetsFound(boolean nodeValuesetsFound) {
		this.nodeValuesetsFound = nodeValuesetsFound;
	}

    public String getRequestedUnit() {
        return requestedUnit;
    }

    public void setRequestedUnit(String requestedUnit) {
        this.requestedUnit = requestedUnit;
    }

	public String getConfiguredAllowableCodesystemNamesForNode() {
		return configuredAllowableCodesystemNamesForNode;
	}

	public void setConfiguredAllowableCodesystemNamesForNode(String configuredAllowableCodesystemNamesForNode) {
		this.configuredAllowableCodesystemNamesForNode = configuredAllowableCodesystemNamesForNode;
	}

	public boolean isCodeSystemFound() {
		return codeSystemFound;
	}

	public void setCodeSystemFound(boolean codeSystemFound) {
		this.codeSystemFound = codeSystemFound;
	}

	public boolean isNodeCodeFound() {
		return nodeCodeFound;
	}

	public void setNodeCodeFound(boolean nodeCodeFound) {
		this.nodeCodeFound = nodeCodeFound;
	}

	public boolean isNodeCodeSystemNameFound() {
		return nodeCodeSystemNameFound;
	}

	public void setNodeCodeSystemNameFound(boolean nodeCodeSystemNameFound) {
		this.nodeCodeSystemNameFound = nodeCodeSystemNameFound;
	}

	public boolean isNodeDisplayNameFound() {
		return nodeDisplayNameFound;
	}

	public void setNodeDisplayNameFound(boolean nodeDisplayNameFound) {
		this.nodeDisplayNameFound = nodeDisplayNameFound;
	}

	public String getRequestedClassCode() {
		return requestedClassCode;
	}

	public void setRequestedClassCode(String requestedClassCode) {
		this.requestedClassCode = requestedClassCode;
	}

	public String getRequestedText() {
		return requestedText;
	}

	public void setRequestedText(String requestedText) {
		this.requestedText = requestedText;
	}

	public boolean isNodeCodeSystemOIDFound() {
		return nodeCodeSystemOIDFound;
	}

	public void setNodeCodeSystemOIDFound(boolean nodeCodeSystemOIDFound) {
		this.nodeCodeSystemOIDFound = nodeCodeSystemOIDFound;
	}
	
	public String getRuleID() {
		return this.ruleID;
	}
	
	public void setRuleID(String id) {
		this.ruleID = id;
	}
}
