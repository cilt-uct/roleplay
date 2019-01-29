/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.useralias.tool.locators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.useralias.logic.UserAliasLogic;
import org.sakaiproject.useralias.model.UserAliasItem;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.org.ponder.beanutil.BeanLocator;

@Slf4j
public class UserAliasItemBeanLocator implements BeanLocator {

	public static final String NEW_PREFIX = "new";
	
	private Map<String, Object> delivered = new HashMap<String, Object>();
	
	@Setter private UserAliasLogic userAliasLogic;
	@Setter private AuthzGroupService authzGroupService;	
	@Setter private EventTrackingService eventTrackingService;
	@Setter private DeveloperHelperService developerHelperService;


	public Object locateBean(String name) {
		Object togo = delivered.get(name);
		log.debug("Locating UserAliasItem: " + name);
		if (togo == null){
			if(name.startsWith(NEW_PREFIX)){
				togo = new UserAliasItem();
			} else {
				//find the bean
				togo = userAliasLogic.getUserAliasitemById(new Long(name));
			}
			delivered.put(name, togo);
		}	
		return togo;
	}

    public void saveAll() {
    	log.debug("SaveAll() for " + delivered.size());
    	
    	boolean updated = false;
    	
        for (Iterator<String> it = delivered.keySet().iterator(); it.hasNext();) {
          String key = (String) it.next();
          UserAliasItem item = (UserAliasItem) delivered.get(key);
                 	  
          if (item.getId() != null || (item.getId() == null && (item.getFirstName() != null || item.getLastName() != null ))) {
        	  log.debug("Saving Item: " + item.getId() + " for user: " + item.getUserId());
        	  userAliasLogic.saveUserAliasItem(item);
        	  updated = true;
          }
          
        }

        // Trigger an event to invalidate the cache for this realm on all app servers.
    	// We use the authz reference format because the service is using a multirefcache.
    	String siteid = developerHelperService.getCurrentLocationReference();
    	String eventref = authzGroupService.authzGroupReference(siteid);
    	eventTrackingService.post(eventTrackingService.newEvent("useralias.upd", eventref, siteid, true, NotificationService.NOTI_NONE));

    }

}
