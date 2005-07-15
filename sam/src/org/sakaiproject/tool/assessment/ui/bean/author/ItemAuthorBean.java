/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.assessment.ui.bean.author;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacade;
import org.sakaiproject.tool.assessment.facade.SectionFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.services.ItemService;
import org.sakaiproject.tool.assessment.services.QuestionPoolService;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.SectionContentsBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;


//import org.osid.shared.*;


/**
 * Backing bean for Item Authoring, uses ItemBean for UI
 * $Id$
 */
public class ItemAuthorBean
  implements Serializable
{
  private static Log log = LogFactory.getLog(ItemAuthorBean.class);

  private static String filename = "/org/sakaiproject/tool/assessment/bundle/AuthorMessages.properties";
  // internal use
  private static final String answerNumbers =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 8266438770394956874L;

  public final static String FROM_QUESTIONPOOL= "questionpool";
  public final static String FROM_ASSESSMENT= "assessment";
  private String assessTitle;
  private String sectionIdent;
  private ArrayList assessmentSectionIdents;
  private String insertPosition;
  private String insertToSection;
  private String insertType;
  private String assessmentID;
  private String currentSection;
//  private String itemId;
  private String itemNo;
  private String itemType;
  private String itemTypeString;  // used for inserting a question
  private String showMetadata;
  private String showOverallFeedback;
  private String showQuestionLevelFeedback;
  private String showSelectionLevelFeedback;
  private ArrayList trueFalseAnswerSelectList;
  private ItemBean currentItem;
  private ItemFacade itemToDelete;
  private ItemFacade itemToPreview;

  // for questionpool
  private String qpoolId;
  private String target;
  private ArrayList poolListSelectItems;

  // for item editing

  private boolean[] choiceCorrectArray;
  private String maxRecordingTime;
  private String maxNumberRecordings;
  private String scaleName;
  private String[] matches;
  private String[] matchAnswers;
  private String[] matchFeedbackList;
  private String[] answerFeedbackList;

  // for navigation
  private String outcome;
  /**
   * Creates a new ItemAuthorBean object.
   */
  public ItemAuthorBean()
  {

  }

   public void setCurrentItem(ItemBean item)
   {
	this.currentItem=item;
   }

  public ItemBean getCurrentItem()
  {
    return currentItem;
  }

  /**
   * @return
   */
  public String getAssessTitle()
  {
    return assessTitle;
  }


  /**
   * @param string
   */
  public void setAssessTitle(String string)
  {
    assessTitle = string;
  }


  /**
   * @return
   */
  public String getSectionIdent()
  {
    return sectionIdent;
  }

  /**
   * @param string
   */
  public void setSectionIdent(String string)
  {
    sectionIdent = string;
  }

  /**
   * @return
   */
  public ArrayList getAssessmentSectionIdents()
  {
    return assessmentSectionIdents;
  }

  /**
   * @param list
   */
  public void setAssessmentSectionIdents(ArrayList list)
  {
    assessmentSectionIdents = list;
  }

  /**
   * @return
   */
  public String getInsertPosition()
  {
    return insertPosition;
  }

  /**
   * @param string
   */
  public void setInsertPosition(String string)
  {
    insertPosition = string;
  }
  /**
   * @return
   */
  public String getInsertToSection()
  {
    return insertToSection;
  }

  /**
   * @param string
   */
  public void setInsertToSection(String string)
  {
    insertToSection= string;
  }


  /**
   * @return
   */
  public String getInsertType()
  {
    return insertType;
  }

  /**
   * @param string
   */
  public void setInsertType(String string)
  {
    insertType= string;
  }

  /**
   * @return
   */
  public String getAssessmentID()
  {
    return assessmentID;
  }

  /**
   * @param string
   */
  public void setAssessmentID(String string)
  {
    assessmentID = string;
  }

  /**
   * @return
   */
  public String getCurrentSection()
  {
    return currentSection;
  }

  /**
   * @param string
   */
  public void setCurrentSection(String string)
  {
    currentSection = string;
  }
  /**
   * @return
   */
  public String getItemNo()
  {
    return itemNo;
  }


  /**
   * @param string
   */
  public void setItemNo(String string)
  {
    itemNo = string;
  }


  /**
   * @return

  public String getItemId()
  {
    return itemId;
  }

   */

  /**
   * @param string
  public void setItemId(String string)
  {
    itemId= string;
  }
   */

  /**
   * @return
   */
  public String getItemType()
  {
    return itemType;
  }

  /**
   * @param string
   */
  public void setItemType(String string)
  {
    itemType = string;
  }

  /**
   * @return
   */
  public String getItemTypeString()
  {
    return itemTypeString;
  }

  /**
   * @param string
   */
  public void setItemTypeString(String string)
  {
    itemTypeString = string;
  }

  /**
   * @return
   */
  public String getShowMetadata()
  {
    return showMetadata;
  }

  /**
   * @param string
   */
  public void setShowMetadata(String string)
  {
    showMetadata = string;
  }

  /**
   * @return
   */
  public String getShowOverallFeedback()
  {
    return showOverallFeedback;
  }

  /**
   * @return
   */
  public String getShowQuestionLevelFeedback()
  {
    return showQuestionLevelFeedback;
  }

  /**
   * @return
   */
  public String getShowSelectionLevelFeedback()
  {
    return showSelectionLevelFeedback;
  }

  /**
   * @param string
   */
  public void setShowOverallFeedback(String string)
  {
    showOverallFeedback = string;
  }

  /**
   * @param string
   */
  public void setShowQuestionLevelFeedback(String string)
  {
    showQuestionLevelFeedback = string;
  }

  /**
   * @param string
   */
  public void setShowSelectionLevelFeedback(String string)
  {
    showSelectionLevelFeedback = string;
  }

  /**
   * @param string
   */
  public void setQpoolId(String string)
  {
    qpoolId= string;
  }

  /**
   * @return
   */
  public String getQpoolId()
  {
    return qpoolId;
  }


  /**
   * @param string
   */
  public void setItemToDelete(ItemFacade param)
  {
    itemToDelete= param;
  }

  /**
   * @return
   */
  public ItemFacade getItemToDelete()
  {
    return itemToDelete;
  }

  /**
   * @param string
   */
  public void setItemToPreview(ItemFacade param)
  {
    itemToPreview= param;
  }

  /**
   * @return
   */
  public ItemFacade getItemToPreview()
  {
    return itemToPreview;
  }

  /**
   * @param string
   */
  public void setTarget(String string)
  {
    target= string;
  }

  /**
   * @return
   */
  public String getTarget()
  {
    return target;
  }

  /**
   * This is an array of correct/not correct flags
   * @return the array of correct/not correct flags
   */
  public boolean[] getChoiceCorrectArray()
  {
    return choiceCorrectArray;
  }

  /**
   * set array of correct/not correct flags
   * @param choiceCorrectArray of correct/not correct flags
   */
  public void setChoiceCorrectArray(boolean[] choiceCorrectArray)
  {
    this.choiceCorrectArray = choiceCorrectArray;
  }

  /**
   * is  the nth choice correct?
   * @param n
   * @return
   */
  public boolean isCorrectChoice(int n)
  {
    return choiceCorrectArray[n];
  }

  /**
   * set the nth choice correct?
   * @param n
   * @param correctChoice true if it is
   */
  public void setCorrectChoice(int n, boolean correctChoice)
  {
    this.choiceCorrectArray[n] = correctChoice;
  }
  /**
   * for audio recording
   * @return maximum time for recording
   */
  public String getMaxRecordingTime()
  {
    return maxRecordingTime;
  }
  /**
   * for audio recording
   * @param maxRecordingTime maximum time for recording
   */
  public void setMaxRecordingTime(String maxRecordingTime)
  {
    this.maxRecordingTime = maxRecordingTime;
  }
  /**
   * for audio recording
   * @return maximum attempts
   */
  public String getMaxNumberRecordings()
  {
    return maxNumberRecordings;
  }

  /**
   * set audio recording maximum attempts
   * @param maxNumberRecordings
   */
  public void setMaxNumberRecordings(String maxNumberRecordings)
  {
    this.maxNumberRecordings = maxNumberRecordings;
  }

  /**
   * for survey
   * @return the scale
   */
  public String getScaleName()
  {
    return scaleName;
  }

  /**
   * set the survey scale
   * @param scaleName
   */
  public void setScaleName(String scaleName)
  {
    this.scaleName = scaleName;
  }

  public String getOutcome()
  {
    return outcome;
  }

  /**
   * set the survey scale
   * @param param
   */
  public void setOutcome(String param)
  {
    this.outcome= param;
  }


  /**
   * Maching only.
   * Get an array of match Strings.
   * @return array of match Strings.
   */
  public String[] getMatches() {
    return matches;
  }

  /**
   * Maching only.
   * Set array of match Strings.
   * @param matches array of match Strings.
   */
  public void setMatches(String[] matches) {
    this.matches = matches;
  }

  /**
   * Maching only.
   * Get the nth match String.
   * @param n
   * @return the nth match String
   */
  public String getMatch(int n) {
    return matches[n];
  }

  /**
   * Maching only.
   * Set the nth match String.
   * @param n
   * @param match
   */
  public void setMatch(int n, String match) {
    matches[n] = match;
  }

  /**
   * get 1, 2, 3... for each match
   * @param n
   * @return
   */
  public int[] getMatchCounter()
  {
    int n = matches.length;
    int count[] = new int[n];
    for (int i = 0; i < n; i++)
    {
      count[i] = i;
    }
    return count;
  }

  /**
   * Derived property.
   * @return ArrayList of model SelectItems
   */

  public ArrayList getTrueFalseAnswerSelectList() {
    ArrayList list = new ArrayList();

    String trueprop= "True";
    String falseprop= "False";
    String[] answerValues = {"true", "false"};  // not to be displayed in the UI
    String[] answerLabelText= {trueprop, falseprop};
    currentItem.setAnswers(answerValues);
    currentItem.setAnswerLabels(answerLabelText);

    for (int i = 0; i < answerValues.length; i++) {
      SelectItem selection = new SelectItem();
      selection.setLabel(answerLabelText[i]);
      selection.setValue(answerValues[i]);
      list.add(selection);
    }

    return list;
  }

/*

  public ArrayList getAnswerSelectList() {
    ArrayList list = new ArrayList();

    for (int i = 0; i < currentItem.getAnswers().length; i++) {
      SelectItem selection = new SelectItem();
      selection.setLabel(getAnswerNumber(i));
      selection.setValue(answers[i]);
      list.add(selection);
    }

    return list;
  }
*/

   /* Derived property.
   * @return ArrayList of model SelectItems
   */

// TODO use sectionBean.getsectionNumberList when its ready

  public ArrayList getSectionSelectList() {
    ArrayList list = new ArrayList();

    AssessmentBean assessbean = (AssessmentBean) ContextUtil.lookupBean("assessmentBean");
    ArrayList sectionSet = assessbean.getSections();
    Iterator iter = sectionSet.iterator();
    int i =0;
    while (iter.hasNext()){
      i = i + 1;
      SectionContentsBean part = (SectionContentsBean) iter.next();
      SelectItem selection = new SelectItem();

      // need to filter out all the random draw parts
      if (part.getSectionAuthorType().equals(SectionDataIfc.RANDOM_DRAW_FROM_QUESTIONPOOL)) {
        // skip random draw parts, cannot add items to this part manually
      }
      else {
        if ("".equals(part.getTitle())) {
          selection.setLabel("Part " + i );
        }
        else {
          selection.setLabel("Part " + i + " - " + part.getTitle());
        }
        selection.setValue(part.getSectionId());
        list.add(selection);
      }

    }

    Collections.reverse(list);
    return list;
  }

  /**
   * Derived property.
   * @return ArrayList of model SelectItems
   */
  public ArrayList getPoolSelectList() {

    //System.out.println("lydiatest in getPoolSelectList");
    poolListSelectItems = new ArrayList();


    QuestionPoolService delegate = new QuestionPoolService();
    ArrayList qplist = delegate.getBasicInfoOfAllPools(AgentFacade.getAgentString());
    Iterator iter = qplist.iterator();

    try {
      while(iter.hasNext())
      {
        QuestionPoolFacade pool = (QuestionPoolFacade) iter.next();
        poolListSelectItems.add(new SelectItem((pool.getQuestionPoolId().toString()), pool.getDisplayName() ) );

      }

    }
    catch (Exception e){
		throw new Error(e);
    }

    //System.out.println("lydiatest in getPoolSelectList total pool in the list = " + poolListSelectItems.size());
    return poolListSelectItems;
  }

  /**
   * Corresponding answer number list ordered for match
   * @return answer number
   */
  public String[] getMatchAnswers() {
    return matchAnswers;
  }

  /**
   * Corresponding answer number list ordered for match
   * @param matchAnswers answer number list ordered for match
   */
  public void setMatchAnswers(String[] matchAnswers) {
    this.matchAnswers = matchAnswers;
  }

  /**
   * Corresponding answer number for nth match
   * @param n
   * @return
   */
  public String getMatchAnswer(int n) {
    return matchAnswers[n];
  }

  /**
   * set answer number for nth match
   * @param n
   * @param matchAnswer
   */
  public void setMatchAnswer(int n, String matchAnswer) {
    matchAnswers[n] = matchAnswer;
  }

  /**
   * feedback for nth match
   * @param n
   * @return     feedback for nth match

   */
  public String getMatchFeedback(int n) {
    return matchFeedbackList[n];
  }

  /**
   * set feedback for nth match
   * @param n
   * @param matchFeedback feedback for match
   */
  public void setMatchFeedback(int n, String matchFeedback) {
    this.matchFeedbackList[n] = matchFeedback;
  }

  /**
   * array of matching feeback
   * @return array of matching feeback
   */
  public String[] getMatchFeedbackList() {
    return matchFeedbackList;
  }

  /**
   * set array of matching feeback
   * @param matchFeedbackList array of matching feeback
   */
  public void setMatchFeedbackList(String[] matchFeedbackList) {
    this.matchFeedbackList = matchFeedbackList;
  }


///////////////////////////////////////////////////////////////////////////
//         ACTION METHODS
///////////////////////////////////////////////////////////////////////////
/*
  public String startCreateItem()
  {
   try{
     //System.out.println("lydiatest in startCreateItem");
     ItemBean item = new ItemBean();

    // check to see if we arrived here from question pool

    //System.out.println("lydiatest we are adding to : "+ this.getTarget() );
    //System.out.println("lydiatest we are adding to : "+ this.getQpoolId() );

// need to get assessmentid
//  String assessmentId = ContextUtil.lookupParam("assessmentid");

// need to set indivdiual item properties
      this.setCurrentItem(item);


	item.setItemType(this.getItemType());
	String nextpage= null;
	int itype=0; //
        if (this.getItemType()!=null) {
		itype = new Integer(this.getItemType()).intValue();
 	}
    //System.out.println("lydiatest selected which type : " + itype);
	switch (itype) {
		case 1:
			item.setMultipleCorrect(Boolean.FALSE.booleanValue());
			item.setMultipleCorrectString(TypeFacade.MULTIPLE_CHOICE.toString());
			this.setItemTypeString("Multiple Choice");  //  need to get it from properties file
			nextpage = "multipleChoiceItem";
    //System.out.println("lydiatest multplecorrctstring should be false : " + item.getMultipleCorrectString());
			break;
		case 2:
// never really use this, put here for completeness
			item.setMultipleCorrect(Boolean.TRUE.booleanValue());
			item.setMultipleCorrectString(TypeFacade.MULTIPLE_CORRECT.toString());
			this.setItemTypeString("Multiple Choice");  //  need to get it from properties file
			nextpage = "multipleChoiceItem";
			break;
		case 3:
			this.setItemTypeString("Survey");  //  need to get it from properties file
			nextpage = "surveyItem";
			break;
		case 4:
			this.setItemTypeString("True or False");  //  need to get it from properties file
			nextpage = "trueFalseItem";
			break;
		case 5:
			this.setItemTypeString("Short Answers/Essay");  //  need to get it from properties file
			nextpage = "shortAnswerItem";
			break;
		case 6:
			this.setItemTypeString("File Upload");  //  need to get it from properties file
			nextpage = "fileUploadItem";
			break;
		case 7:
			this.setItemTypeString("Audio Recording");  //  need to get it from properties file
			nextpage = "audioRecItem";
			break;
		case 8:
			this.setItemTypeString("Fill In the Blank");  //  need to get it from properties file
			nextpage = "fillInBlackItem";
			break;
		case 9:
			this.setItemTypeString("Matching");  //  need to get it from properties file
			nextpage = "matchingItem";
			break;
		case 10:
			this.setItemTypeString("Importing from Question Pool");  //  need to get it from properties file
			nextpage = "poolList";
			break;
    	}
	return nextpage;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new Error(e);
    }
  }
*/


  public String[] getAnswerFeedbackList()
  {
    return answerFeedbackList;
  }


  public void setAnswerFeedbackList(String[] answerFeedbackList)
  {
    this.answerFeedbackList = answerFeedbackList;
  }


  public String doit() {
	//  navigation for ItemModifyListener
    //System.out.println("lydiatest calling doit() ");
	return outcome;
  }


/**
   * delete specified Item
   */
  public String deleteItem() {
//System.out.println("lydiatest in deleteItem()  " );

ItemService delegate = new ItemService();
    //System.out.println("lydiatest item in deleteItem is " + this.getItemToDelete().getItemId());

        Long deleteId= this.getItemToDelete().getItemId();

        ItemFacade itemf = delegate.getItem(deleteId, AgentFacade.getAgentString());
	// save the currSection before itemf.setSection(null), used to reorder question sequences

        SectionFacade  currSection = (SectionFacade) itemf.getSection();
        Integer  currSeq = itemf.getSequence();

	QuestionPoolService qpdelegate = new QuestionPoolService();
        if ((qpdelegate.getPoolIdsByItem(deleteId.toString()) ==  null) ||
           (qpdelegate.getPoolIdsByItem(deleteId.toString()).isEmpty() )){
	// if no reference to this item at all
        delegate.deleteItem(deleteId, AgentFacade.getAgentString());
	}
	else {
	// if some pools still reference to this item , then just set section = null
	  itemf.setSection(null);
	  delegate.saveItem(itemf);
 	}

    AssessmentService assessdelegate = new AssessmentService();
      // reorder item numbers

    SectionFacade sectfacade = assessdelegate.getSection(currSection.getSectionId().toString());
      Set itemset = sectfacade.getItemFacadeSet();
    //System.out.println("lydiatest item itemset size is " + itemset.size());
// should be size-1 now.
      Iterator iter = itemset.iterator();
      while (iter.hasNext()) {
        ItemFacade  itemfacade = (ItemFacade) iter.next();
        Integer itemfacadeseq = itemfacade.getSequence();
    //System.out.println("lydiatest shifting orig seq = " + itemfacadeseq);
        if (itemfacadeseq.compareTo(currSeq) > 0 ){
          itemfacade.setSequence(new Integer(itemfacadeseq.intValue()-1) );
    //System.out.println("lydiatest after the deleted item , shift to = " + itemfacade.getSequence());
	  delegate.saveItem(itemfacade);
        }
      }



    //  go to editAssessment.jsp, need to first reset assessmentBean
    AssessmentBean assessmentBean = (AssessmentBean) ContextUtil.lookupBean(
                                          "assessmentBean");
    AssessmentFacade assessment = assessdelegate.getAssessment(assessmentBean.getAssessmentId());
    assessmentBean.setAssessment(assessment);


	return "editAssessment";


  }


 public String confirmDeleteItem(){
    //System.out.println("lydiatest in confirm Delte Item ");

        ItemService delegate = new ItemService();
        String itemId= ContextUtil.lookupParam("itemid");
    //System.out.println("lydiatest itemId in confirm is " + itemId);

        ItemFacade itemf = delegate.getItem(new Long(itemId), AgentFacade.getAgentString());
        setItemToDelete(itemf);
    //System.out.println("lydiatest itemf in confirm is " + itemf.getItemId());


        return "removeQuestion";
  }


  public void selectItemType(ValueChangeEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        String type = (String) event.getNewValue();
//System.out.println("lydiatest new itemtype editAssessment  =  " + type);
          setItemType(type);
//System.out.println("lydiatest toggle choice set bean.setitemtype =  " + getItemType());

  }



}
