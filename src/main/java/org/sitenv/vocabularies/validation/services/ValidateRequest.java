package org.sitenv.vocabularies.validation.services;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.configuration.ConfiguredExpression;
import org.sitenv.vocabularies.validation.utils.ConfiguredExpressionFilter;

import com.ximpleware.VTDNav;

public class ValidateRequest {

	private ConfiguredExpression ce;
	private ConfiguredExpressionFilter filter;
	private Logger logger;
	private VTDNav nv;
	
	public Logger getLogger() {
		return this.logger;
	}

	public ValidateRequest(ConfiguredExpression ce, ConfiguredExpressionFilter filter, Logger lgr, VTDNav vn) {
		this.ce = ce;
		this.filter = filter;
		this.logger = lgr;
		this.nv = vn;
	}

	public VTDNav getNav() {
		return this.nv;
	}

	public ConfiguredExpressionFilter getFilter() {
		return this.filter;
	}

	public ConfiguredExpression getConfiguredExpression() {
		return this.ce;
	}

}
