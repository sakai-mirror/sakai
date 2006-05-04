/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/tag/InputFileUploadTag.java,v 1.1 2005/05/12 16:13:50 janderse.umich.edu Exp $
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


package org.sakaiproject.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;
import org.sakaiproject.jsf.util.TagUtil;

/**
 *
 * <p>Based on and modified from example code in  </p>
 * <p><i>Core JavaServer Faces</i>
 *  by Cay Horstmann, David Geary </p>
 * <p>Copyright:
 * Portions Copyright Sun Microsystems (c) 2005,
 * available for reuse.<br />
 * Copyright: Copyright  Sakai (c) 2005</p>
 * <p> </p>
 * @author Cay Horstmann,
 * @author David Geary
 * @author (modifications) Daisy Flemming
 * @author (modifications) Ed Smiley
 * @version $Id: InputFileUploadTag.java,v 1.1 2005/05/12 16:13:50 janderse.umich.edu Exp $
 */

public class InputFileUploadTag
  extends UIComponentTag 
{
  private String value;
  private String target;
  private String valueChangeListener;

  public void setValue(String newValue){ value = newValue;}
  public void setTarget(String newValue){ target = newValue;}
  public void setValueChangeListener(String newValue){ valueChangeListener = newValue;}

  public void setProperties(UIComponent component)
  {
    super.setProperties(component);
    TagUtil.setString(component, "target", target);
    TagUtil.setString(component, "value", value);
    TagUtil.setValueChangeListener(component, valueChangeListener);
  }

  public void release()
  {
    super.release();
    value = null;
    target = null;
    valueChangeListener = null;
  }

  public String getRendererType(){ return "org.sakaiproject.InputFileUpload";}
  public String getComponentType(){ return "javax.faces.Input";}
}
