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
package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.log.Logger;
import org.springframework.orm.hibernate.HibernateOptimisticLockingFailureException;

import uk.ac.cam.caret.sakai.rwiki.component.dao.impl.ListProxy;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiPermissionsImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.PageLinkRenderer;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiSecurityService;
import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.ObjectProxy;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiHistoryObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiHistoryObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiPermissions;
import uk.ac.cam.caret.sakai.rwiki.service.exception.CreatePermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.ReadPermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.UpdatePermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.VersionException;
import uk.ac.cam.caret.sakai.rwiki.utils.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.utils.TimeLogger;

/**
 * @author andrew
 * 
 */

// FIXME: Component
public class RWikiObjectServiceImpl implements RWikiObjectService {

	private Logger log;

	private RWikiCurrentObjectDao cdao;

	private RWikiHistoryObjectDao hdao;

	private RWikiSecurityService securityService;

	private RenderService renderService;
	
	public String createTemplatePageName = "default_template";

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#getRWikiObject(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public RWikiCurrentObject getRWikiObject(String name, String user,
			String realm) throws PermissionException {
        return getRWikiObject(name,user,realm,createTemplatePageName);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#getRWikiObject(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public RWikiCurrentObject getRWikiObject(String name, String user,
            String realm, String templatePage) throws PermissionException {
		long start = System.currentTimeMillis();
		try {
			if (log.isDebugEnabled()) {
				log.debug("Looking for object with name " + name + " in realm "
						+ realm + " for user " + user);
			}

			// May throw Permission Exception...
			// only globalise if not already
			name = NameHelper.globaliseName(name, realm);
			long start2 = System.currentTimeMillis();
			RWikiCurrentObject returnable;
			try {
				returnable = cdao.findByGlobalName(name);
			} finally {
				long finish = System.currentTimeMillis();
				TimeLogger.printTimer("dao.findByGlobalName: " + name,
						start2, finish);
			}

			if (returnable == null) {
				if (!securityService.checkCreatePermission(user, realm)) {
					throw new CreatePermissionException("User: " + user
							+ " cannot create pages in realm: " + realm);
				}
				returnable = cdao.createRWikiObject(name,realm);		
				// zero in on the correct space.
				String pageSpace = NameHelper.localizeSpace(name,realm);
				String defTemplate = NameHelper.globaliseName(templatePage,pageSpace);
				RWikiCurrentObject template = cdao.findByGlobalName(defTemplate);
				if ( template != null ) {
					returnable.setContent(template.getContent());
					returnable.setPermissions(template.getPermissions());
					returnable.setUser(user);
					returnable.setOwner(user);
					returnable.setRealm(realm);
					returnable.setReferenced(template.getReferenced());
					returnable.setSha1(template.getSha1());
				} 
				return returnable;
			} else if (checkRead(returnable, user)) {
				// Allowed to read this object
				return returnable;
			} else {
				throw new ReadPermissionException(user, returnable);
			}
		} finally {
			long finish = System.currentTimeMillis();
			TimeLogger.printTimer("dao.GetRWikiObject: " + name + ", " + user
					+ ", " + realm, start, finish);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#findByGlobalNameAndContents(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public List search(String criteria, String user, String realm)
			throws PermissionException {
		if (!securityService.checkSearchPermission(user, realm)) {
			throw new ReadPermissionException(user, realm);
		}

		return cdao.findByGlobalNameAndContents(criteria, user, realm);
	}

	public boolean checkRead(RWikiObject rwo, String user) {
		String progress = "";
		long start = System.currentTimeMillis();
		try {
			if (log.isDebugEnabled()) {
				log.debug("checkRead for " + rwo.getName() + " by user: "
						+ user);
			}
			if (user != null && user.equals(rwo.getOwner())
					&& (rwo.getOwnerRead() || rwo.getOwnerAdmin())) {
				if (log.isDebugEnabled()) {
					log.debug("User is owner and allowed to read");
				}
				progress = progress + "1";
				return true;
			}
			
			if ((rwo.getGroupRead() && securityService.checkGetPermission(user,rwo.getRealm()))
					|| (rwo.getGroupWrite() && securityService.checkUpdatePermission(user,rwo.getRealm()))
					|| (rwo.getGroupAdmin()) && securityService.checkAdminPermission(user, rwo.getRealm())) {
				if (log.isDebugEnabled()) {
					log.debug("User is in group and allowed to read");
				}
				progress = progress + "2";
				return true;
			}

			if (rwo.getPublicRead()) {
				if (log.isDebugEnabled()) {
					log.debug("Object is public read");
				}
				progress = progress + "3";
				return true;
			}

			if (securityService.checkSuperAdminPermission(user, rwo.getRealm())) {
				if (log.isDebugEnabled()) {
					log
							.debug("User is SuperAdmin for Realm thus default allowed to update");
				}
				progress = progress + "4";
				return true;
			}

			if (log.isDebugEnabled()) {
				log.debug("Permission denied to read " + rwo.getName()
						+ " by user: " + user);
			}
			progress = progress + "5";
			return false;
		} finally {
			long finish = System.currentTimeMillis();
			TimeLogger.printTimer("canRead: " + progress, start, finish);
		}
	}

	public boolean checkUpdate(RWikiObject rwo, String user) {
		if (log.isDebugEnabled()) {
			log.debug("checkUpdate for " + rwo.getName() + " by user: " + user);
		}
		if (user != null && user.equals(rwo.getOwner())
				&& (rwo.getOwnerWrite() || rwo.getOwnerAdmin())) {
			if (log.isDebugEnabled()) {
				log.debug("User is owner and allowed to update");
			}
			return true;
		}

		if ((rwo.getGroupWrite() && securityService.checkUpdatePermission(user,rwo.getRealm()))
				|| (rwo.getGroupAdmin()) && securityService.checkAdminPermission(user, rwo.getRealm())) {
			if (log.isDebugEnabled()) {
				log.debug("User is in group and allowed to update");
			}
			return true;
		}

		if (rwo.getPublicWrite()) {
			if (log.isDebugEnabled()) {
				log.debug("Object is public write");
			}
			return true;
		}

		if (securityService.checkSuperAdminPermission(user, rwo.getRealm())) {
			if (log.isDebugEnabled()) {
				log
						.debug("User is SuperAdmin for Realm thus default allowed to update");
			}
			return true;
		}

		if (log.isDebugEnabled()) {
			log.debug("Permission denied to update " + rwo.getName()
					+ " by user: " + user);
		}
		return false;
	}

	public boolean checkAdmin(RWikiObject rwo, String user) {
		
			
		if (log.isDebugEnabled()) {
			log.debug("checkAdmin for " + rwo.getName() + " by user: " + user);
		}
		if (user != null && user.equals(rwo.getOwner()) && rwo.getOwnerAdmin()) {
			if (log.isDebugEnabled()) {
				log.debug("User is owner and allowed to admin");
			}
			return true;
		}

		if (rwo.getGroupAdmin()
				&& securityService.checkAdminPermission(user, rwo.getRealm())) {
			if (log.isDebugEnabled()) {
				log.debug("User is in group and allowed to admin");
			}
			return true;
		}

		
		if (securityService.checkSuperAdminPermission(user, rwo.getRealm())) {
			if (log.isDebugEnabled()) {
				log
						.debug("User is Super Admin for Realm thus default allowed to admin");
			}
			return true;
		}

		if (log.isDebugEnabled()) {
			log.debug("Permission denied to admin " + rwo.getName()
					+ " by user: " + user);
		}
		return false;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public RWikiCurrentObjectDao getRWikiCurrentObjectDao() {
		return cdao;
	}

	public void setRWikiCurrentObjectDao(RWikiCurrentObjectDao cdao) {
		this.cdao = cdao;
	}

	public RWikiHistoryObjectDao getRWikiHistoryObjectDao() {
		return hdao;
	}

	public void setRWikiHistoryObjectDao(RWikiHistoryObjectDao hdao) {
		this.hdao = hdao;
	}

	public RWikiSecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(RWikiSecurityService securityService) {
		this.securityService = securityService;
	}

	public RenderService getRenderService() {
		return renderService;
	}

	public void setRenderService(RenderService renderService) {
		this.renderService = renderService;
	}

	public void update(String name, String user, String realm, Date version,
			String content, RWikiPermissions permissions)
			throws PermissionException, VersionException {

		// May throw ReadPermissionException...
		RWikiCurrentObject rwo = getRWikiObject(name, user, realm);
		RWikiHistoryObject rwho = null;

		if (checkUpdate(rwo, user)) {
			rwho = updateContent(rwo, user, content, version);
		} else {
			throw new UpdatePermissionException("User: " + user
					+ " doesn't have permission to update: " + name);
		}

		if (permissions != null) {
			if (checkAdmin(rwo, user)) {
				rwo.setPermissions(permissions);
			} else {
				throw new UpdatePermissionException("User: " + user
						+ " doesn't have permission to update and admin: "
						+ name);
			}
		}

		rwo.setUser(user);
		if (rwo.getOwner() == null) {
			rwo.setOwner(user);
		}
		try {
			cdao.update(rwo, rwho);
		} catch (HibernateOptimisticLockingFailureException e) {
			throw new VersionException("Version has changed since: " + version,
					e);
		}
	}

	public void update(String name, String user, String realm, Date version,
			String content) throws PermissionException, VersionException {
		// May throw ReadPermissionException...
		update(name, user, realm, version, content, null);
	}

	public void update(String name, String user, String realm, Date version,
			RWikiPermissions permissions) throws PermissionException,
			VersionException {
		if (permissions == null) {
			throw new IllegalArgumentException("permissions must not be null");
		}

		RWikiCurrentObject rwo = getRWikiObject(name, user, realm);

		if (checkAdmin(rwo, user)) {
			RWikiHistoryObject rwho = hdao.createRWikiHistoryObject(rwo);
			rwo.setRevision(new Integer(rwo.getRevision().intValue() + 1));
			rwo.setPermissions(permissions);
			rwo.setVersion(version);
			try {
				cdao.update(rwo, rwho);
			} catch (HibernateOptimisticLockingFailureException e) {
				throw new VersionException("Version has changed since: "
						+ version, e);
			}

		} else {
			throw new UpdatePermissionException("User: " + user
					+ " doesn't have permission to update and admin: " + name);
		}

	}

	private RWikiHistoryObject updateContent(RWikiCurrentObject rwo,
			String user, String content, Date version) {
		// We set the version in order to allow hibernate to tell us if the
		// object has been changed since we last looked at it.
		if (version != null) {
			rwo.setVersion(version);
		}

		if (content != null && !content.equals(rwo.getContent())) {

			// create a history instance
			RWikiHistoryObject rwho = hdao.createRWikiHistoryObject(rwo);

			// set the content and increment the revision
			rwo.setContent(content);
			rwo.setRevision(new Integer(rwo.getRevision().intValue() + 1));

			// render to get a list of links
			final HashSet referenced = new HashSet();
            
            // Links should be globalised against the page space!			
            final String currentSpace = NameHelper.localizeSpace(rwo.getName(), rwo.getRealm());

			PageLinkRenderer plr = new PageLinkRenderer() {
				public void appendLink(StringBuffer buffer, String name,
						String view) {
					referenced
							.add(NameHelper.globaliseName(name, currentSpace));
				}

				public void appendLink(StringBuffer buffer, String name,
						String view, String anchor) {
					referenced
							.add(NameHelper.globaliseName(name, currentSpace));
				}

				public void appendCreateLink(StringBuffer buffer, String name,
						String view) {
					referenced
							.add(NameHelper.globaliseName(name, currentSpace));
				}

				public boolean isCachable() {
					return false; // should not cache this render op
				}

				public boolean canUseCache() {
					return false;
				}

				public void setCachable(boolean cachable) {
				}
				public void setUseCache(boolean b) {
					
				}

			};

			renderService.renderPage(rwo, user, currentSpace, plr);

			// process the references
			StringBuffer sb = new StringBuffer();
			Iterator i = referenced.iterator();
			while (i.hasNext()) {
				sb.append("::").append(i.next());
			}
			sb.append("::");
			rwo.setReferenced(sb.toString());

			return rwho;
		}
		return null;

	}

	public boolean exists(String name, String space) {
		long start = System.currentTimeMillis();
		try {
            
			String globalName = NameHelper.globaliseName(name, space);

			return cdao.exists(globalName);

		} finally {
			long finish = System.currentTimeMillis();
			TimeLogger.printTimer("Exists: " + name, start, finish);

		}
	}

	public List findChangedSince(Date since, String user, String realm) {
		if (!securityService.checkSearchPermission(user, realm)) {
			throw new ReadPermissionException(user, realm);
		}

		return cdao.findChangedSince(since, realm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#findReferencingPages(java.lang.String)
	 */
	public List findReferencingPages(String name) {
		return cdao.findReferencingPages(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#revert(java.lang.String,
	 *      java.lang.String, java.lang.String, java.util.Date, int)
	 */
	public void revert(String name, String user, String realm, Date version,
			int revision) {
		RWikiCurrentObject rwikiObject = getRWikiObject(name, user, realm);

		String content = hdao.getRWikiHistoryObject(rwikiObject, revision)
				.getContent();

		update(name, user, realm, version, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#getRWikiObject(java.lang.String)
	 */
	public RWikiCurrentObject getRWikiObject(RWikiObject reference) {
		return cdao.getRWikiCurrentObject(reference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#getRWikiHistoryObject(java.lang.String,
	 *      int)
	 */
	public RWikiHistoryObject getRWikiHistoryObject(RWikiObject reference,
			int revision) {
		return hdao.getRWikiHistoryObject(reference, revision);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#findRWikiHistoryObjects(java.lang.String)
	 */
	public List findRWikiHistoryObjects(RWikiObject reference) {
		return hdao.findRWikiHistoryObjects(reference);
	}

	/**
	 * @return Returns the createTemplatePageName.
	 */
	public String getCreateTemplatePageName() {
		return createTemplatePageName;
	}

	/**
	 * @param createTemplatePageName The createTemplatePageName to set.
	 */
	public void setCreateTemplatePageName(String createTemplatePageName) {
		this.createTemplatePageName = createTemplatePageName;
	}

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#findRWikiSubPages(java.lang.String)
     */
    public List findRWikiSubPages(String globalParentPageName) {
        return cdao.findRWikiSubPages(globalParentPageName);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#updateNewComment(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.lang.String)
     */
    public void updateNewComment(String name, String user, String realm, Date version, String content) throws PermissionException, VersionException {
        int retries = 0;
        while ( retries < 5 ) { 
            try {
                RWikiObject lastComment = cdao.findLastRWikiSubPage(name);
                int cnum = 0;
                if ( lastComment != null ) {
                    String lastCommentName = lastComment.getName();
                    int lastp = lastCommentName.lastIndexOf(".");
                    if ( lastp >= 0 ) {
                        cnum = Integer.parseInt(lastCommentName.substring(lastp+1))+1;
                    }
                }
                String newCommentName = MessageFormat.format("{0}.{1,number,000}", new Object[] { name, new Integer(cnum) });
                update(newCommentName,user,realm,version,content);
                break;
            } catch (VersionException e) {
                if ( retries >= 5 ) throw e;
                retries++;
            }
        }
    }

	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#createListProxy(java.util.List, uk.ac.cam.caret.sakai.rwiki.service.api.dao.ObjectProxy)
	 */
	public List createListProxy(List commentsList, ObjectProxy lop) {
		return new ListProxy(commentsList,lop);
	}

	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#createNewRWikiCurrentObject()
	 */
	public RWikiObject createNewRWikiCurrentObject() {
		return new RWikiCurrentObjectImpl();
	}

	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#createNewRWikiPermissionsImpl()
	 */
	public RWikiPermissions createNewRWikiPermissionsImpl() {
		return new RWikiPermissionsImpl();
	}


}
