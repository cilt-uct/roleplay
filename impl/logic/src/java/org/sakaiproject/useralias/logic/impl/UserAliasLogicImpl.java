package org.sakaiproject.useralias.logic.impl;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.memory.api.MultiRefCache;
import org.sakaiproject.user.api.ContextualUserDisplayService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.useralias.dao.impl.UserAliasDao;
import org.sakaiproject.useralias.logic.UserAliasLogic;
import org.sakaiproject.useralias.model.UserAliasItem;
import org.sakaiproject.useralias.model.UserAliasSite;

public class UserAliasLogicImpl implements UserAliasLogic, ContextualUserDisplayService {

	private static Log log = LogFactory.getLog(UserAliasLogicImpl.class);
	
	/**
	 * Cache for site lookups
	 */
	protected MultiRefCache siteCache = null;
	protected MultiRefCache itemCache = null;

	/** The # minutes to cache the lookup answers. 0 disables the cache. */
	protected int m_cacheMinutes = 60;

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * 
	 * @param service
	 *        The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	public void init(){
		
		siteCache = m_memoryService.newMultiRefCache("org.sakaiproject.useralias.siteCache");
		itemCache = m_memoryService.newMultiRefCache("org.sakaiproject.useralias.itemCache");

		log.info("init()");
		
	}	
	
	private UserAliasDao dao;
	public void setDao(UserAliasDao dao) {
		this.dao = dao;
	}

	private DeveloperHelperService developerHelperService;
	public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}

	public UserAliasItem getUserAliasItemByIdForContext(String userID, String context) {
		if (this.realmIsAliased(context)) {
			UserAliasItem example = new UserAliasItem();
			example.setUserId(userID);
			example.setContext(context);
			Search search = new Search();
			search.addRestriction(new Restriction("userId", userID));
			search.addRestriction(new Restriction("context", context));
			UserAliasItem ret = dao.findOneBySearch(UserAliasItem.class, search);
			return ret;
		}
		return null;
	}

	/**
	 * Unused.
	 */
	public List<UserAliasItem> getUserAliasItemsForContext(String context) {
		List<UserAliasItem> ret = dao.findBySearch(UserAliasItem.class, new Search("context", context));
		return ret;
	}

	public void saveUserAliasItem(UserAliasItem item) {
		dao.save(item);
	}
	
	/** 
	 * Is aliasing in effect for this realm?
	 */
	public boolean realmIsAliased(String realmId) {
		
		// check the cache
		if (realmId == null)
			return false;
		
		if (siteCache != null)
		{
			final Boolean value = (Boolean) siteCache.get(realmId);
			if (value != null) {
				log.debug("found site: " + realmId + " in cache with value: " + value);
				return value.booleanValue();
			} else {
				log.debug("site: " + realmId + " is not in cache");
			}
		}
		
		// Not found in the cache - look it up and cache result
		
		UserAliasSite site = getSiteByContext(realmId);
		
		Collection<String> azgIds = new Vector<String>();
		azgIds.add(realmId);
		
		boolean found = (site != null);
		
		if (siteCache != null) {
			log.debug("adding site: " + realmId + " to cache with value: " + found);
			siteCache.put(realmId, Boolean.valueOf(found), m_cacheMinutes * 60, null, azgIds);
		}
		
		return found;
	}

	private UserAliasSite getSiteByContext(String realmId) {
		Search search = new Search("siteId", realmId);
		UserAliasSite site = dao.findOneBySearch(UserAliasSite.class, search);
		return site;
	}

	/**
	 * Get item by id
	 */
	public UserAliasItem getUserAliasitemById(Long id) {
		return (UserAliasItem)dao.findById(UserAliasItem.class, id);
	}

	/** 
	 * Unused
	 */
	@SuppressWarnings("unchecked")
	public boolean isAliasedInContext(String userId, String context) {
		UserAliasItem example = new UserAliasItem();
		example.setContext(context);
		example.setUserId(userId);
		Search search = new Search("context", context);
		search.addRestriction(new Restriction("userId", userId));
		List ret = dao.findBySearch(UserAliasItem.class, search);
		if (ret.size()>0)
			return true;
			
		return false;
	}
	
	/**
	 * Override the user's display ID by context, if necessary, or null otherwise. 
	 * 
	 * @see org.sakaiproject.user.api.ContextualUserDisplayService#getUserDisplayId(org.sakaiproject.user.api.User, java.lang.String)
	 */
	public String getUserDisplayId(User user, String contextReference) {
		return null;
	}

	/**
	 * Override the user's display name by context, if necessary, or return null otherwise.
	 * 
	 * @see org.sakaiproject.user.api.ContextualUserDisplayService#getUserDisplayName(org.sakaiproject.user.api.User, java.lang.String)
	 */
	public String getUserDisplayName(User user, String contextReference) {
		String contextualDisplayName = null;
		
		if (!realmIsAliased(contextReference)) {
			return null;
		}
		
		log.debug("Checking for display for user: " + user.getEid() +" in " + contextReference);
		
		if (contextReference != null) {
			//is the context correctly formed?
			if ((contextReference.indexOf("/site")) != 0) {
				contextReference = "/site/" + contextReference;
				log.debug("fixed context ref to " + contextReference);
			}
			
			// check in cache
			UserAliasItem userAliasItem = (UserAliasItem) itemCache.get(user.getId() + "/" + contextReference);
			
			if (userAliasItem == null) {
				
				// Look up from storage
				userAliasItem = getUserAliasItemByIdForContext(user.getId(), contextReference);
		
				Collection<String> azgIds = new Vector<String>();
				azgIds.add(contextReference);

				log.debug("adding item: " + user.getId() + "/" + contextReference + " to cache");
				
				if (userAliasItem != null) {
					// Cache positive lookup						
					itemCache.put(user.getId() + "/" + contextReference, new UserAliasItem(userAliasItem), m_cacheMinutes * 60, null, azgIds);
				} else {
					// Not found - cache negative lookup
					itemCache.put(user.getId() + "/" + contextReference, new UserAliasItem(), m_cacheMinutes * 60, null, azgIds);
				}
			} else {
				log.debug("found item: " + user.getId() + "/" + contextReference + " in cache");
			}
			
			if (userAliasItem != null && userAliasItem.getId() != null) {
				StringBuilder displayNameBuilder = new StringBuilder();
				String firstName = userAliasItem.getFirstName();
				if (firstName != null) {
					displayNameBuilder.append(firstName);
				}
				
				// TODO The sample code shows a blank space being returned as the alias if
				// both the first and last names are null. Worthy of a log warning?
				displayNameBuilder.append(" ");
				
				String lastName = userAliasItem.getLastName();
				if (lastName != null) {
					displayNameBuilder.append(lastName);
				}
				
				contextualDisplayName = displayNameBuilder.toString();
				log.debug("found alias name " + contextualDisplayName);
			}
		}

		return contextualDisplayName;
	}

	public String getUserDisplayId(User user) {
		return null;
	}

	public String getUserDisplayName(User user) {
		log.debug("getUserDisplayName(" + user.getEid() + ")");
		String contextReference = developerHelperService.getCurrentLocationReference();
		return getUserDisplayName(user, contextReference);
	}

	public void setSiteAliasStatus(String context, boolean isAliased) {
		UserAliasSite site = getSiteByContext(context);
		
		if (site == null && isAliased) {
			site = new UserAliasSite();
			site.setSiteId(context);
			dao.save(site);
			siteCache.remove(context);
		} else if (site != null & !isAliased) {
			dao.delete(site);
			siteCache.remove(context);
		}
		
	}

}
