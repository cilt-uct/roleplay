package org.sakaiproject.useralias.tool.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.useralias.logic.UserAliasLogic;
import org.sakaiproject.useralias.model.UserAliasItem;
import org.sakaiproject.useralias.tool.params.CSVViewParamaters;
import org.sakaiproject.useralias.tool.util.SiteComparator;
import org.sakaiproject.util.SortedIterator;

import au.com.bytecode.opencsv.CSVWriter;

import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.processor.HandlerHook;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class CSVHandlerHook implements HandlerHook {

	private static final char COMMA = ',';
	private static Log log = LogFactory.getLog(CSVHandlerHook.class);

	private HttpServletResponse response;
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private UserAliasLogic userAliasLogic;
	public void setLogic(UserAliasLogic ul) {
		this.userAliasLogic = ul;
	}
	
	private ToolManager toolManager;
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}
	
	private ViewParameters viewparams;
	public void setViewparams(ViewParameters viewparams) {
		this.viewparams = viewparams;
	}
	
	private AuthzGroupService authzGroupService;
	public void setAuthzGroupService(AuthzGroupService az) {
		authzGroupService = az;
	}
	
	private UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService uds) {
		this.userDirectoryService = uds;
	}
	
	public boolean handle() {
		

		CSVViewParamaters ivp;
		
		if (viewparams instanceof CSVViewParamaters) {
			ivp = (CSVViewParamaters) viewparams;
			log.debug("got a CSVViewParamaters");
		} else {
			log.debug("Not an csv view!: " + viewparams + ", " + viewparams.viewID);
			return false;
		}
		
		log.debug("handling the CSV request!");
		
		//set the headers
		response.setHeader("Content-disposition", "attachment;filename=\"useralias.csv\"");
		response.setContentType("application/csv");
		//response.setHeader("filename", "useralias.csv");

		OutputStream outputStream;
		try {
			outputStream = response.getOutputStream();


		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
		CSVWriter writer = new CSVWriter(outputStreamWriter, COMMA);		

			String siteId = toolManager.getCurrentPlacement().getContext();
			String context = "/site/" + siteId;
			
			AuthzGroup group = authzGroupService.getAuthzGroup(context);
			Set mem = group.getMembers();
			Iterator it = mem.iterator();
			
			
			writer.writeNext(new String[]{"userid","Surname","firstname","Alias Lastname", "alias firstname"});
		
			List<User> userList = new ArrayList<User>();
			
			while (it.hasNext()) {
				Member member = (Member) it.next();
				log.debug("got member: " + member.getUserEid());

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
			
			while (sortedParticipants.hasNext()) {
				
					List<String> newline = new ArrayList<String>(); 
					User u = (User)sortedParticipants.next();
					UserAliasItem ua = userAliasLogic.getUserAliasItemByIdForContext(u.getId(), context);
					log.debug("adding " + u.getEid() + " " + u.getLastName());
					newline.add(u.getEid());
					newline.add(u.getLastName());
					newline.add(u.getFirstName());
					
					if (ua != null) {
						newline.add(ua.getLastName());
						newline.add(ua.getFirstName());
					}
					writer.writeNext(newline.toArray(new String[]{}));
				
				
				
			}
			
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GroupNotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return true;
	}

}
