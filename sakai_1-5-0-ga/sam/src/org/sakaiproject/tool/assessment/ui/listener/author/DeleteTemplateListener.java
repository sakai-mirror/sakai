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

package org.sakaiproject.tool.assessment.ui.listener.author;

import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;

import org.apache.log4j.Logger;
import osid.shared.SharedManager;


/**
 * <p>Description: Action Listener for deletion of template.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: DeleteTemplateListener.java,v 1.6 2004/11/18 06:19:21 rgollub.stanford.edu Exp $
 */

public class DeleteTemplateListener extends TemplateBaseListener implements ActionListener
{
  boolean isTemplate = true;
  static Logger LOG = Logger.getLogger(DeleteTemplateListener.class.getName());

  public void processAction(ActionEvent ae) throws AbortProcessingException
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map reqMap = context.getExternalContext().getRequestMap();
    Map requestParams = context.getExternalContext().getRequestParameterMap();
    LOG.info("DELETE TEMPLATE LISTENER.");
//    LOG.info("debugging ActionEvent: " + ae);
//    LOG.info("debug requestParams: " + requestParams);
//    LOG.info("debug reqMap: " + reqMap);
    String deleteId = this.lookupTemplateBean(context).getIdString();
    if(!deleteTemplate(deleteId))
    {
      // todo: define package specific RuntimeException
      throw new RuntimeException("Cannot delete template.");
    }

  }

  /**
   * This deletes a template with all its associated parts, items, etc..
   *
   * @param session DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean deleteTemplate(String deleteId)
  {
    LOG.info("deleteTemplate (" + deleteId + ")");

    try
    {
      AssessmentService delegate = new AssessmentService();
      delegate.deleteAssessmentTemplate(new Long(deleteId));

      return true;
    }
    catch(Exception e)
    {
      e.printStackTrace();

      return false;
    }
  }

}
