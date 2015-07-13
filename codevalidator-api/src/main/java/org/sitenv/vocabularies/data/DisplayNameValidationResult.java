package org.sitenv.vocabularies.data;

import java.util.List;

public class DisplayNameValidationResult {
	private String code;
	private String anticipatedDisplayName;
	private	List<String> actualDisplayName;
	private boolean result = false;
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((actualDisplayName == null) ? 0 : actualDisplayName
						.hashCode());
		result = prime
				* result
				+ ((anticipatedDisplayName == null) ? 0
						: anticipatedDisplayName.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (this.result ? 1231 : 1237);
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
		DisplayNameValidationResult other = (DisplayNameValidationResult) obj;
		if (actualDisplayName == null) {
			if (other.actualDisplayName != null)
				return false;
		} else if (!actualDisplayName.equals(other.actualDisplayName))
			return false;
		if (anticipatedDisplayName == null) {
			if (other.anticipatedDisplayName != null)
				return false;
		} else if (!anticipatedDisplayName.equals(other.anticipatedDisplayName))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (result != other.result)
			return false;
		return true;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAnticipatedDisplayName() {
		return anticipatedDisplayName;
	}
	public void setAnticipatedDisplayName(String anticipatedDisplayName) {
		this.anticipatedDisplayName = anticipatedDisplayName;
	}
	public List<String> getActualDisplayName() {
		return actualDisplayName;
	}
	public void setActualDisplayName(List<String> actualDisplayName) {
		this.actualDisplayName = actualDisplayName;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	
	
	
	
}
