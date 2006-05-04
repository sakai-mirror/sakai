/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/type-api/src/java/org/sakaiproject/api/common/type/UuidTypeResolvable.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: UuidTypeResolvable.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 * @since Sakai 2.0
 */
public interface UuidTypeResolvable
{
  /**
   * Provides an abstract way to retrieve objects from a Manager given only the 
   * object's UUID and Type.
   * 
   * @param uuid The universally unique identifier of the Object.
   * @param type The Type of the Object.
   * @return
   * @throws UnsupportedTypeException If the Manager does not support the passed
   * Type.
   */
  public Object getObject(String uuid, Type type)
      throws UnsupportedTypeException;
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/type-api/src/java/org/sakaiproject/api/common/type/UuidTypeResolvable.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
