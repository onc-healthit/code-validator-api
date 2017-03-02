package org.sitenv.vocabularies.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Brian on 2/10/2016.
 */
@XmlRootElement(name = "validator")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguredValidator {
    @XmlElement(name = "name")
    String name;
    @XmlElement(name = "nodeType")
    String nodeType;
    @XmlElement(name = "validationResultSeverityLevels")
    ConfiguredValidationResultSeverityLevel configuredValidationResultSeverityLevel;
    @XmlElement(name = "allowedValuesetOids")
    String allowedValuesetOids;
    @XmlElement(name = "allowedCodesystemNames")
    String allowedCodesystemNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getAllowedValuesetOids() {
        return allowedValuesetOids;
    }

    public void setAllowedValuesetOids(String allowedValuesetOids) {
        this.allowedValuesetOids = allowedValuesetOids;
    }

    public ConfiguredValidationResultSeverityLevel getConfiguredValidationResultSeverityLevel() {
        return configuredValidationResultSeverityLevel;
    }

    public void setConfiguredValidationResultSeverityLevel(ConfiguredValidationResultSeverityLevel configuredValidationResultSeverityLevel) {
        this.configuredValidationResultSeverityLevel = configuredValidationResultSeverityLevel;
    }

    public String getAllowedCodesystemNames() {
        return allowedCodesystemNames;
    }

    public void setAllowedCodesystemNames(String allowedCodesystemNames) {
        this.allowedCodesystemNames = allowedCodesystemNames;
    }
}

