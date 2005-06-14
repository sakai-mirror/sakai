package org.sakaiproject.tool.assessment.data.dao.assessment;
import org.sakaiproject.tool.assessment.data.ifc.assessment.EvaluationModelIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;

import java.util.Date;

/**
 * This keeps track of the submission scheme, and the number allowed.
 *
 * @author Rachel Gollub
 */
public class PublishedEvaluationModel
    implements java.io.Serializable, EvaluationModelIfc
{
  private Long id;
  private AssessmentIfc assessment;
  private String evaluationComponents;
  private Integer scoringType;
  private String numericModelId;
  private Integer fixedTotalScore;
  private Integer gradeAvailable;
  private Integer isStudentIdPublic;
  private Integer anonymousGrading;
  private Integer autoScoring;
  private String toGradeBook;

  /**
   * Creates a new SubmissionModel object.
   */
  public PublishedEvaluationModel()
  {
  }

  public PublishedEvaluationModel(String evaluationComponents, Integer scoringType,
                                 String numericModelId, Integer fixedTotalScore,
                                 Integer gradeAvailable, Integer isStudentIdPublic,
                                 Integer anonymousGrading, Integer autoScoring,
                                 String toGradeBook)
  {
    this.evaluationComponents = evaluationComponents; // =  no limit
    this.scoringType = scoringType; // no. of copy
    this.numericModelId = numericModelId;
    this.fixedTotalScore =  fixedTotalScore;
    this.gradeAvailable = gradeAvailable;
    this.isStudentIdPublic = isStudentIdPublic;
    this.anonymousGrading = anonymousGrading;
    this.autoScoring = autoScoring;
    this.toGradeBook = toGradeBook;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public void setAssessment(AssessmentIfc assessment)
  {
    this.assessment = assessment;
  }

  public AssessmentIfc getAssessment()
  {
    return assessment;
  }

  public void setAssessmentBase(AssessmentBaseIfc assessmentBase)
  {
    setAssessment((AssessmentIfc)assessmentBase);
  }

  public AssessmentBaseIfc getAssessmentBase()
  {
    return getAssessment();
  }

  public String getEvaluationComponents()
  {
    return evaluationComponents;
  }

  public void setEvaluationComponents(String evaluationComponents)
  {
    this.evaluationComponents = evaluationComponents;
  }

  public Integer getScoringType()
  {
    return scoringType;
  }

  public void setScoringType(Integer scoringType)
  {
    this.scoringType = scoringType;
  }

  public String getNumericModelId()
  {
    return numericModelId;
  }

  public void setNumericModelId(String numericModelId)
  {
    this.numericModelId = numericModelId;
  }

  public Integer getFixedTotalScore()
  {
    return fixedTotalScore;
  }

  public void setFixedTotalScore(Integer fixedTotalScore)
  {
    this.fixedTotalScore = fixedTotalScore;
  }

  public Integer getGradeAvailable()
  {
    return gradeAvailable;
  }

  public void setGradeAvailable(Integer gradeAvailable)
  {
    this.gradeAvailable = gradeAvailable;
  }

  public Integer getIsStudentIdPublic()
  {
    return isStudentIdPublic;
  }

  public void setAnonymousGrading(Integer anonymousGrading)
  {
    this.anonymousGrading = anonymousGrading;
  }

  public Integer getAnonymousGrading()
  {
    return anonymousGrading;
  }

  public void setAutoScoring(Integer autoScoring)
  {
    this.autoScoring = autoScoring;
  }

  public Integer getAutoScoring()
  {
    return autoScoring;
  }

  public void setIsStudentIdPublic(Integer isStudentIdPublic)
  {
    this.isStudentIdPublic = isStudentIdPublic;
  }

  public String getToGradeBook() {
    return this.toGradeBook;
  }

  public void setToGradeBook(String toGradeBook) {
    this.toGradeBook = toGradeBook;
  }
}
