package org.sitenv.vocabularies.constants;

import java.util.HashMap;
import java.util.Map;

public class VocabularyConstants {
	public static final String SNOMEDCT_CODE_NAME = "SNOMED-CT";
	public static final String SNOMEDCT_CODE_SYSTEM = "2.16.840.1.113883.6.96";
	
	public static final Map<String, String> CODE_SYSTEM_MAP = new HashMap<String,String>();
	
	static {
		CODE_SYSTEM_MAP.put(SNOMEDCT_CODE_NAME, SNOMEDCT_CODE_SYSTEM);
	}
}
