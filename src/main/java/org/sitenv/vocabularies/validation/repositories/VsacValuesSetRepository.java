package org.sitenv.vocabularies.validation.repositories;

import org.sitenv.vocabularies.validation.entities.VsacValueSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Brian on 2/10/2016.
 */
@Repository
public interface VsacValuesSetRepository extends JpaRepository<VsacValueSet, Integer> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.code = :code and c.codeSystem = :codeSystem and c.codeSystemName = :codeSystemName and c.displayName = :displayName and c.valuesetOid in (:valuesetOids)")
    boolean existsByCodeAndCodeSystemAndCodeSystemNameAndDisplayNameInValuesetOid(@Param("code")String code, @Param("codeSystem")String codeSystem, @Param("codeSystemName")String codeSystemName, @Param("displayName")String displayName, @Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.valuesetOid in (:valuesetOids)")
    boolean valuesetOidsExists(@Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.codeSystem = :codeSystem and c.valuesetOid in (:valuesetOids)")
    boolean codeSystemExistsInValueset(@Param("codeSystem") String codeSystem, @Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.displayName = :displayName and c.code = :code and c.codeSystem = :codeSystem and c.valuesetOid in (:valuesetOids)")
    boolean displayNameExistsForCodeByCodeSystemInValueset(@Param("displayName")String displayName, @Param("code")String code, @Param("codeSystem")String codeSystem, @Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.codeSystemName = :codeSystemName and c.code = :code and c.codeSystem = :codeSystem and c.valuesetOid in (:valuesetOids)")
    boolean codeSystemNameExistsForCodeByCodeSystemInValueset(@Param("codeSystemName")String codeSystemName, @Param("code")String code, @Param("codeSystem")String codeSystem, @Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.displayName = :displayName and c.valuesetOid in (:valuesetOids)")
    boolean displayNameExistsInValueset(@Param("displayName")String displayName, @Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.codeSystemName = :codeSystemName and c.valuesetOid in (:valuesetOids)")
    boolean codeSystemNameExistsInValueset(@Param("codeSystemName")String codeSystemName, @Param("valuesetOids")List<String> valuesetOids);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.code = :code and c.valuesetOid in (:valuesetOids)")
    boolean codeExistsInValueset(@Param("code") String code, @Param("valuesetOids")List<String> valuesetOids);

    List<VsacValueSet> findByValuesetOidIn(List<String> valuesetOids);

    List<VsacValueSet> findByCodeAndValuesetOidIn(String code, List<String> valuesetOids);
}