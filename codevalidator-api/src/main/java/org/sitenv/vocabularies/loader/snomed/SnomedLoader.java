package org.sitenv.vocabularies.loader.snomed;

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

public class SnomedLoader implements Loader {

	private static Logger logger = Logger.getLogger(SnomedLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.SNOMEDCT_CODE_NAME, SnomedLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.SNOMEDCT_CODE_NAME + "(" + VocabularyConstants.SNOMEDCT_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file) {
		Vocabulary snomed = new Vocabulary(file.getName());

		logger.debug("Loading SNOMED File: " + file.getName());

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
					
					snomed.getCodes().add(line[0].toUpperCase());
					snomed.getDisplayNames().add(line[2].toUpperCase());
					
					snomed.getCodeMap().put(line[0].toUpperCase(), line[2].toUpperCase());
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

		return snomed;
	}
	
	public String getCodeName() {
		return VocabularyConstants.SNOMEDCT_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.SNOMEDCT_CODE_SYSTEM;
	}
}
