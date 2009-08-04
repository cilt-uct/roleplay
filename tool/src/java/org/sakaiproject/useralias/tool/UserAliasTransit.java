package org.sakaiproject.useralias.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.useralias.model.UserAliasItem;

public class UserAliasTransit {

	private static Log log = LogFactory.getLog(UserAliasTransit.class);
	
	private UserAliasItem userAlias;
	
	public void setUserAlias(UserAliasItem uai){
		log.info("Got a user Alias Item" + uai.getFirstName());
		
		
	}
	
	
	
}
