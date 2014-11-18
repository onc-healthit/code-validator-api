package org.sitenv.vocabularies.loader.icd9;

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

public class Icd9CmDxLoader implements Loader {

	private static Logger logger = Logger.getLogger(Icd9CmDxLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME, Icd9CmDxLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME + "(" + VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file) {
		Vocabulary icd9CmDx = new Vocabulary(file.getName());

		logger.debug("Loading ICD9CM_DX File: " + file.getName());

		BufferedReader br = null;
		
		try {

			
			int count = 0;

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				if (count++ == 0) {
					continue; // skip header row
				} else {

					String[] line = available.split("\t");
					
					icd9CmDx.getCodes().add(line[0].toUpperCase());
					icd9CmDx.getDisplayNames().add(line[1].toUpperCase());
					
					icd9CmDx.getCodeMap().put(line[0].toUpperCase(), line[1].toUpperCase());
				}


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

		return icd9CmDx;
	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM;
	}

}
