/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/memory/Cacher.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.memory;

/**
 * <p>Cacher is an interface for any object that uses memory caches.</p>
 * <p>Cachers may be asked to clear their caches to free up memory or re-sync with external stores.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public interface Cacher
{
	/**
	 * Clear out as much as possible anything cached; re-sync any cache that is needed to be kept.
	 */
	void resetCache();

	/**
	 * Return the size of the cacher - indicating how much memory in use.
	 * @return The size of the cacher.
	 */
	long getSize();

	/**
	 * Return a description of the cacher.
	 * @return The cacher's description.
	 */
	String getDescription();	
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/memory/Cacher.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
