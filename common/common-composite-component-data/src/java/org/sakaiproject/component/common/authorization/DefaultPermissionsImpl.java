/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
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
 *
 **********************************************************************************/

package org.sakaiproject.component.common.authorization;

import java.util.BitSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.authorization.DefaultPermissions;
import org.sakaiproject.component.common.manager.TypeablePersistableImpl;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan </a>
 * @version $Id$
 */
public class DefaultPermissionsImpl extends TypeablePersistableImpl implements
    DefaultPermissions
{
  private static final Log LOG = LogFactory.getLog(DefaultPermissionsImpl.class);

  private static final int EXECUTE_HAS_VALUE = 0;
  private static final int EXECUTE_VALUE = 1;
  private static final int READ_HAS_VALUE = 2;
  private static final int READ_VALUE = 3;
  private static final int READ_METADATA_HAS_VALUE = 4;
  private static final int READ_METADATA_VALUE = 5;
  private static final int READ_EXTENDED_METADATA_HAS_VALUE = 6;
  private static final int READ_EXTENDED_METADATA_VALUE = 7;
  private static final int CREATE_HAS_VALUE = 8;
  private static final int CREATE_VALUE = 9;
  private static final int CREATE_COLLECTION_HAS_VALUE = 10;
  private static final int CREATE_COLLECTION_VALUE = 11;
  private static final int WRITE_HAS_VALUE = 12;
  private static final int WRITE_VALUE = 13;
  private static final int WRITE_METADATA_HAS_VALUE = 14;
  private static final int WRITE_METADATA_VALUE = 15;
  private static final int WRITE_EXTENDED_METADATA_HAS_VALUE = 16;
  private static final int WRITE_EXTENDED_METADATA_VALUE = 17;
  private static final int DELETE_HAS_VALUE = 18;
  private static final int DELETE_VALUE = 19;
  private static final int DELETE_COLLECTION_HAS_VALUE = 20;
  private static final int DELETE_COLLECTION_VALUE = 21;
  private static final int READ_PERMISSIONS_HAS_VALUE = 22;
  private static final int READ_PERMISSIONS_VALUE = 23;
  private static final int MANAGE_PERMISSIONS_HAS_VALUE = 24;
  private static final int MANAGE_PERMISSIONS_VALUE = 25;
  private static final int TAKE_OWNERSHIP_HAS_VALUE = 26;
  private static final int TAKE_OWNERSHIP_VALUE = 27;
  private static final int AUDIT_HAS_VALUE = 28;
  private static final int AUDIT_VALUE = 29;

  private BitSet bitSet = new BitSet(30);
  private String name;
  private String description;

  /**
   * @return Returns the audit.
   */
  public Boolean getAudit()
  {
    return bitSet.get(AUDIT_HAS_VALUE) ? new Boolean(bitSet.get(AUDIT_VALUE))
        : null;
  }

  /**
   * @param audit The audit to set.
   */
  public void setAudit(Boolean audit)
  {
    if (audit == null)
    {
      bitSet.set(AUDIT_HAS_VALUE, AUDIT_VALUE, false);
    }
    else
    {
      bitSet.set(AUDIT_VALUE, audit.booleanValue());
      bitSet.set(AUDIT_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the create.
   */
  public Boolean getCreate()
  {
    return bitSet.get(CREATE_HAS_VALUE) ? new Boolean(bitSet.get(CREATE_VALUE))
        : null;
  }

  /**
   * @param create The create to set.
   */
  public void setCreate(Boolean create)
  {
    if (create == null)
    {
      bitSet.set(CREATE_HAS_VALUE, CREATE_VALUE, false);
    }
    else
    {
      bitSet.set(CREATE_VALUE, create.booleanValue());
      bitSet.set(CREATE_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the createCollection.
   */
  public Boolean getCreateCollection()
  {
    return bitSet.get(CREATE_COLLECTION_HAS_VALUE) ? new Boolean(bitSet
        .get(CREATE_COLLECTION_VALUE)) : null;
  }

  /**
   * @param createCollection The createCollection to set.
   */
  public void setCreateCollection(Boolean createCollection)
  {
    if (createCollection == null)
    {
      bitSet.set(CREATE_COLLECTION_HAS_VALUE, CREATE_COLLECTION_VALUE, false);
    }
    else
    {
      bitSet.set(CREATE_COLLECTION_VALUE, createCollection.booleanValue());
      bitSet.set(CREATE_COLLECTION_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the delete.
   */
  public Boolean getDelete()
  {
    return bitSet.get(DELETE_HAS_VALUE) ? new Boolean(bitSet.get(DELETE_VALUE))
        : null;
  }

  /**
   * @param delete The delete to set.
   */
  public void setDelete(Boolean delete)
  {
    if (delete == null)
    {
      bitSet.set(DELETE_HAS_VALUE, DELETE_VALUE, false);
    }
    else
    {
      bitSet.set(DELETE_VALUE, delete.booleanValue());
      bitSet.set(DELETE_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the deleteCollection.
   */
  public Boolean getDeleteCollection()
  {
    return bitSet.get(DELETE_COLLECTION_HAS_VALUE) ? new Boolean(bitSet
        .get(DELETE_COLLECTION_VALUE)) : null;
  }

  /**
   * @param deleteCollection The deleteCollection to set.
   */
  public void setDeleteCollection(Boolean deleteCollection)
  {
    if (deleteCollection == null)
    {
      bitSet.set(DELETE_COLLECTION_HAS_VALUE, DELETE_COLLECTION_VALUE, false);
    }
    else
    {
      bitSet.set(DELETE_COLLECTION_VALUE, deleteCollection.booleanValue());
      bitSet.set(DELETE_COLLECTION_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the execute.
   */
  public Boolean getExecute()
  {
    return bitSet.get(EXECUTE_HAS_VALUE) ? new Boolean(bitSet
        .get(EXECUTE_VALUE)) : null;
  }

  /**
   * @param execute The execute to set.
   */
  public void setExecute(Boolean execute)
  {
    if (execute == null)
    {
      bitSet.set(EXECUTE_HAS_VALUE, EXECUTE_VALUE, false);
    }
    else
    {
      bitSet.set(EXECUTE_VALUE, execute.booleanValue());
      bitSet.set(EXECUTE_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the managePermissions.
   */
  public Boolean getManagePermissions()
  {
    return bitSet.get(MANAGE_PERMISSIONS_HAS_VALUE) ? new Boolean(bitSet
        .get(MANAGE_PERMISSIONS_VALUE)) : null;
  }

  /**
   * @param managePermissions The managePermissions to set.
   */
  public void setManagePermissions(Boolean managePermissions)
  {
    if (managePermissions == null)
    {
      bitSet.set(MANAGE_PERMISSIONS_HAS_VALUE, MANAGE_PERMISSIONS_VALUE, false);
    }
    else
    {
      bitSet.set(MANAGE_PERMISSIONS_VALUE, managePermissions.booleanValue());
      bitSet.set(MANAGE_PERMISSIONS_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the read.
   */
  public Boolean getRead()
  {
    return bitSet.get(READ_HAS_VALUE) ? new Boolean(bitSet.get(READ_VALUE))
        : null;
  }

  /**
   * @param read The read to set.
   */
  public void setRead(Boolean read)
  {
    if (read == null)
    {
      bitSet.set(READ_HAS_VALUE, READ_VALUE, false);
    }
    else
    {
      bitSet.set(READ_VALUE, read.booleanValue());
      bitSet.set(READ_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the readExtendedMetadata.
   */
  public Boolean getReadExtendedMetadata()
  {
    return bitSet.get(READ_EXTENDED_METADATA_HAS_VALUE) ? new Boolean(bitSet
        .get(READ_EXTENDED_METADATA_VALUE)) : null;
  }

  /**
   * @param readExtendedMetadata The readExtendedMetadata to set.
   */
  public void setReadExtendedMetadata(Boolean readExtendedMetadata)
  {
    if (readExtendedMetadata == null)
    {
      bitSet.set(READ_EXTENDED_METADATA_HAS_VALUE,
          READ_EXTENDED_METADATA_VALUE, false);
    }
    else
    {
      bitSet.set(READ_EXTENDED_METADATA_VALUE, readExtendedMetadata
          .booleanValue());
      bitSet.set(READ_EXTENDED_METADATA_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the readMetadata.
   */
  public Boolean getReadMetadata()
  {
    return bitSet.get(READ_METADATA_HAS_VALUE) ? new Boolean(bitSet
        .get(READ_METADATA_VALUE)) : null;
  }

  /**
   * @param readMetadata The readMetadata to set.
   */
  public void setReadMetadata(Boolean readMetadata)
  {
    if (readMetadata == null)
    {
      bitSet.set(READ_METADATA_HAS_VALUE, READ_METADATA_VALUE, false);
    }
    else
    {
      bitSet.set(READ_METADATA_VALUE, readMetadata.booleanValue());
      bitSet.set(READ_METADATA_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the readPermissions.
   */
  public Boolean getReadPermissions()
  {
    return bitSet.get(READ_PERMISSIONS_HAS_VALUE) ? new Boolean(bitSet
        .get(READ_PERMISSIONS_VALUE)) : null;
  }

  /**
   * @param readPermissions The readPermissions to set.
   */
  public void setReadPermissions(Boolean readPermissions)
  {
    if (readPermissions == null)
    {
      bitSet.set(READ_PERMISSIONS_HAS_VALUE, READ_PERMISSIONS_VALUE, false);
    }
    else
    {
      bitSet.set(READ_PERMISSIONS_VALUE, readPermissions.booleanValue());
      bitSet.set(READ_PERMISSIONS_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the takeOwnership.
   */
  public Boolean getTakeOwnership()
  {
    return bitSet.get(TAKE_OWNERSHIP_HAS_VALUE) ? new Boolean(bitSet
        .get(TAKE_OWNERSHIP_VALUE)) : null;
  }

  /**
   * @param takeOwnership The takeOwnership to set.
   */
  public void setTakeOwnership(Boolean takeOwnership)
  {
    if (takeOwnership == null)
    {
      bitSet.set(TAKE_OWNERSHIP_HAS_VALUE, TAKE_OWNERSHIP_VALUE, false);
    }
    else
    {
      bitSet.set(TAKE_OWNERSHIP_VALUE, takeOwnership.booleanValue());
      bitSet.set(TAKE_OWNERSHIP_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the write.
   */
  public Boolean getWrite()
  {
    return bitSet.get(WRITE_HAS_VALUE) ? new Boolean(bitSet.get(WRITE_VALUE))
        : null;
  }

  /**
   * @param write The write to set.
   */
  public void setWrite(Boolean write)
  {
    if (write == null)
    {
      bitSet.set(WRITE_HAS_VALUE, WRITE_VALUE, false);
    }
    else
    {
      bitSet.set(WRITE_VALUE, write.booleanValue());
      bitSet.set(WRITE_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the writeExtendedMetadata.
   */
  public Boolean getWriteExtendedMetadata()
  {
    return bitSet.get(WRITE_EXTENDED_METADATA_HAS_VALUE) ? new Boolean(bitSet
        .get(WRITE_EXTENDED_METADATA_VALUE)) : null;
  }

  /**
   * @param writeExtendedMetadata The writeExtendedMetadata to set.
   */
  public void setWriteExtendedMetadata(Boolean writeExtendedMetadata)
  {
    if (writeExtendedMetadata == null)
    {
      bitSet.set(WRITE_EXTENDED_METADATA_HAS_VALUE,
          WRITE_EXTENDED_METADATA_VALUE, false);
    }
    else
    {
      bitSet.set(WRITE_EXTENDED_METADATA_VALUE, writeExtendedMetadata
          .booleanValue());
      bitSet.set(WRITE_EXTENDED_METADATA_HAS_VALUE, true);
    }
  }

  /**
   * @return Returns the writeMetadata.
   */
  public Boolean getWriteMetadata()
  {
    return bitSet.get(WRITE_METADATA_HAS_VALUE) ? new Boolean(bitSet
        .get(WRITE_METADATA_VALUE)) : null;
  }

  /**
   * @param writeMetadata The writeMetadata to set.
   */
  public void setWriteMetadata(Boolean writeMetadata)
  {
    if (writeMetadata == null)
    {
      bitSet.set(WRITE_METADATA_HAS_VALUE, WRITE_METADATA_VALUE, false);
    }
    else
    {
      bitSet.set(WRITE_METADATA_VALUE, writeMetadata.booleanValue());
      bitSet.set(WRITE_METADATA_HAS_VALUE, true);
    }
  }

  /**
   * @see org.sakaiproject.component.common.authorization.PermissionsBaseImpl#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (obj == this) return true;
    if (!(obj instanceof DefaultPermissionsImpl)) return false;
    DefaultPermissionsImpl pi = (DefaultPermissionsImpl) obj;
    return this.bitSet.equals(pi.bitSet);
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return bitSet.hashCode();
  }

  /**
   * @see org.sakaiproject.component.common.authorization.PermissionsBaseImpl#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("{id=");
    sb.append(uuid);
    sb.append("bitSet=");
    sb.append(bitSet);
    sb.append("}");
    return sb.toString();
  }

  /**
   * @return Returns the description.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description The description to set.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @return Returns the name.
   */
  public String getName()
  {
    return name;
  }

  /**
   * @param name The name to set.
   */
  public void setName(String name)
  {
    this.name = name;
  }

}



