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

package org.sakaiproject.tool.assessment.business.entity.helper.item;

import java.io.InputStream;

import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

/**
 * Interface for QTI-versioned item helper implementation.
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: ItemHelperIfc.java,v 1.1 2005/01/21 19:54:57 esmiley.stanford.edu Exp $
 */

public interface ItemHelperIfc
{
  public static final long ITEM_AUDIO = TypeIfc.AUDIO_RECORDING.longValue();
  public static final long ITEM_ESSAY = TypeIfc.ESSAY_QUESTION.longValue();
  public static final long ITEM_FILE = TypeIfc.FILE_UPLOAD.longValue();
  public static final long ITEM_FIB = TypeIfc.FILL_IN_BLANK.longValue();
  public static final long ITEM_MCSC = TypeIfc.MULTIPLE_CHOICE.longValue();
  public static final long ITEM_SURVEY = TypeIfc.MULTIPLE_CHOICE_SURVEY.
    longValue();
  public static final long ITEM_MCMC = TypeIfc.MULTIPLE_CORRECT.longValue();
  public static final long ITEM_TF = TypeIfc.TRUE_FALSE.longValue();
  public static final long ITEM_MATCHING = TypeIfc.MATCHING.longValue();
  public String[] itemTypes =
    {
    "Unknown Type",
    "Multiple Choice",
    "Multiple Choice",
    "Multiple Choice Survey",
    "True or False",
    "Short Answers/Essay",
    "File Upload",
    "Audio Recording",
    "Fill In the Blank",
    "Matching",
  };


  /**
   * Read XML document from input stream
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item readXMLDocument(InputStream inputStream);

  /**
   * Get XML template for a given item type
   * @param type
   * @return
   */
  public String getTemplateFromType(TypeIfc type);
  /**
   * Get item type string which is used for the title of a given item type
   * @param type
   * @return
   */

  public String getItemTypeString(TypeIfc type);

  /**
   * Add/update a response entry/answer
   * @param itemXml
   * @param xpath
   * @param itemText
   * @param isInsert
   * @param responseNo
   * @param responseLabelIdent
   */
  public void addResponseEntry(
    Item itemXml, String xpath, String value,
    boolean isInsert, String responseNo, String responseLabel);
  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param xpath DOCUMENTATION PENDING
   * @param value DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item updateItemXml(Item itemXml, String xpath, String value);
}
