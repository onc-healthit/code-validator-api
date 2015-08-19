package org.sitenv.vocabularies.model;


public class VocabularyModelDefinition {

	private Class<? extends CodeModel> clazz;
	private String codeSystem;
	
	

	public VocabularyModelDefinition(Class<? extends CodeModel> clazz, String codeSystem) {
		super();
		this.clazz = clazz;
		this.codeSystem = codeSystem;
	}


	public Class<? extends CodeModel> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends CodeModel> clazz) {
		this.clazz = clazz;
	}

	public String getCodeSystem() {
		return codeSystem;
	}

	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}



	
	
	
	
	
}
