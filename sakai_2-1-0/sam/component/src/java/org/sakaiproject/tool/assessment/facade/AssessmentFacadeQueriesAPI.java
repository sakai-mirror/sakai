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
package org.sakaiproject.tool.assessment.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentBaseData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentTemplateData;
import org.sakaiproject.tool.assessment.data.dao.assessment.SectionData;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;

public interface AssessmentFacadeQueriesAPI
{

  public IdImpl getId(String id);

  public IdImpl getId(Long id);

  public IdImpl getId(long id);

  public IdImpl getAssessmentId(String id);

  public IdImpl getAssessmentId(Long id);

  public IdImpl getAssessmentId(long id);

  public IdImpl getAssessmentTemplateId(String id);

  public IdImpl getAssessmentTemplateId(Long id);

  public IdImpl getAssessmentTemplateId(long id);

  public Long addTemplate();

  public void removeTemplate(Long assessmentId);

  public Long addAssessment(Long assessmentTemplateId);

  public AssessmentBaseData load(Long id);

  public AssessmentTemplateData loadTemplate(Long assessmentTemplateId);

  public AssessmentData loadAssessment(Long assessmentId);

  /* The following methods are real
   *
   */
  public AssessmentTemplateFacade getAssessmentTemplate(
      Long assessmentTemplateId);

  public ArrayList getAllAssessmentTemplates();

  public ArrayList getAllActiveAssessmentTemplates();

  /**
   *
   * @return a list of AssessmentTemplateFacade. However, it is IMPORTANT to note
   * that it is not a full object, it contains merely assessmentBaseId (which is
   * the templateId) & title. This methods is used when a list of template titles
   * is required for displaying purposes.
   */
  public ArrayList getTitleOfAllActiveAssessmentTemplates();

  public AssessmentFacade getAssessment(Long assessmentId);

  public void removeAssessment(Long assessmentId);

  public AssessmentData cloneAssessmentFromTemplate(AssessmentTemplateData t);

  /** This method is the same as createAssessment() except that no default
   *  section will be created with the assessment.
   */
  public AssessmentFacade createAssessmentWithoutDefaultSection(String title,
      String description, Long typeId, Long templateId);

  public AssessmentFacade createAssessment(String title, String description,
      Long typeId, Long templateId);

  public ArrayList getAllAssessments(String orderBy);

  public ArrayList getAllActiveAssessments(String orderBy);

  public ArrayList getBasicInfoOfAllActiveAssessments(String orderBy,
      boolean ascending);

  public ArrayList getBasicInfoOfAllActiveAssessmentsByAgent(String orderBy,
      String siteAgentId, boolean ascending);

  public ArrayList getBasicInfoOfAllActiveAssessmentsByAgent(String orderBy,
      String siteAgentId);

  public AssessmentFacade getBasicInfoOfAnAssessment(Long assessmentId);

  public ArrayList getSettingsOfAllActiveAssessments(String orderBy);

  public ArrayList getAllAssessments(int pageSize, int pageNumber,
      String orderBy);

  public int getQuestionSize(final Long assessmentId);

  public void saveOrUpdate(AssessmentFacade assessment);

  public void saveOrUpdate(AssessmentTemplateData template);

  public void deleteTemplate(Long templateId);

  public SectionFacade addSection(Long assessmentId);

  public SectionFacade getSection(Long sectionId);

  public void removeSection(Long sectionId);

  public SectionData loadSection(Long sectionId);

  public void saveOrUpdateSection(SectionFacade section);

  /**
   * This method move a set of questions form one section to another
   * @param sourceSectionId
   * @param destSectionId
   */
  public void moveAllItems(Long sourceSectionId, Long destSectionId);

  public ArrayList getBasicInfoOfAllActiveAssessmentTemplates(String orderBy);

  public void checkForQuestionPoolItem(AssessmentData assessment,
      HashMap qpItemHash);

  public void checkForQuestionPoolItem(SectionData section, HashMap qpItemHash);

  public void removeAllItems(Long sourceSectionId);

  public boolean assessmentTitleIsUnique(Long assessmentBaseId, String title, Boolean isTemplate);

  public List getAssessmentByTemplate(Long templateId);

}
