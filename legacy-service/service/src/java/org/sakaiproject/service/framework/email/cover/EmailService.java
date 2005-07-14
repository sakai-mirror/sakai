/**********************************************************************************
* $URL$
* $Id$
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

package org.sakaiproject.service.framework.email.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>EmailService is a static Cover for the {@link org.sakaiproject.service.framework.email.EmailService EmailService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class EmailService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.email.EmailService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.email.EmailService) ComponentManager.get(org.sakaiproject.service.framework.email.EmailService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.email.EmailService) ComponentManager.get(org.sakaiproject.service.framework.email.EmailService.class);
		}
	}
	private static org.sakaiproject.service.framework.email.EmailService m_instance = null;



	public static void sendMail(javax.mail.internet.InternetAddress param0, javax.mail.internet.InternetAddress[] param1, java.lang.String param2, java.lang.String param3, javax.mail.internet.InternetAddress[] param4, javax.mail.internet.InternetAddress[] param5, java.util.List param6)
	{
		org.sakaiproject.service.framework.email.EmailService service = getInstance();
		if (service == null)
			return;

		service.sendMail(param0, param1, param2, param3, param4, param5, param6);
	}

	public static void send(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, java.util.List param6)
	{
		org.sakaiproject.service.framework.email.EmailService service = getInstance();
		if (service == null)
			return;

		service.send(param0, param1, param2, param3, param4, param5, param6);
	}
}



