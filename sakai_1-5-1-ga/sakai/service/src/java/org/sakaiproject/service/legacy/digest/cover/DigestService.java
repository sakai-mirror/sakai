/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/digest/cover/DigestService.java,v 1.6 2004/07/22 17:04:21 janderse.umich.edu Exp $
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

package org.sakaiproject.service.legacy.digest.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>DigestService is a static Cover for the {@link org.sakaiproject.service.legacy.digest.DigestService DigestService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
*/
public class DigestService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.digest.DigestService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.digest.DigestService) ComponentManager.get(org.sakaiproject.service.legacy.digest.DigestService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.digest.DigestService) ComponentManager.get(org.sakaiproject.service.legacy.digest.DigestService.class);
		}
	}
	private static org.sakaiproject.service.legacy.digest.DigestService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.digest.DigestService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.digest.DigestService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_DIGEST = org.sakaiproject.service.legacy.digest.DigestService.SECURE_ADD_DIGEST;
	public static java.lang.String SECURE_EDIT_DIGEST = org.sakaiproject.service.legacy.digest.DigestService.SECURE_EDIT_DIGEST;
	public static java.lang.String SECURE_REMOVE_DIGEST = org.sakaiproject.service.legacy.digest.DigestService.SECURE_REMOVE_DIGEST;

	public static void commit(org.sakaiproject.service.legacy.digest.DigestEdit param0)
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return;

		service.commit(param0);
	}

	public static org.sakaiproject.service.legacy.digest.Digest getDigest(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return null;

		return service.getDigest(param0);
	}

	public static java.util.List getDigests()
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return null;

		return service.getDigests();
	}

	public static org.sakaiproject.service.legacy.digest.DigestEdit add(java.lang.String param0) throws org.sakaiproject.exception.IdUsedException
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return null;

		return service.add(param0);
	}

	public static void remove(org.sakaiproject.service.legacy.digest.DigestEdit param0)
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return;

		service.remove(param0);
	}

	public static void digest(org.sakaiproject.service.legacy.digest.DigestMessage param0)
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return;

		service.digest(param0);
	}

	public static void cancel(org.sakaiproject.service.legacy.digest.DigestEdit param0)
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return;

		service.cancel(param0);
	}

	public static org.sakaiproject.service.legacy.digest.DigestEdit edit(java.lang.String param0) throws org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.digest.DigestService service = getInstance();
		if (service == null)
			return null;

		return service.edit(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/digest/cover/DigestService.java,v 1.6 2004/07/22 17:04:21 janderse.umich.edu Exp $
*
**********************************************************************************/
