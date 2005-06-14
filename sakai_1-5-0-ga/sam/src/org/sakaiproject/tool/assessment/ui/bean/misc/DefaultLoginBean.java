
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

import org.navigoproject.CompositeObjectUtil;

import java.io.Serializable;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: DefaultLoginBean.java,v 1.2 2004/10/01 15:56:55 esmiley.stanford.edu Exp $
 *
 * Used to be org.navigoproject.ui.web.login.DefaultLoginActionForm
 */
public class DefaultLoginBean implements Serializable
{
  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    return CompositeObjectUtil.printToString(this);
  }

  private String username;
  private String password;
  private String uniqueId;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param username DOCUMENTATION PENDING
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param password DOCUMENTATION PENDING
   */
  public void setPassword(String password)
  {
    this.password = password;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getUniqueId()
  {
    return uniqueId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param uniqueId DOCUMENTATION PENDING
   */
  public void setUniqueId(String uniqueId)
  {
    this.uniqueId = uniqueId;
  }
}
