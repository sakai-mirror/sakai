/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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
 * IdPwEvidence is a utility class that implements the IdPwEvidence interface.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class IdPwEvidence implements org.sakaiproject.api.common.authentication.IdPwEvidence
{
	/** The user identifier string. */
	protected String m_identifier = null;

	/** The password string. */
	protected String m_password = null;

	/**
	 * Construct, with identifier and password.
	 * 
	 * @param identifier
	 *        The user identifier string.
	 * @param password
	 *        The password string.
	 */
	public IdPwEvidence(String identifier, String password)
	{
		m_identifier = identifier;
		m_password = password;
	}

	/**
	 * @inheritDoc
	 */
	public String getIdentifier()
	{
		return m_identifier;
	}

	/**
	 * @inheritDoc
	 */
	public String getPassword()
	{
		return m_password;
	}
}



