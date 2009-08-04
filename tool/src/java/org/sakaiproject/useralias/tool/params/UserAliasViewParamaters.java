package org.sakaiproject.useralias.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class UserAliasViewParamaters extends SimpleViewParameters {
	public String id; // an identifier for an item
	public String userId;

	/**
	 * Basic empty constructor
	 */
	public UserAliasViewParamaters() {
	}

	/**
	 *  Constructor for ading a specific user
	 */
	public UserAliasViewParamaters(String id, String viewID, String userid) {
		this.id = id;
		this.viewID = viewID;
		this.userId = userid;
	}
	
	
	
	/**
	 * Constructor for a ViewParameters object for Adding Items
	 * @param viewID the target view for these parameters
	 * @param id a unique identifier for a RsfcrudItem object
	 */
	
	public UserAliasViewParamaters(String viewID) {
		this.id = id;
		this.viewID = viewID;
	}

	/* (non-Javadoc)
	 * @see uk.org.ponder.rsf.viewstate.ViewParameters#getParseSpec()
	 */
	public String getParseSpec() {
		// include a comma delimited list of the public properties in this class
		return super.getParseSpec() + ",id,userId";
	}
}

