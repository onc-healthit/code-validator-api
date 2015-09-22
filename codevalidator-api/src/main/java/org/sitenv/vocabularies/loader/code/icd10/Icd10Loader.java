package org.sitenv.vocabularies.loader.code.icd10;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.Map;
import org.sitenv.vocabularies.loader.IcdLoader;
import org.sitenv.vocabularies.model.CodeModel;

public abstract class Icd10Loader<T extends CodeModel> extends IcdLoader<T> {
	protected Icd10Loader(Class<T> modelClass) {
		super(modelClass);
	}
	
	@Override
	protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, Map<String, String> fields, int lineIndex,
		String line) {
		fields.clear();
		fields.put("code", buildDelimitedIcdCode(line.substring(6, 13).trim()));
		fields.put("displayName", line.substring(16, 76).trim());
		fields.putAll(baseFields);
		
		this.loadDocument(dbConnection, doc, fields);
		
		fields.put("displayName", line.substring(77).trim());
		
		this.loadDocument(dbConnection, doc, fields);
		
		return true;
	}
}
