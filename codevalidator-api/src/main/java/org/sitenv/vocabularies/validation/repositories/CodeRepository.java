package org.sitenv.vocabularies.validation.repositories;

import org.sitenv.vocabularies.validation.entities.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Brian on 2/7/2016.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
    List<Code> findByCode(String code);
    List<Code> findByDisplayName(String displayName);
}
