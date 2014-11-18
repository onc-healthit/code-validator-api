package org.sitenv.vocabularies.loader.rxnorm;

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
import org.sitenv.vocabularies.loader.snomed.SnomedLoader;

public class RxNormLoader implements Loader {

	private static Logger logger = Logger.getLogger(RxNormLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.RXNORM_CODE_NAME, RxNormLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.RXNORM_CODE_NAME + "(" + VocabularyConstants.RXNORM_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file) {
		Vocabulary rxNorm = new Vocabulary(file.getName());

		logger.debug("Loading RXNORM File: " + file.getName());

		BufferedReader br = null;
		
		try {


			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				
				String[] line = available.split("\\|");
				
				rxNorm.getCodes().add(line[0].toUpperCase());
				rxNorm.getDisplayNames().add(line[14].toUpperCase());
				
				rxNorm.getCodeMap().put(line[0].toUpperCase(), line[14].toUpperCase());
			


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

		return rxNorm;
	}
	
	public String getCodeName() {
		return VocabularyConstants.RXNORM_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.RXNORM_CODE_SYSTEM;
	}
}
