package org.sakaiproject.tool.assessment.data.dao.questionpool;

import java.io.Serializable;
import org.navigoproject.osid.impl.PersistenceService;
/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolAccess.java,v 1.1 2004/10/04 17:55:51 daisyf.stanford.edu Exp $
 */
public class QuestionPoolAccess
  implements Serializable
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 9180085666292824370L;

  private Long questionPoolId;
  private String agentId;
  private Long accessTypeId;

  public QuestionPoolAccess(){
  }

  public QuestionPoolAccess(Long questionPoolId, String agentId, Long accessTypeId){
    this.questionPoolId = questionPoolId;
    this.agentId = agentId;  
    this.accessTypeId = accessTypeId;  
  }

  public Long getQuestionPoolId()
  {
    return questionPoolId;
  }

  public void setQuestionPoolId(Long questionPoolId)
  {
    this.questionPoolId = questionPoolId;
  }

  public String getAgentId()
  {
    return agentId;
  }

  public void setAgentId(String agentId)
  {
    this.agentId = agentId;
  }

  public Long getAccessTypeId()
  {
    return accessTypeId;
  }

  public void setAccessTypeId(Long accessTypeId)
  {
    this.accessTypeId = accessTypeId;
  }

  public boolean equals(Object questionPoolAccess){
    boolean returnValue = false;
    if (this == questionPoolAccess)
      returnValue = true;  
    if (questionPoolAccess != null && questionPoolAccess.getClass()==this.getClass()){
      QuestionPoolAccess qpi = (QuestionPoolAccess)questionPoolAccess;
      if ((this.getAccessTypeId()).equals(qpi.getAccessTypeId())
          && (this.getAgentId()).equals(qpi.getAgentId())
          && (this.getQuestionPoolId()).equals(qpi.getQuestionPoolId()))
        returnValue = true;
    }
    return returnValue;
  }

  public int hashCode(){
    String s = this.agentId+":"+(this.questionPoolId).toString()+":"+(this.accessTypeId).toString();
    return (s.hashCode());
  }
}
