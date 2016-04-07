package org.sitenv.vocabularies.validation.utils;

public enum CCDADocumentNamespaces {
    sdtc ("urn:hl7-org:sdtc"),
    v3 ("urn:hl7-org:v3"),
    defaultNameSpaceForCcda ("urn:hl7-org:v3"),
    voc ("urn:hl7-org:v3/voc");

    private final String namespace;

    CCDADocumentNamespaces(final String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }
}
