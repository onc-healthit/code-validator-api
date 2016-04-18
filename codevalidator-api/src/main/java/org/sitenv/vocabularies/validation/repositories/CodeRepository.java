package org.sitenv.vocabularies.validation.repositories;

import org.sitenv.vocabularies.validation.entities.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Brian on 2/7/2016.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.codeSystem in (:codesystems)")
    boolean foundCodesystems(@Param("codesystems")List<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.code = :code and c.displayName = :displayName and c.codeSystem in (:codesystems)")
    boolean foundCodeAndDisplayNameInCodesystem(@Param("code")String code, @Param("displayName")String displayName, @Param("codesystems")List<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.code = :code and c.codeSystem in (:codesystems)")
    boolean foundCodeInCodesystems(@Param("code")String code, @Param("codesystems")List<String> codesystems);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.displayName = :displayName and c.codeSystem in (:codesystems)")
    boolean foundDisplayNameInCodesystems(@Param("displayName")String displayName, @Param("codesystems")List<String> codesystems);

}
