package uk.ac.cam.caret.sakai.rwiki.bean;

import org.sakaiproject.service.legacy.authzGroup.Role;


import uk.ac.cam.caret.sakai.rwiki.service.impl.RWikiSecurityServiceImpl;

public class RoleBean {

	private Role role;
	
	public RoleBean() {
		
	}
	
	public RoleBean(Role role) {
		this.role = role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getId() {
		return role.getId();
	}
	
	public String getDescription() {
		return role.getDescription();
	}
	
	public boolean isSecureRead() {
		return role.isAllowed(RWikiSecurityServiceImpl.SECURE_READ);
	}
	
	public boolean isSecureUpdate() {
		return role.isAllowed(RWikiSecurityServiceImpl.SECURE_UPDATE);
	}
	
	public boolean isSecureAdmin() {
		return role.isAllowed(RWikiSecurityServiceImpl.SECURE_ADMIN);
	}
	
	public boolean isSecureDelete() {
		return role.isAllowed(RWikiSecurityServiceImpl.SECURE_DELETE);
	}
	
	public boolean isSecureCreate() {
		return role.isAllowed(RWikiSecurityServiceImpl.SECURE_CREATE);
	}
	
	public boolean isSecureSuperAdmin() {
		return role.isAllowed(RWikiSecurityServiceImpl.SECURE_SUPER_ADMIN);
	}
}
