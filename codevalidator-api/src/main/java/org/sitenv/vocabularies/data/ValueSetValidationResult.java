package org.sitenv.vocabularies.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ValueSetValidationResult {
	
	private String requestedValueSetOid;
	private String requestedCodeSystemOid;
	private String requestedCodeSystemName;
	private String requestedCode;
	private String requestedDescription;
	

	private List<String> valueSetNames = new ArrayList<String>();
	
	private Set<String> expectedDescriptionsForCode = new TreeSet<String>();
	private Set<String> expectedCodeSystemNamesForOid = new TreeSet<String>();
	private Set<String> expectedOidsForCodeSystemName = new TreeSet<String>();
	private Set<String> expectedCodesForDescription = new TreeSet<String>();
	private Set<String> expectedCodeSystemsForCode = new TreeSet<String>();
	private Set<String> expectedCodeSystemsForValueSet = new TreeSet<String>();
	
	private Boolean codeExistsInValueSet;
	private Boolean codeExistsInCodeSystem;

	private Boolean descriptionExistsInValueSet;
	private Boolean descriptionExistsInCodeSystem;
	private Boolean descriptionMatchesCode;
	
	
	private Boolean codeSystemAndNameMatch;
	private Boolean codeSystemExistsInValueSet;
	public String getRequestedValueSetOid() {
		return requestedValueSetOid;
	}
	public void setRequestedValueSetOid(String requestedValueSetOid) {
		this.requestedValueSetOid = requestedValueSetOid;
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
	public List<String> getValueSetNames() {
		return valueSetNames;
	}
	public void setValueSetNames(List<String> valueSetNames) {
		this.valueSetNames = valueSetNames;
	}
	public Set<String> getExpectedDescriptionsForCode() {
		return expectedDescriptionsForCode;
	}
	public void setExpectedDescriptionsForCode(
			Set<String> expectedDescriptionsForCode) {
		this.expectedDescriptionsForCode = expectedDescriptionsForCode;
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
	public Set<String> getExpectedCodesForDescription() {
		return expectedCodesForDescription;
	}
	public void setExpectedCodesForDescription(
			Set<String> expectedCodesForDescription) {
		this.expectedCodesForDescription = expectedCodesForDescription;
	}
	public Set<String> getExpectedCodeSystemsForCode() {
		return expectedCodeSystemsForCode;
	}
	public void setExpectedCodeSystemsForCode(Set<String> expectedCodeSystemsForCode) {
		this.expectedCodeSystemsForCode = expectedCodeSystemsForCode;
	}
	public Boolean getCodeExistsInValueSet() {
		return codeExistsInValueSet;
	}
	public void setCodeExistsInValueSet(Boolean codeExistsInValueSet) {
		this.codeExistsInValueSet = codeExistsInValueSet;
	}
	public Boolean getCodeExistsInCodeSystem() {
		return codeExistsInCodeSystem;
	}
	public void setCodeExistsInCodeSystem(Boolean codeExistsInCodeSystem) {
		this.codeExistsInCodeSystem = codeExistsInCodeSystem;
	}
	public Boolean getDescriptionExistsInValueSet() {
		return descriptionExistsInValueSet;
	}
	public void setDescriptionExistsInValueSet(Boolean descriptionExistsInValueSet) {
		this.descriptionExistsInValueSet = descriptionExistsInValueSet;
	}
	public Boolean getDescriptionExistsInCodeSystem() {
		return descriptionExistsInCodeSystem;
	}
	public void setDescriptionExistsInCodeSystem(
			Boolean descriptionExistsInCodeSystem) {
		this.descriptionExistsInCodeSystem = descriptionExistsInCodeSystem;
	}
	public Boolean getDescriptionMatchesCode() {
		return descriptionMatchesCode;
	}
	public void setDescriptionMatchesCode(Boolean descriptionMatchesCode) {
		this.descriptionMatchesCode = descriptionMatchesCode;
	}
	public Boolean getCodeSystemAndNameMatch() {
		return codeSystemAndNameMatch;
	}
	public void setCodeSystemAndNameMatch(Boolean codeSystemAndNameMatch) {
		this.codeSystemAndNameMatch = codeSystemAndNameMatch;
	}
	public Boolean getCodeSystemExistsInValueSet() {
		return codeSystemExistsInValueSet;
	}
	public void setCodeSystemExistsInValueSet(Boolean codeSystemExistsInValueSet) {
		this.codeSystemExistsInValueSet = codeSystemExistsInValueSet;
	}
	public Set<String> getExpectedCodeSystemsForValueSet() {
		return expectedCodeSystemsForValueSet;
	}
	public void setExpectedCodeSystemsForValueSet(
			Set<String> expectedCodeSystemsForValueSet) {
		this.expectedCodeSystemsForValueSet = expectedCodeSystemsForValueSet;
	}
	
	
	
	
}
