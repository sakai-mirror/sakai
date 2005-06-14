/*
 * Created on Oct 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.component.help.shared;


import java.util.Properties;

import org.osid.OsidContext;
import org.osid.OsidException;
import org.osid.id.IdException;
import org.osid.shared.Id;
import org.sakaiproject.service.legacy.id.IdService;


/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: IdManager.java,v 1.2 2004/11/19 16:34:16 casong.indiana.edu Exp $
 */
public class IdManager implements org.osid.id.IdManager
{
  
  /**
   * @see org.osid.id.IdManager#getId(java.lang.String)
   */
  public Id getId(String idString)
  {
      return new org.sakaiproject.component.help.shared.Id(idString);
  }

/* (non-Javadoc)
 * @see org.osid.id.IdManager#createId()
 */
public Id createId() throws IdException
{
  // TODO Auto-generated method stub
  return null;
}

/* (non-Javadoc)
 * @see org.osid.OsidManager#getOsidContext()
 */
public OsidContext getOsidContext() throws OsidException
{
  // TODO Auto-generated method stub
  return null;
}

/* (non-Javadoc)
 * @see org.osid.OsidManager#assignOsidContext(org.osid.OsidContext)
 */
public void assignOsidContext(OsidContext arg0) throws OsidException
{
  // TODO Auto-generated method stub
  
}

/* (non-Javadoc)
 * @see org.osid.OsidManager#assignConfiguration(java.util.Properties)
 */
public void assignConfiguration(Properties arg0) throws OsidException
{
  // TODO Auto-generated method stub
  
}

/* (non-Javadoc)
 * @see org.osid.OsidManager#osidVersion_2_0()
 */
public void osidVersion_2_0() throws OsidException
{
  // TODO Auto-generated method stub
  
}
}