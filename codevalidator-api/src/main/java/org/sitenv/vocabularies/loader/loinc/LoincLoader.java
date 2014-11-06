package org.sitenv.vocabularies.loader.loinc;

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

public class LoincLoader implements Loader {

	private static Logger logger = Logger.getLogger(LoincLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.LOINC_CODE_NAME, LoincLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.LOINC_CODE_NAME + "(" + VocabularyConstants.LOINC_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file) {
		
		Vocabulary loinc = new Vocabulary(file.getName());

		logger.debug("Loading LOINC File: " + file.getName());

		BufferedReader br = null;
		
		try {

			
			int count = 0;

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				if (count++ == 0) {
					continue; // skip header row
				} else {

					String[] line = available.split(",");
					String code = line[0].replace("\"", "").toUpperCase();
					String name = line[1].replace("\"", "").toUpperCase();
					
					loinc.getCodes().add(code);
					loinc.getDisplayNames().add(name);
					
					loinc.getCodeMap().put(code, name);
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
		}

		return loinc;
		
	}
	
	
	public String getCodeName() {
		return VocabularyConstants.LOINC_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.LOINC_CODE_SYSTEM;
	}
}
