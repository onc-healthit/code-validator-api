package org.sitenv.vocabularies.validation.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class AutoPilotWrapperPool extends GenericObjectPool<AutoPilotWrapper> {

	public AutoPilotWrapperPool(PooledObjectFactory<AutoPilotWrapper> factory) {
		super(factory);
	}

	public AutoPilotWrapperPool(PooledObjectFactory<AutoPilotWrapper> factory, GenericObjectPoolConfig config) {
		super(factory, config);
	}

}
