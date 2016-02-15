package org.sitenv.vocabularies.configuration;

import javax.xml.bind.annotation.*;
import java.util.List;

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
