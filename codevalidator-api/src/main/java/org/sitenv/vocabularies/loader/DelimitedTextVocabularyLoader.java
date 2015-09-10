package org.sitenv.vocabularies.loader;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import org.sitenv.vocabularies.model.CodeModel;
import org.sitenv.vocabularies.repository.VocabularyRepository;

public abstract class DelimitedTextVocabularyLoader<T extends CodeModel> extends VocabularyLoader<T> {
	protected int headerLines;
	
	protected DelimitedTextVocabularyLoader(Class<T> modelClass, int headerLines) {
		super(modelClass);
		
		this.headerLines = headerLines;
	}

	@Override
	protected int loadFile(VocabularyRepository vocabRepo, OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, File file) throws
		Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int fileCount = 0, lineIndex = -1;
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				lineIndex++;
				
				if (line.isEmpty()) {
					continue;
				}
				
				if (lineIndex < this.headerLines) {
					this.processHeaderLine(baseFields, lineIndex, line);
				} else if (this.processLine(dbConnection, doc, baseFields, lineIndex, line)) {
					fileCount++;
				}
			}
		} finally {
			reader.close();
		}
		
		return fileCount;
	}
	
	protected abstract boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, int lineIndex, String line);
	
	protected void processHeaderLine(Map<String, String> baseFields, int headerLineIndex, String headerLine) {
	}
}
