package org.sakaiproject.tool.assessment.data.ifc.assessment;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Rachel Gollub
 * @version 1.0
 */
public interface AssessmentTemplateIfc
    extends Serializable, AssessmentBaseIfc
{
  Long getAssessmentTemplateId();

  void setAssessmentTemplateId(Long assessmentTemplateId);
}