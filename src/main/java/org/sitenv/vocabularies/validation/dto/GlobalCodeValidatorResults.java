package org.sitenv.vocabularies.validation.dto;

public class GlobalCodeValidatorResults {
	
	private int vocabularyValidationConfigurationsCount;
	private int vocabularyValidationConfigurationsErrorCount;
	
	public int getVocabularyValidationConfigurationsCount() {
		return vocabularyValidationConfigurationsCount;
	}

	public void setVocabularyValidationConfigurationsCount(int vocabularyValidationConfigurationsCount) {
		this.vocabularyValidationConfigurationsCount = vocabularyValidationConfigurationsCount;
	}
	
	public int getVocabularyValidationConfigurationsErrorCount() {
		return vocabularyValidationConfigurationsErrorCount;
	}	
	
	public void setVocabularyValidationConfigurationsErrorCount(int vocabularyValidationConfigurationsErrorCount) {
		this.vocabularyValidationConfigurationsErrorCount = vocabularyValidationConfigurationsErrorCount;
	}
	
}
