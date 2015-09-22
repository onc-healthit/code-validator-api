package org.sitenv.vocabularies.loader.code.icd10;

import java.util.Map;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;

public class Icd10CmLoader extends Icd10Loader<Icd10CmModel> {
	public Icd10CmLoader() {
		super(Icd10CmModel.class);
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.ICD10CM_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.ICD10CM_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
