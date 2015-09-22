package org.sitenv.vocabularies.loader.code.icd9;

import java.util.Map;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.model.impl.Icd9CmSgModel;

public class Icd9CmSgLoader extends Icd9Loader<Icd9CmSgModel> {
	public Icd9CmSgLoader() {
		super(Icd9CmSgModel.class);
	}
	
	@Override
	protected Map<String, String> buildBaseFields() {
		Map<String, String> baseFields = super.buildBaseFields();
		baseFields.put("codeSystemId", VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_ID);
		baseFields.put("codeSystemName", VocabularyConstants.ICD9CM_PROCEDURE_CODE_SYSTEM_NAME);
		
		return baseFields;
	}
}
