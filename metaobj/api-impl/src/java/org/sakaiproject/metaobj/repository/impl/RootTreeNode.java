/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/impl/RootTreeNode.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.repository.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.repository.model.TreeNode;

public class RootTreeNode extends TreeNode {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public RootTreeNode() {
      setName("");
      setUri("");
   }

}
