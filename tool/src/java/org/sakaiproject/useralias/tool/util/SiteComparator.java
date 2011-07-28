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


package org.sakaiproject.useralias.tool.util;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.user.api.User;

/**
 * the SiteComparator class
 */
public class SiteComparator
	implements Comparator
{
	private static Log log = LogFactory.getLog(SiteComparator.class);
	
	
	
	public static final String SORTED_BY_PARTICIPANT_NAME = "participant_name";
	
	/**
	 * the criteria
	 */
	String m_criterion = null;
	String m_asc = null;
	
	/**
	 * constructor
	 * @param criteria The sort criteria string
	 * @param asc The sort order string. TRUE_STRING if ascending; "false" otherwise.
	 */
	public SiteComparator (String criterion, String asc)
	{
		m_criterion = criterion;
		m_asc = asc;
		
	}	// constructor
	
	/**
	* implementing the Comparator compare function
	* @param o1 The first object
	* @param o2 The second object
	* @return The compare result. 1 is o1 < o2; -1 otherwise
	*/
	public int compare ( Object o1, Object o2)
	{
		int result = -1;
		
		
		if (m_criterion.equals (SORTED_BY_PARTICIPANT_NAME))
		{
			// sort by whether the site is joinable or not
			String s1 = null;
			s1 = ((User) o1).getLastName() + "," + ((User) o1).getFirstName() + ((User) o1).getEid();
			String s2 = null;
			s2 = ((User) o2).getLastName() + "," + ((User) o2).getFirstName() + ((User) o2).getEid();
			log.debug("chedking " + s1 + " vs " + s2);
			if (s1==null && s2==null)
			{
				result = 0;
			}
			else if (s2==null)
			{
				result = 1;
			}
			else if (s1==null && s2 != null)
			{
				result = -1;
			}
			else
			{
				result = s1.compareToIgnoreCase (s2);
			}
		}
		log.debug("returning " + result);
		return result;
		
} //SiteComparator

}