package org.sitenv.vocabularies.validation.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class AutoPilotWrapperFactory extends BasePooledObjectFactory<AutoPilotWrapper> {

	private String xpath;

	public AutoPilotWrapperFactory(String xpath) {
		super();
		this.xpath=xpath;
	}
	
	@Override
	public AutoPilotWrapper create() throws Exception {
		return new AutoPilotWrapper(xpath);
	}

	@Override
	public PooledObject<AutoPilotWrapper> wrap(AutoPilotWrapper parser) {
		return new DefaultPooledObject<AutoPilotWrapper>(parser);
	}

	@Override
	public void passivateObject(PooledObject<AutoPilotWrapper> po) throws Exception {
		po.getObject().reset();
	}

	@Override
	public boolean validateObject(PooledObject<AutoPilotWrapper> po) {
		return po.getObject().isValid();
	}

}
