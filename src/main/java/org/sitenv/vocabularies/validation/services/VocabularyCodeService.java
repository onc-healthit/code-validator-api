package org.sitenv.vocabularies.validation.services;

import org.sitenv.vocabularies.validation.entities.Code;
import org.sitenv.vocabularies.validation.repositories.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        String cleanedCode = code.trim().toUpperCase();
        String cleanedDisplayName = displayName.trim().toUpperCase();
        ArrayList<String> cleanedCodeSystems = new ArrayList<>();
        for(String codeSystem : codeSystems){
            cleanedCodeSystems.add(codeSystem.trim().toUpperCase());
        }

        return codeRepository.foundCodeAndDisplayNameInCodesystem(cleanedCode, cleanedDisplayName, cleanedCodeSystems);
    }

    public boolean isFoundByCodeInCodeSystems(String code, Set<String> codeSystems){
        String cleanedCode = code.trim().toUpperCase();
        ArrayList<String> cleanedCodeSystems = new ArrayList<>();
        for(String codeSystem : codeSystems){
            cleanedCodeSystems.add(codeSystem.trim().toUpperCase());
        }
        return codeRepository.foundCodeInCodesystems(cleanedCode, cleanedCodeSystems);
    }

    public List<Code> getByCodeInCodeSystems(String code, List<String> codeSystems){
        String cleanedCode = code.trim().toUpperCase();
        ArrayList<String> cleanedCodeSystems = new ArrayList<>();
        for(String codeSystem : codeSystems){
            cleanedCodeSystems.add(codeSystem.trim().toUpperCase());
        }
        return codeRepository.findByCodeAndCodeSystemIn(cleanedCode, cleanedCodeSystems);
    }
}
