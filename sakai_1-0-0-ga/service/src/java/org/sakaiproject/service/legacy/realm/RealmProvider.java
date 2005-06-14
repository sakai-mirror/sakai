/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmProvider.java,v 1.1 2004/07/07 17:13:28 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.realm;

// imports
import java.util.Map;

/**
* <p>RealmProvider provides Abilities for a User to a Realm, as defined "elsewhere" in some way.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public interface RealmProvider
{
	/**
	* Access the Abilities for this particular user in the external realm.
	* @param realm The realm (to provide roles).
	* @param id The external realm id.
	* @param userId The user Id.
	* @return an Abilities object for this user (may be empty).
	*/
	public Abilities getAbilities(Realm realm, String id, String user);

	/**
	* Access the userId - Abilities Map for all know users.
	* @param realm The realm (to provide roles).
	* @param id The external realm id.
	* @return the userId - Abilities Map for all know users (may be empty).
	*/
	public Map getAbilities(Realm realm, String id);

}	// RealmProvider

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/RealmProvider.java,v 1.1 2004/07/07 17:13:28 ggolden.umich.edu Exp $
*
**********************************************************************************/
