package org.sitenv.vocabularies.model;

import com.orientechnologies.orient.core.annotation.ODocumentInstance;
import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class CodeModel {
	@OId
    protected ORecordId rid;
    @ODocumentInstance
    protected ODocument document;
	protected String code;
	protected String displayName;
	protected String tty;
	protected String codeSystemId;
	protected String codeSystemName;
	protected String codeSystemVersion;

	public CodeModel() {
	}

	public CodeModel(String code, String displayName, String tty, String codeSystemId, String codeSystemName, String codeSystemVersion) {
		this.code = code;
		this.displayName = displayName;
		this.tty = tty;
		this.codeSystemId = codeSystemId;
		this.codeSystemName = codeSystemName;
		this.codeSystemVersion = codeSystemVersion;
	}

	public ORecordId getRid() {
        return this.rid;
    }

    public void setRid(ORecordId rid) {
        this.rid = rid;
    }

    public ODocument getDocument() {
        return this.document;
    }

    public void setDocument(ODocument document) {
        this.document = document;
    }

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getTty() {
		return this.tty;
	}

	public void setTty(String tty) {
		this.tty = tty;
	}

	public String getCodeSystemId() {
		return this.codeSystemId;
	}

	public void setCodeSystemId(String codeSystemId) {
		this.codeSystemId = codeSystemId;
	}

	public String getCodeSystemName() {
		return this.codeSystemName;
	}

	public void setCodeSystemName(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}

	public String getCodeSystemVersion() {
		return this.codeSystemVersion;
	}

	public void setCodeSystemVersion(String codeSystemVersion) {
		this.codeSystemVersion = codeSystemVersion;
	}
}
