/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/content/ContentResource.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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

// import
import java.io.InputStream;

import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>ContentResource is the core interface for a Resource object in the GenericContentHostingService.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
* @see org.chefproject.service.generic.GenericContentHostingService
* @see org.chefproject.core.Resource
*/
public interface ContentResource
	extends Resource
{
	/**
	* Access the content byte length.
	* @return The content byte length.
	*/
	public int getContentLength();

	/**
	* Access the resource MIME type.
	* @return The resource MIME type.
	*/
	public String getContentType();

	/**
	* Access an array of the bytes of the resource.
	* @return An array containing the bytes of the resource's content.
	*/
	public byte[] getContent();

	/**
	 * Access the content as a stream.
	 * Please close the stream when done as it may be holding valuable system resources.
	 * @return an InputStream through which the bytes of the resource can be read.
	 * @throws ServerOverloadException if the server cannot produce the content stream at this time.
	 */
	public InputStream streamContent() throws ServerOverloadException;

}	// ContentResource

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/content/ContentResource.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
