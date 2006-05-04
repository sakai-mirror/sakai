/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/Authentication.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
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
 * Authentication is the successful result of an authentication attempt.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public interface Authentication
{
	/**
	 * Access the internal id of the authenticated end user.
	 * 
	 * @return The internal id (if known) of the authenticated end user, null if not known.
	 */
	String getUid();

	/**
	 * Access the enterprise id of the authenticated end user.
	 * 
	 * @return The enterprise id (if known) of the authenticated end user, null if not known.
	 */
	String getEid();
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/Authentication.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
