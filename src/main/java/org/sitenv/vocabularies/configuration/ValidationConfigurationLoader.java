package org.sitenv.vocabularies.configuration;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.validation.pool.AutoPilotObjectPoolInitializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.Unmarshaller;

/**
 * Created by Brian on 2/10/2016.
 */
public class ValidationConfigurationLoader implements InitializingBean {
    private static Logger logger = Logger.getLogger(ValidationConfigurationLoader.class);
    private Unmarshaller unmarshaller;
    private String validationConfigurationFilePath;
    private Configurations configurations;

    public void setValidationConfigurationFilePath(String validationConfigurationFilePath) {
        this.validationConfigurationFilePath = validationConfigurationFilePath;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Configurations getConfigurations(){
        return configurations;
    }

    //Converts XML to Java Object
    public Object xmlToObject(String fileName) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            configurations = (Configurations) unmarshaller.unmarshal(new StreamSource(fis));
            logger.info("Loading expressions from: " + fileName + ". # Expressions:" + configurations.getExpressions().size());
            return configurations;
        } finally {
            fis.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        xmlToObject(validationConfigurationFilePath);
    	// ------------------------- INTERNAL CODE CHANGE START --------------------------
        AutoPilotObjectPoolInitializer initializer = new AutoPilotObjectPoolInitializer();
        initializer.initFromExpressions(configurations.getExpressions());
    	// ------------------------- INTERNAL CODE CHANGE END --------------------------
    }
}