package org.sitenv.vocabularies.loader.valueset.phvs;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sitenv.vocabularies.loader.DelimitedTextVocabularyLoader;
import org.sitenv.vocabularies.model.impl.PhinVadsModel;

public class PhinVadsLoader extends DelimitedTextVocabularyLoader<PhinVadsModel> {
	public PhinVadsLoader() {
		super(PhinVadsModel.class, 4);
	}

	@Override
	protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, int lineIndex, String line) {
		String[] lineParts = StringUtils.splitPreserveAllTokens(line, "\t", 9);
		
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put("code", lineParts[0]);
		fields.put("displayName", lineParts[1]);
		fields.put("codeSystemId", lineParts[4]);
		fields.put("codeSystemName", lineParts[5]);
		fields.put("codeSystemVersion", lineParts[7]);
		fields.putAll(baseFields);
		
		this.loadDocument(doc, fields);
		
		return true;
	}

	@Override
	protected void processHeaderLine(Map<String, String> baseFields, int headerLineIndex, String headerLine) {
		if (headerLineIndex == 1) {
			String[] headerLineParts = StringUtils.splitPreserveAllTokens(headerLine, "\t", 5);
			
			baseFields.put("valueSetId", headerLineParts[2]);
			baseFields.put("valueSetName", headerLineParts[0]);
			baseFields.put("valueSetVersion", headerLineParts[3]);
		}
	}
}
