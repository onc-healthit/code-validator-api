package org.sitenv.vocabularies.constants;

import java.util.HashMap;
import java.util.Map;

public class VocabularyConstants {
	public static final String SNOMEDCT_CODE_NAME = "SNOMED-CT";
	public static final String SNOMEDCT_CODE_SYSTEM = "2.16.840.1.113883.6.96";
	
	public static final String LOINC_CODE_NAME = "LOINC";
	public static final String LOINC_CODE_SYSTEM = "2.16.840.1.113883.6.1";
	
	public static final String ICD9CM_DIAGNOSIS_CODE_NAME = "ICD9CM_DX";
	public static final String ICD9CM_DIAGNOSIS_CODE_SYSTEM = "2.16.840.1.113883.6.103";
	
	public static final String ICD9CM_PROCEDURE_CODE_NAME = "ICD9CM_SG";
	public static final String ICD9CM_PROCEDURE_CODE_SYSTEM = "2.16.840.1.113883.6.104";
	
	public static final String ICD10CM_CODE_NAME = "ICD10CM";
	public static final String ICD10CM_CODE_SYSTEM = "2.16.840.1.113883.6.90";
	
	public static final String ICD10PCS_CODE_NAME = "ICD10PCS";
	public static final String ICD10PCS_CODE_SYSTEM = "2.16.840.1.113883.6.4";
	
	public static final String RXNORM_CODE_NAME = "RXNORM";
	public static final String RXNORM_CODE_SYSTEM = "2.16.840.1.113883.6.88";
	
	public static final Map<String, String> CODE_SYSTEM_MAP = new HashMap<String,String>();
	
	static {
		CODE_SYSTEM_MAP.put(SNOMEDCT_CODE_NAME, SNOMEDCT_CODE_SYSTEM);
		CODE_SYSTEM_MAP.put(LOINC_CODE_NAME, LOINC_CODE_SYSTEM);
		CODE_SYSTEM_MAP.put(ICD9CM_DIAGNOSIS_CODE_NAME, ICD9CM_DIAGNOSIS_CODE_SYSTEM);
		CODE_SYSTEM_MAP.put(ICD9CM_PROCEDURE_CODE_NAME, ICD9CM_PROCEDURE_CODE_SYSTEM);
		CODE_SYSTEM_MAP.put(ICD10CM_CODE_NAME, ICD10CM_CODE_SYSTEM);
		CODE_SYSTEM_MAP.put(ICD10PCS_CODE_NAME, ICD10PCS_CODE_SYSTEM);
		CODE_SYSTEM_MAP.put(RXNORM_CODE_NAME, RXNORM_CODE_SYSTEM);
	}
}
