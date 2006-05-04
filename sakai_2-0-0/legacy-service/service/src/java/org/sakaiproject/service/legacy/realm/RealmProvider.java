/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/realm/RealmProvider.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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
* <p>RealmProvider provides Role for Users in a Realm, as defined "elsewhere" in some way.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public interface RealmProvider
{
	/**
	 * Access the Role name for this particular user in the external realm.
	 * @param id The external realm id.
	 * @param userId The user Id.
	 * @return the Role name for this particular user in the external realm, or null if none.
	 */
	String getRole(String id, String user);

	/**
	 * Access the userId - Role name map for all users in the external realm.
	 * @param id The external realm id.
	 * @return the userId - Role name map for all users in the external realm (may be empty).
	 */
	Map getUserRolesForRealm(String id);

	/**
	 * Access the external realm id - Role name map for this users in all external realms.
	 * If the provider supports compound external ids, the get() of this Map needs to accept
	 * a compount external id and return the single role (if any) resolved from any individual
	 * external realm ids in the map.
	 * @param userId The user id.
	 * @return the the external realm id - Role name map for this users in all external realms. (may be empty).
	 */
	Map getRealmRolesForUser(String userId);
	
	/**
	 * Unpack a possibly compound id into it's component ids, returning at least the id unchanged if not compound.
	 * @param id The external realm id.
	 * @return a String array of one or more ids upacked from this possibly compound id.
	 */
	String[] unpackId(String id);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/realm/RealmProvider.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
