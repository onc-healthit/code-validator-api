package org.sitenv.vocabularies.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

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
            return configurations = (Configurations) unmarshaller.unmarshal(new StreamSource(fis));
        } finally {
            if (fis == null) {
                throw new RuntimeException("Error loading validation configuration: " +
                (validationConfigurationFilePath == null
                        ?"validationConfigurationFilePath not set"
                        :"could not parse " + validationConfigurationFilePath)
                );
            } else {
                fis.close();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (validationConfigurationFilePath == null) {
            logger.error("validationConfigurationFilePath is null");
        }
        xmlToObject(validationConfigurationFilePath);
    }
}