package org.sitenv.vocabularies.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Brian on 2/10/2016.
 */
public class ValidationConfigurationLoader implements InitializingBean {
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private String validationConfigurationFilePath;
    private Configurations configurations;

    public void setValidationConfigurationFilePath(String validationConfigurationFilePath) {
        this.validationConfigurationFilePath = validationConfigurationFilePath;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Configurations getConfigurations(){
        return configurations;
    }
    //Converts Object to XML file
    public void objectToXML(String fileName, Object graph) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            marshaller.marshal(graph, new StreamResult(fos));
        } finally {
            fos.close();
        }
    }
    //Converts XML to Java Object
    public Object xmlToObject(String fileName) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            return configurations = (Configurations) unmarshaller.unmarshal(new StreamSource(fis));
        } finally {
            fis.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        xmlToObject(validationConfigurationFilePath);
    }
}