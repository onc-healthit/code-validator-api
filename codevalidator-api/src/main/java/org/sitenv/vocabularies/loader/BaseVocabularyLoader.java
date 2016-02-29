package org.sitenv.vocabularies.loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Brian on 2/6/2016.
 */
public abstract class BaseVocabularyLoader {

    public boolean doInsert(String sql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        boolean inserted = true;
        try{
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        }finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
        }
       return inserted;
    }
}
