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

package org.sakaiproject.service.legacy.entity;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;

/**
 * <p>
 * Provide entity access via http for use in the access servlet.
 * </p>
 */
public interface HttpAccess
{
	/**
	 * Handle an HTTP request for access. The request and response objects are provider.<br />
	 * The access is for the referenced entity.<br />
	 * Use the response object to send the headers, length, content type, and bytes of the response in whatever manner needed.<br />
	 * Make the response ONLY if it is permitted and exists and otherwise valid. Use the exceptions for any error handling.
	 * 
	 * @param req
	 *        The request object.
	 * @param res
	 *        The response object.
	 * @param ref
	 *        The entity reference
	 * @param copyrightAcceptedRefs
	 *        The collection (entity reference String) of entities that the end user in this session have already accepted the copyright for.
	 * @throws PermissionException
	 *         Throw this if the current user does not have permission for the access.
	 * @throws IdUnusedException
	 *         Throw this if the ref is not supported or the entity is not available for access in any way.
	 * @throws ServerOverloadException
	 *         Throw this if you are rejecting an otherwise valid request because of some sort of server resource shortage or limit.
	 * @throws CopyrightException
	 *         Throw this if you are rejecting an otherwise valid request because the user needs to agree to the copyright and has not yet done so.
	 */
	void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws PermissionException,
			IdUnusedException, ServerOverloadException, CopyrightException;
}
