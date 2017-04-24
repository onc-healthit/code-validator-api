package org.sitenv.vocabularies.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ximpleware.AutoPilot;
import com.ximpleware.XPathParseException;

/**
 * Created by Brian on 2/10/2016.
 */
@XmlRootElement(name = "expression")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguredExpression {
    @XmlAttribute(name = "xpathExpression")
    String configuredXpathExpression;
    @XmlElement(name = "validator")
    List<ConfiguredValidator> configuredValidators = null;
    
    public String getConfiguredXpathExpression() {
        return configuredXpathExpression;
    }
        
    public void setConfiguredXpathExpression(String configuredXpathExpression) {
        this.configuredXpathExpression = configuredXpathExpression;
    }

    public List<ConfiguredValidator> getConfiguredValidators() {
        return configuredValidators;
    }

    public void setConfiguredValidators(List<ConfiguredValidator> configuredValidators) {
        this.configuredValidators = configuredValidators;
    }
}
