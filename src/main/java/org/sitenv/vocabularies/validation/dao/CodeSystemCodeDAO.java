package org.sitenv.vocabularies.validation.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

@Component
public class CodeSystemCodeDAO {

	private static Logger logger = Logger.getLogger(CodeSystemCodeDAO.class);
	private static Map<String, Set<String>> CodeToCodeSystems = new HashMap<String, Set<String>>();
	private static Set<String> CodeSystems = new HashSet<String>();
	private static Map<String, Set<String>> DisplayNameToCodeSystems = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeAndDisplayNameToCodeSystems = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeAndDisplayNameAndCodeSystemOIDToCodeSystems = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeSystemOIDToCodeSystems = new HashMap<String, Set<String>>();

	private static boolean done = false;
	private static boolean hasDBCleanupDone = false;

	private void addRow(String code, String codeSystem, String displayName, String codeSystemOID) {
		CodeSystems.add(codeSystem);

		if (CodeToCodeSystems.get(code) == null) {
			CodeToCodeSystems.put(code, new HashSet<String>());
		}
		
		CodeToCodeSystems.get(code).add(codeSystem);

		if (DisplayNameToCodeSystems.get(displayName) == null) {
			DisplayNameToCodeSystems.put(displayName, new HashSet<String>());
		}
		DisplayNameToCodeSystems.get(displayName).add(codeSystem);

		String key = "C/" + code + "/D/" + displayName;
		if (CodeAndDisplayNameToCodeSystems.get(key) == null) {
			CodeAndDisplayNameToCodeSystems.put(key, new HashSet<String>());
		}
		CodeAndDisplayNameToCodeSystems.get(key).add(codeSystem);

		key = "C/" + code + "/D/" + displayName + "/OID/" + codeSystemOID;
		if (CodeAndDisplayNameAndCodeSystemOIDToCodeSystems.get(key) == null) {
			CodeAndDisplayNameAndCodeSystemOIDToCodeSystems.put(key, new HashSet<String>());
		}
		CodeAndDisplayNameAndCodeSystemOIDToCodeSystems.get(key).add(codeSystem);

		if (CodeSystemOIDToCodeSystems.get(codeSystemOID) == null) {
			CodeSystemOIDToCodeSystems.put(codeSystemOID, new HashSet<String>());
		}
		CodeSystemOIDToCodeSystems.get(codeSystemOID).add(codeSystem);
	}

	public synchronized void loadCodes(DataSource ds) {
		if (done) {
			return;
		}
		
		String sql = "SELECT code, displayName,codeSystem, codeSystemOID FROM Codes";
		JdbcTemplate tmpl = new JdbcTemplate(ds);
		CodeSetRowHandler handler = new CodeSetRowHandler();
		tmpl.query(sql, handler);
	
		done = true;
		logger.info("Code Maps initialized. Total rows:" + handler.n);
	}
	
	public synchronized void cleanupDBAfterLoadingToHashSets(DataSource ds) {
		if (hasDBCleanupDone) {
			return;
		}
		String deleteCodesQry = "Delete FROM Codes";
		JdbcTemplate tmpl = new JdbcTemplate(ds);
		tmpl.update(deleteCodesQry);
		hasDBCleanupDone = true;
		logger.info("*********** All Codes deleted from DB *************");
	}

	public class CodeSetRowHandler implements RowCallbackHandler {
		public long n = 0;
		@Override
		public void processRow(ResultSet arg0) throws SQLException {
			String code = arg0.getString("code");
			String codeSystem = arg0.getString("codeSystem");
			String displayName = arg0.getString("displayName");
			String codeSystemOID = arg0.getString("codeSystemOID");
			addRow(code, codeSystem, displayName, codeSystemOID);				
			n++;
		}		
	}
	
	// 1
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code
	// c WHERE c.codeSystem in (:codesystems)")
	public boolean foundCodesystems(List<String> codesystems) {
		for (String s : codesystems) {
			if (CodeSystems.contains(s)) {
				return true;
			}
		}
		return false;
	}

	// 2
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code
	// c WHERE c.code = :code and c.displayName = :displayName and c.codeSystem
	// in (:codesystems)")
	public boolean foundCodeAndDisplayNameInCodesystem(String code, String displayName, List<String> codesystems) {
		String key = "C/" + code + "/D/" + displayName;
		Set<String> codeSystems = CodeAndDisplayNameToCodeSystems.get(key);
		if (codeSystems == null) {
			return false;
		}
		for (String s : codesystems) {
			if (codeSystems.contains(s)) {
				return true;
			}
		}
		return false;
	}

	// 3
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code
	// c WHERE c.code = :code and c.displayName = :displayName and
	// c.codeSystemOID = :codeSystemOID and c.codeSystem in (:codesystems)")
	// boolean
	// foundCodeAndDisplayNameAndCodeSystemOIDInCodesystem(@Param("code")String
	// code, @Param("displayName")String displayName,
	// @Param("codeSystemOID")String codeSystemOID,
	// @Param("codesystems")List<String> codesystems);
	public boolean foundCodeAndDisplayNameAndCodeSystemOIDInCodesystem(String code, String displayName,
			String codeSystemOID, List<String> codesystems) {
		String key = "C/" + code + "/D/" + displayName + "/OID/" + codeSystemOID;
		Set<String> codeSystems = CodeAndDisplayNameAndCodeSystemOIDToCodeSystems.get(key);
		if (codeSystems == null) {
			return false;
		}
		for (String s : codesystems) {
			if (codeSystems.contains(s)) {
				return true;
			}
		}
		return false;
	}

	// 4
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code
	// c WHERE c.code = :code and c.codeSystem in (:codesystems)")
	public boolean foundCodeInCodesystems(String code, List<String> codesystems) {
		Set<String> codeSystems = CodeToCodeSystems.get(code);
		if (codeSystems == null) {
			return false;
		}

		for (String s : codesystems) {
			if (codeSystems.contains(s)) {
				return true;
			}
		}
		return false;
	}

	// 5
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code
	// c WHERE c.displayName = :displayName and c.codeSystem in (:codesystems)")
	public boolean foundDisplayNameInCodesystems(String displayName, List<String> codesystems) {
		Set<String> codeSystems = DisplayNameToCodeSystems.get(displayName);
		if (codeSystems == null) {
			return false;
		}

		for (String s : codesystems) {
			if (codeSystems.contains(s)) {
				return true;
			}
		}
		return false;
	}

	// 6
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code
	// c WHERE c.codeSystemOID = :codeSystemOID and c.codeSystem in
	// (:codesystems)")
	// boolean foundCodeSystemOIDInCodesystems(@Param("codeSystemOID")String
	// codeSystemOID, @Param("codesystems")List<String> codesystems);
	public boolean foundCodeSystemOIDInCodesystems(String codeSystemOID, List<String> codesystems) {
		Set<String> codeSystems = CodeSystemOIDToCodeSystems.get(codeSystemOID);
		if (codeSystems == null) {
			return false;
		}

		for (String s : codesystems) {
			if (codeSystems.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
}
