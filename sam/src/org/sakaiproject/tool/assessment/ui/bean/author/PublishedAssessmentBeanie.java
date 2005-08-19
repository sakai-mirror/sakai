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
import java.util.List;

import javax.faces.model.SelectItem;

import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.services.shared.TypeService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.ItemContentsBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.SectionContentsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 *
 * Used to be org.navigoproject.ui.web.asi.author.assessment.AssessmentActionForm.java
 */
public class PublishedAssessmentBeanie
    implements Serializable {
    private static Log log = LogFactory.getLog(PublishedAssessmentBeanie.class);

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -630950053380808339L;
  private PublishedAssessmentFacade assessment;
  private String assessmentId;
  private String title;
  // ArrayList of SectionContentsBean
  private ArrayList sections = new ArrayList();
  private ArrayList partNumbers = new ArrayList();
  private int questionSize=0;
  private float totalScore=0;
  private String newQuestionTypeId;

  /*
   * Creates a new AssessmentBean object.
   */
  public PublishedAssessmentBeanie() {
  }

  public PublishedAssessmentFacade getAssessment() {
    return assessment;
  }

  public void setAssessment(PublishedAssessmentFacade assessment) {
    try {
      this.assessment = assessment;
      this.assessmentId = assessment.getAssessmentId().toString();
      this.title = assessment.getTitle();

      // work out the question side & total point
      this.sections = new ArrayList();
      ArrayList sectionArray = assessment.getSectionArraySorted();
      for (int i=0; i<sectionArray.size(); i++){
        SectionDataIfc section = (SectionDataIfc)sectionArray.get(i);
        SectionContentsBean sectionBean = new SectionContentsBean(section);
        this.sections.add(sectionBean);
      }
      setPartNumbers();
      setQuestionSizeAndTotalScore();
    }
    catch (Exception ex) {
    }
  }

  // properties from Assessment
  public String getAssessmentId() {
    return this.assessmentId;
  }

  public void setAssessmentId(String assessmentId) {
    this.assessmentId = assessmentId;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ArrayList getSections() {
    return sections;
  }

  public void setSections(ArrayList sections) {
    this.sections = sections;
  }

  public ArrayList getPartNumbers() {
    return partNumbers;
  }

  public void setPartNumbers() {
    this.partNumbers = new ArrayList();
    for (int i=1; i<=this.sections.size(); i++){
      this.partNumbers.add(new SelectItem(i+""));
    }
  }

  public int getQuestionSize() {
    return this.questionSize;
  }

  public void setQuestionSizeAndTotalScore() {
   this.questionSize = 0;
   this.totalScore = 0;
   for(int i=0;i<this.sections.size();i++){
      SectionContentsBean sectionBean = (SectionContentsBean) sections.get(i);
      ArrayList items = sectionBean.getItemContents();
      this.questionSize += items.size();
      for (int j=0; j<items.size();j++){
        ItemContentsBean item = (ItemContentsBean)items.get(j);
        if (item.getItemData().getScore()!=null){
          this.totalScore += item.getItemData().getScore().floatValue();
        }
      }
    }
  }

  public float getTotalScore() {
    return this.totalScore;
  }

  public String getNewQuestionTypeId() {
    return this.newQuestionTypeId;
  }

  public void setNewQuestionTypeId(String newQuestionTypeId) {
    this.newQuestionTypeId = newQuestionTypeId;
  }


  public SelectItem[] getItemTypes(){
    // return list of TypeD
    TypeService service = new TypeService();
    List list = service.getFacadeItemTypes();
    SelectItem[] itemTypes = new SelectItem[list.size()];
    for (int i=0; i<list.size();i++){
      TypeIfc t = (TypeIfc) list.get(i);
      itemTypes[i] = new SelectItem(
          t.getTypeId().toString(), t.getKeyword());
    }
    return itemTypes;
  }

}
