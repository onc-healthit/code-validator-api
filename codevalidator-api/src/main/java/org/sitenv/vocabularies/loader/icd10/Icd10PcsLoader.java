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

public class Icd10PcsLoader implements Loader {

	private static Logger logger = Logger.getLogger(Icd10PcsLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD10PCS_CODE_NAME, Icd10PcsLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD10PCS_CODE_NAME + "(" + VocabularyConstants.ICD10PCS_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file) {
		Vocabulary icd10Pcs = new Vocabulary(file.getName());

		logger.debug("Loading ICD10PCS File: " + file.getName());

		BufferedReader br = null;
		
		try {			
			

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				
				String code = available.substring(6, 13).trim();
				String displayName = available.substring(77).trim();
			
				icd10Pcs.getCodes().add(code);
				icd10Pcs.getDisplayNames().add(displayName);
				
				icd10Pcs.getCodeMap().put(code, displayName);
				


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

		return icd10Pcs;
	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD10PCS_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD10PCS_CODE_SYSTEM;
	}



}
