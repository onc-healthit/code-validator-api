package org.sitenv.vocabularies.validation.repositories;

import org.sitenv.vocabularies.validation.entities.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by Brian on 2/7/2016.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.codeSystem in (:codesystems)")
    boolean foundCodesystems(@Param("codesystems")Set<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.code = :code and c.displayName = :displayName and c.codeSystem in (:codesystems)")
    boolean foundCodeAndDisplayNameInCodesystem(@Param("code")String code, @Param("displayName")String displayName, @Param("codesystems")List<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.code = :code and c.displayName = :displayName and c.codeSystemOID = :codeSystemOID and c.codeSystem in (:codesystems) and c.active = true")
    boolean foundActiveCodeAndDisplayNameAndCodeSystemOIDInCodesystem(@Param("code")String code, @Param("displayName")String displayName, @Param("codeSystemOID")String codeSystemOID, @Param("codesystems")Set<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.code = :code and c.codeSystem in (:codesystems)")
    boolean foundCodeInCodesystems(@Param("code")String code, @Param("codesystems")Set<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.displayName = :displayName and c.codeSystem in (:codesystems)")
    boolean foundDisplayNameInCodesystems(@Param("displayName")String displayName, @Param("codesystems")Set<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.codeSystemOID = :codeSystemOID and c.codeSystem in (:codesystems)")
    boolean foundCodeSystemOIDInCodesystems(@Param("codeSystemOID")String codeSystemOID, @Param("codesystems")Set<String> codesystems);

    List<Code> findByCodeAndCodeSystemIn(String code, List<String> codesystems);

    @Query("SELECT c.active FROM Code c WHERE c.code = :code and c.codeSystem in (:codesystems)")
    boolean codeIsActive(@Param("code")String code, @Param("codesystems")Set<String> codesystems);
}
