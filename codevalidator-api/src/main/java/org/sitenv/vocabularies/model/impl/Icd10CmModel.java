package org.sitenv.vocabularies.model.impl;

import org.sitenv.vocabularies.model.CodeModel;

public class Icd10CmModel implements CodeModel{
	
	
	private String code;
	private String displayName;
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
