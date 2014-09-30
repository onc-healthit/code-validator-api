package org.sitenv.vocabularies.data;

import java.util.Map;

public class VocabularyDataStore {
	
	// Singleton self reference
	private static final VocabularyDataStore ACTIVE_INSTANCE = new VocabularyDataStore();
	
	private Map<String, Map<String, Vocabulary>> vocabulariesMap;
	
	private VocabularyDataStore () {}
	
	
	public static VocabularyDataStore getInstance() {
		return ACTIVE_INSTANCE;
	}
	
	
	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((vocabulariesMap == null) ? 0 : vocabulariesMap.hashCode());
		return result;
	}

	@Override
	public synchronized boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VocabularyDataStore other = (VocabularyDataStore) obj;
		if (vocabulariesMap == null) {
			if (other.vocabulariesMap != null)
				return false;
		} else if (!vocabulariesMap.equals(other.vocabulariesMap))
			return false;
		return true;
	}

	public synchronized Map<String,Map<String, Vocabulary>> getVocabulariesMap() {
		return vocabulariesMap;
	}

	public synchronized void setVocabulariesMap(Map<String, Map<String, Vocabulary>> vocabulariesMap) {
		this.vocabulariesMap = vocabulariesMap;
	}

	
}
