package org.sitenv.vocabularies.loader.code.icd10;

import java.util.Map;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.model.impl.Icd10PcsModel;

public class Icd10PcsLoader extends Icd10Loader<Icd10PcsModel> {
	public Icd10PcsLoader() {
		super(Icd10PcsModel.class);
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.ICD10PCS_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.ICD10PCS_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
