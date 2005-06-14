package org.sakaiproject.component.syllabus.data;


import org.sakaiproject.service.syllabus.data.SyllabusData;
import org.sakaiproject.service.syllabus.data.SyllabusItem;

/**
 * A syllabus item contains information relating to a syllabus and an order
 * within a particular context (site).
 * 
 * @author Jarrod Lannan
 * @version $Id: 
 * 
 */

public class SyllabusDataImpl implements SyllabusData, Comparable
{
  private Long syllabusId;  
  private String asset;
  private Integer position;
  private Integer lockId; // optimistic lock
  //private Date version;  
  private String title;
  private String view;
  private String status;
  private String emailNotification;
  
  /**
   * @return Returns the emailNotification.
   */
  public String getEmailNotification()
  {
    return emailNotification;
  }
  /**
   * @param emailNotification The emailNotification to set.
   */
  public void setEmailNotification(String emailNotification)
  {
    this.emailNotification = emailNotification;
  }
  /**
   * @return Returns the status.
   */
  public String getStatus()
  {
    return status;
  }
  /**
   * @param status The status to set.
   */
  public void setStatus(String status)
  {
    this.status = status;
  }
  /**
   * @return Returns the title.
   */
  public String getTitle()
  {
    return title;
  }
  /**
   * @param title The title to set.
   */
  public void setTitle(String title)
  {
    this.title = title;
  }
  /**
   * @return Returns the view.
   */
  public String getView()
  {
    return view;
  }
  /**
   * @param view The view to set.
   */
  public void setView(String view)
  {
    this.view = view;
  }
  private SyllabusItem syllabusItem;
  
  
  /**
   * @return Returns the assetId.
   */
  public String getAsset()
  {
    return asset;
  }
  /**
   * @param assetId The assetId to set.
   */
  public void setAsset(String asset)
  {
    this.asset = asset;
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
   * @return Returns the position.
   */
  public Integer getPosition()
  {
    return position;
  }
  /**
   * @param position The position to set.
   */
  public void setPosition(Integer position)
  {
    this.position = position;
  }
  /**
   * @return Returns the syllabusId.
   */
  public Long getSyllabusId()
  {
    return syllabusId;
  }
  /**
   * @param syllabusId The syllabusId to set.
   */
  public void setSyllabusId(Long syllabusId)
  {
    this.syllabusId = syllabusId;
  } 
  /**
   * @return Returns the syllabusItem.
   */
  public SyllabusItem getSyllabusItem()
  {
    return syllabusItem;
  }
  /**
   * @param syllabusItem The syllabusItem to set.
   */
  public void setSyllabusItem(SyllabusItem syllabusItem)
  {
    this.syllabusItem = syllabusItem;
  }  
  
    
  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("{syllabusId=");
    sb.append(syllabusId);
    sb.append(", syllabusItem=");
    sb.append(syllabusItem);    
    sb.append(", assetId=");
    sb.append(asset);
    sb.append(", position=");
    sb.append(position);
    sb.append(", title=");
    sb.append(title);    
    sb.append(", view=");
    sb.append(view);    
    sb.append(", status=");
    sb.append(status);    
    sb.append(", emailNotification=");
    sb.append(emailNotification);    
    sb.append(", lockId=");
    sb.append(lockId);
    sb.append("}");
    return sb.toString();
  } 
  
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof SyllabusDataImpl)) return false;
    SyllabusDataImpl other = (SyllabusDataImpl) obj;

    if ((syllabusId == null ? other.syllabusId == null : syllabusId
        .equals(other.syllabusId)))
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
    return syllabusId.hashCode();           
  }
  
  public int compareTo(Object obj)
  {
    return this.position.compareTo(((SyllabusData) obj).getPosition());  
  }


  
}