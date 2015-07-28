package org.sitenv.vocabularies.loader.code;

import java.util.HashMap;
import java.util.Map;

import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.model.CodeModel;

public class CodeLoaderManager {
	
	private static Logger logger = Logger.getLogger(CodeLoaderManager.class);
	
	private static CodeLoaderManager INSTANCE = new CodeLoaderManager();
	
	private Map<String, Class<? extends CodeLoader>> loaderMap;
	
	private CodeLoaderManager() {
		loaderMap = new TreeMap<String, Class<? extends CodeLoader>>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public void registerLoader(String key, Class<? extends CodeLoader> clazz) {
		loaderMap.put(key, clazz);
	}
	
	public static CodeLoaderManager getInstance() {
		return INSTANCE;
	}
	
	public CodeLoader buildLoader(String loaderName) {
		CodeLoader instance = null;
		
		try 
		{
			Class<? extends CodeLoader> clazz = loaderMap.get(loaderName);
			
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
