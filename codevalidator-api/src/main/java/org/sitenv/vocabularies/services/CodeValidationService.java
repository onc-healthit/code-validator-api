package org.sitenv.vocabularies.services;

import org.sitenv.vocabularies.data.CodeValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Created by Brian on 2/10/2016.
 */
@Service
public class CodeValidationService {
    @Autowired
    DataSource dataSource;

    public static CodeValidationResult validateCode(String codeSystem, String codeSystemName, String code, String displayName) {
       return null;
    }
}
