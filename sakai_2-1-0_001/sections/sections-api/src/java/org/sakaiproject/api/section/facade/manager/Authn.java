/**********************************************************************************
*
* $Id: Authn.java 637 2005-07-15 16:35:46Z jholtzman@berkeley.edu $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The Regents of the University of Michigan,
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

package org.sakaiproject.api.section.facade.manager;

/**
 * The service interface for the authentication mechanism.
 * 
 * @author <a href="jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface Authn {
	/**
	 * Gets the current user's uuid.
	 * 
	 * @param request The object containing the context information.  This will
	 * usually be something like a HttpServletRequest.  In Sakai, the object
	 * can be null.
	 * 
	 * @return
	 */
    public String getUserUid(Object request);
}


/**************************************************************************************************************************************************************************************************************************************************************
 * $Id: Authn.java 637 2005-07-15 16:35:46Z jholtzman@berkeley.edu $
 *************************************************************************************************************************************************************************************************************************************************************/