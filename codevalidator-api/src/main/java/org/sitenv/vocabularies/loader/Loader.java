package org.sitenv.vocabularies.loader;

import java.io.File;

import org.sitenv.vocabularies.data.Vocabulary;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public interface Loader {

	public Vocabulary load(File file, OObjectDatabaseTx dbConnection);
	
	public String getCodeName();
	public String getCodeSystem();
	
}
