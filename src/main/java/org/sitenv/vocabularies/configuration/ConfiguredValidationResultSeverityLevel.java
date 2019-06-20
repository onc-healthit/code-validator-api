package org.sitenv.vocabularies.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.sitenv.vocabularies.constants.VocabularyConstants.ConfiguredSeverityLevel;
import org.sitenv.vocabularies.constants.VocabularyConstants.SeverityLevel;

/**
 * Created by Brian on 2/10/2016.
 */
@XmlRootElement(name = "validationResultSeverityLevels")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguredValidationResultSeverityLevel {
    @XmlElement(name = "codeSeverityLevel")
    String codeSeverityLevel;

	public ConfiguredValidationResultSeverityLevel() { }
    
    public ConfiguredValidationResultSeverityLevel(String codeSeverityLevel) {
    	this.codeSeverityLevel = codeSeverityLevel;
    }

    public String getCodeSeverityLevel() {
        return codeSeverityLevel;
    }

    public void setCodeSeverityLevel(String codeSeverityLevel) {
        this.codeSeverityLevel = codeSeverityLevel;
    }

	public SeverityLevel getSeverityLevelConversion() {
		return convertConfiguredValidationResultSeverityLevelToSeverityLevel(
				ConfiguredSeverityLevel.valueOf(codeSeverityLevel));
	}
	
	private SeverityLevel convertConfiguredValidationResultSeverityLevelToSeverityLevel(
			ConfiguredSeverityLevel configuredSeverityLevel) {		
		switch (configuredSeverityLevel) {
		case MAY:
			return SeverityLevel.INFO;		
		case SHOULD:
			return SeverityLevel.WARNING;			
		case SHALL:
			return SeverityLevel.ERROR;
		default:
			return SeverityLevel.INFO; 
		}    	
    }
}

