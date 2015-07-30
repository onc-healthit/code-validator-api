package org.sitenv.vocabularies.data;

import java.util.Set;
import java.util.Set;
import java.util.TreeSet;

public class CodeValidationResult {

	private String requestedCode;
	private String requestedCodeSystemOid;
	private String requestedCodeSystemName;
	private String requestedDisplayName;
	
	private Set<String> expectedDisplayNamesForCode = new TreeSet<String>();
	private Set<String> expectedCodeSystemNamesForOid = new TreeSet<String>();
	private Set<String> expectedOidsForCodeSystemName = new TreeSet<String>();
	private Set<String> expectedCodesForDisplayName = new TreeSet<String>();
	
	private Boolean displayNameExistsForCode = false;
	private Boolean codeSystemAndNameMatch = false;
	private Boolean codeExistsInCodeSystem = false;
	private Boolean displayNameExistsInCodeSystem = false;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((codeExistsInCodeSystem == null) ? 0
						: codeExistsInCodeSystem.hashCode());
		result = prime
				* result
				+ ((codeSystemAndNameMatch == null) ? 0
						: codeSystemAndNameMatch.hashCode());
		result = prime
				* result
				+ ((displayNameExistsForCode == null) ? 0
						: displayNameExistsForCode.hashCode());
		result = prime
				* result
				+ ((displayNameExistsInCodeSystem == null) ? 0
						: displayNameExistsInCodeSystem.hashCode());
		result = prime
				* result
				+ ((expectedCodeSystemNamesForOid == null) ? 0
						: expectedCodeSystemNamesForOid.hashCode());
		result = prime
				* result
				+ ((expectedCodesForDisplayName == null) ? 0
						: expectedCodesForDisplayName.hashCode());
		result = prime
				* result
				+ ((expectedDisplayNamesForCode == null) ? 0
						: expectedDisplayNamesForCode.hashCode());
		result = prime
				* result
				+ ((expectedOidsForCodeSystemName == null) ? 0
						: expectedOidsForCodeSystemName.hashCode());
		result = prime * result
				+ ((requestedCode == null) ? 0 : requestedCode.hashCode());
		result = prime
				* result
				+ ((requestedCodeSystemName == null) ? 0
						: requestedCodeSystemName.hashCode());
		result = prime
				* result
				+ ((requestedCodeSystemOid == null) ? 0
						: requestedCodeSystemOid.hashCode());
		result = prime
				* result
				+ ((requestedDisplayName == null) ? 0 : requestedDisplayName
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeValidationResult other = (CodeValidationResult) obj;
		if (codeExistsInCodeSystem == null) {
			if (other.codeExistsInCodeSystem != null)
				return false;
		} else if (!codeExistsInCodeSystem.equals(other.codeExistsInCodeSystem))
			return false;
		if (codeSystemAndNameMatch == null) {
			if (other.codeSystemAndNameMatch != null)
				return false;
		} else if (!codeSystemAndNameMatch.equals(other.codeSystemAndNameMatch))
			return false;
		if (displayNameExistsForCode == null) {
			if (other.displayNameExistsForCode != null)
				return false;
		} else if (!displayNameExistsForCode
				.equals(other.displayNameExistsForCode))
			return false;
		if (displayNameExistsInCodeSystem == null) {
			if (other.displayNameExistsInCodeSystem != null)
				return false;
		} else if (!displayNameExistsInCodeSystem
				.equals(other.displayNameExistsInCodeSystem))
			return false;
		if (expectedCodeSystemNamesForOid == null) {
			if (other.expectedCodeSystemNamesForOid != null)
				return false;
		} else if (!expectedCodeSystemNamesForOid
				.equals(other.expectedCodeSystemNamesForOid))
			return false;
		if (expectedCodesForDisplayName == null) {
			if (other.expectedCodesForDisplayName != null)
				return false;
		} else if (!expectedCodesForDisplayName
				.equals(other.expectedCodesForDisplayName))
			return false;
		if (expectedDisplayNamesForCode == null) {
			if (other.expectedDisplayNamesForCode != null)
				return false;
		} else if (!expectedDisplayNamesForCode
				.equals(other.expectedDisplayNamesForCode))
			return false;
		if (expectedOidsForCodeSystemName == null) {
			if (other.expectedOidsForCodeSystemName != null)
				return false;
		} else if (!expectedOidsForCodeSystemName
				.equals(other.expectedOidsForCodeSystemName))
			return false;
		if (requestedCode == null) {
			if (other.requestedCode != null)
				return false;
		} else if (!requestedCode.equals(other.requestedCode))
			return false;
		if (requestedCodeSystemName == null) {
			if (other.requestedCodeSystemName != null)
				return false;
		} else if (!requestedCodeSystemName
				.equals(other.requestedCodeSystemName))
			return false;
		if (requestedCodeSystemOid == null) {
			if (other.requestedCodeSystemOid != null)
				return false;
		} else if (!requestedCodeSystemOid.equals(other.requestedCodeSystemOid))
			return false;
		if (requestedDisplayName == null) {
			if (other.requestedDisplayName != null)
				return false;
		} else if (!requestedDisplayName.equals(other.requestedDisplayName))
			return false;
		return true;
	}
	public String getRequestedCode() {
		return requestedCode;
	}
	public void setRequestedCode(String requestedCode) {
		this.requestedCode = requestedCode;
	}
	public String getRequestedCodeSystemOid() {
		return requestedCodeSystemOid;
	}
	public void setRequestedCodeSystemOid(String requestedCodeSystemOid) {
		this.requestedCodeSystemOid = requestedCodeSystemOid;
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
	public Set<String> getExpectedDisplayNamesForCode() {
		return expectedDisplayNamesForCode;
	}
	public void setExpectedDisplayNamesForCode(
			Set<String> expectedDisplayNamesForCode) {
		this.expectedDisplayNamesForCode = expectedDisplayNamesForCode;
	}
	public Set<String> getExpectedCodeSystemNamesForOid() {
		return expectedCodeSystemNamesForOid;
	}
	public void setExpectedCodeSystemNamesForOid(
			Set<String> expectedCodeSystemNamesForOid) {
		this.expectedCodeSystemNamesForOid = expectedCodeSystemNamesForOid;
	}
	public Set<String> getExpectedOidsForCodeSystemName() {
		return expectedOidsForCodeSystemName;
	}
	public void setExpectedOidsForCodeSystemName(
			Set<String> expectedOidsForCodeSystemName) {
		this.expectedOidsForCodeSystemName = expectedOidsForCodeSystemName;
	}
	public Set<String> getExpectedCodesForDisplayName() {
		return expectedCodesForDisplayName;
	}
	public void setExpectedCodesForDisplayName(
			Set<String> expectedCodesForDisplayName) {
		this.expectedCodesForDisplayName = expectedCodesForDisplayName;
	}
	public Boolean getDisplayNameExistsForCode() {
		return displayNameExistsForCode;
	}
	public void setDisplayNameExistsForCode(Boolean displayNameExistsForCode) {
		this.displayNameExistsForCode = displayNameExistsForCode;
	}
	public Boolean getCodeSystemAndNameMatch() {
		return codeSystemAndNameMatch;
	}
	public void setCodeSystemAndNameMatch(Boolean codeSystemAndNameMatch) {
		this.codeSystemAndNameMatch = codeSystemAndNameMatch;
	}
	public Boolean getCodeExistsInCodeSystem() {
		return codeExistsInCodeSystem;
	}
	public void setCodeExistsInCodeSystem(Boolean codeExistsInCodeSystem) {
		this.codeExistsInCodeSystem = codeExistsInCodeSystem;
	}
	public Boolean getDisplayNameExistsInCodeSystem() {
		return displayNameExistsInCodeSystem;
	}
	public void setDisplayNameExistsInCodeSystem(
			Boolean displayNameExistsInCodeSystem) {
		this.displayNameExistsInCodeSystem = displayNameExistsInCodeSystem;
	}
	
	
	
	
	
	
}
