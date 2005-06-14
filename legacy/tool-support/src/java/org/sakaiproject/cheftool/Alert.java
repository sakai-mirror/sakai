/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/cheftool/Alert.java,v 1.1 2005/04/12 03:11:38 ggolden.umich.edu Exp $
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

package org.sakaiproject.cheftool;

import java.io.Serializable;

/**
* <p>Alert is a set of messages intended for user display in the user interface.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class Alert implements Serializable
{
	/** The Alert text. */
	protected String m_msg = null;

	/**
	 * Add a new alert line.  A line separator will be appended as needed.
	 * @param alert The alert message to add.
	 */
	public void add(String alert)
	{
		// if this is the first, just set it
		if (m_msg == null)
		{
			m_msg = alert;
		}

		// otherwise append it with a line break
		else
		{
			m_msg = "\n" + alert;
		}
	}

	/**
	 * Access the alert message.  Once accessed, the message is cleared.
	 * @return The alert message.
	 */
	public String getAlert()
	{
		String tmp = m_msg;
		m_msg = null;

		return tmp;
	}

	/**
	 * Access the alert message, but unlike getAlert(), do not clear the message.
	 * @return The alert message.
	 */
	public String peekAlert()
	{
		return m_msg;
	}

	/**
	 * Check to see if the alert is empty, or has been populated.
	 * @return true of the alert is empty, false if there have been alerts set.
	 */
	public boolean isEmpty()
	{
		return ((m_msg == null) || (m_msg.length() == 0));
	}

	/**
	 * Remove any messages in the Alert.
	 */
	public void clear()
	{
		m_msg = null;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/cheftool/Alert.java,v 1.1 2005/04/12 03:11:38 ggolden.umich.edu Exp $
*
**********************************************************************************/
