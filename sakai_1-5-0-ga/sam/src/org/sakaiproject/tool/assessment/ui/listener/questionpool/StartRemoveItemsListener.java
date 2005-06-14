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

package org.sakaiproject.tool.assessment.ui.listener.questionpool;

import javax.faces.component.UICommand;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.context.FacesContext;
import java.util.*;

import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;


// from navigo
import org.sakaiproject.tool.assessment.business.AAMTree;
import org.sakaiproject.tool.assessment.business.questionpool.QuestionPoolTreeImpl;
import org.sakaiproject.tool.assessment.services.QuestionPoolService;
import org.sakaiproject.tool.assessment.services.ItemService;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacade;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.QuestionPoolIteratorFacade;

import org.sakaiproject.tool.assessment.ui.bean.questionpool.QuestionPoolBean;
import org.sakaiproject.tool.assessment.ui.bean.questionpool.QuestionPoolDataBean;

import org.sakaiproject.tool.assessment.facade.AgentFacade;


import org.apache.log4j.Logger;
/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @version $Id: StartRemoveItemsListener.java,v 1.2 2005/02/12 09:18:54 lydial.stanford.edu Exp $
 */

public class StartRemoveItemsListener implements ActionListener
{
 static Logger LOG = Logger.getLogger(StartRemoveItemsListener.class.getName());
  private static ContextUtil cu;


  /**
   * Standard process action method.
   * @param ae ActionEvent
   * @throws AbortProcessingException
   */
  public void processAction(ActionEvent ae) throws AbortProcessingException
  {
    LOG.info("StartRemoveItemsListener:");
    QuestionPoolBean  qpoolbean= (QuestionPoolBean) cu.lookupBean("questionpool");
    if (!startRemoveItems(qpoolbean))
    {
      throw new RuntimeException("failed to populateItemBean.");
    }

  }


public boolean startRemoveItems(QuestionPoolBean qpoolbean){
// used by the editPool.jsp, to remove one or more items
    //System.out.println("lydiatest in startRemoveQuestions");
	try {
      String itemId= "";
  
        ArrayList destItems= ContextUtil.paramArrayValueLike("removeCheckbox");

        if (destItems.size() > 0) {
                // only go to remove confirmatin page when at least one  checkbox is checked

        List items= new ArrayList();
        Iterator iter = destItems.iterator();
        while(iter.hasNext())
        {

        itemId = (String) iter.next();

        ItemService delegate = new ItemService();
        ItemFacade itemfacade= delegate.getItem(new Long(itemId), AgentFacade.getAgentString());
        items.add(itemfacade);

        }

        qpoolbean.setItemsToDelete(items);
        qpoolbean.setOutcome("removeQuestionFromPool");
        }
        else {
         // otherwise go to poollist
        qpoolbean.setOutcome("poolList");
        }
	}
	catch (Exception e) { 
		e.printStackTrace();
		return false;
	}	
	

	return true;
  }

   
}
