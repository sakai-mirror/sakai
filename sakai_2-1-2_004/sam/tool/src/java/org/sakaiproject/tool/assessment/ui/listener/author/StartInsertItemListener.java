/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.assessment.ui.listener.author;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemAuthorBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import java.util.List;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;

/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @version $Id$
 */

public class StartInsertItemListener implements ValueChangeListener
{
    private static Log log = LogFactory.getLog(StartInsertItemListener.class);


  /**
   * Standard process action method.
   * @param ae ValueChangeEvent
   * @throws AbortProcessingException
   */
  public void processValueChange(ValueChangeEvent ae) throws AbortProcessingException
  {
    log.info("StartInsertItemListener valueChangeLISTENER.");
    ItemAuthorBean itemauthorbean = (ItemAuthorBean) ContextUtil.lookupBean("itemauthor");

    String olditemtype = (String) ae.getOldValue();
    String selectedvalue= (String) ae.getNewValue();
    String newitemtype = null;
    String insertItemPosition = null;
    String insertToSection = null;

    // only set itemtype when the value has indeed changed.
    if ((selectedvalue!=null) && (!selectedvalue.equals("")) ){
      String[] strArray = selectedvalue.split(",");

      try
      {
        newitemtype = strArray[0].trim();
        ///////// SAK-3114: ///////////////////////////////////////////
        // note: you must include at least one selectItem in the form
        // type#,p#,q#
        // the rest, in a selectItems will get the p#,q# added in.
        ///////////////////////////////////////////////////////////////
        if (strArray.length < 2)
        {
          UISelectOne comp = (UISelectOne) ae.getComponent();
          List children = comp.getChildren();
          // right now there are two kids selectItems & selectItem
          // we use loop to keep this flexible
          for (int i = 0; i < children.size(); i++) {

            if (children.get(i) instanceof UISelectItem)
            {
              UISelectItem selectItem = (UISelectItem) children.get(i);
//              log.info("***" + i + "***");
              log.info("selectItem.getItemValue()="+selectItem.getItemValue());
              String itemValue =  (String) selectItem.getItemValue();
              String[] insertArray = itemValue.split(",");
              // add in p#,q#
              insertToSection = insertArray[1].trim();
              // SAK-3160: workaround
              /*
               It seems very difficult to track down why the sequence number is
               getting lost in the JSF lifecycle in the nested lists in the
               backing beans (it appears) when there is more than one part.

               Therefore I have added a workaround fix that supplies 0 for the
               sequence number if it is not available.

               This fixes two things.
                1. No error or warning is logged
                2. The type of item chosen is retained, and in the correct part.
                */

              if (insertArray.length > 2)
              {
                insertItemPosition = insertArray[2].trim();
              }
              else
              {
                insertItemPosition = "0";
              }
              break;
            }
          }
        }
        else
        {
          insertToSection = strArray[1].trim();
          insertItemPosition = strArray[2].trim();
        }
      }
      catch (Exception ex)
      {
        log.warn("unable to process value change: " + ex);
//        ex.printStackTrace();
        return;
      }
      itemauthorbean.setItemType(newitemtype);
      itemauthorbean.setInsertToSection(insertToSection);
      itemauthorbean.setInsertPosition(insertItemPosition);
      itemauthorbean.setInsertType(newitemtype);
      itemauthorbean.setItemNo(String.valueOf(Integer.parseInt(insertItemPosition) +1));

    StartCreateItemListener listener = new StartCreateItemListener();

    if (!listener.startCreateItem(itemauthorbean))
    {
      throw new RuntimeException("failed to startCreatItem.");
    }


    }

  }


}
