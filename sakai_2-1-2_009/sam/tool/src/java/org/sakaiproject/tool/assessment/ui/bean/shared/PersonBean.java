/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/sam/tool/src/java/org/sakaiproject/tool/assessment/ui/bean/shared/PersonBean.java $
* $Id: PersonBean.java 1291 2005-08-19 17:05:33Z esmiley@stanford.edu $
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.ui.bean.shared;

import java.io.Serializable;

import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * <p> </p>
 * <p>Description: Person Bean with some properties</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $id: $
 */

public class PersonBean implements Serializable
{
  private static Log log = LogFactory.getLog(PersonBean.class);
  private String anonymousId;
 
  public PersonBean(){}
  {
  }

  public String getAgentString()
  {
    return AgentFacade.getAgentString();
  }

  public String getAnonymousId()
  {
    return anonymousId;
  }

  public String getId()
  {
    DeliveryBean delivery = (DeliveryBean) ContextUtil.lookupBean("delivery");
    if (delivery.getAnonymousLogin())
      return getAnonymousId();
    else
      return getAgentString();
  }

  public void setAnonymousId(String anonymousId)
  {
    this.anonymousId=anonymousId;
  }


  public boolean getIsAdmin()
  {
    if (("admin").equals(getAgentString()))
      return true;
    else
      return false;
  }

}