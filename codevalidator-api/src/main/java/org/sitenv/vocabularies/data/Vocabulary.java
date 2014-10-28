package org.sitenv.vocabularies.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Vocabulary {

	private String file;
	private Set<String> codes;
	private Set<String> displayNames;
	private Map<String, String> codeMap;
	
	public Vocabulary (String fileName) {
		codes = new HashSet<String>();
		displayNames = new HashSet<String>();
		file = fileName;
		codeMap = new HashMap<String,String>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeMap == null) ? 0 : codeMap.hashCode());
		result = prime * result + ((codes == null) ? 0 : codes.hashCode());
		result = prime * result
				+ ((displayNames == null) ? 0 : displayNames.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vocabulary other = (Vocabulary) obj;
		if (codeMap == null) {
			if (other.codeMap != null)
				return false;
		} else if (!codeMap.equals(other.codeMap))
			return false;
		if (codes == null) {
			if (other.codes != null)
				return false;
		} else if (!codes.equals(other.codes))
			return false;
		if (displayNames == null) {
			if (other.displayNames != null)
				return false;
		} else if (!displayNames.equals(other.displayNames))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Set<String> getCodes() {
		return codes;
	}

	public void setCodes(Set<String> codes) {
		this.codes = codes;
	}

	public Set<String> getDisplayNames() {
		return displayNames;
	}

	public void setDisplayNames(Set<String> displayNames) {
		this.displayNames = displayNames;
	}

	public Map<String, String> getCodeMap() {
		return codeMap;
	}

	public void setCodeMap(Map<String, String> codeMap) {
		this.codeMap = codeMap;
	}

	
	
	
	
	
	
}
