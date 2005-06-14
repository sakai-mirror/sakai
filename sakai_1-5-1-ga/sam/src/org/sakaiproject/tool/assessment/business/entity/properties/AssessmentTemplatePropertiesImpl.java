
/*
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
 */


package org.sakaiproject.tool.assessment.business.entity.properties;
import java.util.Hashtable;

import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

/**
 * <p>An implementation of the Assessment Template properties. This determines the
 * permissions for each template field.</p>
  * <p>Copyright: Copyright (c) 2005</p>
  * <p>Organization: Sakai Project</p>
  * @author Rachel Gollub
  * @author Ed Smiley
  * @version $Id: AssessmentTemplatePropertiesImpl.java,v 1.3 2005/01/05 01:11:41 esmiley.stanford.edu Exp $
  */
public class AssessmentTemplatePropertiesImpl
  extends AssessmentPropertiesImpl
  implements AssessmentTemplateProperties
{
  private Hashtable studentViewable;
  private Hashtable instructorViewable;
  private Hashtable instructorEditable;

  /**
   * Creates a new AssessmentTemplatePropertiesImpl object.
   */
  public AssessmentTemplatePropertiesImpl()
  {
    studentViewable = new Hashtable();
    instructorViewable = new Hashtable();
    instructorEditable = new Hashtable();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   * @param viewable DOCUMENTATION PENDING
   */
  public void setStudentViewable(String field, boolean viewable)
  {
    studentViewable.put(field, new Boolean(viewable));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isStudentViewable(String field)
  {
    Boolean viewable = (Boolean) studentViewable.get(field);
    if(viewable == null)
    {
      return false;
    }

    return viewable.booleanValue();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   * @param viewable DOCUMENTATION PENDING
   */
  public void setInstructorViewable(String field, boolean viewable)
  {
    instructorViewable.put(field, new Boolean(viewable));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isInstructorViewable(String field)
  {
    Boolean viewable = (Boolean) instructorViewable.get(field);
    if(viewable == null)
    {
      return false;
    }

    return viewable.booleanValue();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   * @param editable DOCUMENTATION PENDING
   */
  public void setInstructorEditable(String field, boolean editable)
  {
    instructorEditable.put(field, new Boolean(editable));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isInstructorEditable(String field)
  {
    Boolean editable = (Boolean) instructorEditable.get(field);
    if(editable == null)
    {
      return false;
    }

    return editable.booleanValue();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Hashtable getInstructorEditableMap()
  {
    return instructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Hashtable getInstructorViewableMap()
  {
    return instructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Hashtable getStudentViewableMap()
  {
    return studentViewable;
  }


}
