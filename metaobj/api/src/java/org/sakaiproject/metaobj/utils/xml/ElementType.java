/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.utils.xml;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 25, 2004
 * Time: 3:26:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ElementType {
   public Class getObjectType();

   public int getLength();

   public int getMaxLength();

   public int getMinLength();

   public String getDefaultValue();

   public String getFixedValue();

   public List getEnumeration();

   /**
    * @return A regular expression that expresses a constraint on legal value(s) for the element.
    */
   public Pattern getPattern();

   /**
    * @return ValueRange object describing the type's range.
    *         Objects within this range will be of the class
    *         getObjectType().  This is null if there is no restriction
    *         on the value's range
    */
   public ValueRange getRange();

   /**
    * @return the base xml type of the associated node
    */
   public String getBaseType();

}
