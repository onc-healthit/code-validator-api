package org.sitenv.vocabularies.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Brian on 2/10/2016.
 */
@XmlRootElement(name = "validationResultSeverityLevels")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguredValidationResultSeverityLevel {
    @XmlElement(name = "codeSeverityLevel")
    String codeSeverityLevel;

    public String getCodeSeverityLevel() {
        return codeSeverityLevel;
    }

    public void setCodeSeverityLevel(String codeSeverityLevel) {
        this.codeSeverityLevel = codeSeverityLevel;
    }
}

