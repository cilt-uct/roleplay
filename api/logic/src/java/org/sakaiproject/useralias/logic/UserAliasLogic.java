package org.sakaiproject.useralias.logic;

import java.util.List;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.useralias.model.UserAliasItem;

public interface UserAliasLogic {

	
	
	public UserAliasItem getUserAliasItemByIdForContext(String userID,String context);
	
	public List<UserAliasItem> getUserAliasItemsForContext(String context);
	
	public void saveUserAliasItem(UserAliasItem item);
	
	public boolean realmIsAliased(String realmId);
	
	public UserAliasItem getUserAliasitemById(Long id);
	
	public boolean isAliasedInContext(String userId, String context);
}
