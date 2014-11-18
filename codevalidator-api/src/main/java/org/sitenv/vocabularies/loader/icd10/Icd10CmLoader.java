package org.sitenv.vocabularies.loader.icd10;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.Vocabulary;
import org.sitenv.vocabularies.loader.Loader;
import org.sitenv.vocabularies.loader.LoaderManager;

public class Icd10CmLoader  implements Loader {

	private static Logger logger = Logger.getLogger(Icd10CmLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD10CM_CODE_NAME, Icd10CmLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD10CM_CODE_NAME + "(" + VocabularyConstants.ICD10CM_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file) {
		Vocabulary icd10Cm = new Vocabulary(file.getName());

		logger.debug("Loading ICD10CM File: " + file.getName());

		BufferedReader br = null;
		
		try {

			
			

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				
				String code = available.substring(6, 13).trim();
				String displayName = available.substring(77).trim();
			
				icd10Cm.getCodes().add(code);
				icd10Cm.getDisplayNames().add(displayName);
				
				icd10Cm.getCodeMap().put(code, displayName);
				


			}
		} catch (FileNotFoundException e) {
			// TODO: log4j
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Runtime r = Runtime.getRuntime();
			r.gc();
		}

		return icd10Cm;
	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD10CM_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD10CM_CODE_SYSTEM;
	}


}
