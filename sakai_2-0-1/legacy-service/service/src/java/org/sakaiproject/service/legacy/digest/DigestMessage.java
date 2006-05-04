/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/digest/DigestMessage.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.service.legacy.digest;

// imports

/**
* <p>DigestMessage is one message in a digest</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class DigestMessage
{
	/** The id of the User who gets this message. */
	protected  String m_to = null;

	/** The subject. */
	protected String m_subject = null;

	/** The body. */
	protected String m_body = null;

	/**
	* Construct.
	* @param to The id of the User who gets this message.
	* @param subject The subject.
	* @param body The body.
	*/
	public DigestMessage(String to, String subject, String body)
	{
		m_to = to;
		m_subject = subject;
		m_body = body;

	}	// DigestMessage

	/**
	* Access the to (user id) of the message.
	* @return The to (user id) of the message.
	*/
	public String getTo() { return m_to; }

	/**
	* Set the to (user id) of the message.
	* @param subject The to (user id) of the message.
	*/
	public void setTo(String to) { m_to = to; }

	/**
	* Access the subject of the message.
	* @return The subject of the message.
	*/
	public String getSubject() { return m_subject; }

	/**
	* Set the subject of the message
	* @param subject The subject of the message.
	*/
	public void setSubject(String subject) { m_subject = subject; }

	/**
	* Access the body of the message.
	* @return The body of the message.
	*/
	public String getBody() { return m_body; }

	/**
	* Set the body of the message
	* @param body The subject of the message.
	*/
	public void setBody(String body) { m_body = body; }

}   // DigestMessage

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/digest/DigestMessage.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
*
**********************************************************************************/