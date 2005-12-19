/**********************************************************************************
 *
 * $Id$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of California
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

package org.sakaiproject.test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.PropertyResourceBundle;

import junit.framework.TestCase;

import org.apache.catalina.loader.WebappClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.ComponentManager;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

public class SakaiTestBase extends TestCase {
	private static final Log log = LogFactory.getLog(SakaiTestBase.class);

	protected static ComponentManager compMgr;
	
	/**
	 * Initialize the component manager once for all tests, and log in as admin.
	 */
	protected static void oneTimeSetup() throws Exception {
		if(compMgr == null) {
			// Find the sakai home dir
			String tomcatHome = getTomcatHome();
			String sakaiHome = tomcatHome + File.separatorChar + "sakai" + File.separatorChar;
			String componentsDir = tomcatHome + "components/";
			
			// Set the system properties needed by the sakai component manager
			System.setProperty("sakai.home", sakaiHome);
			System.setProperty(ComponentManager.SAKAI_COMPONENTS_ROOT_SYS_PROP, componentsDir);

			// Get a tomcat classloader
			log.debug("Creating a tomcat classloader for component loading");
			WebappClassLoader wcloader = new WebappClassLoader(Thread.currentThread().getContextClassLoader());
			wcloader.start();

			// Initialize spring component manager
			log.debug("Loading component manager via tomcat's classloader");
			Class clazz = wcloader.loadClass(
					"org.sakaiproject.component.kernel.component.SpringCompMgr");
			Constructor constructor = clazz.getConstructor(new Class[] {ComponentManager.class});
			compMgr = (ComponentManager)constructor.newInstance(new Object[] {null});
			Method initMethod = clazz.getMethod("init", null);
			initMethod.invoke(compMgr, null);
		}
		
		// Sign in as admin
		if(SessionManager.getCurrentSession() == null) {
			SessionManager.startSession();
			Session session = SessionManager.getCurrentSession();
			session.setUserId("admin");
		}
	}

	/**
	 * Close the component manager when the tests finish.
	 */
	public static void oneTimeTearDown() {
		if(compMgr != null) {
			compMgr.close();
		}
	}

	/**
	 * Fetches the "maven.tomcat.home" property from the maven build.properties
	 * file located in the user's $HOME directory.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String getTomcatHome() throws Exception {
		String homeDir = System.getProperty("user.home");
		File file = new File(homeDir + File.separatorChar + "build.properties");
		FileInputStream fis = new FileInputStream(file);
		PropertyResourceBundle rb = new PropertyResourceBundle(fis);
		return rb.getString("maven.tomcat.home");
	}
	
	/**
	 * Gets a service bean from the Sakai component manager.
	 * 
	 * @param beanId The id of the service
	 * 
	 * @return The service, or null if the ID is not registered
	 */
	protected Object getService(String beanId) {
		return org.sakaiproject.api.kernel.component.cover.ComponentManager.get(beanId);
	}

	/**
	 * Sets the current user in sakai
	 * 
	 * @param userUid The user to become
	 */
	protected void setUser(String userUid) {
		Session session = SessionManager.getCurrentSession();
		session.setUserId(userUid);
	}
	
	/**
	 * Creates a somewhat unique site id for testing
	 * 
	 * @return
	 */
	protected String generateSiteId() {
		return "site-" + getClass().getName() + "-" + new Date().getTime();
	}
}
