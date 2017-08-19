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

package org.sakaiproject.useralias.logic.test;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.useralias.dao.impl.UserAliasDao;
import org.sakaiproject.useralias.logic.impl.UserAliasLogicImpl;
import org.sakaiproject.useralias.model.UserAliasItem;
import org.sakaiproject.useralias.model.UserAliasSite;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Testing the template processing logic
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
@ContextConfiguration(locations = { "/hibernate-test.xml", "classpath:spring-hibernate.xml" })
public class TestUserAliasLogicUtils extends AbstractJUnit4SpringContextTests {

	private static final String SITE_1_ID = "/site/dgsdgsadgasdf";

	private static Logger log = LoggerFactory.getLogger(TestUserAliasLogicUtils.class);
	protected UserAliasDao dao;
	protected UserAliasLogicImpl ual;
	protected MemoryService memService;

	// private UserAliasTestDataLoad userAliasTestDataLoad;

	// run this before each test starts
	@Before
	public void onSetUpBeforeTransaction() throws Exception {
		// load the spring created dao class bean from the Spring Application
		// Context
		dao = (UserAliasDao) applicationContext.getBean("org.sakaiproject.useralias.dao.UserAliasDao");
		if (dao == null) {
			throw new NullPointerException("DAO could not be retrieved from spring context");
		}

		ual = new UserAliasLogicImpl();
		ual.setDao(dao);
		// ual.setCache(memoryService.newCache("org.sakaiproject.useralias.logic.impl.UserAliasLogic.siteCache"));
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.evaluation.logic.impl.utils.TextTemplateLogicUtils#processTextTemplate(java.lang.String, java.util.Map)}.
	 */
	@Test
	public void testUserAliasLogic() {

		dao = (UserAliasDao) applicationContext.getBean("org.sakaiproject.useralias.dao.UserAliasDao");
		if (dao == null) {
			throw new NullPointerException("DAO could not be retrieved from spring context");
		}
		UserAliasSite site1 = new UserAliasSite();
		site1.setSiteId(SITE_1_ID);
		log.info("Site: " + site1.toString());
		dao.save(site1);
		Assert.assertNotNull(site1.getId());

		Assert.assertTrue(ual.realmIsAliased(SITE_1_ID));
		Assert.assertFalse(ual.realmIsAliased("aasdfasdgasdf"));

		// test the name stuff
		UserAliasItem item1 = new UserAliasItem();
		item1.setContext(SITE_1_ID);
		item1.setEid("dDuck");
		item1.setFirstName("Daffy");
		item1.setLastName("Duck");
		item1.setUserId("user1");
		dao.save(item1);

		Assert.assertNotNull(item1.getId());
		Assert.assertNotNull(ual.getUserAliasItemByIdForContext("user1", SITE_1_ID));
	}
}
