/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/content/ContentTypeImageService.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.content;

// imports

/**
* <p>ContentTypeImageService is the Interface for looking up proper image based on the content type.  It also
* associates a display name and a set of file extensions to known types.</p>
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface ContentTypeImageService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = ContentTypeImageService.class.getName();

	/**
	* Get the image file name based on the content type.
	* @param contentType The content type string.
	* @return The image file name based on the content type.
	*/
	public String getContentTypeImage(String contentType);
	
	/**
	* Get the display name of the content type.
	* @param contentType The content type string.
	* @return The display name of the content type.
	*/
	public String getContentTypeDisplayName(String contentType);
	
	/**
	* Get the file extension value of the content type.
	* @param contentType The content type string.
	* @return The file extension value of the content type.
	*/
	public String getContentTypeExtension(String contentType);

	/**
	* Get the content type string that is used for this file extension.
	* @param extension The file extension (to the right of the dot, not including the dot).
	* @return The content type string that is used for this file extension.
	*/
	public String getContentType(String extension);

	/**
	* Is the type one of the known types used when the file type is unknown?
	* @param contentType The content type string to test.
	* @return true if the type is a type used for unknown file types, false if not.
	*/
	public boolean isUnknownType(String contentType);

}	// ContentTypeImageService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/content/ContentTypeImageService.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
