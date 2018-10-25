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

package org.sakaiproject.useralias.logic;

import java.util.List;

import org.sakaiproject.useralias.model.UserAliasItem;

public interface UserAliasLogic {

	
	
	/**
	 * @param userID
	 * @param context
	 * @return
	 */
	public UserAliasItem getUserAliasItemByIdForContext(String userID,String context);
	
	/**
	 * @param context
	 * @return
	 */
	public List<UserAliasItem> getUserAliasItemsForContext(String context);
	
	/**
	 * @param item
	 */
	public void saveUserAliasItem(UserAliasItem item);
	
	/**
	 * Is this context aliased
	 * @param realmId
	 * @return
	 */
	public boolean realmIsAliased(String realmId);
	
	/**
	 * @param id
	 * @return
	 */
	public UserAliasItem getUserAliasitemById(Long id);
	
	/**
	 * @param userId
	 * @param context
	 * @return
	 */
	public boolean isAliasedInContext(String userId, String context);
	
	/**
	 * 
	 * @param context
	 * @param isAliased
	 */
	public void setSiteAliasStatus(String context, boolean isAliased);
}
