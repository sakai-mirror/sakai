/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/framework/email/BasicEmailService.java,v 1.3 2005/05/14 23:54:50 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.sakaiproject.service.framework.email.EmailService;
import org.sakaiproject.service.framework.log.Logger;

/**
 * <p>BasicEmailService implements the EmailService.</p>
 * <p>See the {@link org.sakaiproject.service.framework.email.EmailService} interface for details.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.3 $
 */
public class BasicEmailService implements EmailService
{
	/*******************************************************************************
	* Dependencies and their setter methods
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

	/** Configuration: smtp server to use. */
	protected String m_smtp = null;

	/**
	 * Configuration: smtp server to use.
	 * @param value The smtp server string.
	 */
	public void setSmtp(String value)
	{
		m_smtp = value;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this +".init(): smtp: " + m_smtp);
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

	protected static final String SERVER_PROP = "mail.smtp.host";

	protected static final String CONTENT_TYPE = "text/plain";

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
		List additionalHeaders
		)
	{
		if (m_smtp == null)
		{
			m_logger.warn(this + ".sendMail: smtp not set");
			return;
		}

		Properties props = new Properties();
		props.put(SERVER_PROP, m_smtp);

		Session session = Session.getDefaultInstance(props, null);

		if (m_logger.isInfoEnabled())
		{
			StringBuffer buf = new StringBuffer();
			buf.append("Email.sendMail: from: ");
			buf.append(from);
			buf.append(" subject: ");
			buf.append(subject);
			if (to != null)
			{
				buf.append(" to:");
				for (int i = 0; i < to.length; i++)
				{
					buf.append(" ");
					buf.append(to[i]);
				}
			}
			if (headerTo != null)
			{
				buf.append(" headerTo:");
				for (int i = 0; i < headerTo.length; i++)
				{
					buf.append(" ");
					buf.append(headerTo[i]);
				}
			}
			m_logger.info(buf.toString());
		}

		try
		{
			MimeMessage msg = new MimeMessage(session);
			
			// the FULL content-type header, for example:
			// Content-Type: text/plain; charset=windows-1252; format=flowed
			String contentType = null;			
			
			// the character set, for example, windows-1252 or UTF-8
			String charset = null;

			// set the additional headers on the message
			// but treat Content-Type specially as we need to check the charset
			if (additionalHeaders != null)
			{
				Iterator i = additionalHeaders.iterator();
				while (i.hasNext())
				{
					String header = (String)i.next();
					if (header.startsWith("Content-Type: ")) 
					{
						contentType = header;
					}
					else
					{
						msg.addHeaderLine(header);
					}
				}
			}					
			
			msg.setFrom(from);
			
			if (headerTo != null)
			{
				msg.setRecipients(Message.RecipientType.TO, headerTo);
			}
			else
			{
				msg.setRecipients(Message.RecipientType.TO, to);
			}

			msg.setSubject(subject);
			
			if (replyTo != null)
			{
				msg.setReplyTo(replyTo);
			}
			
			// figure out what charset encoding to use
			//
			// first try to use the charset from the forwarded
			// Content-Type header (if there is one).
			// if that charset doesn't work, try a couple others.			
			if (contentType != null)
			{
				// try and extract the charset from the Content-Type header
				int charsetStart = contentType.toLowerCase().indexOf("charset=");
				if (charsetStart != -1)
				{
					int charsetEnd = contentType.indexOf(";", charsetStart);
					if (charsetEnd == -1) charsetEnd = contentType.length();
					charset = contentType.substring(charsetStart+"charset=".length(), charsetEnd).trim();
				}
			}
			
			if (charset != null && canUseCharset(content, charset))
			{
				// use the charset from the Content-Type header
			}
			else if (canUseCharset(content, "ISO-8859-1"))
			{
				if (contentType != null && charset != null) contentType = contentType.replaceAll(charset, "ISO-8859-1");
				charset = "ISO-8859-1";
			}
			else if (canUseCharset(content, "windows-1252"))
			{
				if (contentType != null && charset != null) contentType = contentType.replaceAll(charset, "windows-1252");
				charset = "windows-1252";
			}
			else
			{
				// catch-all - UTF-8 should be able to handle anything
				if (contentType != null && charset != null) contentType = contentType.replaceAll(charset, "UTF-8");
				charset = "UTF-8";
			}

			// fill in the body of the message
			msg.setText(content, charset);
			
			// if we have a full Content-Type header, set it NOW
			// (after setting the body of the message so that format=flowed is preserved)
			if (contentType != null)
			{
				msg.addHeaderLine(contentType);
			}						
			
			if (headerTo != null)
			{
				Transport.send(msg, to);
			}
			else
			{
				Transport.send(msg);
			}
		}
		catch (MessagingException e)
		{
			//			System.out.println(e);
		}
	}
	
	
	/** Returns true if the given content String can be encoded in the given charset */
	private static boolean canUseCharset(String content, String charsetName)
	{
		try
		{
			return Charset.forName(charsetName).newEncoder().canEncode(content);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void send(String fromStr, String toStr, String subject, String content, String headerToStr, String replyToStr, List additionalHeaders)
	{
		try
		{
			InternetAddress from = new InternetAddress(fromStr);

			StringTokenizer tokens = new StringTokenizer(toStr, ", ");
			InternetAddress[] to = new InternetAddress[tokens.countTokens()];

			int i = 0;
			while (tokens.hasMoreTokens())
			{
				String next = (String) tokens.nextToken();
				to[i] = new InternetAddress(next);

				i++;
			} // cycle through and collect all of the Internet addresses from the list.

			InternetAddress[] headerTo = null;
			if (headerToStr != null)
			{
				headerTo = new InternetAddress[1];
				headerTo[0] = new InternetAddress(headerToStr);
			}

			InternetAddress[] replyTo = null;
			if (replyToStr != null)
			{
				replyTo = new InternetAddress[1];
				replyTo[0] = new InternetAddress(replyToStr);
			}

			sendMail(from, to, subject, content, headerTo, replyTo, additionalHeaders);

		}
		catch (AddressException e)
		{
			m_logger.warn(this + ".send: " + e);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/framework/email/BasicEmailService.java,v 1.3 2005/05/14 23:54:50 ggolden.umich.edu Exp $
*
**********************************************************************************/
