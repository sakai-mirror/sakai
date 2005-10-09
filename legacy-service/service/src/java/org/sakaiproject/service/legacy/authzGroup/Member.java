/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004,2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.service.legacy.authzGroup;

import java.io.Serializable;

/**
 * <p>
 * Member records membership in an AuthzGroup; user, role, and flags.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface Member extends Comparable, Serializable
{
	/**
	 * Access the user id of the member.
	 * 
	 * @return The user id of the member.
	 */
	String getUserId();

	/**
	 * Access the member's Role.
	 * 
	 * @return The member's Role.
	 */
	Role getRole();

	/**
	 * Check if the membership is from the external provider.
	 * 
	 * @return true if the membership is from the external provider, false if not.
	 */
	boolean isProvided();

	/**
	 * Check if the membership is active.
	 * 
	 * @return true if the membership is active, false if not.
	 */
	boolean isActive();
}
