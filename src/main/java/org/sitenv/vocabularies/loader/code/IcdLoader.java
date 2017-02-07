package org.sitenv.vocabularies.loader.code;

import org.apache.commons.lang3.text.StrBuilder;
import org.sitenv.vocabularies.loader.BaseCodeLoader;

public abstract class IcdLoader extends BaseCodeLoader {
	protected final static char ICD_CODE_DELIM = '.';
	
	protected final static int ICD_CODE_PART_LEN = 3;
	
	protected IcdLoader() {
	}
	
	protected static String buildDelimitedIcdCode(String code) {
		int codeLen = code.length();
		
		if (codeLen <= 3) {
			return code;
		}
		
		int lastDelimIndex = -1, codeIndex;
		StrBuilder builder = new StrBuilder((codeLen + (codeLen / ICD_CODE_PART_LEN)));
		
		for (codeIndex = 0; codeIndex < codeLen; codeIndex++) {
			if (((codeIndex + 1) % ICD_CODE_PART_LEN) == 0) {
				if (!builder.isEmpty()) {
					builder.append(ICD_CODE_DELIM);
				}
				
				builder.append(code.substring(++lastDelimIndex, (codeIndex + 1)));
				
				lastDelimIndex = codeIndex;
			}
		}
		
		if (lastDelimIndex < (codeLen - 1)) {
			if (!builder.isEmpty()) {
				builder.append(ICD_CODE_DELIM);
			}
			
			builder.append(code.substring(++lastDelimIndex, codeLen));
		}
		
		return builder.build();

	}
}
