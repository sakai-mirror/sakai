/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.api.common.edu.person;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.api.common.type.Type;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public interface SakaiPersonManager
{
  /**
   * Creates a persistent SakaiPerson record.
   *  
   * @param agentUuid
   * @param recordType {@link #getSystemMutableType()} or 
   *        {@link #getUserMutableType()} 
   * @return
   */
  public SakaiPerson create(String agentUuid, Type recordType);

  /**
   * Creates a persistent SakaiPerson record.
   *  
   * @param agentUuid
   * @param uid e.g. username
   * @param recordType {@link #getSystemMutableType()} or 
   *        {@link #getUserMutableType()} 
   * @return
   */
  public SakaiPerson create(String agentUuid, String uid, Type recordType);

  /**
   * Get a new instantiation of an empty SakaiPerson object (has no persistent 
   * state). For example, useful if you query-by-example finder method.
   * 
   * @return
   */
  public SakaiPerson getPrototype();

  /**
   * Retrieve SakaiPerson by uid (username).
   * 
   * @param uid username
   * @return List of SakaiPerson objects incuding both system and user mutable
   *          Types.
   */
  public List findSakaiPersonByUid(String uid);

  /**
   * Query-by-Example finder signature.
   * 
   * @param queryByExample A SakaiPerson protoype. All non-null preoperties will
   *        be searched using a logical AND.
   * 
   * @return
   */
  public List findSakaiPerson(SakaiPerson queryByExample);

  /**
   * Assumes current user.  If you would like to specify the user, see
   * {@link #findSakaiPerson(String, Type)}.
   * 
   * @param recordType
   *  See {@link #getSystemMutableType()} or {@link #getUserMutableType()}.
   * @return
   */
  public SakaiPerson getSakaiPerson(Type recordType);

  /**
   * Find all SakaiPerson objects with specified type. Types should be obtained 
   * through the Type constant getter methods.
   * 
   * @param agent
   * @param recordType
   *  See {@link #getSystemMutableType()} or {@link #getUserMutableType()}.
   * @return
   */
  public SakaiPerson getSakaiPerson(String agentUuid, Type recordType);

  /**
   * Returns the userMutableType constant. SakaiPerson's of this Type allow the
   * user to modify all attributes.
   * 
   * @return
   */
  public Type getUserMutableType();

  /**
   * Returns the systemMutableType constant. SakaiPerson's of this Type can only
   * be modified by the "system", i.e. not the end user, and would normally
   * consist of enterprise data (e.g. LDAP, etc).
   * 
   * @return
   */
  public Type getSystemMutableType();

  /**
   * Save or update the SakaiPerson bean.
   * 
   * @param sakaiPerson
   */
  public void save(SakaiPerson sakaiPerson);

  /**
   * Removes SakaiPerson from persistent state.
   * 
   * @param sakaiPerson
   */
  public void delete(SakaiPerson sakaiPerson);

  /**
   * Search the "common" SakaPerson fields for a given String.
   * 
   * @param simpleSearchCriteria String used to search for SakaiPerson objects
   *        where the following properties are like this String: 
   *        uid, givenName, surname.
   * @return
   */
  public List findSakaiPerson(String simpleSearchCriteria);

  /**
   * Composite call to determine if a Set of Agents have the FERPA flag
   * enabled.
   * 
   * @param agentUuids
   * @return A Map keyed by agentUuid with a Boolean as the value. TRUE == 
   * FERPA enabled.
   */
  public Map isFerpaEnabled(Set agentUuids);

}
