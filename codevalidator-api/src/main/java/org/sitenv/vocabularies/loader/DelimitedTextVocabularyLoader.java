package org.sitenv.vocabularies.loader;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

public abstract class DelimitedTextVocabularyLoader<T extends CodeModel> extends VocabularyLoader<T> {
	protected int numHeaderLines;
	
	protected DelimitedTextVocabularyLoader(Class<T> modelClass, int numBaseFields, int numFields, int numHeaderLines) {
		super(modelClass, numBaseFields, numFields);
		
		this.numHeaderLines = numHeaderLines;
	}

	@Override
	protected int loadFile(VocabularyRepository vocabRepo, OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields,
		Map<String, String> fields, File file) throws Exception {
		BufferedReader reader = null;
		int fileCount = 0, lineIndex = -1;
		String line;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			
			while ((line = reader.readLine()) != null) {
				lineIndex++;
				
				if (line.isEmpty()) {
					continue;
				}
				
				try {
					if (lineIndex < this.numHeaderLines) {
						this.processHeaderLine(baseFields, lineIndex, line);
					} else if (this.processLine(dbConnection, doc, baseFields, fields, lineIndex, line)) {
						fileCount++;
					}
				} catch (Exception e) {
					throw new IOException(String.format("Unable to process vocabulary model (name=%s) file line: %d", this.modelName, lineIndex), e);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return fileCount;
	}
	
	protected abstract boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, Map<String, String> fields,
		int lineIndex, String line);
	
	protected void processHeaderLine(Map<String, String> baseFields, int headerLineIndex, String headerLine) {
	}
}
