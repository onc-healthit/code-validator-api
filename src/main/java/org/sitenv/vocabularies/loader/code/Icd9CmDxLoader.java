package org.sitenv.vocabularies.loader.code;

import org.springframework.stereotype.Component;

/**
 * Created by Brian on 2/7/2016.
 */
@Component(value = "ICD9CM_DX")
public class Icd9CmDxLoader extends Icd9BaseLoader {

    public Icd9CmDxLoader() {
        this.oid = CodeSystemOIDs.ICD9CMDX.codesystemOID();
    }
}
