package org.sitenv.vocabularies.model;

public abstract class VocabularyModelDefinition<T extends CodeModel> {
	protected Class<T> modelClass;
	protected String type;

	protected VocabularyModelDefinition(Class<T> modelClass, String type) {
		this.modelClass = modelClass;
		this.type = type;
	}

	public Class<T> getModelClass() {
		return this.modelClass;
	}

	public String getType() {
		return this.type;
	}
}
