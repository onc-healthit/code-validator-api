package org.sitenv.vocabularies.loader;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

/**
 * Created by Brian on 2/7/2016.
 */
public interface VocabularyLoader {
    long load(List<File> file, DataSource ds);
}
