package org.sakaiproject.useralias.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.useralias.logic.UserAliasLogic;
import org.sakaiproject.useralias.model.UserAliasItem;
import org.sakaiproject.useralias.tool.locators.UserAliasItemBeanLocator;

public class AliasBean {
	private static Log log = LogFactory.getLog(AliasBean.class);
	public UserAliasItem userAlias = new UserAliasItem(); 
	public String newEid = null;
	
	private UserAliasLogic userAliasLogic;
	public void setLogic(UserAliasLogic ul) {
		this.userAliasLogic = ul;
	}
	
	private UserAliasItemBeanLocator userAliasItemBeanLocator;
	public void setUserAliasItemBeanLocator(UserAliasItemBeanLocator ubl) {
		userAliasItemBeanLocator = ubl;
	}
	
	private UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService uds) {
		this.userDirectoryService = uds;
	}
	
	public Map userAliases = new HashMap();
	
	public String processActionAdd() {
		log.info("Saving!");
		userAliasItemBeanLocator.saveAll();
		return "success";
		
		
	}
}
