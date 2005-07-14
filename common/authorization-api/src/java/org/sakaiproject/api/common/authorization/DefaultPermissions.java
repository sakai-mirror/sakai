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

package org.sakaiproject.api.common.authorization;

/**
 * This class represents zero or more fine grained permissions. This class also 
 * represents all of the "base" permissions that Sakai's Common will support. 
 * This class really represents two logical entities: Permissions and Roles as 
 * combinations of Permissions (i.e. permissions matrix).
 * <p>
 * The fine grained permissions are defined as:
 * 
 * <pre>
 *  Permission______________|_Allow_|_Deny_|
 *  ________________________|_______|______|
 *  Execute_________________|_______|______| Traverse collection / Execute entity 
 *  Read____________________|_______|______| List collection / Read entity
 *  Read Metadata___________|_______|______| Basic metadata (e.g. last modified, etc)
 *  Read Extended Metadata__|_______|______| (e.g. Dublin Core, IMS, etc)
 *  Create__________________|_______|______| Create an entity in a given context
 *  Create Collection_______|_______|______| Create a collection (e.g. folder) in a given context
 *  Write___________________|_______|______| Modify an existing entity
 *  Write Metadata__________|_______|______| Basic metadata (e.g. last modified, etc)
 *  Write Extended Metadata_|_______|______| (e.g. Dublin Core, IMS, etc)
 *  Delete__________________|_______|______| Delete an existing entity
 *  Delete Collection_______|_______|______| Delete an existing collection and all children
 *  Read Permissions________|_______|______| Display permissions for a given entity
 *  Manage Permissions______|_______|______| Modify permissions for a given entity
 *  Take Ownership__________|_______|______| Take ownership of a given entity
 *  Audit___________________|_______|______| Create/modify audits for a given entity
 * </pre>
 * 
 * <p>
 * Notes:
 * </p>
 * <ol>
 * <li>Permissions can have three states: Null, True, False. Null indicates that
 * the caller does not care about the state, i.e. it behaves like a wildcard. 
 * True or False indicates that the caller<i>does </i> care about matching the 
 * state.</li>
 * <li>Denies always take precedence over allows.</li>
 * <li>If tools dictate that this set of permissions is not sufficient, 
 * alternate FunctionRole classes can be created.</li>
 * </ol>
 * <p>
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface DefaultPermissions extends Permissions
{
  public static final String EXECUTE = "execute";

  public Boolean getExecute();

  public void setExecute(Boolean b);

  public static final String READ = "read";

  public Boolean getRead();

  public void setRead(Boolean b);

  public static final String READ_METADATA = "readMetadata";

  public Boolean getReadMetadata();

  public void setReadMetadata(Boolean b);

  public static final String READ_EXTENDED_METADATA = "readExtendedMetadata";

  public Boolean getReadExtendedMetadata();

  public void setReadExtendedMetadata(Boolean b);

  public static final String CREATE = "create";

  public Boolean getCreate();

  public void setCreate(Boolean b);

  public static final String CREATE_COLLECTION = "createCollection";

  public Boolean getCreateCollection();

  public void setCreateCollection(Boolean b);

  public static final String WRITE = "write";

  public Boolean getWrite();

  public void setWrite(Boolean b);

  public static final String WRITE_METADATA = "writeMetadata";

  public Boolean getWriteMetadata();

  public void setWriteMetadata(Boolean b);

  public static final String WRITE_EXTENDED_METADATA = "writeExtendedMetadata";

  public Boolean getWriteExtendedMetadata();

  public void setWriteExtendedMetadata(Boolean b);

  public static final String DELETE = "delete";

  public Boolean getDelete();

  public void setDelete(Boolean b);

  public static final String DELETE_COLLECTION = "deleteCollection";

  public Boolean getDeleteCollection();

  public void setDeleteCollection(Boolean b);

  public static final String READ_PERMISSIONS = "readPermissions";

  public Boolean getReadPermissions();

  public void setReadPermissions(Boolean b);

  public static final String MANAGE_PERMISSIONS = "managePermissions";

  public Boolean getManagePermissions();

  public void setManagePermissions(Boolean b);

  public static final String TAKE_OWNERSHIP = "takeOwnership";

  public Boolean getTakeOwnership();

  public void setTakeOwnership(Boolean b);

  public static final String AUDIT = "audit";

  public Boolean getAudit();

  public void setAudit(Boolean b);
}



