
/**
 * @author jlannan
 * @version $Id: SyllabusItem.java,v 1.4 2004/12/02 20:21:03 jlannan.iupui.edu Exp $
 */
package org.sakaiproject.service.syllabus.data;

import java.util.Set;

/**
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan</a>
 * @version $id
 */
public interface SyllabusItem
{
  /**
   * @return Returns the syllabi.
   */
  public Set getSyllabi();

  /**
   * @param syllabi The syllabi to set.
   */
  public void setSyllabi(Set syllabi);

  /**
   * @return Returns the contextId.
   */
  public String getContextId();

  /**
   * @param contextId The contextId to set.
   */
  public void setContextId(String contextId);

  /**
   * @return Returns the lockId.
   */
  public Integer getLockId();

  /**
   * @return Returns the surrogateKey.
   */
  public Long getSurrogateKey();

  /**
   * @return Returns the userId.
   */
  public String getUserId();

  /**
   * @param userId The userId to set.
   */
  public void setUserId(String userId);

  /**
   * @return Returns the redirectURL.
   */
  public String getRedirectURL();

  /**
   * @param redirectURL The redirectURL to set.
   */
  public void setRedirectURL(String redirectURL);
}