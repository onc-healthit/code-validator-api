package org.sitenv.vocabularies.loader.code.icd9;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.DelimitedTextVocabularyLoader;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;

public class Icd9CmDxLoader extends DelimitedTextVocabularyLoader<Icd9CmDxModel> {
	public Icd9CmDxLoader() {
		super(Icd9CmDxModel.class, 1);
	}
	
	@Override
	protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, int lineIndex, String line) {
		String[] lineParts = StringUtils.splitPreserveAllTokens(line, "\t", 2);
		
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put("code", lineParts[0].trim());
		fields.put("displayName", lineParts[1].trim());
		fields.putAll(baseFields);
		
		this.loadDocument(doc, fields);
		
		return true;
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
