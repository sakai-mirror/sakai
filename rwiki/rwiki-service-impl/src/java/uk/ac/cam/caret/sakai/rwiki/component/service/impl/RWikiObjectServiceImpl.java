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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.springframework.orm.hibernate.HibernateOptimisticLockingFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.cam.caret.sakai.rwiki.component.dao.impl.ListProxy;
import uk.ac.cam.caret.sakai.rwiki.component.model.impl.RWikiEntityImpl;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiPermissionsImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.EntityHandler;
import uk.ac.cam.caret.sakai.rwiki.service.api.PageLinkRenderer;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiSecurityService;
import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.ObjectProxy;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiHistoryObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiEntity;
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

	// dependancy
	/**
	 * Contains a map of handler beans injected
	 */
	private Map m_handlers = null;

	public String createTemplatePageName = "default_template";

	/**
	 * Register this as an EntityProducer
	 * 
	 */
	public void init() {
		// register a transient notification for resources
		 NotificationEdit edit = NotificationService.addTransientNotification();

		// set functions
		edit.setFunction(RWikiObjectService.EVENT_RESOURCE_ADD);
		edit.addFunction(RWikiObjectService.EVENT_RESOURCE_WRITE);

		// set the filter to any site related resource
		edit.setResourceFilter(RWikiObjectService.REFERENCE_ROOT);
		// %%% is this the best we can do? -ggolden

		// set the action
		edit.setAction(new SiteEmailNotificationRWiki());
		
		EntityManager.registerEntityProducer(this, RWikiObjectService.REFERENCE_ROOT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#getRWikiObject(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public RWikiCurrentObject getRWikiObject(String name, String user,
			String realm) throws PermissionException {
		return getRWikiObject(name, user, realm, createTemplatePageName);
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
				TimeLogger.printTimer("dao.findByGlobalName: " + name, start2,
						finish);
			}

			if (returnable == null) {
				if (!securityService.checkCreatePermission(user, realm)) {
					throw new CreatePermissionException("User: " + user
							+ " cannot create pages in realm: " + realm);
				}
				returnable = cdao.createRWikiObject(name, realm);
				// zero in on the correct space.
				String pageSpace = NameHelper.localizeSpace(name, realm);
				String defTemplate = NameHelper.globaliseName(templatePage,
						pageSpace);
				RWikiCurrentObject template = cdao
						.findByGlobalName(defTemplate);
				if (template != null) {
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

			if ((rwo.getGroupRead() && securityService.checkGetPermission(user,
					rwo.getRealm()))
					|| (rwo.getGroupWrite() && securityService
							.checkUpdatePermission(user, rwo.getRealm()))
					|| (rwo.getGroupAdmin())
					&& securityService.checkAdminPermission(user, rwo
							.getRealm())) {
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

		if ((rwo.getGroupWrite() && securityService.checkUpdatePermission(user,
				rwo.getRealm()))
				|| (rwo.getGroupAdmin())
				&& securityService.checkAdminPermission(user, rwo.getRealm())) {
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
			Entity e = getEntity(rwo);
			EventTrackingService.post(EventTrackingService.newEvent(
					EVENT_RESOURCE_WRITE, 
					e.getReference(), true,
					NotificationService.PREF_IMMEDIATE));
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
//				 track it
				Entity e = getEntity(rwo);
				EventTrackingService.post(EventTrackingService.newEvent(
						EVENT_RESOURCE_WRITE, 
						e.getReference(), true,
						NotificationService.PREF_IMMEDIATE));
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
			final String currentSpace = NameHelper.localizeSpace(rwo.getName(),
					rwo.getRealm());

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
	 * @param createTemplatePageName
	 *            The createTemplatePageName to set.
	 */
	public void setCreateTemplatePageName(String createTemplatePageName) {
		this.createTemplatePageName = createTemplatePageName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#findRWikiSubPages(java.lang.String)
	 */
	public List findRWikiSubPages(String globalParentPageName) {
		return cdao.findRWikiSubPages(globalParentPageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#updateNewComment(java.lang.String,
	 *      java.lang.String, java.lang.String, java.util.Date,
	 *      java.lang.String)
	 */
	public void updateNewComment(String name, String user, String realm,
			Date version, String content) throws PermissionException,
			VersionException {
		int retries = 0;
		while (retries < 5) {
			try {
				RWikiObject lastComment = cdao.findLastRWikiSubPage(name);
				int cnum = 0;
				if (lastComment != null) {
					String lastCommentName = lastComment.getName();
					int lastp = lastCommentName.lastIndexOf(".");
					if (lastp >= 0) {
						cnum = Integer.parseInt(lastCommentName
								.substring(lastp + 1)) + 1;
					}
				}
				String newCommentName = MessageFormat.format(
						"{0}.{1,number,000}", new Object[] { name,
								new Integer(cnum) });
				update(newCommentName, user, realm, version, content);
				break;
			} catch (VersionException e) {
				if (retries >= 5)
					throw e;
				retries++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#createListProxy(java.util.List,
	 *      uk.ac.cam.caret.sakai.rwiki.service.api.dao.ObjectProxy)
	 */
	public List createListProxy(List commentsList, ObjectProxy lop) {
		return new ListProxy(commentsList, lop);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#createNewRWikiCurrentObject()
	 */
	public RWikiObject createNewRWikiCurrentObject() {
		return new RWikiCurrentObjectImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService#createNewRWikiPermissionsImpl()
	 */
	public RWikiPermissions createNewRWikiPermissionsImpl() {
		return new RWikiPermissionsImpl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public String getLabel() {
		return REFERENCE_LABEL;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public boolean willArchiveMerge() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public boolean willImport() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public void syncWithSiteChange(Site site, ChangeType change) {
		// stolen :) from BaseContentService

		String[] toolIds = { "sakai.rwiki" };

		// for a delete, just disable
		if (EntityProducer.ChangeType.REMOVE == change) {
			disableWiki(site);
		}

		// otherwise enable if we now have the tool, disable otherwise
		else {
			// collect the tools from the site
			Collection tools = site.getTools(toolIds);

			// if we have the tool
			if (!tools.isEmpty()) {
				enableWiki(site);
			}

			// if we do not
			else {
				disableWiki(site);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * Archive all the wiki pages in the site as a single collection
	 */
	public String archive(String siteId, Document doc, Stack stack,
			String archivePath, List attachments) {
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// start with an element with our very own name
		Element element = doc.createElement(RWikiObjectService.SERVICE_NAME);
		((Element) stack.peek()).appendChild(element);
		stack.push(element);

		try {
			List l = cdao.findRWikiSubPages("/site/" + siteId);
			for (Iterator i = l.iterator(); i.hasNext();) {
				RWikiObject rwo = (RWikiObject) i.next();
				RWikiEntity rwe = (RWikiEntity) getEntity(rwo);
				results.append("Archiving " + rwo.getName() + "\n");
				rwe.toXml(doc, stack);
				List lh = this.findRWikiHistoryObjects(rwo);
				for (Iterator ih = lh.iterator(); ih.hasNext();) {
					RWikiObject rwoh = (RWikiObject) ih.next();
					RWikiEntity rwoeh = (RWikiEntity) getEntity(rwo);
					results.append("Archiving " + rwoh.getName() + " version "
							+ rwoh.getVersion() + "\n");
					rwoeh.toXml(doc, stack);
				}
			}
		} catch (Exception any) {
			results.append("Error archiving pages from site: " + siteId + " "
					+ any.toString() + "\n");
		}

		stack.pop();

		return results.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The archive contains the current version, followed by historical versions
	 * If any of these aer out of order, only versions upto the first
	 * encoundered version will be merged. If the page exists, then only version
	 * that dont exist, and are not already present will be added in practice
	 * this means all the pages in the set will be rejected.
	 * 
	 * 
	 */
	public String merge(String siteId, Element root, String archivePath,
			String fromSiteId, Map attachmentNames, Map userIdTrans,
			Set userListAllowImport) {
		// stolen :) from BaseContentService
		// get the system name: FROM_WT, FROM_CT, FROM_SAKAI
		//String source = null;
		// root: <service> node
		//Node parent = root.getParentNode(); // parent: <archive> node containing
		// "system"
		//if (parent.getNodeType() == Node.ELEMENT_NODE) {
		//	Element parentEl = (Element) parent;
		//	source = parentEl.getAttribute("system");
		//}
		String user = SessionManager.getCurrentSessionUserId();

		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		NodeList children = root.getChildNodes();
		final int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element element = (Element) child;

			try {
				String defaultRealm = SiteService.getSite(siteId)
						.getReference();


				RWikiCurrentObjectImpl archiverwo = new RWikiCurrentObjectImpl();
				RWikiEntity rwe = (RWikiEntity) getEntity(archiverwo);
				rwe.fromXml(element, defaultRealm);
				
				// clear the ID to remove hibernate session issues and recreate a new id issues
				archiverwo.setId(null);

				String pageName = archiverwo.getName();


				if (exists(pageName, defaultRealm)) {
					// page exists, add to history, if the version does not
					// exist
					RWikiObject rwo = getRWikiObject(pageName, user,
							defaultRealm);
					if (archiverwo.getRevision().intValue() >= rwo
							.getRevision().intValue()) {
						results
								.append("Page ")
								.append(rwo.getName())
								.append(" already exists with revision ")
								.append(rwo.getRevision())
								.append(
										" which is earlier than the revision form the archive"
												+ " therefore I have rejected the merge from the archive,"
												+ " please report this a bug to JIRA if you feel that"
												+ " this functionality is required \n");
					} else {
						RWikiHistoryObject rwho = getRWikiHistoryObject(rwo,
								archiverwo.getRevision().intValue());
						if (rwho == null) {
							rwho = hdao.createRWikiHistoryObject(archiverwo);
							// connect to the correct master object
							rwho.setRwikiobjectid(rwo.getId());
							// save
							hdao.update(rwho);
							
							rwho = getRWikiHistoryObject(rwo,
									archiverwo.getRevision().intValue());
							results.append("Created ").append(rwho.getName())
									.append(" revision ").append(
											rwho.getRevision()).append(
											" with version ").append(
											rwho.getVersion().getTime())
									.append(" date ").append(rwho.getVersion())
									.append("\n");
						} else {
							results
									.append("Page ")
									.append(rwo.getName())
									.append(" already exists with revision ")
									.append(rwo.getRevision())
									.append(
											" therefore I have rejected the merge of"
													+ " corresponding revision from the archive,"
													+ " please report this a bug to JIRA if you feel that"
													+ " this functionality is required \n");
						}
					}

				} else {
					// page does not exist, create
					String newUser = (String) userIdTrans.get(archiverwo
							.getOwner());
					if (newUser == null)
						newUser = archiverwo.getOwner();

					// go direct, if we use the utility methods, all sorts of
					// things get reset, which is bad
					cdao.update(archiverwo, null);

					RWikiObject savedrwo = getRWikiObject(archiverwo.getName(),
							newUser, archiverwo.getRealm());
					results.append("Created ").append(savedrwo.getName())
							.append(" revision ")
							.append(savedrwo.getRevision()).append(
									" with version ").append(
									savedrwo.getVersion().getTime()).append(
									" date ").append(savedrwo.getVersion())
							.append("\n");
				}
			} catch (Exception ex) {
				log.error("Failed to add page ", ex);
				results.append("Failed to add ").append(
						element.getAttribute("page-name")).append(" revision ")
						.append(element.getAttribute("revision")).append(
								" because  ").append(ex.getMessage()).append(
								"\n");

			}
		}
		return results.toString();
	}

	/**
	 * {@inheritDoc} Only the current version of a page is imported, history is
	 * left behind.
	 */
	public void importEntities(String fromContext, String toContext, List ids) {
		if (fromContext.equals(toContext))
			return;
		if (!fromContext.endsWith("/")) {
			fromContext = fromContext + "/";
		}
		if (!toContext.endsWith("/")) {
			toContext = toContext + "/";
		}
		String user = SessionManager.getCurrentSessionUserId();
		List pages = findRWikiSubPages(fromContext);

		for (Iterator i = pages.iterator(); i.hasNext();) {
			RWikiObject rwo = (RWikiObject) i.next();
			boolean transfer = true;
			// if the list exists, is this id in the list ?
			if (ids != null && ids.size() > 0) {
				transfer = false;
				for (Iterator j = ids.iterator(); j.hasNext() && !transfer;) {
					String id = (String) j.next();
					if (id.equals(rwo.getRwikiobjectid())) {
						transfer = true;
					}
				}
			}
			// ok to transfer
			if (transfer) {
				String pageName = rwo.getName();
				// relocate the page name
				pageName = NameHelper.localizeName(pageName, NameHelper
						.localizeSpace(pageName, rwo.getRealm()));
				pageName = NameHelper.globaliseName(pageName, toContext);
				try {
					// create a brand new page containing the content,
					// this does not copy prior versions
					update(pageName, user, fromContext, new Date(), rwo
							.getContent(), rwo.getPermissions());
				} catch (Throwable t) {
					log.error("Failed to import " + pageName + " from "
							+ fromContext + " to " + toContext);
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * The parsing process iterates though a list of regular expressions to
	 * generate a match
	 */
	public boolean parseEntityReference(String reference, Reference ref) {
		EntityHandler eh = findEntityReferenceMatch(reference);
		if (eh == null)
			return false;
		eh.setReference(SERVICE_NAME, ref, reference);
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public String getEntityDescription(Reference ref) {
		checkReference(ref);
		EntityHandler eh = findEntityHandler(ref);

		Entity e =  getEntity(ref, eh);
		return eh.getDescription(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public ResourceProperties getEntityResourceProperties(Reference ref) {
		checkReference(ref);
		EntityHandler eh = findEntityHandler(ref);
		Entity e =  getEntity(ref, eh);
		return eh.getProperties(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public Entity getEntity(Reference ref) {
		checkReference(ref);
		EntityHandler eh = findEntityHandler(ref);
		return getEntity(ref, eh);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The format of the URL is controlled by a MessageFormat String injected
	 * into urlFormat. The parameters are 0 = global Page Name
	 */
	public String getEntityUrl(Reference ref) {
		checkReference(ref);
		EntityHandler eh = findEntityHandler(ref);
		Entity entity = getEntity(ref, eh);
		return eh.getUrl(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getEntityAuthzGroups(Reference ref) {
		checkReference(ref);
		EntityHandler eh = findEntityHandler(ref);
		return eh.getAuthzGroups(ref);
	}

	/**
	 * {@inheritDoc}
	 */
	public HttpAccess getHttpAccess() {
		return new HttpAccess() {
			public void handleAccess(HttpServletRequest req,
					HttpServletResponse res, Reference ref,
					Collection copyrightAcceptedRefs)
					throws PermissionException, IdUnusedException,
					ServerOverloadException {
				checkReference(ref);
				try {

					EntityHandler eh = findEntityHandler(ref);
					Entity entity =  getEntity(ref, eh);
					String user = req.getRemoteUser();
					if ( entity instanceof RWikiEntity ) {
						RWikiEntity rwe = (RWikiEntity) entity;
						RWikiObject rwo = rwe.getRWikiObject();
						if ( rwo != null ) {
							if ( checkRead(rwo,user) ) {
								eh.outputContent(entity, req, res);
							} else {
								res.sendError(HttpServletResponse.SC_FORBIDDEN,"No Permission to read this wiki page, sorry");
							}
						} else {
							// this is a container, read on the site
							if ( securityService.checkGetPermission(user,ref.getContext()) ) {
								eh.outputContent(entity,req,res);
							} else {
								res.sendError(HttpServletResponse.SC_FORBIDDEN,"No Permission to read this wiki site, sorry");
							}
						}
					} 
					res.sendError(HttpServletResponse.SC_NOT_FOUND," Resource Now found "+ref.getReference());

				} catch (Throwable t) {
					t.printStackTrace();

					throw new IdUnusedException(ref.getReference());
				}
			}
		};
	}

	/**
	 * see if the reference matches one of the regeistered regex patterns
	 * 
	 * @param reference
	 * @return the Entity handler that shoul be used to generate content and
	 *         format the URL
	 */
	private EntityHandler findEntityReferenceMatch(String reference) {
		if (!reference.startsWith(REFERENCE_ROOT))
			return null;
		for (Iterator i = m_handlers.keySet().iterator(); i.hasNext();) {
			String s = (String) i.next();
			EntityHandler eh = (EntityHandler) m_handlers.get(s);
			if (eh.matches(reference))
				return eh;
		}
		return null;
	}

	private void checkReference(Reference ref) {
		if (!SERVICE_NAME.equals(ref.getType()))
			throw new RuntimeException(
					"Request Routed to incorrect EntityProducer by the kernel expected "
							+ SERVICE_NAME + " got " + ref.getType());

	}

	/**
	 * Looks up the entity handler based on sybtype, the registerd subtype must
	 * match the key in the m_handlers map
	 * 
	 * @param ref
	 * @return
	 */
	private EntityHandler findEntityHandler(Reference ref) {
		if (!SERVICE_NAME.equals(ref.getType()))
			return null;
		String subtype = ref.getSubType();
		return (EntityHandler) m_handlers.get(subtype);
	}

	/**
	 * Get the entity, already having looked up the entity handler
	 * 
	 * @param eh
	 * @return
	 */
	private Entity getEntity(Reference ref, EntityHandler eh) {

		RWikiObject rwo = this.getRWikiCurrentObjectDao().findByGlobalName(
				ref.getId());
		
			
			
		

		int revision = eh.getRevision(ref);
		if (rwo != null && revision != -1 && revision != rwo.getRevision().intValue()) {
			RWikiObject hrwo = this.getRWikiHistoryObjectDao()
					.getRWikiHistoryObject(rwo, revision);
			if (hrwo != null) {
				rwo = hrwo;
			} 

		}
		RWikiEntity rwe = null;
		if ( rwo == null ) {
			rwe = (RWikiEntity) getReferenceEntity(ref);
		} else {
			rwe = (RWikiEntity) getEntity(rwo);
		}
		return rwe;
	}
	
	public Entity getReferenceEntity(Reference ref ) {
		return new RWikiEntityImpl(ref);
	}
	/**
	 * {@inheritDoc}
	 * @param rwo
	 * @return
	 */
	public Entity getEntity(RWikiObject rwo) {
		return new RWikiEntityImpl(rwo);
	}
	/**
	 * {@inheritDoc}
	 * @param rwo
	 * @return
	 */
	public Reference getReference(RWikiObject rwo ) {
		return EntityManager.newReference(getEntity(rwo).getReference());
	}

	/**
	 * @return Returns the handlers.
	 */
	public Map getHandlers() {
		return m_handlers;
	}

	/**
	 * @param m_handlers
	 *            The handlers to set.
	 */
	public void setHandlers(Map m_handlers) {
		this.m_handlers = m_handlers;
	}

	/**
	 * Disable the tool from the site
	 * 
	 * @param site
	 */
	private void disableWiki(Site site) {
		// ? we are not going to delete the content, so do nothing TODO
	}

	/**
	 * Enable the tool in the site
	 * 
	 * @param site
	 */
	private void enableWiki(Site site) {
		// ? we are not going to delete the content, so do nothing TODO
	}


	/**
	 * {@inheritDoc}
	 */
	public List findAllChangedSince(Date time, String user, String basepath) {
		// TODO: Put authz in place 
		return cdao.findAllChangedSince(time,basepath);
	}
	
	
}
