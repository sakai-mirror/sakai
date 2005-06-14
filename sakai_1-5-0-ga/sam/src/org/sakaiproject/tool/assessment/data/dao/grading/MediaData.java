package org.sakaiproject.tool.assessment.data.dao.grading;

import java.io.Serializable;
import java.io.InputStream;

import java.util.Date;
import java.io.*;

public class MediaData
  implements Serializable
{
  private Long mediaId;
  private ItemGradingData itemGradingData;
  private byte[] media;
  private Long fileSize; // in kilobyte
  private String mimeType;
  private String description;
  private String location;
  private String filename;
  private boolean isLink;
  private boolean isHtmlInline;
  private Integer status;
  private String createdBy;
  private Date createdDate;
  private String lastModifiedBy;
  private Date lastModifiedDate;

  public MediaData()
  {
  }

  public MediaData(ItemGradingData itemGradingData, byte[] media, Long fileSize,
                   String mimeType, String description, String location,
                   String filename, boolean isLink, boolean isHtmlInline,
                   Integer status, String createdBy, Date createdDate,
                   String lastModifiedBy, Date lastModifiedDate){
    this.itemGradingData = itemGradingData;
    this.media = media;
    this.fileSize = fileSize;
    this.mimeType = mimeType;
    this.description = description;
    this.location = location;
    this.filename = filename;
    this.isLink = isLink;
    this.isHtmlInline = isHtmlInline;
    this.status = status;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedDate = lastModifiedDate;
  }

  public MediaData(byte[] media, String mimeType)
  {
    setMimeType(mimeType);
    setMedia(media);
    setFileSize(new Long(media.length));
  }

  public Long getMediaId()
  {
    return mediaId;
  }

  public void setMediaId(Long mediaId)
  {
    this.mediaId = mediaId;
  }

  public ItemGradingData getItemGradingData() {
    return itemGradingData;
  }

  public void setItemGradingData(ItemGradingData itemGradingData) {
    this.itemGradingData = itemGradingData;
  }

  public byte[] getMedia()
  {
    return media;
  }

  public void setMedia(byte[] media)
  {
    this.media = media;
  }

  public Long getFileSize()
  {
    return fileSize;
  }

  public void setFileSize(Long fileSize)
  {
    this.fileSize = fileSize;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  public String getMimeType()
  {
    return mimeType;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String pdescription)
  {
    description = pdescription;
  }

  public String getLocation()
  {
    return location;
  }

  public void setLocation(String location)
  {
    this.location = location;
  }

  public String getFilename()
  {
    return filename;
  }

  public void setFilename(String filename)
  {
    filename = filename;
  }

 public boolean getIsLink()
 {
   return isLink;
 }

 public void setIsLink(boolean isLink)
 {
   this.isLink = isLink;
 }

  public boolean getIsHtmlInline()
  {
    return isHtmlInline;
  }

  public void setIsHtmlInline(boolean isHtmlInline)
  {
    this.isHtmlInline = isHtmlInline;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getLastModifiedDate() {
    return this.lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

}
