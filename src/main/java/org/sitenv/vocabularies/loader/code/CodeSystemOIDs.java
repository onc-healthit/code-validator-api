package org.sitenv.vocabularies.loader.code;

/**
 * Created by Brian on 8/9/2016.
 */
public enum CodeSystemOIDs {
	SNOMEDCT("2.16.840.1.113883.6.96"), 
	RXNORM("2.16.840.1.113883.6.88"),    
	LOINC("2.16.840.1.113883.6.1"),
    ICD10CM("2.16.840.1.113883.6.90"),
    ICD10PCS("2.16.840.1.113883.6.4"),
    CPT4("2.16.840.1.113883.6.12"),
    UNII("2.16.840.1.113883.4.9"),
    CDT("2.16.840.1.113883.6.13"),
    ICD9CMDX("2.16.840.1.113883.6.103"),
    ICD9CMSG("2.16.840.1.113883.6.104");

    private final String codesystemOID;

    CodeSystemOIDs(final String oid) {
            this.codesystemOID = oid;
    }

    public String codesystemOID(){
        return codesystemOID;
    }
}
