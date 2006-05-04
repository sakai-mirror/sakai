/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/type-api/src/java/org/sakaiproject/api/common/type/TypeManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.type;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: TypeManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 * @since Sakai 2.0
 */
public interface TypeManager
{
  /**
   * Create a new Type.
   * 
   * @param authority
   * @param domain
   * @param keyword
   * @param displayName
   * @param description
   * @return
   */
  public Type createType(String authority, String domain, String keyword,
      String displayName, String description);

  /**
   * Retrieve Type by uuid.
   * 
   * @param uuid
   * @return
   */
  public Type getType(String uuid);

  /**
   * Retrieve a Type by the "tuple".
   * 
   * @param authority
   * @param domain
   * @param keyword
   * @return
   */
  public Type getType(String authority, String domain, String keyword);

  /**
   * Update persistent state of passed Type.
   * 
   * @param type
   */
  public void saveType(Type type);

  /**
   * Remove the persistent state of passed Type.
   * 
   * @param type
   * @throws UnsupportedOperationException Some (many) implementations will not
   *          support deleting Types.  Callers need to handle this condition
   *          gracefully.
   */
  public void deleteType(Type type) throws UnsupportedOperationException;
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/type-api/src/java/org/sakaiproject/api/common/type/TypeManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
