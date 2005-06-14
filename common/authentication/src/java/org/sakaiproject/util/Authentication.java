/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/util/Authentication.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.util;

/**
 * <p>
 * Authentication is a utility class that implements the Authentication interface.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class Authentication implements org.sakaiproject.api.common.authentication.Authentication
{
	/** The UUID identifier string. */
	protected String m_uid = null;

	/** The enterprise identifier string. */
	protected String m_eid = null;

	/**
	 * Construct, with uid and eid
	 * 
	 * @param uid
	 *        The UUID internal end user identifier string.
	 * @param eid
	 *        The enterprise end user identifier string.
	 */
	public Authentication(String uid, String eid)
	{
		m_uid = uid;
		m_eid = eid;
	}

	/**
	 * @inheritDoc
	 */
	public String getUid()
	{
		return m_uid;
	}

	/**
	 * @inheritDoc
	 */
	public String getEid()
	{
		return m_eid;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/util/Authentication.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
