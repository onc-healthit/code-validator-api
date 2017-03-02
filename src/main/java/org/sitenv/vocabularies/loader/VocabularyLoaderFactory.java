package org.sitenv.vocabularies.loader;

/**
 * Created by Brian on 2/7/2016.
 */
public interface VocabularyLoaderFactory {
    VocabularyLoader getVocabularyLoader(String loaderType);
}
