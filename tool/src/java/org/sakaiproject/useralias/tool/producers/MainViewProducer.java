package org.sakaiproject.useralias.tool.producers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.ResourceProperties;
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
import org.sakaiproject.useralias.tool.util.SiteComparator;
import org.sakaiproject.util.SortedIterator;


import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class MainViewProducer implements DefaultView, ViewComponentProducer {


	
	
	public static String VIEW_ID = "aliasUser";
	
	
	
	public String getViewID() {
		// TODO Auto-generated method stub
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
	
	public void fillComponents(UIContainer tofill, ViewParameters viewparams,
			ComponentChecker checker) {
		
		log.debug("Rendering main view");
		boolean canEdit = false;
		if (this.isSiteOwner()) {
			
			canEdit = true;
		}
		String siteId = toolManager.getCurrentPlacement().getContext();
		String context = "/site/" + siteId;
		
		//make the link to download the csv
		UIInternalLink.make(tofill, "download-link", new CSVViewParamaters("CSVHandlerHook", context));
		
		try {
			
			Site site = siteService.getSite(siteId);
			ResourcePropertiesEdit rp = site.getPropertiesEdit();
			try {
				if(!rp.getBooleanProperty("aliasEnabled")) {
					//we need to set if
					log.debug("Setting property");
					rp.addProperty("aliasEnabled", "true");
					siteService.save(site);
				}
			}
			catch(EntityPropertyNotDefinedException en) {
				log.debug("Setting property");
				rp.addProperty("aliasEnabled", "true");
				siteService.save(site);
			}
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		try {
		
		AuthzGroup group = authzGroupService.getAuthzGroup(context);
		Set mem = group.getMembers();
		Iterator it = mem.iterator();
		UIForm form = UIForm.make(tofill,"userAliasForm");
		List userList = new ArrayList();
		
		while (it.hasNext()) {
			Member member = (Member) it.next();
			log.debug("got member: " + member.getUserEid());
			//get the user
			
			try {
				
				User u = userDirectoryService.getUser(member.getUserId());
				userList.add(u);
			}
			catch (UserNotDefinedException ue) {
				//nothing to do 
			}
		
		}
		Iterator sortedParticipants = null;
		sortedParticipants = new SortedIterator (userList.iterator (), new SiteComparator (SiteComparator.SORTED_BY_PARTICIPANT_NAME, "sortedAsc"));
		userList.clear();
		while (sortedParticipants.hasNext())
		{
			User u = (User)sortedParticipants.next();
			log.debug("adding " + u.getLastName());
			userList.add(u);
		}
		
		
		int i = 0 ;
		for (int q = 0; q < userList.size(); q++) {
		User u = (User)userList.get(q);	
		
		UIBranchContainer row = UIBranchContainer.make(form, "user-row:", u.getId());
		
		UIOutput.make(row,"user-last",u.getLastName());
		UIOutput.make(row,"user-first",u.getFirstName());
		UIOutput.make(row,"user-eid",u.getEid());
		
		
		UserAliasItem ua = userAliasLogic.getUserAliasItemByIdForContext(u.getId(), context);
		if (canEdit) {
			if (ua != null) {
				UIInput.make(row, "aliasFirst","#{userAliasItemBeanLocator." + ua.getId().toString() + ".firstName}", ua.getFirstName());
				UIInput.make(row, "aliasLast","#{userAliasItemBeanLocator." + ua.getId()+ ".lastName}", ua.getLastName());
			} else {
				i++;
				log.debug("new alias #" + i);
				//Lets Try do this with OTP
				//"#{UserAliasItem.new " + i +".firstName." + u.getId() +"}"
				//new UIELBinding("UserAliasItem.new " + i,)
				UIInput.make(row, "aliasFirst","userAliasItemBeanLocator.new " + i +".firstName");
				UIInput.make(row, "aliasLast","userAliasItemBeanLocator.new " + i +".lastName");
				form.parameters.add(new UIELBinding("userAliasItemBeanLocator.new " + i +".userId",u.getId()));
				form.parameters.add(new UIELBinding("userAliasItemBeanLocator.new " + i +".context",context));
			}
		} else {
		
			//print the names
			if (ua != null) {
				UIOutput.make(row,"text-first",ua.getFirstName());
				UIOutput.make(row,"text-last",ua.getLastName());
			}
		}

		

		
		}
			//UICommand.make(form, "submit","submit","#{itemBean.processActionAdd}");
		if (canEdit)
			UICommand.make(form, "submit", "Update", "userAliasItemBeanLocator.saveAll");
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		

		
		
	}


	
	  private boolean isSiteOwner(){
		  if (securityService.isSuperUser())
			  return true;
		  else if (securityService.unlock("site.upd", "/site/" + toolManager.getCurrentPlacement().getContext()))
			  return true;
		  else
			  return false;
	  }
}
