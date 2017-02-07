package org.sitenv.vocabularies.loader.code;

import org.springframework.stereotype.Component;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "ICD10PCS")
public class Icd10PcsLoader extends Icd10BaseLoader {
    public Icd10PcsLoader() {
        this.oid = CodeSystemOIDs.ICD10PCS.codesystemOID();
    }
}
