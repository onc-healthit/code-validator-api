package org.sitenv.vocabularies.loader.code.snomed;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.loader.DelimitedTextVocabularyLoader;
import org.sitenv.vocabularies.model.impl.SnomedModel;

public class SnomedLoader extends DelimitedTextVocabularyLoader<SnomedModel> {
	public SnomedLoader() {
		super(SnomedModel.class, 1);
	}
	
	@Override
	protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, int lineIndex, String line) {
		String[] lineParts = StringUtils.splitPreserveAllTokens(line, "\t", 4);
		
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put("code", lineParts[0]);
		fields.put("displayName", lineParts[2]);
		fields.putAll(baseFields);
		
		this.loadDocument(doc, fields);
		
		return true;
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.SNOMEDCT_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.SNOMEDCT_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
