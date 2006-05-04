
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

package org.sakaiproject.tool.assessment.ui.bean.misc;

import java.io.Serializable;

import org.navigoproject.CompositeObjectUtil;

/**
 *
 * <p>Title: sakaiproject.org</p>
 * <p>Description: OKI based implementation</p>
* <p>Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation</p>
 *
 * Used to be org.navigoproject.ui.web.messaging.DisplayMessageForm
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: DisplayMessageBean.java,v 1.2 2004/10/01 15:56:55 esmiley.stanford.edu Exp $
 */
public class DisplayMessageBean implements Serializable
{
  private String submitDisplayMessage;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    return CompositeObjectUtil.printToString(this);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param submitDisplayMessage DOCUMENTATION PENDING
   */
  public void setSubmitDisplayMessage(String submitDisplayMessage)
  {
    this.submitDisplayMessage = submitDisplayMessage;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmitDisplayMessage()
  {
    return submitDisplayMessage;
  }
}