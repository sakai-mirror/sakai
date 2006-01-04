/**********************************************************************************
 * $URL$
 * $Id$
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

// package
package org.sakaiproject.service.legacy.security;

// imports

/**
 * <p>
 * SecurityAdvisor is a stackable policy process that is given a chance to determine if a security question can be answered, over the logic of the SecurityService component.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface SecurityAdvisor
{
	/**
	 * <p>
	 * SecurityAdvice enumerates different SecurityAdvisor results.
	 * </p>
	 */
	public class SecurityAdvice
	{
		private final String m_id;

		private SecurityAdvice(String id)
		{
			m_id = id;
		}

		public String toString()
		{
			return m_id;
		}

		/** Security result that indicates the end user is allowed the function. */
		public static final SecurityAdvice ALLOWED = new SecurityAdvice("allowed");

		/** Security result that indicates the end user is NOT allowed the function. */
		public static final SecurityAdvice NOT_ALLOWED = new SecurityAdvice("not allowed");

		/** Security result that indicates the SecurityAdvisor cannot answer the question. */
		public static final SecurityAdvice PASS = new SecurityAdvice("pass");
	}

	/**
	 * Can the current session user perform the requested function on the referenced Entity?
	 * 
	 * @param userId
	 *        The user id.
	 * @param function
	 *        The lock id string.
	 * @param reference
	 *        The resource reference string.
	 * @return ALLOWED or NOT_ALLOWED if the advisor can answer that the user can or cannot, or PASS if the advisor cannot answer.
	 */
	SecurityAdvice isAllowed(String userId, String function, String reference);
}