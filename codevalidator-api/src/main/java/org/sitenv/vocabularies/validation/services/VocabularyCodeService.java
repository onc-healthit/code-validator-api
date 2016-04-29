package org.sitenv.vocabularies.validation.services;

import org.sitenv.vocabularies.validation.repositories.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Brian on 2/23/2016.
 */
@Service
public class VocabularyCodeService {
    private CodeRepository codeRepository;

    @Autowired
    public VocabularyCodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public boolean isFoundByCodeAndDisplayNameInCodeSystems(String code, String displayName, Set<String> codeSystems){
        return codeRepository.foundCodeAndDisplayNameInCodesystem(code, displayName, new ArrayList<>(codeSystems));
    }

    public boolean isFoundByCodeInCodeSystems(String code, Set<String> codeSystems){
        return codeRepository.foundCodeInCodesystems(code, new ArrayList<>(codeSystems));
    }
}
