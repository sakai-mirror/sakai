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

package org.navigoproject.ui.web.form.questionpool;

import java.io.Serializable;
import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.upload.FormFile;

/**
 * This holds question pool information.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 * @author Lydia Li<lydial@stanford.edu>
 * $Id: QuestionPoolForm.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class QuestionPoolForm
  extends ActionForm
  implements Serializable
{
  private String name;
  private Collection pools;
  private QuestionPoolBean currentPool;
  private QuestionPoolBean parentPool;

  private boolean allPoolsSelected;
  private boolean allItemsSelected;
  private boolean rootPoolSelected;
  private String[] selectedPools = {  };
  private String[] selectedQuestions = {  };
  private String[] destPools = {  }; // for multibox
  private String destPool; // for Move Pool radio button 
  private FormFile filename; // for import /export
  private int htmlIdLevel; // pass this to javascript:collapseAll()  
  private String questionType; // the question type to add 

  // for search Question
  private String[] searchByTypes = {  }; // for multibox
  private String searchQtext; 
  private String searchQkeywords; 
  private String searchQobj; 
  private String searchQrubrics; 
  private String assessmentID; 
  private String selectedAssessment; 
  private String fromAuthoring; 

  // html: buttons
  private boolean badd;
  private boolean bsearch;
  private boolean bimport;
  private boolean buserAdmin;
  private boolean bcopy;
  private boolean bmove;
  private boolean bremove;
  private boolean bexport;
  private boolean baddSubPool;
  private static Logger logger =
    Logger.getLogger(QuestionPoolForm.class.getName());

  /**
   * Creates a new QuestionPoolForm object.
   */
  public QuestionPoolForm()
  {
    super();
    resetFields();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionErrors validate(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    ActionErrors errors = new ActionErrors();

    return errors;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    // Set checkboxes to false here.
    selectedPools = new String[0];
    selectedQuestions = new String[0];
    destPools = new String[0];
    searchByTypes= new String[0];
    allPoolsSelected = false;
    allItemsSelected = false;
    rootPoolSelected = false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getPools()
  {
    return pools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] getPoolArray()
  {
    return pools.toArray();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newPools DOCUMENTATION PENDING
   */
  public void setPools(Collection newPools)
  {
    pools = newPools;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param newtype DOCUMENTATION PENDING
   */
  public void setQuestionType(String newtype)
  {
    questionType= newtype;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQuestionType()
  {
	return questionType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newtype DOCUMENTATION PENDING
   */
  public void setSearchQtext(String pstr)
  {
    searchQtext = pstr;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSearchQtext()
  {
        return searchQtext;
  }
 

  /**
   * DOCUMENTATION PENDING
   *
   * @param newtype DOCUMENTATION PENDING
   */
  public void setSearchQkeywords (String pstr)
  {
    searchQkeywords = pstr;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSearchQkeywords()
  {
        return searchQkeywords;
  }
 

  /**
   * DOCUMENTATION PENDING
   *
   * @param newtype DOCUMENTATION PENDING
   */
  public void setSearchQobj(String pstr)
  {
    searchQobj = pstr;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSearchQobj()
  {
        return searchQobj;
  }
 
  /**
   * DOCUMENTATION PENDING
   *
   * @param newtype DOCUMENTATION PENDING
   */
  public void setSearchQrubrics (String pstr)
  {
    searchQrubrics = pstr;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSearchQrubrics()
  {
        return searchQrubrics;
  }
 





  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPoolBean getCurrentPool()
  {
    return currentPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newPool DOCUMENTATION PENDING
   */
  public void setCurrentPool(QuestionPoolBean newPool)
  {
    currentPool = newPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPoolBean getParentPool()
  {
    return parentPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newPool DOCUMENTATION PENDING
   */
  public void setParentPool(QuestionPoolBean newPool)
  {
    parentPool = newPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getName()
  {
    logger.debug("Getting name q.");

    return name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newName DOCUMENTATION PENDING
   */
  public void setName(String newName)
  {
    logger.debug("Setting name q " + newName);
    name = newName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newName DOCUMENTATION PENDING
   */
  public void setRootPoolSelected(boolean pallpools)
  {
    rootPoolSelected = pallpools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRootPoolSelected()
  {
    return rootPoolSelected;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newName DOCUMENTATION PENDING
   */
  public void setAllPoolsSelected(boolean pallpools)
  {
    allPoolsSelected = pallpools;
  }

  public void setAllItemsSelected(boolean pallpools)
  {
    allItemsSelected = pallpools;
  }

  public boolean getAllItemsSelected()
  {
    return allItemsSelected;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAllPoolsSelected()
  {
    return allPoolsSelected;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pPool DOCUMENTATION PENDING
   */
  public void setDestPool(String pPool)
  {
    destPool = pPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDestPool()
  {
    return destPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pPool DOCUMENTATION PENDING
   */
  public void setDestPools(String[] pPool)
  {
    destPools = pPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String[] getDestPools()
  {
    return destPools;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param pPool DOCUMENTATION PENDING
   */
  public void setSearchByTypes(String[] pstr)
  {
    searchByTypes= pstr;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String[] getSearchByTypes()
  {
    return searchByTypes;
  }

  public String getAssessmentID()
  {
    return assessmentID;
  }

  public void setAssessmentID(String pstr)
  {
    assessmentID = pstr;
  }

  public String getSelectedAssessment()
  {
    return selectedAssessment;
  }

  public void setSelectedAssessment(String pstr)
  {
    selectedAssessment= pstr;
  }

  public String getFromAuthoring()
  {
    return fromAuthoring;
  }

  public void setFromAuthoring(String pstr)
  {
    fromAuthoring = pstr;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pPool DOCUMENTATION PENDING
   */
  public void setSelectedQuestions(String[] pPool)
  {
    selectedQuestions = pPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pPool DOCUMENTATION PENDING
   */
  public void setSelectedPools(String[] pPool)
  {
    selectedPools = pPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String[] getSelectedQuestions()
  {
    return selectedQuestions;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String[] getSelectedPools()
  {
    return selectedPools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getExport()
  {
    return bexport;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setExport(boolean pbutton)
  {
    bexport = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRemove()
  {
    return bremove;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setRemove(boolean pbutton)
  {
    bremove = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMove()
  {
    return bmove;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setMove(boolean pbutton)
  {
    bmove = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getCopy()
  {
    return bcopy;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setCopy(boolean pbutton)
  {
    bcopy = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getUserAdmin()
  {
    return buserAdmin;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setUserAdmin(boolean pbutton)
  {
    buserAdmin = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getImport()
  {
    return bimport;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setImport(boolean pbutton)
  {
    bimport = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSearch()
  {
    return bsearch;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pbutton  DOCUMENTATION PENDING
   */
  public void setSearch(boolean pbutton)
  {
    bsearch = pbutton;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAdd()
  {
    return badd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pAdd  DOCUMENTATION PENDING
   */
  public void setAdd(boolean pAdd)
  {
    badd = pAdd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAddSubPool()
  {
    return baddSubPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pPool DOCUMENTATION PENDING
   */
  public void setAddSubPool(boolean pPool)
  {
    baddSubPool = pPool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param file DOCUMENTATION PENDING
   */
  public void setFilename(FormFile file)
  {
    filename = file;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param file DOCUMENTATION PENDING
   */
  public FormFile getFilename()
  {
    return filename;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param int DOCUMENTATION PENDING
   */
  public int getHtmlIdLevel()
  {
    return htmlIdLevel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param int DOCUMENTATION PENDING
   */
  public void setHtmlIdLevel(int plevel)
  {
    htmlIdLevel = plevel;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void resetFields()
  {
    pools = new ArrayList();
  }
}
