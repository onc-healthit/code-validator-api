package org.sitenv.vocabularies.loader.code.loinc;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.DelimitedTextVocabularyLoader;
import org.sitenv.vocabularies.model.impl.LoincModel;

public class LoincLoader extends DelimitedTextVocabularyLoader<LoincModel> {
	public LoincLoader() {
		super(LoincModel.class, 2, 4, 1);
	}
	
	@Override
	protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, Map<String, String> fields, int lineIndex,
		String line) {
		String[] lineParts = StringUtils.splitPreserveAllTokens(line, ",", 3);
		
		fields.clear();
		fields.put("code", StringUtils.strip(lineParts[0], "\""));
		fields.put("displayName", StringUtils.strip(lineParts[1], "\""));
		fields.putAll(baseFields);
		
		this.loadDocument(dbConnection, doc, fields);
		
		return true;
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.LOINC_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.LOINC_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
