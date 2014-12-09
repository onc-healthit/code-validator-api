package org.sitenv.vocabularies.loader;

import java.io.File;

import org.sitenv.vocabularies.model.VocabularyModelDefinition;

public interface Loader {

	public VocabularyModelDefinition load(File file);
	
	public String getCodeName();
	public String getCodeSystem();
	
}
