package org.sitenv.vocabularies.model.impl;

import org.sitenv.vocabularies.model.CodeModel;

public class Icd10CmModel implements CodeModel{
	
	
	private String code;
	private String displayName;
	
	private String codeIndex;
	private String displayNameIndex;
	
	public String getCodeIndex() {
		return codeIndex;
	}
	public void setCodeIndex(String codeIndex) {
		this.codeIndex = codeIndex;
	}
	public String getDisplayNameIndex() {
		return displayNameIndex;
	}
	public void setDisplayNameIndex(String displayNameIndex) {
		this.displayNameIndex = displayNameIndex;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}
