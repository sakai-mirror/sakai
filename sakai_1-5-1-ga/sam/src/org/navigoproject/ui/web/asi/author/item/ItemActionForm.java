/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

/*
 * Created on Aug 19, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.item;

import java.util.ArrayList;
import org.navigoproject.AuthoringConstantStrings;
import org.apache.struts.action.ActionForm;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ItemActionForm
  extends ActionForm
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemActionForm.class);
  private String assessTitle;
  private String itemNo;
  private String sectionIdent;
  private ArrayList assessmentSectionIdents;
  private String fibAnswer;
  private String insertPosition;
  private String assessmentID;
  private String currentSection;
  private String ItemIdent;
	private String itemType;
	private String showMetadata;
	private String showOverallFeedback;
	private String showQuestionLevelFeedback;
	private String showSelectionLevelFeedback;

  // for questionpool
 	 private String action ="-- Select --";
 	 private String target;
	private ArrayList poolsAvailable;
	private ArrayList	poolsSelected;

  /**
   * Creates a new ItemActionForm object.
   */
  public ItemActionForm()
  {
    LOG.debug("Constructor ItemActionForm()");
	}

  /**
   * @return
   */
  public String getAssessTitle()
  {
    return assessTitle;
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
  public void setAssessTitle(String string)
  {
    assessTitle = string;
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
  public String getFibAnswer()
  {
    return fibAnswer;
  }

  /**
   * @param string
   */
  public void setFibAnswer(String string)
  {
    fibAnswer = string;
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
  public String getItemIdent()
  {
    return ItemIdent;
  }

  /**
   * @param string
   */
  public void setItemIdent(String string)
  {
    ItemIdent = string;
  }

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
  public void setTarget(String string)
  {
    target = string;
  }

  /**
   * @return
   */
  public String getTarget()
  {
    return target;
  }

  /**
   * @param string
   */
  public void setAction(String string)
  {
    action = string;
  }


  /**
   * @return
   */
  public String getAction()
  {
    return action;
  }
  /**
   * @return
   */
  public ArrayList getPoolsAvailable()
  {
    return poolsAvailable;
  }

  /**
   * @param list
   */
  public void setPoolsAvailable(ArrayList list)
  {
    poolsAvailable = list;
  }

  /**
   * @return
   */
  public ArrayList getPoolsSelected()
  {
    return poolsSelected;
  }

  /**
   * @param list
   */
  public void setPoolsSelected(ArrayList list)
  {
    poolsSelected = list;
  }

}
