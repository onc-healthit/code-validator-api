package org.sitenv.vocabularies.loader.code;

import java.io.File;
import java.util.List;

public interface CodeLoader {

	public void load(List<File> file);
	
	public String getCodeName();
	public String getCodeSystem();
	
}
