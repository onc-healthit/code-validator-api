package org.sitenv.vocabularies.loader.valueset;

import java.io.File;
import java.util.List;

public interface ValueSetLoader {
	
	public String getValueSetAuthorName();

	public void load(List<File> file);
	
}
