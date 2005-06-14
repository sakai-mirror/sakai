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

/**
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author Rachel Gollub
 * @author Ed Smiley
 * @version $Id: AssessmentTemplateProperties.java,v 1.2 2005/01/05 01:11:41 esmiley.stanford.edu Exp $
 */
public interface AssessmentTemplateProperties
  extends AssessmentProperties
{
  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   * @param viewable DOCUMENTATION PENDING
   */
  public void setStudentViewable(String field, boolean viewable);

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isStudentViewable(String field);

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   * @param viewable DOCUMENTATION PENDING
   */
  public void setInstructorViewable(String field, boolean viewable);

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isInstructorViewable(String field);

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   * @param editable DOCUMENTATION PENDING
   */
  public void setInstructorEditable(String field, boolean editable);

  /**
   * DOCUMENTATION PENDING
   *
   * @param field DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isInstructorEditable(String field);
}
