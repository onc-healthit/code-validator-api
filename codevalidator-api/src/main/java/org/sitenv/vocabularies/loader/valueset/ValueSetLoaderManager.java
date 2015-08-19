package org.sitenv.vocabularies.loader.valueset;

import java.util.HashMap;
import java.util.Map;

import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.code.CodeLoader;
import org.sitenv.vocabularies.loader.code.CodeLoaderManager;

public class ValueSetLoaderManager {
	private static Logger logger = Logger.getLogger(ValueSetLoaderManager.class);
	
	private static ValueSetLoaderManager INSTANCE = new ValueSetLoaderManager();
	
	private Map<String, Class<? extends ValueSetLoader>> loaderMap;
	
	private ValueSetLoaderManager() {
		loaderMap = new TreeMap<String, Class<? extends ValueSetLoader>>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public void registerLoader(String key, Class<? extends ValueSetLoader> clazz) {
		loaderMap.put(key, clazz);
	}
	
	public static ValueSetLoaderManager getInstance() {
		return INSTANCE;
	}
	
	public ValueSetLoader buildLoader(String loaderName) {
		ValueSetLoader instance = null;
		
		try 
		{
			Class<? extends ValueSetLoader> clazz = loaderMap.get(loaderName);
			
			if (clazz != null)
			{
				instance = clazz.newInstance();
			}
			
		} 
		catch (Exception e)
		{
			logger.error("Could not build the loader " + loaderName + "...", e);
		}
		
		return instance;
	}
}
