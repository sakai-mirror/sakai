/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/IdPwEvidence.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
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

package org.sakaiproject.api.common.authentication;

/**
 * <p>
 * IdPwEvidence is Authetication evidence made up of a user identifier and a password. Note the "id" used here is something the user offers for authentication purposes, and is *not* the user's Sakai user object UUID.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public interface IdPwEvidence extends Evidence
{
	/**
	 * Access the user identifier.
	 * 
	 * @return The user identifier.
	 */
	String getIdentifier();

	/**
	 * Access the password.
	 * 
	 * @return The password.
	 */
	String getPassword();
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/IdPwEvidence.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
