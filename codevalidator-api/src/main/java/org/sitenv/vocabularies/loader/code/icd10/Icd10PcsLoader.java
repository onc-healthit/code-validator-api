package org.sitenv.vocabularies.loader.code.icd10;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.LinkedHashMap;
import java.util.Map;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.DelimitedTextVocabularyLoader;
import org.sitenv.vocabularies.model.impl.Icd10PcsModel;

public class Icd10PcsLoader extends DelimitedTextVocabularyLoader<Icd10PcsModel> {
	public Icd10PcsLoader() {
		super(Icd10PcsModel.class, 0);
	}
	
	@Override
	protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, int lineIndex, String line) {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put("code", line.substring(6, 13).trim());
		fields.put("displayName", line.substring(77).trim());
		fields.putAll(baseFields);
		
		this.loadDocument(doc, fields);
		
		return true;
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.ICD10PCS_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.ICD10PCS_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
