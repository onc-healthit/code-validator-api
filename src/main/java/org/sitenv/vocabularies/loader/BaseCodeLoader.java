package org.sitenv.vocabularies.loader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Brian on 2/6/2016.
 */
public abstract class BaseCodeLoader implements VocabularyLoader{

    public final String codeTableInsertSQLPrefix = "insert into CODES (ID, CODE, DISPLAYNAME, CODESYSTEM, CODESYSTEMOID, ACTIVE) values ";
    protected static final int BATCH_SIZE = 100;

    protected final boolean CODES_IN_THIS_SYSTEM_ARE_ALWAYS_ACTIVE = true;
    protected String code;
    protected String codeSystem;
    protected String oid;
    protected boolean active;

    public boolean insertCode(String sql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        boolean inserted = false;
        if(sql.endsWith(",")){
            sql = StringUtils.chop(sql);
        }
        try{
            preparedStatement = connection.prepareStatement(sql);
            inserted = preparedStatement.execute();
            connection.commit();
        }finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
        }
       return inserted;
    }

    protected void buildCodeInsertQueryString(StrBuilder insertQueryBuilder, String code, String displayName, String codeSystem, String oid, boolean active) {
        insertQueryBuilder.append("(");
        insertQueryBuilder.append("DEFAULT");
        insertQueryBuilder.append(",'");
        insertQueryBuilder.append(code.trim().toUpperCase());
        insertQueryBuilder.append("','");
        insertQueryBuilder.append(displayName.trim().toUpperCase().replaceAll("'", "''"));
        insertQueryBuilder.append("','");
        insertQueryBuilder.append(codeSystem);
        insertQueryBuilder.append("','");
        insertQueryBuilder.append(oid);
        insertQueryBuilder.append("',");
        insertQueryBuilder.append(active);
        insertQueryBuilder.append("),");
    }
}
