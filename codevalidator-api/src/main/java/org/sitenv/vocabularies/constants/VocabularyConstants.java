package org.sitenv.vocabularies.constants;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;

public class VocabularyConstants {
	public static final String ICD9CM_DIAGNOSIS_CODE_SYSTEM_ID = "2.16.840.1.113883.6.103";
	public static final String ICD9CM_DIAGNOSIS_CODE_SYSTEM_NAME = "ICD-9-CM, Volume 1&2";
	public static final String ICD9CM_DIAGNOSIS_CODE_SYSTEM_TYPE = "ICD9CM_DX";
	
	public static final String ICD9CM_PROCEDURE_CODE_SYSTEM_ID = "2.16.840.1.113883.6.104";
	public static final String ICD9CM_PROCEDURE_CODE_SYSTEM_NAME = "ICD-9 CM, Volume 3";
	public static final String ICD9CM_PROCEDURE_CODE_SYSTEM_TYPE = "ICD9CM_SG";
	
	public static final String ICD10CM_CODE_SYSTEM_ID = "2.16.840.1.113883.6.90";
	public static final String ICD10CM_CODE_SYSTEM_NAME = "ICD-10-CM Diagnosis";
	public static final String ICD10CM_CODE_SYSTEM_TYPE = "ICD10CM";
	
	public static final String ICD10PCS_CODE_SYSTEM_ID = "2.16.840.1.113883.6.4";
	public static final String ICD10PCS_CODE_SYSTEM_NAME = "ICD-10-PCS Procedure";
	public static final String ICD10PCS_CODE_SYSTEM_TYPE = "ICD10PCS";
	
	public static final String LOINC_CODE_SYSTEM_ID = "2.16.840.1.113883.6.1";
	public static final String LOINC_CODE_SYSTEM_NAME = "LOINC";
	public static final String LOINC_CODE_SYSTEM_TYPE = "LOINC";
	
	public static final String RXNORM_CODE_SYSTEM_ID = "2.16.840.1.113883.6.88";
	public static final String RXNORM_CODE_SYSTEM_NAME = "RxNorm";
	public static final String RXNORM_CODE_SYSTEM_TYPE = "RXNORM";
	
	public static final String SNOMEDCT_CODE_SYSTEM_ID = "2.16.840.1.113883.6.96";
	public static final String SNOMEDCT_CODE_SYSTEM_NAME = "SNOMED-CT";
	public static final String SNOMEDCT_CODE_SYSTEM_TYPE = "SNOMED-CT";
	
	@SuppressWarnings("serial")
	public static final BidiMap<String, String> CODE_SYSTEM_NAMES = new DualLinkedHashBidiMap<String, String>(){{
		this.put(ICD9CM_DIAGNOSIS_CODE_SYSTEM_ID, ICD9CM_DIAGNOSIS_CODE_SYSTEM_NAME);
		this.put(ICD9CM_PROCEDURE_CODE_SYSTEM_ID, ICD9CM_PROCEDURE_CODE_SYSTEM_NAME);
		this.put(ICD10CM_CODE_SYSTEM_ID, ICD10CM_CODE_SYSTEM_NAME);
		this.put(ICD10PCS_CODE_SYSTEM_ID, ICD10PCS_CODE_SYSTEM_NAME);
		this.put(LOINC_CODE_SYSTEM_ID, LOINC_CODE_SYSTEM_NAME);
		this.put(RXNORM_CODE_SYSTEM_ID, RXNORM_CODE_SYSTEM_NAME);
		this.put(SNOMEDCT_CODE_SYSTEM_ID, SNOMEDCT_CODE_SYSTEM_NAME);
	}};
	
	public static final String PHIN_VADS_VALUE_SET_TYPE = "PHVS";
	
	public static final String VSAC_VALUE_SET_TYPE = "VSAC";
}
