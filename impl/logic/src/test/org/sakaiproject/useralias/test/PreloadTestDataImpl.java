/**
 * PreloadTestDataImpl.java - evaluation - Dec 25, 2006 10:07:31 AM - azeckoski
 * $URL$
 * $Id$
 **************************************************************************
 * Copyright (c) 2008 Centre for Applied Research in Educational Technologies, University of Cambridge
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.sakaiproject.useralias.test;

/*
 * #%L
 * sakai-useralias-logic-impl
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.useralias.dao.impl.UserAliasDao;






/**
 * This preloads data needed for testing<br/>
 * Do not load this data into a live or production database<br/>
 * Load this after the normal preload<br/>
 * Add the following (or something like it) to a spring beans def file:<br/>
 * <pre>
	<!-- create a test data preloading bean -->
	<bean id="org.sakaiproject.evaluation.test.PreloadTestData"
		class="org.sakaiproject.evaluation.test.PreloadTestDataImpl"
		init-method="init">
		<property name="evaluationDao"
			ref="org.sakaiproject.evaluation.dao.EvaluationDao" />
		<property name="preloadData"
			ref="org.sakaiproject.evaluation.dao.PreloadData" />
	</bean>
 * </pre>
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class PreloadTestDataImpl {

   private static Log log = LogFactory.getLog(PreloadTestDataImpl.class);

   private UserAliasDao dao;
   public void setDao(UserAliasDao dao) {
      this.dao = dao;
   }



   private UserAliasTestDataLoad etdl;
   /**
    * @return the test data loading class with copies of all saved objects
    */
   public UserAliasTestDataLoad getEtdl() {
      return etdl;
   }

   public void init() {
      log.info("INIT");

      
   }


}
