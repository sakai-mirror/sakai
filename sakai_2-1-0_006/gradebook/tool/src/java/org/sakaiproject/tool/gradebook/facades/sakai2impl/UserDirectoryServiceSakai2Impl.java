/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The MIT Corporation
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

package org.sakaiproject.tool.gradebook.facades.sakai2impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.exception.IdUnusedException;

import org.sakaiproject.service.gradebook.shared.UnknownUserException;
import org.sakaiproject.tool.gradebook.facades.UserDirectoryService;

/**
 * Sakai2 implementation of the gradebook UserDirectoryService API.
 */
public class UserDirectoryServiceSakai2Impl implements UserDirectoryService {
    private static final Log log = LogFactory.getLog(UserDirectoryServiceSakai2Impl.class);

    public String getUserDisplayName(String userUid) throws UnknownUserException {
        try {
            org.sakaiproject.service.legacy.user.User sakaiUser =
            	org.sakaiproject.service.legacy.user.cover.UserDirectoryService.getUser(userUid);
            return sakaiUser.getDisplayName();
        } catch (IdUnusedException e) {
            throw new UnknownUserException("Unknown uid: " + userUid);
        }
    }
}