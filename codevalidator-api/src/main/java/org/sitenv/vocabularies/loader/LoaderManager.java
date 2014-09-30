package org.sitenv.vocabularies.loader;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LoaderManager {
	
	private static Logger logger = Logger.getLogger(LoaderManager.class);
	
	private static LoaderManager INSTANCE = new LoaderManager();
	
	private Map<String, Class<? extends Loader>> loaderMap;
	
	private LoaderManager() {
		loaderMap = new HashMap<String, Class<? extends Loader>>();
	}
	
	public void registerLoader(String key, Class<? extends Loader> clazz) {
		loaderMap.put(key.toUpperCase(), clazz);
	}
	
	public static LoaderManager getInstance() {
		return INSTANCE;
	}
	
	public Loader buildLoader(String loaderName) {
		try 
		{
			return loaderMap.get(loaderName.toUpperCase()).newInstance();
		} 
		catch (Exception e)
		{
			// TODO: log4j
			logger.error("Could not build the loader " + loaderName + "...", e);
			return null;
		}
	}
}
