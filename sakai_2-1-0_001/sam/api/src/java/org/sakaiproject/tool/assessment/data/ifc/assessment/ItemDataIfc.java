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
package org.sakaiproject.tool.assessment.data.ifc.assessment;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

public interface ItemDataIfc extends java.io.Serializable {

  public static Integer ACTIVE_STATUS = new Integer(1);
  public static Integer INACTIVE_STATUS = new Integer(0);

  Long getItemId();

  void setItemId(Long itemId);

  String getItemIdString();

  void setItemIdString(String itemIdString);

  SectionDataIfc getSection();

  void setSection(SectionDataIfc section);

  Integer getSequence();

  void setSequence(Integer sequence);

  Integer getDuration();

  void setTriesAllowed(Integer triesAllowed);

  Integer getTriesAllowed();

  void setDuration(Integer duration);

  String getInstruction();

  void setInstruction(String instruction);

  String getDescription();

  void setDescription(String description);

  Long getTypeId();

  void setTypeId(Long typeId);

  String getGrade();

  void setGrade(String grade);

  Float getScore();

  void setScore(Float score);

  String getHint();

  void setHint(String hint);

  Boolean getHasRationale();

  void setHasRationale(Boolean hasRationale);

  Integer getStatus();

  void setStatus(Integer status);

  String getCreatedBy();

  void setCreatedBy(String createdBy);

  Date getCreatedDate();

  void setCreatedDate(Date createdDate);

  String getLastModifiedBy();

  void setLastModifiedBy(String lastModifiedBy);

  Date getLastModifiedDate();

  void setLastModifiedDate(Date lastModifiedDate);

  Set getItemTextSet();

  void setItemTextSet(Set itemTextSet) ;

  void addItemText(String itemText, Set answerSet);

  Set getItemMetaDataSet();

  void setItemMetaDataSet(Set itemMetaDataSet);

  HashMap getItemMetaDataMap(Set itemMetaDataSet) ;

  Set getItemFeedbackSet();

  void setItemFeedbackSet(Set itemFeedbackSet);

  HashMap getItemFeedbackMap(Set itemFeedbackSet) ;

  String getItemMetaDataByLabel(String label);

  void addItemMetaData(String label, String entry);

  String getCorrectItemFeedback();

  void setCorrectItemFeedback(String text);

  String getInCorrectItemFeedback();

  void setInCorrectItemFeedback(String text);

  String getGeneralItemFeedback();

  void setGeneralItemFeedback(String text);

  String getItemFeedback(String typeId);

  void addItemFeedback(String typeId, String text);

  void removeFeedbackByType(String typeId);

  public Boolean getIsTrue();

  public String getText();

  TypeIfc getType();

  ArrayList getItemTextArray();

  ArrayList getItemTextArraySorted();

  String getAnswerKey();

}
