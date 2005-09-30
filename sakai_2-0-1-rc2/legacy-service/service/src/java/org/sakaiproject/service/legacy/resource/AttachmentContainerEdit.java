/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/AttachmentContainerEdit.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.service.legacy.resource;

// import

/**
* <p>AttachmentContainer is the interface for objects that may contain attachments and may modify them.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface AttachmentContainerEdit
	extends AttachmentContainer
{
	/**
	* Add an attachment.
	* @param ref The attachment Reference.
	*/
	public void addAttachment(Reference ref);

	/**
	* Remove an attachment.
	* @param ref The attachment Reference to remove (the one removed will equal this, they need not be ==).
	*/
	public void removeAttachment(Reference ref);

	/**
	* Replace the attachment set.
	* @param attachments A ReferenceVector that will become the new set of attachments.
	*/
	public void replaceAttachments(ReferenceVector attachments);

	/**
	* Clear all attachments.
	*/
	public void clearAttachments();

}	// AttachmentContainerEdit

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/AttachmentContainerEdit.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
