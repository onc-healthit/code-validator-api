package org.sitenv.vocabularies.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Brian on 2/5/2016.
 */
@Entity
@Table(name = "VALUESETS")
public class VsacValueSet {
    @Id
    @Column(name = "ID")
    private Integer Id;

    @Column(name ="CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CODESYSTEM")
    private String codeSystem;

    @Column(name = "CODESYSTEMVERSION")
    private String codeSystemVersion;

    @Column(name = "CODESYSTEMOID")
    private String codeSystemOid;

    @Column(name = "TTY")
    private String tty;

    @Column(name = "VALUESETNAME")
    private String valuesetName;

    @Column(name = "OID")
    private String oid;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "DEFINITIONVERSION")
    private String definitionVersion;

    @Column(name = "STEWARD")
    private String steward;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    public String getCodeSystemVersion() {
        return codeSystemVersion;
    }

    public void setCodeSystemVersion(String codeSystemVersion) {
        this.codeSystemVersion = codeSystemVersion;
    }

    public String getCodeSystemOid() {
        return codeSystemOid;
    }

    public void setCodeSystemOid(String codeSystemOid) {
        this.codeSystemOid = codeSystemOid;
    }

    public String getTty() {
        return tty;
    }

    public void setTty(String tty) {
        this.tty = tty;
    }

    public String getValuesetName() {
        return valuesetName;
    }

    public void setValuesetName(String valuesetName) {
        this.valuesetName = valuesetName;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefinitionVersion() {
        return definitionVersion;
    }

    public void setDefinitionVersion(String definitionVersion) {
        this.definitionVersion = definitionVersion;
    }

    public String getSteward() {
        return steward;
    }

    public void setSteward(String steward) {
        this.steward = steward;
    }
}