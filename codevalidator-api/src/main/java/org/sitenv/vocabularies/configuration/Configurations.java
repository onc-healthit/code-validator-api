package org.sitenv.vocabularies.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Brian on 2/10/2016.
 */
@XmlRootElement(name = "configurations")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configurations {
    @XmlElement(name="expression")
    private List<ConfiguredExpression> expressions = null;

    public List<ConfiguredExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<ConfiguredExpression> expressions) {
        this.expressions = expressions;
    }
}
