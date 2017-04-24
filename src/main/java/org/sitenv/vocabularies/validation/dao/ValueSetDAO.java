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
public class ValueSetDAO {

	private static Logger logger = Logger.getLogger(ValueSetDAO.class);
	private static Map<String, Set<String>> CodeToValueSetOID = new HashMap<String, Set<String>>();
	private static Set<String> ValueSetOIDS = new HashSet<String>();
	private static Map<String, Set<String>> CodeAndCodeSystemAndCodeSystemNameAndDisplayNameToValueSetOID = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeSystemToValueSetOID = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeAndCodeSystemToValueSetOID = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> DisplayNameAndCodeAndCodeSystemToValueSetOID = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeSystemNameAndCodeAndCodeSystemToValueSetOID = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> CodeSystemNameToValueSetOID = new HashMap<String,Set<String>>();
	private static Map<String, Set<String>> DisplayNameToValueSetOID = new HashMap<String, Set<String>>();
	
	private static boolean done = false;
	
	private void addRow(String code, String codeSystem, String displayName, String codeSystemName,
			String valueSetOID) {
		ValueSetOIDS.add(valueSetOID);

		Set<String> codes = CodeToValueSetOID.get(code);
		if (codes == null) {
			CodeToValueSetOID.put(code, new HashSet<String>());
		}
		CodeToValueSetOID.get(code).add(valueSetOID);

		String key = "C/" + code + "/CS/" + codeSystem + "/CSN/" + codeSystemName + "/DN/" + displayName;
		Set<String> vsoids = CodeAndCodeSystemAndCodeSystemNameAndDisplayNameToValueSetOID.get(key);
		if (vsoids == null) {
			CodeAndCodeSystemAndCodeSystemNameAndDisplayNameToValueSetOID.put(key, new HashSet<String>());
		}
		CodeAndCodeSystemAndCodeSystemNameAndDisplayNameToValueSetOID.get(key).add(valueSetOID);

		vsoids = CodeSystemToValueSetOID.get(codeSystem);
		if (vsoids == null) {
			CodeSystemToValueSetOID.put(codeSystem, new HashSet<String>());
		}
		CodeSystemToValueSetOID.get(codeSystem).add(valueSetOID);

		key = "C/" + code + "/CS/" + codeSystem;
		vsoids = CodeAndCodeSystemToValueSetOID.get(key);
		if (vsoids == null) {
			CodeAndCodeSystemToValueSetOID.put(key, new HashSet<String>());
		}
		CodeAndCodeSystemToValueSetOID.get(key).add(valueSetOID);

		key = "D/" + displayName + "/C/" + code + "/CS/" + codeSystem;
		vsoids = DisplayNameAndCodeAndCodeSystemToValueSetOID.get(key);
		if (vsoids == null) {
			DisplayNameAndCodeAndCodeSystemToValueSetOID.put(key, new HashSet<String>());
		}
		DisplayNameAndCodeAndCodeSystemToValueSetOID.get(key).add(valueSetOID);

		key = "CSN/" + codeSystemName + "/C/" + code + "/CS/" + codeSystem;
		vsoids = CodeSystemNameAndCodeAndCodeSystemToValueSetOID.get(key);
		if (vsoids == null) {
			CodeSystemNameAndCodeAndCodeSystemToValueSetOID.put(key, new HashSet<String>());
		}
		CodeSystemNameAndCodeAndCodeSystemToValueSetOID.get(key).add(valueSetOID);
				
		if (CodeSystemNameToValueSetOID.get(codeSystemName)==null) {
			CodeSystemNameToValueSetOID.put(codeSystemName, new HashSet<String>());
		}
		CodeSystemNameToValueSetOID.get(codeSystemName).add(valueSetOID);
		
		if (DisplayNameToValueSetOID.get(displayName)==null) {
			DisplayNameToValueSetOID.put(displayName, new HashSet<String>());
		}
		DisplayNameToValueSetOID.get(displayName).add(valueSetOID);
	}

	public synchronized void loadValueSets(DataSource ds) {
		if (done) {
			return;
		}

		String sql = "SELECT code, displayName,codeSystem, codeSystemName, valuesetOid FROM ValueSets";
		JdbcTemplate tmpl = new JdbcTemplate(ds);
		ValueSetRowHandler handler = new ValueSetRowHandler();
		tmpl.query(sql, handler);
		done = true;
		logger.info("ValueSet Maps initialized. Total rows:" + handler.n);
		logger.info("ValueSetOIDs:" + ValueSetOIDS.size());
	}
	
	public class ValueSetRowHandler implements RowCallbackHandler {
		public long n = 0;
		@Override
		public void processRow(ResultSet arg0) throws SQLException {
			String code = arg0.getString("code");
			String codeSystem = arg0.getString("codeSystem");
			String displayName = arg0.getString("displayName");
			String codeSystemName = arg0.getString("codeSystemName");
			String valueSetOID = arg0.getString("valuesetOid");
			addRow(code, codeSystem, displayName, codeSystemName, valueSetOID);
			n++;
		}		
	}

	// 1
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.code = :code and c.codeSystem = :codeSystem and
	// c.codeSystemName = :codeSystemName and c.displayName = :displayName and
	// c.valuesetOid in (:valuesetOids)")
	public boolean existsByCodeAndCodeSystemAndCodeSystemNameAndDisplayNameInValuesetOid(String code, String codeSystem,
			String codeSystemName, String displayName, List<String> valuesetOids) {

		String key = "C/" + code + "/CS/" + codeSystem + "/CSN/" + codeSystemName + "/DN/" + displayName;
		Set<String> oids = CodeAndCodeSystemAndCodeSystemNameAndDisplayNameToValueSetOID.get(key);
		if (oids == null) {
			return false;
		}
		for (String oid : valuesetOids) {
			if (oids.contains(oid)) {
				return true;
			}
		}
		return false;
	}

	// 2
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.valuesetOid in (:valuesetOids)")
	public boolean valuesetOidsExists(List<String> valuesetOids) {
		for (String vsoid : valuesetOids) {
			if (ValueSetOIDS.contains(vsoid)) {
				return true;
			}
		}
		return false;
	}

	// 3
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.codeSystem = :codeSystem and c.valuesetOid in
	// (:valuesetOids)")
	public boolean codeSystemExistsInValueset(String codeSystem, List<String> valuesetOids) {
		Set<String> oids = CodeSystemToValueSetOID.get(codeSystem);
		if (oids == null) {
			return false;
		}
		for (String oid : valuesetOids) {
			if (oids.contains(oid)) {
				return true;
			}
		}
		return false;
	}

	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.code = :code and c.codeSystem = :codeSystem and
	// c.valuesetOid in (:valuesetOids)")
	public boolean codeExistsByCodeSystemInValuesetOid(String code, String codeSystem, List<String> valuesetOids) {

		String key = "C/" + code + "/CS/" + codeSystem;
		Set<String> oids = CodeAndCodeSystemToValueSetOID.get(key);
		if (oids == null) {
			return false;
		}
		for (String oid : valuesetOids) {
			if (oids.contains(oid)) {
				return true;
			}
		}
		return false;
	}
	
	// 4
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.displayName = :displayName and c.code = :code and
	// c.codeSystem = :codeSystem and c.valuesetOid in (:valuesetOids)")
	public boolean displayNameExistsForCodeByCodeSystemInValueset(String displayName, String code, String codeSystem,
			List<String> valuesetOids) {
		String key = "D/" + displayName + "/C/" + code + "/CS/" + codeSystem;
		Set<String> oids = DisplayNameAndCodeAndCodeSystemToValueSetOID.get(key);
		if (oids == null) {
			return false;
		}
		for (String oid : valuesetOids) {
			if (oids.contains(oid)) {
				return true;
			}
		}
		return false;
	}

	// 5
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.codeSystemName = :codeSystemName and c.code =
	// :code and c.codeSystem = :codeSystem and c.valuesetOid in
	// (:valuesetOids)")
	public boolean codeSystemNameExistsForCodeByCodeSystemInValueset(String codeSystemName, String code,
			String codeSystem, List<String> valuesetOids) {
		String key = "CSN/" + codeSystemName + "/C/" + code + "/CS/" + codeSystem;
		Set<String> oids = CodeSystemNameAndCodeAndCodeSystemToValueSetOID.get(key);
		if (oids == null) {
			return false;
		}
		for (String oid : valuesetOids) {
			if (oids.contains(oid)) {
				return true;
			}
		}
		return false;
	}

	// 6
    // @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.displayName = :displayName and c.valuesetOid in (:valuesetOids)")
	public boolean displayNameExistsInValueset(String displayName, List<String> valueSetOids) {
		Set<String> codes = DisplayNameToValueSetOID.get(displayName);
		if (codes == null) {
			return false;
		}
		for (String vsoid : valueSetOids) {
			if (codes.contains(vsoid)) {
				return true;
			}
		}
		return false;
	}

	// 7
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM VsacValueSet c WHERE c.codeSystemName = :codeSystemName and c.valuesetOid in (:valuesetOids)")
	public boolean codeSystemNameExistsInValueset(String codeSystemName,
			List<String> valuesetOids) {
		Set<String> codes = CodeSystemNameToValueSetOID.get(codeSystemName);
		if (codes == null) {
			return false;
		}
		for (String vsoid : valuesetOids) {
			if (codes.contains(vsoid)) {
				return true;
			}
		}
		return false;
	}

	// 8
	// @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM
	// VsacValueSet c WHERE c.code = :code and c.valuesetOid in
	// (:valuesetOids)")
	public boolean codeExistsInValueset(String code, List<String> valuesetOids) {
		Set<String> codes = CodeToValueSetOID.get(code);
		if (codes == null) {
			return false;
		}
		for (String vsoid : valuesetOids) {
			if (codes.contains(vsoid)) {
				return true;
			}
		}
		return false;
	}
}
