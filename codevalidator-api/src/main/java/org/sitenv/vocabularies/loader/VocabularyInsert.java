package org.sitenv.vocabularies.loader;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Brian on 2/6/2016.
 */
public interface VocabularyInsert {
    boolean doInsert(String sql, Connection connection) throws SQLException;
}
