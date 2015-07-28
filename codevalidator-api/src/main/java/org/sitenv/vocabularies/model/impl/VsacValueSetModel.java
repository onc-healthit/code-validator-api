package org.sitenv.vocabularies.model.impl;

import org.sitenv.vocabularies.model.ValueSetModel;

public class VsacValueSetModel implements ValueSetModel {

	private String valueSet;
	private String valueSetName;
	private String type;
	private String definitionVersion;
	private String steward;
	private String code;
	private String description;
	private String codeSystemName;
	private String codeSystemVersion;
	private String codeSystem;
	private String tty;
	
	private String valueSetIndex;
	private String valueSetNameIndex;
	private String codeIndex;
	private String descriptionIndex;
	private String codeSystemIndex;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((codeIndex == null) ? 0 : codeIndex.hashCode());
		result = prime * result
				+ ((codeSystem == null) ? 0 : codeSystem.hashCode());
		result = prime * result
				+ ((codeSystemIndex == null) ? 0 : codeSystemIndex.hashCode());
		result = prime * result
				+ ((codeSystemName == null) ? 0 : codeSystemName.hashCode());
		result = prime
				* result
				+ ((codeSystemVersion == null) ? 0 : codeSystemVersion
						.hashCode());
		result = prime
				* result
				+ ((definitionVersion == null) ? 0 : definitionVersion
						.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime
				* result
				+ ((descriptionIndex == null) ? 0 : descriptionIndex.hashCode());
		result = prime * result + ((steward == null) ? 0 : steward.hashCode());
		result = prime * result + ((tty == null) ? 0 : tty.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((valueSet == null) ? 0 : valueSet.hashCode());
		result = prime * result
				+ ((valueSetIndex == null) ? 0 : valueSetIndex.hashCode());
		result = prime * result
				+ ((valueSetName == null) ? 0 : valueSetName.hashCode());
		result = prime
				* result
				+ ((valueSetNameIndex == null) ? 0 : valueSetNameIndex
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
		VsacValueSetModel other = (VsacValueSetModel) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (codeIndex == null) {
			if (other.codeIndex != null)
				return false;
		} else if (!codeIndex.equals(other.codeIndex))
			return false;
		if (codeSystem == null) {
			if (other.codeSystem != null)
				return false;
		} else if (!codeSystem.equals(other.codeSystem))
			return false;
		if (codeSystemIndex == null) {
			if (other.codeSystemIndex != null)
				return false;
		} else if (!codeSystemIndex.equals(other.codeSystemIndex))
			return false;
		if (codeSystemName == null) {
			if (other.codeSystemName != null)
				return false;
		} else if (!codeSystemName.equals(other.codeSystemName))
			return false;
		if (codeSystemVersion == null) {
			if (other.codeSystemVersion != null)
				return false;
		} else if (!codeSystemVersion.equals(other.codeSystemVersion))
			return false;
		if (definitionVersion == null) {
			if (other.definitionVersion != null)
				return false;
		} else if (!definitionVersion.equals(other.definitionVersion))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (descriptionIndex == null) {
			if (other.descriptionIndex != null)
				return false;
		} else if (!descriptionIndex.equals(other.descriptionIndex))
			return false;
		if (steward == null) {
			if (other.steward != null)
				return false;
		} else if (!steward.equals(other.steward))
			return false;
		if (tty == null) {
			if (other.tty != null)
				return false;
		} else if (!tty.equals(other.tty))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (valueSet == null) {
			if (other.valueSet != null)
				return false;
		} else if (!valueSet.equals(other.valueSet))
			return false;
		if (valueSetIndex == null) {
			if (other.valueSetIndex != null)
				return false;
		} else if (!valueSetIndex.equals(other.valueSetIndex))
			return false;
		if (valueSetName == null) {
			if (other.valueSetName != null)
				return false;
		} else if (!valueSetName.equals(other.valueSetName))
			return false;
		if (valueSetNameIndex == null) {
			if (other.valueSetNameIndex != null)
				return false;
		} else if (!valueSetNameIndex.equals(other.valueSetNameIndex))
			return false;
		return true;
	}
	public String getValueSet() {
		return valueSet;
	}
	public void setValueSet(String valueSet) {
		this.valueSet = valueSet;
	}
	public String getValueSetName() {
		return valueSetName;
	}
	public void setValueSetName(String valueSetName) {
		this.valueSetName = valueSetName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDefinitionVersion() {
		return definitionVersion;
	}
	public void setDefinitionVersion(String definitionVersion) {
		this.definitionVersion = definitionVersion;
	}
	public String getSteward() {
		return steward;
	}
	public void setSteward(String steward) {
		this.steward = steward;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCodeSystemName() {
		return codeSystemName;
	}
	public void setCodeSystemName(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}
	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}
	public void setCodeSystemVersion(String codeSystemVersion) {
		this.codeSystemVersion = codeSystemVersion;
	}
	public String getCodeSystem() {
		return codeSystem;
	}
	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}
	public String getTty() {
		return tty;
	}
	public void setTty(String tty) {
		this.tty = tty;
	}
	public String getValueSetIndex() {
		return valueSetIndex;
	}
	public void setValueSetIndex(String valueSetIndex) {
		this.valueSetIndex = valueSetIndex;
	}
	public String getValueSetNameIndex() {
		return valueSetNameIndex;
	}
	public void setValueSetNameIndex(String valueSetNameIndex) {
		this.valueSetNameIndex = valueSetNameIndex;
	}
	public String getCodeIndex() {
		return codeIndex;
	}
	public void setCodeIndex(String codeIndex) {
		this.codeIndex = codeIndex;
	}
	public String getDescriptionIndex() {
		return descriptionIndex;
	}
	public void setDescriptionIndex(String descriptionIndex) {
		this.descriptionIndex = descriptionIndex;
	}
	public String getCodeSystemIndex() {
		return codeSystemIndex;
	}
	public void setCodeSystemIndex(String codeSystemIndex) {
		this.codeSystemIndex = codeSystemIndex;
	}
	
	
	
	
	
	

}
