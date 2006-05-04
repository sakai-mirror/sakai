/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005, 2006 The Regents of the University of Michigan, Trustees of Indiana University,
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

// package
package org.sakaiproject.component.framework.email;

// imports
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.email.EmailService;
import org.sakaiproject.service.framework.log.Logger;

/**
 * <p>BasicEmailService implements the EmailService.</p>
 * <p>See the {@link org.sakaiproject.service.framework.email.EmailService} interface for details.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class TestEmailService implements EmailService
{
	/*******************************************************************************
	* Dependencies and their setter methods
	* Note: keep these in sync with the BasidEmailService, to make switching between them easier -ggolden
	*******************************************************************************/

	/** Dependency: logging service. */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: ServerConfigurationService. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * 
	 * @param service
	 *        The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/** Configuration: smtp server to use. */
	protected String m_smtp = null;

	/**
	 * Configuration: smtp server to use.
	 * 
	 * @param value
	 *        The smtp server string.
	 */
	public void setSmtp(String value)
	{
		m_smtp = value;
	}

	/** Configuration: smtp server port to use. */
	protected String m_smtpPort = null;

	/**
	 * Configuration: smtp server port to use.
	 * 
	 * @param value
	 *        The smtp server port string.
	 */
	public void setSmtpPort(String value)
	{
		m_smtpPort = value;
	}

	/** Configuration: optional smtp mail envelope return address. */
	protected String m_smtpFrom = null;

	/**
	 * Configuration: smtp mail envelope return address.
	 * 
	 * @param value
	 *        The smtp mail from address string.
	 */
	public void setSmtpFrom(String value)
	{
		m_smtpFrom = value;
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
	* Work interface methods: org.sakai.service.email.EmailService
	*******************************************************************************/

	protected String listToStr(List list)
	{
		if (list == null) return "";
		return arrayToStr(list.toArray());
	}

	protected String arrayToStr(Object[] array)
	{
		StringBuffer buf = new StringBuffer();
		if (array != null)
		{
			buf.append("[");
			for (int i = 0; i < array.length; i++)
			{
				if (i != 0) buf.append(", ");
				buf.append(array[i].toString());
			}
			buf.append("]");
		}
		else
		{
			buf.append("");
		}

		return buf.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendMail(
		InternetAddress from,
		InternetAddress[] to,
		String subject,
		String content,
		InternetAddress[] headerTo,
		InternetAddress[] replyTo,
		List additionalHeaders)
	{
		m_logger.info(
			this
				+ "sendMail: from: "
				+ from
				+ " to: "
				+ arrayToStr(to)
				+ " subject: "
				+ subject
				+ " headerTo: "
				+ arrayToStr(headerTo)
				+ " replyTo: "
				+ arrayToStr(replyTo)
				+ " content: "
				+ content
				+ " additionalHeaders: "
				+ listToStr(additionalHeaders)
				);
	}

	/**
	 * {@inheritDoc}
	 */
	public void send(String fromStr, String toStr, String subject, String content, String headerToStr, String replyToStr, List additionalHeaders)
	{
		m_logger.info(
			this
				+ "send: from: "
				+ fromStr
				+ " to: "
				+ toStr
				+ " subject: "
				+ subject
				+ " headerTo: "
				+ headerToStr
				+ " replyTo: "
				+ replyToStr
				+ " content: "
				+ content
				+ " additionalHeaders: "
				+ listToStr(additionalHeaders)
				);
	}
}



