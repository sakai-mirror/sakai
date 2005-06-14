/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/component/sample/crud/CrudServiceImpl.java,v 1.8 2004/09/30 20:20:28 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
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

package org.sakaiproject.component.sample.crud;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.VersionException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.sample.crud.CrudObject;
import org.sakaiproject.service.sample.crud.CrudService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
* <p>MyService implements the org.sakaiproject.service.sample.myservice.GreetingService, as a service component example.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.8 $
*/
public class CrudServiceImpl extends HibernateDaoSupport implements CrudService
{
	/** Security function name for create. */
	protected static final String SECURE_CREATE = "crud.create";

	/** Security function name for read. */
	protected static final String SECURE_READ = "crud.read";

	/** Security function name for update. */
	protected static final String SECURE_UPDATE = "crud.update";

	/** Security function name for delete. */
	protected static final String SECURE_DELETE = "crud.delete";

	// Note: security needs a proper Resource reference; CrudObject needs to be a proper resource.
	// for now, it's null so won't work real well. -ggolden

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: a logger component. */
	private Logger m_logger = null;

	/**
	 * Establish my logger component dependency.
	 * @param logger the logger component.
	 */
	public void setLogger(Logger logger)
	{
		m_logger = logger;
	}

	/** Dependency: SecurityService */
	protected SecurityService m_securityService = null;

	/**
	 * Dependency: SecurityService.
	 * @param service The SecurityService
	 */
	public void setSecurityService(SecurityService service)
	{
		m_securityService = service;
	}

	/** Dependency: UsageSessionService */
	protected UsageSessionService m_sessionService = null;

	/**
	 * Dependency: UsageSessionService.
	 * @param service The UsageSessionService
	 */
	public void setUsageSessionService(UsageSessionService service)
	{
		m_sessionService = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this +".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* Work interface methods: org.sakaiproject.service.sample.crud.CrudService
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public boolean allowCreate()
	{
		// TODO: the reference!
		return m_securityService.unlock(SECURE_CREATE, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public CrudObject create() throws PermissionException
	{
		if (!allowCreate())
			throw new PermissionException(m_sessionService.getSessionUserId(), SECURE_CREATE, "");

		// make an object
		CrudObjectImpl co = new CrudObjectImpl();

		try
		{
			// give it to the storage
			update(co);

			// get it back from storage, which will cause it to have the id & version filled in
			// TODO: may not be needed
			co = (CrudObjectImpl) get(co.getId());
		}
		catch (VersionException e)
		{
			m_logger.warn(this +".create(): " + e);
		}
		catch (IdUnusedException e)
		{
			m_logger.warn(this +".create(): " + e);
		}

		return co;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowGet(String id)
	{
		// TODO: the reference!
		return m_securityService.unlock(SECURE_READ, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public CrudObject get(String id) throws IdUnusedException, PermissionException
	{
		if (!allowGet(id))
			throw new PermissionException(m_sessionService.getSessionUserId(), SECURE_READ, "");

		// use hibernate to get our object
		CrudObjectImpl co = (CrudObjectImpl) getHibernateTemplate().get(CrudObjectImpl.class, id);

		// if not found, throw the exception
		if (co == null)
		{
			throw new IdUnusedException(id);
		}

		// return the object found
		return co;
	}

	/**
	 * {@inheritDoc}
	 */
	public List findByName(final String name) throws PermissionException
	{
		if (!allowGet(null))
			throw new PermissionException(m_sessionService.getSessionUserId(), SECURE_READ, "");

		//final String _name = name;
		HibernateCallback callback = new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				return session
					.createCriteria(CrudObject.class)
					.add(Expression.like("name", "%" + name + "%"))
					.addOrder(Order.asc("name"))
					.list();
			}
		};
		return (List) getHibernateTemplate().execute(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowUpdate(CrudObject co)
	{
		// TODO: the reference!
		return m_securityService.unlock(SECURE_UPDATE, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(CrudObject co) throws PermissionException, VersionException
	{
		if (!allowUpdate(co))
			throw new PermissionException(m_sessionService.getSessionUserId(), SECURE_UPDATE, "");

		// use hibernate to save (new) or update (old) the object
		try
		{
			CrudObjectImpl coi = (CrudObjectImpl) co;
			getHibernateTemplate().saveOrUpdate(coi);
		}
		catch (OptimisticLockingFailureException e)
		{
			// we have a version exception, folks!
			throw new VersionException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowDelete(String id)
	{
		// TODO: the reference!
		return m_securityService.unlock(SECURE_DELETE, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(String id) throws PermissionException, IdUnusedException
	{
		if (!allowDelete(id))
			throw new PermissionException(m_sessionService.getSessionUserId(), SECURE_DELETE, "");

		// find the object
		CrudObjectImpl co = (CrudObjectImpl) get(id);

		// if not found, throw the exception
		if (co == null)
		{
			throw new IdUnusedException(id);
		}

		// use hibernate to delete the object
		getHibernateTemplate().delete(co);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/component/sample/crud/CrudServiceImpl.java,v 1.8 2004/09/30 20:20:28 ggolden.umich.edu Exp $
*
**********************************************************************************/
