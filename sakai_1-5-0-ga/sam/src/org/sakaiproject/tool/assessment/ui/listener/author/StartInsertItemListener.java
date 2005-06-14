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

import javax.faces.component.UICommand;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.event.ActionListener;
import javax.faces.context.FacesContext;
import java.util.*;

import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemBean;
import org.sakaiproject.tool.assessment.ui.bean.author.MatchItemBean;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemAuthorBean;
import org.sakaiproject.tool.assessment.ui.bean.author.AnswerBean;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.dao.assessment.AnswerFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemMetaData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.ItemService;


import org.apache.log4j.Logger;
/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @version $Id: StartInsertItemListener.java,v 1.5 2005/02/01 01:01:37 lydial.stanford.edu Exp $
 */

public class StartInsertItemListener implements ValueChangeListener 
{
 static Logger LOG = Logger.getLogger(StartInsertItemListener.class.getName());


  /**
   * Standard process action method.
   * @param ae ValueChangeEvent 
   * @throws AbortProcessingException
   */
  public void processValueChange(ValueChangeEvent ae) throws AbortProcessingException
  {
    LOG.info("StartInsertItemListener valueChangeLISTENER.");
    System.out.println("lydiatest BEGIN StartInsertItemListener processValueChange ------  ");
    ItemAuthorBean itemauthorbean = (ItemAuthorBean) ContextUtil.lookupBean("itemauthor");

    String olditemtype = (String) ae.getOldValue();	
    System.out.println("lydiatest ae.getOldValue : " + olditemtype );
    String selectedvalue= (String) ae.getNewValue();	
    System.out.println("lydiatest ae.getNewValue : " + selectedvalue);
    String newitemtype = null;
    String insertItemPosition = null;
    String insertToSection = null;

    // only set itemtype when the value has indeed changed.
    if ((selectedvalue!=null) && (!selectedvalue.equals("")) ){
      String[] strArray = selectedvalue.split(",");
    System.out.println("lydiatest ae.getNewValue [0] : " + strArray[0]);
    System.out.println("lydiatest ae.getNewValue [1] : " + strArray[1]);
    System.out.println("lydiatest ae.getNewValue [2] : " + strArray[2]);
      newitemtype = strArray[0].trim();
      insertToSection = strArray[1].trim();
      insertItemPosition= strArray[2].trim();
      itemauthorbean.setItemType(newitemtype);
      itemauthorbean.setInsertToSection(insertToSection);
      itemauthorbean.setInsertPosition(insertItemPosition);
      itemauthorbean.setInsertType(newitemtype);

    System.out.println("lydiatest StartInsertItem item type " + itemauthorbean.getItemType());
    System.out.println("lydiatest STartInsertItem param insertItemPosition " + insertItemPosition);
    System.out.println("lydiatest STartInsertItem param insert to Section " + insertToSection);


    StartCreateItemListener listener = new StartCreateItemListener();

    if (!listener.startCreateItem(itemauthorbean))
    {
      throw new RuntimeException("failed to startCreatItem.");
    }


    }

  }

  
}
