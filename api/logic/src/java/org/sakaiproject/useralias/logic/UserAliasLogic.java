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

/*
 * #%L
 * sakai-useralias-logic-api
 * %%
 * Copyright (C) 2006 - 2015 Sakai Project
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

import org.sakaiproject.useralias.model.UserAliasItem;

public interface UserAliasLogic {

	
	
	public UserAliasItem getUserAliasItemByIdForContext(String userID,String context);
	
	public List<UserAliasItem> getUserAliasItemsForContext(String context);
	
	public void saveUserAliasItem(UserAliasItem item);
	
	/**
	 * Is this context aliased
	 * @param realmId
	 * @return
	 */
	public boolean realmIsAliased(String realmId);
	
	public UserAliasItem getUserAliasitemById(Long id);
	
	public boolean isAliasedInContext(String userId, String context);
	
	/**
	 * 
	 * @param context
	 * @param isAliased
	 */
	public void setSiteAliasStatus(String context, boolean isAliased);
}
