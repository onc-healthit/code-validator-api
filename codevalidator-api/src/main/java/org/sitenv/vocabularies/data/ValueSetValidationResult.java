package org.sitenv.vocabularies.data;

import java.util.ArrayList;
import java.util.List;

public class ValueSetValidationResult {
	
	private String requestedValueSetOid;
	private String requestedValueSetName;
	private String requestedCodeSystemOid;
	private String requestedCodeSystemName;
	private String requestedCode;
	private String requestedDescription;
	
	private List<String> expectedDescriptionsForCode = new ArrayList<String>();
	private List<String> expectedCodeSystemNamesForOid = new ArrayList<String>();
	private List<String> expectedOidsForCodeSystemName = new ArrayList<String>();
	private List<String> expectedCodesForDescription = new ArrayList<String>();
	
	private Boolean codeExistsInValueSet;
	private Boolean descriptionExistsInValueSet;
	private Boolean displayNameExistsForCode;
	private Boolean codeSystemAndNameMatch;
	private Boolean codeExistsInCodeSystem;
	private Boolean displayNameExistsInCodeSystem;
	
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
				+ ((codeExistsInValueSet == null) ? 0 : codeExistsInValueSet
						.hashCode());
		result = prime
				* result
				+ ((codeSystemAndNameMatch == null) ? 0
						: codeSystemAndNameMatch.hashCode());
		result = prime
				* result
				+ ((descriptionExistsInValueSet == null) ? 0
						: descriptionExistsInValueSet.hashCode());
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
				+ ((expectedCodesForDescription == null) ? 0
						: expectedCodesForDescription.hashCode());
		result = prime
				* result
				+ ((expectedDescriptionsForCode == null) ? 0
						: expectedDescriptionsForCode.hashCode());
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
				+ ((requestedDescription == null) ? 0 : requestedDescription
						.hashCode());
		result = prime
				* result
				+ ((requestedValueSetName == null) ? 0 : requestedValueSetName
						.hashCode());
		result = prime
				* result
				+ ((requestedValueSetOid == null) ? 0 : requestedValueSetOid
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
		ValueSetValidationResult other = (ValueSetValidationResult) obj;
		if (codeExistsInCodeSystem == null) {
			if (other.codeExistsInCodeSystem != null)
				return false;
		} else if (!codeExistsInCodeSystem.equals(other.codeExistsInCodeSystem))
			return false;
		if (codeExistsInValueSet == null) {
			if (other.codeExistsInValueSet != null)
				return false;
		} else if (!codeExistsInValueSet.equals(other.codeExistsInValueSet))
			return false;
		if (codeSystemAndNameMatch == null) {
			if (other.codeSystemAndNameMatch != null)
				return false;
		} else if (!codeSystemAndNameMatch.equals(other.codeSystemAndNameMatch))
			return false;
		if (descriptionExistsInValueSet == null) {
			if (other.descriptionExistsInValueSet != null)
				return false;
		} else if (!descriptionExistsInValueSet
				.equals(other.descriptionExistsInValueSet))
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
		if (expectedCodesForDescription == null) {
			if (other.expectedCodesForDescription != null)
				return false;
		} else if (!expectedCodesForDescription
				.equals(other.expectedCodesForDescription))
			return false;
		if (expectedDescriptionsForCode == null) {
			if (other.expectedDescriptionsForCode != null)
				return false;
		} else if (!expectedDescriptionsForCode
				.equals(other.expectedDescriptionsForCode))
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
		if (requestedDescription == null) {
			if (other.requestedDescription != null)
				return false;
		} else if (!requestedDescription.equals(other.requestedDescription))
			return false;
		if (requestedValueSetName == null) {
			if (other.requestedValueSetName != null)
				return false;
		} else if (!requestedValueSetName.equals(other.requestedValueSetName))
			return false;
		if (requestedValueSetOid == null) {
			if (other.requestedValueSetOid != null)
				return false;
		} else if (!requestedValueSetOid.equals(other.requestedValueSetOid))
			return false;
		return true;
	}
	
	public String getRequestedValueSetOid() {
		return requestedValueSetOid;
	}
	public void setRequestedValueSetOid(String requestedValueSetOid) {
		this.requestedValueSetOid = requestedValueSetOid;
	}
	public String getRequestedValueSetName() {
		return requestedValueSetName;
	}
	public void setRequestedValueSetName(String requestedValueSetName) {
		this.requestedValueSetName = requestedValueSetName;
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
	public String getRequestedCode() {
		return requestedCode;
	}
	public void setRequestedCode(String requestedCode) {
		this.requestedCode = requestedCode;
	}
	public String getRequestedDescription() {
		return requestedDescription;
	}
	public void setRequestedDescription(String requestedDescription) {
		this.requestedDescription = requestedDescription;
	}
	public List<String> getExpectedDescriptionsForCode() {
		return expectedDescriptionsForCode;
	}
	public void setExpectedDescriptionsForCode(
			List<String> expectedDescriptionsForCode) {
		this.expectedDescriptionsForCode = expectedDescriptionsForCode;
	}
	public List<String> getExpectedCodeSystemNamesForOid() {
		return expectedCodeSystemNamesForOid;
	}
	public void setExpectedCodeSystemNamesForOid(
			List<String> expectedCodeSystemNamesForOid) {
		this.expectedCodeSystemNamesForOid = expectedCodeSystemNamesForOid;
	}
	public List<String> getExpectedOidsForCodeSystemName() {
		return expectedOidsForCodeSystemName;
	}
	public void setExpectedOidsForCodeSystemName(
			List<String> expectedOidsForCodeSystemName) {
		this.expectedOidsForCodeSystemName = expectedOidsForCodeSystemName;
	}
	public List<String> getExpectedCodesForDescription() {
		return expectedCodesForDescription;
	}
	public void setExpectedCodesForDescription(
			List<String> expectedCodesForDescription) {
		this.expectedCodesForDescription = expectedCodesForDescription;
	}
	public Boolean getCodeExistsInValueSet() {
		return codeExistsInValueSet;
	}
	public void setCodeExistsInValueSet(Boolean codeExistsInValueSet) {
		this.codeExistsInValueSet = codeExistsInValueSet;
	}
	public Boolean getDescriptionExistsInValueSet() {
		return descriptionExistsInValueSet;
	}
	public void setDescriptionExistsInValueSet(Boolean descriptionExistsInValueSet) {
		this.descriptionExistsInValueSet = descriptionExistsInValueSet;
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
