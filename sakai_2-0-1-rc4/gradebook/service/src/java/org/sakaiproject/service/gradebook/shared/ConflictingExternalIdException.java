/**********************************************************************************
*
* $Header: /cvs/sakai2/gradebook/service/src/java/org/sakaiproject/service/gradebook/shared/ConflictingExternalIdException.java,v 1.2 2005/05/26 18:04:55 josh.media.berkeley.edu Exp $
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

package org.sakaiproject.service.gradebook.shared;

/**
 * Indicates that an assignment name conflict within a gradebook would ensue
 * from the requested operation.
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class ConflictingExternalIdException extends GradebookException {
	public ConflictingExternalIdException(String message) {
        super(message);
    }
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/gradebook/service/src/java/org/sakaiproject/service/gradebook/shared/ConflictingExternalIdException.java,v 1.2 2005/05/26 18:04:55 josh.media.berkeley.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/