package org.sitenv.vocabularies.loader.code.icd9;

import java.util.Map;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;

public class Icd9CmDxLoader extends Icd9Loader<Icd9CmDxModel> {
	public Icd9CmDxLoader() {
		super(Icd9CmDxModel.class);
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
