/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.mgt.home;

import org.sakaiproject.metaobj.shared.mgt.CloneableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.util.Date;

/**
 * marker for structured artifact home
 */
public interface StructuredArtifactHomeInterface extends WritableObjectHome, CloneableObjectHome, PresentableObjectHome {

   public String getSiteId();

   public SchemaNode getRootSchema();

   public Type getType();

   public String getInstruction();

   public Date getModified();

   public String getRootNode();

   public SchemaNode getSchema();

}
