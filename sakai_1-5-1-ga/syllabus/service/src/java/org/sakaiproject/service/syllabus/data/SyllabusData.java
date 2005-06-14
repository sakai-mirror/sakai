
/**
 * @author jlannan
 * @version $Id: SyllabusData.java,v 1.5 2004/12/02 20:21:03 jlannan.iupui.edu Exp $
 */
package org.sakaiproject.service.syllabus.data;


/**
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan</a>
 * @version $id
 */
public interface SyllabusData
{
  /**
   * @return Returns the emailNotification.
   */
  public String getEmailNotification();

  /**
   * @param emailNotification The emailNotification to set.
   */
  public void setEmailNotification(String emailNotification);

  /**
   * @return Returns the status.
   */
  public String getStatus();

  /**
   * @param status The status to set.
   */
  public void setStatus(String status);

  /**
   * @return Returns the title.
   */
  public String getTitle();

  /**
   * @param title The title to set.
   */
  public void setTitle(String title);

  /**
   * @return Returns the view.
   */
  public String getView();

  /**
   * @param view The view to set.
   */
  public void setView(String view);

  /**
   * @return Returns the assetId.
   */
  public String getAsset();

  /**
   * @param assetId The assetId to set.
   */
  public void setAsset(String assetId);

  /**
   * @return Returns the lockId.
   */
  public Integer getLockId();

  /**
   * @return Returns the position.
   */
  public Integer getPosition();

  /**
   * @param position The position to set.
   */
  public void setPosition(Integer position);

  /**
   * @return Returns the syllabusId.
   */
  public Long getSyllabusId();

  /**
   * @return Returns the syllabusItem.
   */
  public SyllabusItem getSyllabusItem();

  /**
   * @param syllabusItem The syllabusItem to set.
   */
  public void setSyllabusItem(SyllabusItem syllabusItem);
}