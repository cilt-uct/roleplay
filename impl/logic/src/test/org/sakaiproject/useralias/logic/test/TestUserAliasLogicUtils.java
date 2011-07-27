/**
 * TestTextTemplateLogicUtils.java - evaluation - 2008 Jan 16, 2008 5:12:13 PM - azeckoski
 */

package org.sakaiproject.useralias.logic.test;

import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.useralias.dao.impl.UserAliasDao;
import org.sakaiproject.useralias.logic.impl.UserAliasLogicImpl;
import org.sakaiproject.useralias.model.UserAliasItem;
import org.sakaiproject.useralias.model.UserAliasSite;
import org.sakaiproject.useralias.test.UserAliasTestDataLoad;
import org.springframework.test.AbstractTransactionalSpringContextTests;





/**
 * Testing the template processing logic
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public class TestUserAliasLogicUtils extends AbstractTransactionalSpringContextTests {

	private static final String SITE_1_ID = "/site/dgsdgsadgasdf";
 
	protected UserAliasDao dao;
	protected UserAliasLogicImpl ual;
	protected MemoryService memService;
	
	private UserAliasTestDataLoad userAliasTestDataLoad;
	protected String[] getConfigLocations() {
	      // point to the needed spring config files, must be on the classpath
	      // (add component/src/webapp/WEB-INF to the build path in Eclipse),
	      // they also need to be referenced in the maven file
	      return new String[] {"hibernate-test.xml", "spring-hibernate.xml"};
	   }

	   // run this before each test starts
	   protected void onSetUpBeforeTransaction() throws Exception {
	      // load the spring created dao class bean from the Spring Application Context
	      dao = (UserAliasDao) applicationContext.getBean("org.sakaiproject.useralias.dao.UserAliasDao");
	      if (dao == null) {
	         throw new NullPointerException("DAO could not be retrieved from spring context");
	      }
	      
	      ual = new UserAliasLogicImpl();
	      ual.setDao(dao);
	      //ual.setCache(memoryService.newCache("org.sakaiproject.useralias.logic.impl.UserAliasLogic.siteCache"));
	   }


	
   /**
    * Test method for {@link org.sakaiproject.evaluation.logic.impl.utils.TextTemplateLogicUtils#processTextTemplate(java.lang.String, java.util.Map)}.
    */
   public void testUserAliasLogic() {
	   UserAliasSite site1 = new UserAliasSite();
	   site1.setSiteId(SITE_1_ID);
	   dao.save(site1);
	   assertNotNull(site1.getId());
	   
	  assertTrue(ual.realmIsAliased(SITE_1_ID));
	  assertFalse(ual.realmIsAliased("aasdfasdgasdf"));
	  
	  
	  //test the name stuff
	  UserAliasItem item1 = new UserAliasItem();
	  item1.setContext(SITE_1_ID);
	  item1.setEid("dDuck");
	  item1.setFirstName("Daffy");
	  item1.setLastName("Duck");
	  item1.setUserId("user1");
	  dao.save(item1);
	  
	  assertNotNull(item1.getId());
	  assertNotNull(ual.getUserAliasItemByIdForContext("user1", SITE_1_ID));
	  
	  
   }  

}
