package org.sakaiproject.useralias.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.genericdao.hibernate.HibernateCompleteGenericDao;

public class UserAliasDaoImpl extends HibernateCompleteGenericDao 
	implements UserAliasDao {

	private static Log log = LogFactory.getLog(UserAliasDaoImpl.class);

	public void init() {
		log.debug("init");
	}

}
