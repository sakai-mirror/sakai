/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authentication-component/src/java/org/sakaiproject/component/common/authentication/OpenAuthnComponent.java,v 1.2 2005/04/08 01:53:52 ggolden.umich.edu Exp $
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

package org.sakaiproject.component.common.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.authentication.Authentication;
import org.sakaiproject.api.common.authentication.AuthenticationException;
import org.sakaiproject.api.common.authentication.AuthenticationManager;
import org.sakaiproject.api.common.authentication.AuthenticationUnknownException;
import org.sakaiproject.api.common.authentication.Evidence;
import org.sakaiproject.api.common.authentication.ExternalTrustedEvidence;
import org.sakaiproject.api.common.authentication.IdPwEvidence;

/**
 * <p>
 * A placeholder wide open Authentication - returns the password as the authentication user (UUID) id.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.2 $
 */
public class OpenAuthnComponent implements AuthenticationManager
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(OpenAuthnComponent.class);

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		M_log.info("init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: AuthenticationManager
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public Authentication authenticate(Evidence e) throws AuthenticationException
	{
		if (e instanceof IdPwEvidence)
		{
			IdPwEvidence evidence = (IdPwEvidence) e;

			// reject null or blank
			if ((evidence.getPassword() == null) || (evidence.getPassword().trim().length() == 0)
					|| (evidence.getIdentifier() == null) || (evidence.getIdentifier().trim().length() == 0))
			{
				throw new AuthenticationException("invalid login");
			}

			// we always pass the authentication

			// use the password as the uid, the name as the eid
			Authentication rv = new org.sakaiproject.util.Authentication(evidence.getPassword(), evidence.getIdentifier());
			return rv;
		}
		else if (e instanceof ExternalTrustedEvidence)
		{
			ExternalTrustedEvidence evidence = (ExternalTrustedEvidence) e;

			// reject null or blank
			if ((evidence.getIdentifier() == null) || (evidence.getIdentifier().trim().length() == 0))
			{
				throw new AuthenticationException("invalid login");
			}

			// accept, but we need to lookup the user in our database to get a uuid.

			Authentication rv = new org.sakaiproject.util.Authentication("000-00-0000", evidence.getIdentifier());
			return rv;
		}
		else
		{
			throw new AuthenticationUnknownException(e.toString());
		}
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/common/authentication-component/src/java/org/sakaiproject/component/common/authentication/OpenAuthnComponent.java,v 1.2 2005/04/08 01:53:52 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
