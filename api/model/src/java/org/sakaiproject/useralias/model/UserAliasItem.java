package org.sakaiproject.useralias.model;

public class UserAliasItem {

	private String userId;
	private String aliasFirstName;
	private String aliasLastName;
	private String aliasEid;
	private String context;
	private Long id;
	
	public UserAliasItem () {
	}

	/** 
	 * Limited constructor for caching use.
	 * @param ua
	 */
	public UserAliasItem(UserAliasItem ua) {
		this.id = ua.getId();
		this.aliasFirstName = ua.getFirstName();
		this.aliasLastName = ua.getLastName();
		this.aliasEid = ua.getEid();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String id) {
		userId = id;
	}
	
	public String getFirstName() {
		return aliasFirstName;
	}
	
	public void setFirstName(String fn) {
		aliasFirstName = fn;
	}
	
	public String getLastName() {
		return aliasLastName;
	}
	
	public void setLastName(String ln) {
		aliasLastName = ln;
	}	
	
	public String getEid() {
		return aliasEid;
	}
	
	public void setEid(String eid) {
		aliasEid = eid;
	}
	
	public void setContext(String con) {
		context = con;
	}
	
	public String getContext(){
		return context;
	}
}
