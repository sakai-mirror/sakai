/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.component.framework.email;

import java.nio.charset.Charset;
import java.util.Date;
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

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.email.EmailService;
import org.sakaiproject.service.framework.log.Logger;

/**
 * <p>
 * BasicEmailService implements the EmailService.
 * </p>
 * <p>
 * See the {@link org.sakaiproject.service.framework.email.EmailService} interface for details.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BasicEmailService implements EmailService
{
	protected static final String POSTMASTER = "postmaster";

	protected static final String SMTP_HOST = "mail.smtp.host";

	protected static final String SMTP_PORT = "mail.smtp.port";

	protected static final String SMTP_FROM = "mail.smtp.from";

	protected static final String CONTENT_TYPE = "text/plain";

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: logging service. */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * 
	 * @param service
	 *        The logging service.
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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// if no m_mailfrom set, set to the postmaster
		if (m_smtpFrom == null)
		{
			m_smtpFrom = POSTMASTER + "@" + m_serverConfigurationService.getServerName();
		}

		// promote these to the system properties, to keep others (James) from messing with them
		if (m_smtp != null) System.setProperty(SMTP_HOST, m_smtp);
		if (m_smtpPort != null) System.setProperty(SMTP_PORT, m_smtpPort);
		System.setProperty(SMTP_FROM, m_smtpFrom);

		m_logger.info(this + ".init(): smtp: " + m_smtp + ((m_smtpPort != null) ? (":" + m_smtpPort) : "") + " bounces to: " + m_smtpFrom);
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: org.sakai.service.email.EmailService
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public void sendMail(InternetAddress from, InternetAddress[] to, String subject, String content, InternetAddress[] headerTo,
			InternetAddress[] replyTo, List additionalHeaders)
	{
		if (m_smtp == null)
		{
			m_logger.warn(this + ".sendMail: smtp not set");
			return;
		}

		if (from == null)
		{
			m_logger.warn(this + ".sendMail: null from");
			return;
		}

		if (to == null)
		{
			m_logger.warn(this + ".sendMail: null to");
			return;
		}

		if (content == null)
		{
			m_logger.warn(this + ".sendMail: null content");
			return;
		}

		Properties props = new Properties();

		// set the server host
		props.put(SMTP_HOST, m_smtp);

		// set the port, if specified
		if (m_smtpPort != null)
		{
			props.put(SMTP_PORT, m_smtpPort);
		}

		// set the mail envelope return address
		props.put(SMTP_FROM, m_smtpFrom);

		Session session = Session.getDefaultInstance(props, null);

		if (m_logger.isInfoEnabled())
		{
			StringBuffer buf = new StringBuffer();
			buf.append("Email.sendMail: from: ");
			buf.append(from);
			buf.append(" subject: ");
			buf.append(subject);
			buf.append(" to:");
			for (int i = 0; i < to.length; i++)
			{
				buf.append(" ");
				buf.append(to[i]);
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
			// see if we have a message-id in the additional headers
			String mid = null;  
			if (additionalHeaders != null)
			{
				Iterator i = additionalHeaders.iterator();
				while (i.hasNext())
				{
					String header = (String) i.next();
					if (header.startsWith("Message-Id: "))
					{
						mid = header.substring(12);
					}
				}
			}

			// use the special extension that can set the id
			MimeMessage msg = new MyMessage(session, mid);

			// the FULL content-type header, for example:
			// Content-Type: text/plain; charset=windows-1252; format=flowed
			String contentType = null;

			// the character set, for example, windows-1252 or UTF-8
			String charset = null;

			// set the additional headers on the message
			// but treat Content-Type specially as we need to check the charset
			// and we already dealt with the message id
			if (additionalHeaders != null)
			{
				Iterator i = additionalHeaders.iterator();
				while (i.hasNext())
				{
					String header = (String) i.next();
					if (header.startsWith("Content-Type: "))
					{
						contentType = header;
					}
					else if (!header.startsWith("Message-Id: "))
					{
						msg.addHeaderLine(header);
					}
				}
			}

			// date
			if (msg.getHeader("Date") == null)
			{
				msg.setSentDate(new Date(System.currentTimeMillis()));
			}

			msg.setFrom(from);

			if (msg.getHeader("To") == null)
			{
				if (headerTo != null)
				{
					msg.setRecipients(Message.RecipientType.TO, headerTo);
				}
			}

			if ((subject != null) && (msg.getHeader("Subject") == null))
			{
				msg.setSubject(subject);
			}

			if ((replyTo != null) && (msg.getHeader("Reply-To") == null))
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
					charset = contentType.substring(charsetStart + "charset=".length(), charsetEnd).trim();
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

			Transport.send(msg, to);
		}
		catch (MessagingException e)
		{
			// System.out.println(e);
		}
	}

	/** Returns true if the given content String can be encoded in the given charset */
	protected static boolean canUseCharset(String content, String charsetName)
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
	public void send(String fromStr, String toStr, String subject, String content, String headerToStr, String replyToStr,
			List additionalHeaders)
	{
		if (fromStr == null)
		{
			m_logger.warn(this + ".send: null fromStr");
			return;
		}

		if (toStr == null)
		{
			m_logger.warn(this + ".send: null toStr");
			return;
		}

		if (content == null)
		{
			m_logger.warn(this + ".send: null content");
			return;
		}

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

	// inspired by http://java.sun.com/products/javamail/FAQ.html#msgid
	protected class MyMessage extends MimeMessage
	{
		protected String m_id = null;

		public MyMessage(Session session, String id)
		{
			super(session);
			m_id = id;
		}

		protected void updateHeaders() throws MessagingException
		{
			super.updateHeaders();
			if (m_id != null)
			{
				setHeader("Message-Id", m_id);
			}
		}
	}
}
