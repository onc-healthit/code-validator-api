package org.sitenv.vocabularies.validation.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Brian on 2/5/2016.
 */
@Entity
@Table(name = "CODES")
public class Code {
    @Id
    @Column(name = "ID")
    private Integer Id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DISPLAYNAME")
    private String displayName;

    @Column(name = "CODESYSTEM")
    private String codeSystem;

    @Column(name = "CODESYSTEMOID")
    private String codeSystemOID;

    public String getCodeSystemOID() {
        return codeSystemOID;
    }

    public void setCodeSystemOID(String codeSystemOID) {
        this.codeSystemOID = codeSystemOID;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }
}