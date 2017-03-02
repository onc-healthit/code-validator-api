package org.sitenv.vocabularies.loader;

import java.io.File;
import java.sql.Connection;
import java.util.List;

/**
 * Created by Brian on 2/7/2016.
 */
public interface VocabularyLoader {
    void load(List<File> file, Connection connection);
}
