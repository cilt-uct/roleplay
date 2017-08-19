/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/uct/useralias/trunk/tool/src/java/org/sakaiproject/useralias/tool/handler/CSVHandlerHook.java $
 * $Id: CSVHandlerHook.java 75839 2011-07-28 14:08:40Z david.horwitz@uct.ac.za $
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

package org.sakaiproject.useralias.tool.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import org.sakaiproject.useralias.tool.params.XLSXViewParamaters;
import org.sakaiproject.useralias.tool.util.SiteComparator;
import org.sakaiproject.util.SortedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.ponder.rsf.processor.HandlerHook;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class XLSXHandlerHook implements HandlerHook {

	private static Logger log = LoggerFactory.getLogger(XLSXHandlerHook.class);

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

	@Override
	public boolean handle() {


		if (viewparams instanceof XLSXViewParamaters) {
			log.debug("got a XLSXViewParamaters");
		} else {
			log.debug("Not an XLSX view!: " + viewparams + ", " + viewparams.viewID);
			return false;
		}

		log.debug("handling the XLSX request!");

		// set the headers
		response.setHeader("Content-disposition", "attachment;filename=\"useralias.xlsx\"");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		OutputStream outputStream;
		try {

			outputStream = response.getOutputStream();
			SXSSFWorkbook wbs = new SXSSFWorkbook();
			//for (int i = 0; i < wbs.length; i++) {
				//Workbook wb = wbs[i];
				//CreationHelper createHelper = wb.getCreationHelper();

				// create a new sheet
				Sheet sheet = wbs.createSheet();
				;

				String siteId = toolManager.getCurrentPlacement().getContext();
				String context = "/site/" + siteId;

				AuthzGroup group = authzGroupService.getAuthzGroup(context);
				Set<Member> mem = group.getMembers();
				Iterator<Member> it = mem.iterator();

				// Header row
				Row row = sheet.createRow((short) 0);
				row.createCell(0).setCellValue("userid");
				row.createCell(1).setCellValue("Surname");
				row.createCell(2).setCellValue("firstname");
				row.createCell(3).setCellValue("Alias Lastname");
				row.createCell(4).setCellValue("alias firstname");

				List<User> userList = new ArrayList<User>();

				while (it.hasNext()) {
					Member member = it.next();
					log.debug("got member: " + member.getUserEid());

					try {

						User u = userDirectoryService.getUser(member.getUserId());
						userList.add(u);
					} catch (UserNotDefinedException ue) {
						// nothing to do
					}

				}

				Iterator sortedParticipants = null;
				sortedParticipants = new SortedIterator(userList.iterator(),
						new SiteComparator(SiteComparator.SORTED_BY_PARTICIPANT_NAME, "sortedAsc"));
				userList.clear();
				int rowId = 1;
				while (sortedParticipants.hasNext()) {

					Row thisRow = sheet.createRow((short) rowId);
					rowId++;

					User u = (User) sortedParticipants.next();
					UserAliasItem ua = userAliasLogic.getUserAliasItemByIdForContext(u.getId(), context);
					if (log.isDebugEnabled()) {
						log.debug("adding " + u.getEid() + " " + u.getLastName() + " to row:" + (rowId - 1));
					}

					thisRow.createCell(0).setCellValue(u.getEid());
					thisRow.createCell(1).setCellValue(u.getLastName());
					thisRow.createCell(2).setCellValue(u.getFirstName());

					if (ua != null) {
						if (log.isDebugEnabled()) {
						 log.debug("ua: " + ua.getFirstName() + " " + ua.getLastName());
						}
						if (ua.getLastName() != null) {
						 thisRow.createCell(3).setCellValue(ua.getLastName());
						} else {
							log.debug("lastName: Null");
						}
						if (ua.getFirstName() != null) {
							thisRow.createCell(4).setCellValue(ua.getFirstName());
						} else {
							log.debug("firstName: null!");
						}
					}

				}

				wbs.write(outputStream);
			//}

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
