package org.sitenv.vocabularies.loader.code;

import org.springframework.stereotype.Component;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "ICD10CM")
public class Icd10CmLoader extends Icd10BaseLoader {

    @Override
    protected void setOID(String oid) {
        oid = CodeSystemOIDs.ICD10CM.codesystemOID();
    }
}
