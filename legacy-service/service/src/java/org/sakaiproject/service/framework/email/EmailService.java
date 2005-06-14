/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/email/EmailService.java,v 1.1 2005/05/12 15:45:40 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.email;

import javax.mail.internet.InternetAddress;
import java.util.List;

/**
 * <p>EmailService is an interface to sending emails.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.1 $
 */
public interface EmailService
{
	/** 
	 * Creates and sends a generic text MIME message to the address contained in to.
	 * @param from The address this message is to be listed as coming from.
	 * @param to The address(es) this message should be sent to.
	 * @param subject The subject of this message.
	 * @param content The body of the message.
	 * @param headerToStr If specified, this is placed into the message header, but "to" is used for the recipients.
	 * @param replyTo If specified, this is the reply to header address(es).
	 * @param additionalHeaders Additional email headers to send (List of String).  For example, content type or forwarded headers (may be null)
	 */
	void sendMail(InternetAddress from, InternetAddress[] to, String subject, String content,
								InternetAddress[] headerTo, InternetAddress[] replyTo, List additionalHeaders);

	/** 
	 * Creates and sends a generic text MIME message to the address contained in to.
	 * @param fromStr The address this message is to be listed as coming from.
	 * @param toStr The address(es) this message should be sent to.
	 * @param subject The subject of this message.
	 * @param content The body of the message.
	 * @param headerToStr If specified, this is placed into the message header, but "too" is used for the recipients.
	 * @param replyToStr If specified, the reply-to header value.
	 * @param additionalHeaders Additional email headers to send (List of String).  For example, content type or forwarded headers (may be null)
	 */
	void send(String fromStr, String toStr, String subject, String content, String headerToStr,
							String replyToStr, List additionalHeaders);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/email/EmailService.java,v 1.1 2005/05/12 15:45:40 ggolden.umich.edu Exp $
*
**********************************************************************************/
