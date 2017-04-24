package org.sitenv.vocabularies.validation.pool;

import java.util.List;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;

public class AutoPilotObjectPoolInitializer {

	private static Logger logger = Logger.getLogger(AutoPilotObjectPoolInitializer.class);
	private static boolean done = false;

	private void initPools(List<ConfiguredExpression> ces) throws Exception {
		if (done) {
			return;
		}
		long s = System.currentTimeMillis();
		for (ConfiguredExpression ce : ces) {
			AutoPilotWrapper apw = AutoPilotPool.newInstance().borrow(ce.getConfiguredXpathExpression());
			AutoPilotPool.newInstance().returnObject(apw);

		}
		logger.info("Init AutoPilot ObjectPools in " + (System.currentTimeMillis() - s) + " ms for " + ces.size() + " expressions.");
		done = true;
	}

	public void initFromExpressions(List<ConfiguredExpression> expressions) throws Exception {
		initPools(expressions);
	}

}
