package org.sitenv.vocabularies.model;

public abstract class ValueSetCodeModel extends CodeModel {
	protected String valueSetId;
	protected String valueSetName;
	protected String valueSetVersion;
	protected String steward;
	protected String type;

	public ValueSetCodeModel() {
		super();
	}

	public ValueSetCodeModel(String code, String displayName, String tty, String codeSystemId, String codeSystemName, String codeSystemVersion,
		String valueSetId, String valueSetName, String valueSetVersion, String steward, String type) {
		super(code, displayName, tty, codeSystemId, codeSystemName, codeSystemVersion);
		this.valueSetId = valueSetId;
		this.valueSetName = valueSetName;
		this.valueSetVersion = valueSetVersion;
		this.steward = steward;
		this.type = type;
	}

	public String getValueSetId() {
		return this.valueSetId;
	}

	public void setValueSetId(String valueSetId) {
		this.valueSetId = valueSetId;
	}

	public String getValueSetName() {
		return this.valueSetName;
	}

	public void setValueSetName(String valueSetName) {
		this.valueSetName = valueSetName;
	}

	public String getValueSetVersion() {
		return this.valueSetVersion;
	}

	public void setValueSetVersion(String valueSetVersion) {
		this.valueSetVersion = valueSetVersion;
	}

	public String getSteward() {
		return this.steward;
	}

	public void setSteward(String steward) {
		this.steward = steward;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
