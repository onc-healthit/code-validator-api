package org.sitenv.vocabularies.model;

public class ValueSetModelDefinition<T extends ValueSetCodeModel> extends VocabularyModelDefinition<T> {
	public ValueSetModelDefinition(Class<T> modelClass, String type) {
		super(modelClass, type);
	}
}
