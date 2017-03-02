package org.sitenv.vocabularies.validation.entities;

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

    @Column(name = "DISPLAYNAME")
    private String displayName;

    @Column(name = "CODESYSTEMNAME")
    private String codeSystemName;

    @Column(name = "CODESYSTEMVERSION")
    private String codeSystemVersion;

    @Column(name = "CODESYSTEM")
    private String codeSystem;

    @Column(name = "TTY")
    private String tty;

    @Column(name = "VALUESETNAME")
    private String valuesetName;

    @Column(name = "VALUESETOID")
    private String valuesetOid;

    @Column(name = "VALUESETTYPE")
    private String valuesetType;

    @Column(name = "VALUESETDEFINITIONVERSION")
    private String valuesetDefinitionVersion;

    @Column(name = "VALUESETSTEWARD")
    private String valuesetSteward;

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

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }

    public String getCodeSystemVersion() {
        return codeSystemVersion;
    }

    public void setCodeSystemVersion(String codeSystemVersion) {
        this.codeSystemVersion = codeSystemVersion;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
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

    public String getValuesetOid() {
        return valuesetOid;
    }

    public void setValuesetOid(String valuesetOid) {
        this.valuesetOid = valuesetOid;
    }

    public String getValuesetType() {
        return valuesetType;
    }

    public void setValuesetType(String valuesetType) {
        this.valuesetType = valuesetType;
    }

    public String getValuesetDefinitionVersion() {
        return valuesetDefinitionVersion;
    }

    public void setValuesetDefinitionVersion(String valuesetDefinitionVersion) {
        this.valuesetDefinitionVersion = valuesetDefinitionVersion;
    }

    public String getValuesetSteward() {
        return valuesetSteward;
    }

    public void setValuesetSteward(String valuesetSteward) {
        this.valuesetSteward = valuesetSteward;
    }
}