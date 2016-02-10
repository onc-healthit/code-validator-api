package org.sitenv.vocabularies.repositories;

import org.sitenv.vocabularies.entities.VsacValueSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Brian on 2/10/2016.
 */
public interface VsacValuesSetRepository extends JpaRepository<VsacValueSet, Integer> {
    @Query("SELECT c FROM VsacValueSet c WHERE c.valuesetName = :valuesetName group by c.code, c.codeSystem")
    List<VsacValueSet> findByValuesetNameGroupedByCodeAndCodeSystem(String valuesetName);
    VsacValueSet findDistinctVsacValueSetByValuesetName(String valuesetName);
    List<VsacValueSet> findByValuesetNameAndCode(String valuesetName, String code);
    VsacValueSet findByValuesetNameAndDescription(String valuesetName, String description);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.valuesetName = :valuesetName")
    boolean existsByValuesetName(@Param("valuesetName") String valuesetName);
}