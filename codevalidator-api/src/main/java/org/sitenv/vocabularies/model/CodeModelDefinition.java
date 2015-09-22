package org.sitenv.vocabularies.model;

public class CodeModelDefinition<T extends CodeModel> extends VocabularyModelDefinition<T> {
    private String codeSystemId;
    private String codeSystemName;
    
    public CodeModelDefinition(Class<T> modelClass, String type, String codeSystemId, String codeSystemName) {
        super(modelClass, type);
        
        this.codeSystemId = codeSystemId;
        this.codeSystemName = codeSystemName;
    }

    public String getCodeSystemId() {
        return this.codeSystemId;
    }

    public String getCodeSystemName() {
        return this.codeSystemName;
    }
}
