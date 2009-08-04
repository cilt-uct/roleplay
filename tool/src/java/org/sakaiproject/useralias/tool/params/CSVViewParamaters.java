package org.sakaiproject.useralias.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class CSVViewParamaters extends SimpleViewParameters {
	
	public String realm;
	
	public CSVViewParamaters() {
		
	}
	
	public CSVViewParamaters(String view) {
		this.viewID = view; 
	}
	public CSVViewParamaters(String view, String realm) {
		this.viewID = view;
		this.realm = realm;
	}
}
