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

package org.sakaiproject.useralias.tool.producers;

/*
 * #%L
 * sakai-useralias-tool
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.useralias.logic.UserAliasLogic;
import org.sakaiproject.useralias.model.UserAliasItem;
import org.sakaiproject.useralias.tool.params.CSVViewParamaters;
import org.sakaiproject.useralias.tool.params.XLSXViewParamaters;
import org.sakaiproject.useralias.tool.util.SiteComparator;
import org.sakaiproject.util.SortedIterator;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class MainViewProducer implements DefaultView, ViewComponentProducer {

	public static final String VIEW_ID = "aliasUser";

	public String getViewID() {
		return VIEW_ID;
	}

	private static Log log = LogFactory.getLog(MainViewProducer.class);

	private SiteService siteService;

	public void setSiteService(SiteService ss) {
		siteService = ss;
	}

	private UserAliasLogic userAliasLogic;

	public void setLogic(UserAliasLogic ul) {
		this.userAliasLogic = ul;
	}

	private ToolManager toolManager;

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	private SecurityService securityService;

	public void setSecurityService(SecurityService ss) {
		securityService = ss;
	}

	private UserDirectoryService userDirectoryService;

	public void setUserDirectoryService(UserDirectoryService uds) {
		this.userDirectoryService = uds;
	}

	private AuthzGroupService authzGroupService;

	public void setAuthzGroupService(AuthzGroupService az) {
		authzGroupService = az;
	}


	private ServerConfigurationService serverConfigurationService;
	public void setServerConfigurationService(ServerConfigurationService az) {
		serverConfigurationService = az;
	}
	
	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

		log.debug("Rendering main view");
		boolean canEdit = false;
		if (this.isSiteOwner()) {

			canEdit = true;
		}

		String siteId = toolManager.getCurrentPlacement().getContext();
		String context = "/site/" + siteId;

		// is the site enabled for useralias?
		if (!userAliasLogic.realmIsAliased(context)) {
			userAliasLogic.setSiteAliasStatus(context, true);
		}

		// make the link to download the csv
		//boolean xmlsx = true; 
		Boolean xmlsx = serverConfigurationService.getBoolean("userAlias.xlxs", true);
		log.debug("userAlias:" + xmlsx);
		if (xmlsx  == true) {
			log.debug("xmlsx!");
			UIInternalLink.make(tofill, "download-link", UIMessage.make("link_download"),
					new XLSXViewParamaters("XLSXHandlerHook", context));		
		} else {
			log.debug("CSV!");
			UIInternalLink.make(tofill, "download-link", UIMessage.make("link_download"),
					new CSVViewParamaters("CSVHandlerHook", context));
		}

		try {

			Site site = siteService.getSite(siteId);
			ResourcePropertiesEdit rp = site.getPropertiesEdit();
			try {
				if (!rp.getBooleanProperty("aliasEnabled")) {
					// we need to set if
					log.debug("Setting property");
					rp.addProperty("aliasEnabled", "true");
					siteService.save(site);
				}
			} catch (EntityPropertyNotDefinedException en) {
				log.debug("Setting property");
				rp.addProperty("aliasEnabled", "true");
				siteService.save(site);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		try {

			AuthzGroup group = authzGroupService.getAuthzGroup(context);
			Set<Member> mem = group.getMembers();
			Iterator<Member> it = mem.iterator();
			UIForm form = UIForm.make(tofill, "userAliasForm");
			List<User> userList = new ArrayList<User>();

			while (it.hasNext()) {
				Member member = (Member) it.next();
				log.debug("got member: " + member.getUserEid());
				// get the user

				try {

					User u = userDirectoryService.getUser(member.getUserId());
					userList.add(u);
				} catch (UserNotDefinedException ue) {
					// nothing to do
				}

			}
			Iterator<User> sortedParticipants = null;
			sortedParticipants = new SortedIterator(userList.iterator(),
					new SiteComparator(SiteComparator.SORTED_BY_PARTICIPANT_NAME, "sortedAsc"));
			userList.clear();
			while (sortedParticipants.hasNext()) {
				User u = (User) sortedParticipants.next();
				log.debug("adding " + u.getLastName());
				userList.add(u);
			}

			int i = 0;
			for (int q = 0; q < userList.size(); q++) {
				User u = (User) userList.get(q);

				UIBranchContainer row = UIBranchContainer.make(form, "user-row:", u.getId());

				UIOutput.make(row, "user-last", u.getLastName());
				UIOutput.make(row, "user-first", u.getFirstName());
				UIOutput.make(row, "user-eid", u.getEid());

				UserAliasItem ua = userAliasLogic.getUserAliasItemByIdForContext(u.getId(), context);
				if (canEdit) {
					if (ua != null) {
						UIInput.make(row, "aliasFirst",
								"#{userAliasItemBeanLocator." + ua.getId().toString() + ".firstName}",
								ua.getFirstName());
						UIInput.make(row, "aliasLast", "#{userAliasItemBeanLocator." + ua.getId() + ".lastName}",
								ua.getLastName());
					} else {
						i++;
						log.debug("new alias #" + i);
						// Lets Try do this with OTP
						// "#{UserAliasItem.new " + i +".firstName." + u.getId()
						// +"}"
						// new UIELBinding("UserAliasItem.new " + i,)
						UIInput.make(row, "aliasFirst", "userAliasItemBeanLocator.new " + i + ".firstName");
						UIInput.make(row, "aliasLast", "userAliasItemBeanLocator.new " + i + ".lastName");
						form.parameters
								.add(new UIELBinding("userAliasItemBeanLocator.new " + i + ".userId", u.getId()));
						form.parameters.add(new UIELBinding("userAliasItemBeanLocator.new " + i + ".context", context));
					}
				} else {

					// print the names
					if (ua != null) {
						UIOutput.make(row, "text-first", ua.getFirstName());
						UIOutput.make(row, "text-last", ua.getLastName());
					}
				}

			}
			// UICommand.make(form,
			// "submit","submit","#{itemBean.processActionAdd}");
			if (canEdit)
				UICommand.make(form, "submit", UIMessage.make("Update"), "userAliasItemBeanLocator.saveAll");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isSiteOwner() {
		if (securityService.isSuperUser())
			return true;
		else if (securityService.unlock("site.upd", "/site/" + toolManager.getCurrentPlacement().getContext()))
			return true;
		else
			return false;
	}
}
