package org.sitenv.vocabularies.validation.services;

import org.sitenv.vocabularies.validation.entities.VsacValueSet;
import org.sitenv.vocabularies.validation.repositories.VsacValuesSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Brian on 2/23/2016.
 */
@Service
public class VocabularyValuesetService {
    private VsacValuesSetRepository vsacValuesSetRepository;

    @Autowired
    public VocabularyValuesetService(VsacValuesSetRepository vsacValuesSetRepository) {
        this.vsacValuesSetRepository = vsacValuesSetRepository;
    }

    public List<VsacValueSet> getValuesetsByOids(Set<String> valuesetOids){
        return vsacValuesSetRepository.findByValuesetOidIn(new ArrayList<>(valuesetOids));
    }

    public boolean isFoundByCodeInValuesetOids(String code, Set<String> valuesetOids){
        return vsacValuesSetRepository.codeExistsInValueset(code, new ArrayList<>(valuesetOids));
    }

    public List<VsacValueSet> getValuesetByCodeInValuesetOids(String code, Set<String> valuesetOids){
        return vsacValuesSetRepository.findByCodeAndValuesetOidIn(code, new ArrayList<>(valuesetOids));
    }
}
