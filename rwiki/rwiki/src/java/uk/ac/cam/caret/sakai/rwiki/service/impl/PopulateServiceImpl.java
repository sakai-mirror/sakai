/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package uk.ac.cam.caret.sakai.rwiki.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.Role;

import uk.ac.cam.caret.sakai.rwiki.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.DefaultRole;
import uk.ac.cam.caret.sakai.rwiki.service.PopulateService;

/**
 * @author andrew
 *
 */
public class PopulateServiceImpl implements PopulateService {
	
	private Logger log;
	private HashMap seenPageSpaces = new HashMap();
	private HashMap seenGroups = new HashMap();
	private List seedPages;
	private List roles;
	
	private RWikiCurrentObjectDao dao;
	private AuthzGroupService authz;
	
	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.PopulateService#populateRealm(java.lang.String, java.lang.String)
	 */
	public void populateRealm(String user, String realm, String group)
	throws PermissionException {
		// FIXME: Make this work
		synchronized (seenGroups) {
			if (seenGroups.get(group) == null ) {
				// configure the expected default templates
				// groups that are referenced in current objects MUST have been setup
				if ( dao.getPageCount(group) == 0 ) {
					if ( roles != null ) {
						AuthzGroup azg = null;
						try {
							log.warn("Loading group "+group);
							azg = authz.getAuthzGroup(group);
							log.warn(" Got "+azg);
						} catch (IdUnusedException e) {
						}
						
						if ( azg != null ) {
							for ( Iterator i = roles.iterator(); i.hasNext(); ) {
								DefaultRole dr = (DefaultRole) i.next();
								Map enabledRoles = dr.getEnabledFunctions();
								String roleID = dr.getRoleId();
								
								Role role = azg.getRole(roleID);
								if ( role == null ) {
									try {
										role = azg.addRole(roleID);
									} catch (IdUsedException e) {
										continue;
									}
								}
								for ( Iterator ir = enabledRoles.keySet().iterator(); ir.hasNext(); ) {
									String functionName = (String) ir.next();
									
									
									String enabled = (String)enabledRoles.get(functionName);
									// FIXME: this may not be right, how do we tell if the function exists in the role
									log.warn("Setting role "+roleID+" function "+functionName+" to "+enabled);
									if ( "true".equalsIgnoreCase(enabled) ) {
										role.allowFunction(functionName);
									} else {
										role.disallowFunction(functionName);
									}
								}
							}
							try {
								authz.save(azg);
							} catch (IdUnusedException e) {
								log.warn("Unable to populate default AuthZGroup "+group+" "+e.getMessage());
							} catch (org.sakaiproject.exception.PermissionException e) {
								log.warn("Unable to populate default AuthZGroup "+group+" "+e.getMessage());
							}
						} else {
							log.warn("Cant load default group setup into "+group);
						}
					}
				}
				seenGroups.put(group, group);
			}
		}
		
		synchronized (seenPageSpaces) {
			if (seenPageSpaces.get(realm) == null ) {
				for ( Iterator i = seedPages.iterator(); i.hasNext(); ) {
					if (log.isDebugEnabled()) {
						log.debug("Populating realm: " + realm);
					}
					
					RWikiCurrentObject seed = (RWikiCurrentObject) i.next();
					
					String name = NameHelper.globaliseName(seed.getName(), realm);
					log.	warn("Populating Realm with "+seed.getName());
					if (dao.findByGlobalName(name) == null) {
						if (log.isDebugEnabled()) {
							log.debug("Creating Page: " + name);
						}
						log.warn("Creating Page :"+name);
						RWikiCurrentObject rwo = dao.createRWikiObject(name, realm);
						seed.copyTo(rwo);
						rwo.setName(name);
						rwo.setRealm(realm);
						dao.update(rwo,null);
						log.warn("Page Created ");
					} else {
						log.warn("Page Already exists ");
					}
				}
				seenPageSpaces.put(realm, realm);
			}
		}
	}
	
	
	
	public Logger getLog() {
		return log;
	}
	
	public void setLog(Logger log) {
		this.log = log;
	}
	
	public List getSeedPages() {
		return seedPages;
	}
	
	public void setSeedPages(List seedPages) {
		this.seedPages = seedPages;
	}
	
	public RWikiCurrentObjectDao getRWikiCurrentObjectDao() {
		return dao;
	}
	
	public void setRWikiCurrentObjectDao(RWikiCurrentObjectDao dao) {
		this.dao = dao;
	}
	
	/**
	 * @return Returns the roles.
	 */
	public List getRoles() {
		return roles;
	}
	
	/**
	 * @param roles The roles to set.
	 */
	public void setRoles(List roles) {
		this.roles = roles;
	}
	
	/**
	 * @return Returns the authz.
	 */
	public AuthzGroupService getAuthz() {
		return authz;
	}
	
	/**
	 * @param authz The authz to set.
	 */
	public void setAuthz(AuthzGroupService authz) {
		this.authz = authz;
	}
	
	
	
}
