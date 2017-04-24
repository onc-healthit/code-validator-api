package org.sitenv.vocabularies.validation.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class AutoPilotPool {

	private static Map<String, AutoPilotWrapperPool> POOLS = new HashMap<String, AutoPilotWrapperPool>();
	private static AutoPilotPool pool = null;
	
	public static AutoPilotPool newInstance() {
		if (pool==null) {
			pool = new AutoPilotPool();
		}
		return pool;
	}
		
	public AutoPilotWrapper borrow(String xpath) throws Exception {
		AutoPilotWrapperPool apwpool = POOLS.get(xpath);
		if (apwpool == null) {
			
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxIdle(200);
			config.setMaxTotal(200);
			config.setMinIdle(100);
			config.setTestOnBorrow(true);
			config.setTestOnReturn(true);

			apwpool = new AutoPilotWrapperPool(new AutoPilotWrapperFactory(xpath), config);
			POOLS.put(xpath, apwpool);
		}
		return apwpool.borrowObject();
	}

	public void returnObject(AutoPilotWrapper apw) {
		AutoPilotWrapperPool apwpool = POOLS.get(apw.getXPath());
		if (apwpool!=null) {
			apwpool.returnObject(apw);
		}
	}
}
