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

package org.sakaiproject.tool.assessment.jsf.tag;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *
 * <p> </p>
 * <p>Description:<br />
 * This class is the tag handler custom for color picker control</p>
 * <p>Based on example code by Sun Microsystems. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: DatePickerTag.java,v 1.6 2005/01/07 16:59:28 esmiley.stanford.edu Exp $
 */

public class DatePickerTag extends UIComponentTag
{
  private TagUtil util;

  private String size;
  private String value;

  /**
   * set size
   * @param size
   */
  public void setSize(String size)
  {
    this.size = size;
  }

  /**
   * set the value
   * @param value
   */
  public void setValue(String value)
  {
    this.value = value;
  }

  /**
   * get component type
   * @return component type
   */
  public String getComponentType()
  {
    return ("javax.faces.Input");
  }

  public String getRendererType()
  {
    return "DatePicker";
  }

  /**
   * Set the properties.
   * @param component
   */
  protected void setProperties(UIComponent component)
  {

    super.setProperties(component);

    FacesContext context = getFacesContext();
    System.out.println("value="+value);
    util.setString(component, "value", value);
    System.out.println("value="+value);
    util.setString(component, "size", size);
  }

  /**
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

}