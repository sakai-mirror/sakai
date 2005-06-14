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
import org.sakaiproject.tool.assessment.ui.bean.questionpool.QuestionPoolBean;
import org.sakaiproject.tool.assessment.ui.bean.author.AssessmentBean;
import org.sakaiproject.tool.assessment.ui.bean.author.AnswerBean;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.dao.assessment.AnswerFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemMetaData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.ItemService;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;


import org.apache.log4j.Logger;
/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @version $Id: StartCreateItemListener.java,v 1.11 2005/02/01 01:02:34 lydial.stanford.edu Exp $
 */

public class StartCreateItemListener implements ValueChangeListener, ActionListener 
{
 static Logger LOG = Logger.getLogger(StartCreateItemListener.class.getName());
  private static ContextUtil cu;
  private String scalename;  // used for multiple choice Survey


  /**
   * Standard process action method.
   * @param ae ValueChangeEvent 
   * @throws AbortProcessingException
   */
  public void processValueChange(ValueChangeEvent ae) throws AbortProcessingException
  {
    LOG.info("StartCreateItemListener valueChangeLISTENER.");
    System.out.println("lydiatest BEGIN STartCreateItem processValueChange ");
    ItemAuthorBean itemauthorbean = (ItemAuthorBean) cu.lookupBean("itemauthor");

    FacesContext context = FacesContext.getCurrentInstance();

    System.out.println("lydiatest BEGIN STartCreateItem item type " + itemauthorbean.getItemType());


    String selectedvalue= (String) ae.getNewValue();
    System.out.println("lydiatest selectedvalue in StartCreateItemListener's process value change() =   " + selectedvalue);
    if ((selectedvalue!=null) && (!selectedvalue.equals("")) ){
      itemauthorbean.setItemType(selectedvalue);
      if (!startCreateItem(itemauthorbean))
      {
        throw new RuntimeException("failed to startCreatItem.");
      }
    }


  }

// TODO need to get rid of this, and figure out how selectonemenu can navigate to a diff page
  /**
   * Standard process action method.
   * @param ae ActionEvent 
   * @throws AbortProcessingException
   */
  public void processAction(ActionEvent ae) throws AbortProcessingException
  // used by question pool's selectQuestionType.jsp 
  {
    LOG.info("StartCreateItemListener actionLISTENER.");
    System.out.println("lydiatest BEGIN STartCreateItem processAction ");
    ItemAuthorBean itemauthorbean = (ItemAuthorBean) cu.lookupBean("itemauthor");

    System.out.println("lydiatest BEGIN STartCreateItem item type " + itemauthorbean.getItemType());


    if (!startCreateItem(itemauthorbean))
    {
      throw new RuntimeException("failed to startCreatItem.");
    }


  }

  
  public boolean startCreateItem(ItemAuthorBean itemauthorbean) {

   String nextpage= null;
   ItemBean item = new ItemBean();
   try{

System.out.println("lydiatest in method startCreateItem()  ");

    // check to see if we arrived here from question pool

    System.out.println("lydiatest we are adding to : "+ itemauthorbean.getTarget() );
    System.out.println("lydiatest we are adding itemtype : "+ itemauthorbean.getItemType() );

// need to get assessmentid
//  String assessmentId = ContextUtil.lookupParam("assessmentid");

// need to set indivdiual item properties
      itemauthorbean.setCurrentItem(item);

/*
 Use TypeFacade 's constants for item type,
  Note:   10 = import from question pool
  public static Long MULTIPLE_CHOICE = new Long(1);
  public static Long MULTIPLE_CORRECT = new Long(2); //not used
  public static Long MULTIPLE_CHOICE_SURVEY = new Long(3);
  public static Long TRUE_FALSE = new Long(4);
  public static Long ESSAY_QUESTION = new Long(5);
  public static Long FILE_UPLOAD = new Long(6);
  public static Long AUDIO_RECORDING = new Long(7);
  public static Long FILL_IN_BLANK = new Long(8);
  public static Long MATCHING = new Long(9);
*/

        item.setItemType(itemauthorbean.getItemType());
     
 
        itemauthorbean.setItemType("");
        itemauthorbean.setItemTypeString("");
        int itype=0; //
        if (item.getItemType()!=null) {
                itype = new Integer(item.getItemType()).intValue();
        }
    System.out.println("lydiatest selected which type : " + itype);
        switch (itype) {
                case 1:
                        item.setMultipleCorrect(Boolean.FALSE.booleanValue());
                        item.setMultipleCorrectString(TypeFacade.MULTIPLE_CHOICE.toString());
                        nextpage = "multipleChoiceItem";
    System.out.println("lydiatest multplecorrctstring should be false : " + item.getMultipleCorrectString());
                        break;
                case 2:
// never really use this, put here for completeness
                        item.setMultipleCorrect(Boolean.TRUE.booleanValue());
                        item.setMultipleCorrectString(TypeFacade.MULTIPLE_CORRECT.toString());
                        nextpage = "multipleChoiceItem";
                        break;
                case 3:
                        nextpage = "surveyItem";
                        break;
                case 4:
                        nextpage = "trueFalseItem";
                        break;
                case 5:
                        nextpage = "shortAnswerItem";
                        break;
               case 6:
                        nextpage = "fileUploadItem";
                        break;
                case 7:
                        nextpage = "audioRecItem";
                        break;
                case 8:
                        nextpage = "fillInBlackItem";
                        break;
                case 9:
System.out.println("lydiatest in Start creating a matching");
     			MatchItemBean matchitem = new MatchItemBean();

      			item.setCurrentMatchPair(matchitem);
      			item.setMatchItemBeanList(new ArrayList());
                        nextpage = "matchingItem";
                        break;
                case 10:
System.out.println("lydiatest in Start creating Import question from pool ");
    			QuestionPoolBean qpoolBean= (QuestionPoolBean) cu.lookupBean("questionpool");
			qpoolBean.setImportToAuthoring(true);
                        nextpage = "poolList";
                        break;
        }
   }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new Error(e);
    }

// check for metadata settings
    if ("assessment".equals(itemauthorbean.getTarget())) {
      AssessmentService assessdelegate = new AssessmentService();
      AssessmentBean assessmentBean = (AssessmentBean) cu.lookupBean("assessmentBean");
      AssessmentFacade assessment = assessdelegate.getAssessment(assessmentBean.getAssessmentId());
      itemauthorbean.setShowMetadata(assessment.getMetaDataQuestions_isInstructorEditable());

      System.out.println("lydiatest showMetadata : " + itemauthorbean.getShowMetadata());
      // set section 

        if (itemauthorbean.getInsertToSection()!=null) {
       // for inserting an item, this should be sequence, e.g. 1, 2, ...etc
      System.out.println("lydiatest itemauthorbean.getInsertToSection not null : " + itemauthorbean.getInsertToSection());
	  String sectionid= (assessment.getSection(new Long(itemauthorbean.getInsertToSection()))).getSectionId().toString();
          item.setSelectedSection(sectionid);
      System.out.println("lydiatest itemauthorbean.getInsertToSection not null : " + item.getSelectedSection());
        }
        else {
       // for adding an item, add to the default section, sequence = 1 
          SectionDataIfc section = assessment.getDefaultSection();  
          String sectionid = section.getSectionId().toString();
          item.setSelectedSection(sectionid);
      System.out.println("lydiatest itemauthorbean.getInsertToSection IS null : " + item.getSelectedSection());
        }

	// reset insertToSection to null;
        itemauthorbean.setInsertToSection(null);


        if (item.getItemType().equals("10")) {
    	  QuestionPoolBean qpoolBean= (QuestionPoolBean) cu.lookupBean("questionpool");
	  qpoolBean.setSelectedSection(item.getSelectedSection());
          System.out.println("lydiatest selecte section in pool : " + qpoolBean.getSelectedSection());
        }
      System.out.println("lydiatest selecte section : " + item.getSelectedSection());
     
    }
    else {
     // for question pool , always show metadata as default
      itemauthorbean.setShowMetadata("true");
      System.out.println("lydiatest showMetadata : " + itemauthorbean.getShowMetadata());
    }


    // set outcome for action
    itemauthorbean.setOutcome(nextpage);
    System.out.println("lydiatest selected next page : " + nextpage);
    return true;

  }






   
}
