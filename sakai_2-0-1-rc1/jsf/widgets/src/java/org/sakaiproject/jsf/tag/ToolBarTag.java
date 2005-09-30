/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/tag/ToolBarTag.java,v 1.2 2005/05/10 02:46:19 esmiley.stanford.edu Exp $
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

import javax.faces.webapp.UIComponentTag;

/**
 */
public class ToolBarTag extends UIComponentTag
{
  public String getComponentType()
  {
    return "org.sakaiproject.ToolBar";
  }

  public String getRendererType()
  {
    return "org.sakaiproject.ToolBar";
  }
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/tag/ToolBarTag.java,v 1.2 2005/05/10 02:46:19 esmiley.stanford.edu Exp $
*
**********************************************************************************/
