package org.sitenv.vocabularies.validation.pool;

import java.util.UUID;

import com.ximpleware.AutoPilot;

public class AutoPilotWrapper {

	private AutoPilot autopilot;
	private String xpath;
	private boolean ok = false;

	private final UUID objID = UUID.randomUUID();

	/*
	 * Have to implement the equals method, since Spring puts a proxy around the object.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AutoPilotWrapper)) {
			return false;
		}
		AutoPilotWrapper w = (AutoPilotWrapper) o;
		return (objID == null) ? w.objID==null : objID.equals(w.objID);
	}

	public AutoPilotWrapper(String xpath) {
		try {
			this.xpath = xpath;
			this.autopilot = new AutoPilot();
			this.autopilot.declareXPathNameSpace("sdtc", "urn:hl7-org:sdtc");
			this.autopilot.declareXPathNameSpace("v3", "urn:hl7-org:v3");
			this.autopilot.declareXPathNameSpace("voc", "urn:hl7-org:v3/voc");
			this.autopilot.declareXPathNameSpace("", "urn:hl7-org:v3");
			this.autopilot.selectXPath(this.xpath);
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AutoPilot getAutoPilot() {
		this.autopilot.resetXPath();
		return this.autopilot;
	}

	public String getXPath() {
		return xpath;
	}

	public boolean isValid() {
		return ok;
	}

	public void reset() {
		// void
	}
}
