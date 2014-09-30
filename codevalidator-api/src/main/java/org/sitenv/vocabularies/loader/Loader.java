package org.sitenv.vocabularies.loader;

import java.io.File;

import org.sitenv.vocabularies.data.Vocabulary;

public interface Loader {

	
	public Vocabulary load(File file);
	
}
