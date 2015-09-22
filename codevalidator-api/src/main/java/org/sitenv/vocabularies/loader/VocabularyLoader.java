package org.sitenv.vocabularies.loader;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

public abstract class VocabularyLoader<T extends CodeModel> {
	protected Class<T> modelClass;
	protected int numBaseFields;
	protected int numFields;
	protected String modelName;
	
	private static Logger logger = Logger.getLogger(VocabularyLoader.class);
	
	protected VocabularyLoader(Class<T> modelClass, int numBaseFields, int numFields) {
		this.modelClass = modelClass;
		this.numBaseFields = numBaseFields;
		this.numFields = numFields;
		this.modelName = this.modelClass.getSimpleName();
	}
	
	public void load(File ... files) {
		int totalCount = 0, fileCount;
		VocabularyRepository vocabRepo = VocabularyRepository.getInstance();
		OObjectDatabaseTx dbConnection = null;
		ODocument doc = new ODocument();
		Map<String, String> baseFields, fields;
		
		try {
			dbConnection = vocabRepo.getInactiveDbConnection();
			
			vocabRepo.initializeModel(true, dbConnection, this.modelClass);
			
			dbConnection.declareIntent(new OIntentMassiveInsert());
			
			for (File file : files) {
				baseFields = this.buildBaseFields();
				fields = this.buildFields();
				
				try {
					fileCount = this.loadFile(vocabRepo, dbConnection, doc, baseFields, fields, file);
					
					totalCount += fileCount;
					
					logger.debug(String.format("Loaded %d vocabulary model (name=%s) record(s) from file: %s", fileCount, this.modelName, file.getPath()));
				} catch (Exception e) {
					logger.error(String.format("Unable to load vocabulary model (name=%s) file: %s", this.modelName, file), e);
				}
			}
			
			logger.info(String.format("Loaded %d vocabulary model (name=%s) record(s) from %d file(s).", totalCount, this.modelName, files.length));
		} catch (Exception e) {
			logger.error(String.format("Unable to load vocabulary model (name=%s).", this.modelName), e);
		} finally {
			if (dbConnection != null) {
				dbConnection.declareIntent(null);
				
				dbConnection.close();
			}
		}
	}
	
	protected abstract int loadFile(VocabularyRepository vocabRepo, OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields,
		Map<String, String> fields, File file)
		throws Exception;
	
	protected void loadDocument(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> fields) {
		this.loadDocument(dbConnection, doc, this.modelName, fields);
	}
	
	protected void loadDocument(OObjectDatabaseTx dbConnection, ODocument doc, String modelClassName, Map<String, String> fields) {
		doc.reset();
		doc.setClassName(modelClassName);
		
		for (String fieldName : fields.keySet()) {
			doc.field(fieldName, fields.get(fieldName));
		}
		
		dbConnection.getUnderlying().save(doc);
	}
	
	protected Map<String, String> buildFields() {
		return new LinkedHashMap<String, String>(this.numFields);
	}
	
	protected Map<String, String> buildBaseFields() {
		return new LinkedHashMap<String, String>(this.numBaseFields);
	}
}
