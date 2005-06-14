package org.sakaiproject.component.syllabus.data;


import java.util.Set;
import java.util.TreeSet;

import org.sakaiproject.service.syllabus.data.SyllabusItem;

/**
 * A syllabus item contains information relating to a syllabus and an order
 * within a particular context (site).
 * 
 * @author Jarrod Lannan
 * @version $Id: 
 * 
 */

public class SyllabusItemImpl implements SyllabusItem
{
  private Long surrogateKey;
  private String userId;
  private String contextId;
  private String redirectURL;
  private Integer lockId; // optimistic lock
  
  private Set syllabi = new TreeSet();
  
  /**
   *  Public no-arg Constructor.
   */
  public SyllabusItemImpl(){
    
    ;
  }
  
  
  /**
   * @param userId
   * @param contextId
   * @param redirectURL
   *        SyllabusEntry Constructor. Package protected.
   */
  SyllabusItemImpl(String userId, String contextId,
      String redirectURL)
  {

    if (userId == null || contextId == null)
    {
      throw new IllegalArgumentException();
    }
    
    this.userId = userId;    
    this.contextId = contextId;
    this.redirectURL = redirectURL;
  }    

  
  /**
   * @return Returns the syllabi.
   */
  public Set getSyllabi()
  {
    return syllabi;
  }
  /**
   * @param syllabi The syllabi to set.
   */
  public void setSyllabi(Set syllabi)
  {
    this.syllabi = syllabi;
  }
  /**
   * @return Returns the contextId.
   */
  public String getContextId()
  {
    return contextId;
  }
  /**
   * @param contextId The contextId to set.
   */
  public void setContextId(String contextId)
  {
    this.contextId = contextId;
  }  
  /**
   * @return Returns the lockId.
   */
  public Integer getLockId()
  {
    return lockId;
  }
  /**
   * @param lockId The lockId to set.
   */
  public void setLockId(Integer lockId)
  {
    this.lockId = lockId;
  }
  /**
   * @return Returns the surrogateKey.
   */
  public Long getSurrogateKey()
  {
    return surrogateKey;
  }
  /**
   * @param surrogateKey The surrogateKey to set.
   */
  private void setSurrogateKey(Long surrogateKey)
  {
    this.surrogateKey = surrogateKey;
  }  
  /**
   * @return Returns the userId.
   */
  public String getUserId()
  {
    return userId;
  }
  /**
   * @param userId The userId to set.
   */
  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  /**
   * @return Returns the redirectURL.
   */
  public String getRedirectURL()
  {
    return redirectURL;
  }
  /**
   * @param redirectURL The redirectURL to set.
   */
  public void setRedirectURL(String redirectURL)
  {
    this.redirectURL = redirectURL;
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof SyllabusItemImpl)) return false;
    SyllabusItemImpl other = (SyllabusItemImpl) obj;

    if ((userId == null ? other.userId == null : userId
        .equals(other.userId))        
        && (contextId == null ? other.contextId == null : contextId.equals(other.contextId))
        && (redirectURL == null ? other.redirectURL == null : redirectURL.equals(other.redirectURL)))
    {
      return true;
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return userId.hashCode() + contextId.hashCode() +
           redirectURL.hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("{surrogateKey=");
    sb.append(surrogateKey);
    sb.append(", userId=");
    sb.append(userId);    
    sb.append(", contextId=");
    sb.append(contextId);
    sb.append(", redirectURL=");
    sb.append(redirectURL);    
    sb.append(", lockId=");
    sb.append(lockId);
    sb.append("}");
    return sb.toString();
  }   
}