package org.sitenv.vocabularies.model;

public class ValueSetModelDefinition {
	private Class<? extends ValueSetModel> clazz;
	private String valueSetAuthorName;
	
	

	public ValueSetModelDefinition(Class<? extends ValueSetModel> clazz, String valueSetAuthorName) {
		super();
		this.clazz = clazz;
		this.valueSetAuthorName = valueSetAuthorName;
	}


	public Class<? extends ValueSetModel> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends ValueSetModel> clazz) {
		this.clazz = clazz;
	}


	public String getValueSetAuthorName() {
		return valueSetAuthorName;
	}


	public void setValueSetAuthorName(String valueSetAuthorName) {
		this.valueSetAuthorName = valueSetAuthorName;
	}


}
