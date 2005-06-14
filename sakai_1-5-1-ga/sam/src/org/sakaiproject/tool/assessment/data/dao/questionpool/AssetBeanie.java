
/*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
*/

/**
 * <p>Description: OKI based implementation</p>
 * <p>Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation</p>
 * @author jlannan
 * @version $Id: AssetBeanie.java,v 1.1 2004/10/04 17:55:51 daisyf.stanford.edu Exp $
 */
package org.sakaiproject.tool.assessment.data.dao.questionpool;

import java.util.Calendar;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.navigoproject.business.entity.Item;

public class AssetBeanie implements Serializable
{
  private static final Logger LOG = Logger.getLogger(AssetBeanie.class);
  private String id;
  private Character deleted;
  private String description;
  private String title;
  private String drId;
  private String typeId;
  private Calendar created;
  private Calendar effectiveDate;
  private Calendar expirationDate;
  private byte[] assetData;
  private Item data;

  /**
   * Creates a new AssetBean object.
   */
  public AssetBeanie()
  {
  }

  /**
   * Creates a new AssetBeanie object.
   *
   * @param id
   *          DOCUMENTATION PENDING
   * @param created
   *          DOCUMENTATION PENDING
   * @param data
   *          DOCUMENTATION PENDING
   * @param description
   *          DOCUMENTATION PENDING
   * @param title
   *          DOCUMENTATION PENDING
   * @param drId
   *          DOCUMENTATION PENDING
   * @param effectiveDate
   *          DOCUMENTATION PENDING
   * @param expirationDate
   *          DOCUMENTATION PENDING
   * @param typeId
   *          DOCUMENTATION PENDING
   */

  public AssetBeanie(Calendar created, Character deleted,
      byte[] assetData, String description, String title, String drId,
      Calendar effectiveDate, Calendar expirationDate, String typeId)
  {
    this.deleted = deleted;
    this.created = created;
    this.description = description;
    this.title = title;
    this.drId = drId;
    this.effectiveDate = effectiveDate;
    this.expirationDate = expirationDate;
    this.typeId = typeId;
    setAssetData(assetData);
  }

  public String getId()
  {
    return this.id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public Character getDeleted()
  {
    return this.deleted;
  }

  public void setDeleted(Character deleted)
  {
    this.deleted = deleted;
  }

  public Calendar getCreated()
  {
    return this.created;
  }

  public void setCreated(Calendar created)
  {
    this.created = created;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getTitle()
  {
    return this.title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getDrId()
  {
    return this.drId;
  }

  public void setDrId(String drId)
  {
    this.drId = drId;
  }

  public Calendar getEffectiveDate()
  {
    return this.effectiveDate;
  }

  public void setEffectiveDate(Calendar effectiveDate)
  {
    this.effectiveDate = effectiveDate;
  }

  public Calendar getExpirationDate()
  {
    return this.expirationDate;
  }

  public void setExpirationDate(Calendar expirationDate)
  {
    this.expirationDate = expirationDate;
  }

  public String getTypeId()
  {
    return this.typeId;
  }

  public void setTypeId(String typeId)
  {
    this.typeId = typeId;
  }

  public byte[] getAssetData()
  {
    return this.assetData;
  }

  public void setAssetData(byte[] assetData)
  {
    this.assetData = assetData;
    this.data = new Item(new String(assetData));
  }

  public Item getData()
  {
    return this.data;
  }

  public void setData(Item data)
  {
    this.data = data;
    this.assetData = (data.stringValue()).getBytes(); // see XmlStringBuffer
  }

}
