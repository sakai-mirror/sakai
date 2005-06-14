/*
 * Created on Oct 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.component.help.shared;
import java.io.Serializable;

import org.osid.shared.SharedException;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: Id.java,v 1.2 2004/11/19 16:34:16 casong.indiana.edu Exp $
 */
public class Id implements org.osid.shared.Id, Serializable
{
//  private String id;
  private Long longId;
  /**
   * @param idString
   */
  public Id(String idString)
  {
    longId = new Long(idString);
  }
  /* (non-Javadoc)
   * @see org.osid.shared.Id#getIdString()
   */
  public String getIdString() throws SharedException
  { 
    return longId.toString();
  }
  /* (non-Javadoc)
   * @see org.osid.shared.Id#isEqual(org.osid.shared.Id)
   */
  public boolean isEqual(org.osid.shared.Id arg0) throws SharedException
  {
    // TODO Auto-generated method stub
    return false;
  }

//  Id(String idString)
//  {
//    if (idString == null)
//    {
//      throw new IllegalArgumentException("idString == null");
//    }
//    this.id = idString;
//  }
//
//  /**
//   * @see org.osid.shared.Id#getIdString()
//   */
//  public String getIdString() throws SharedException
//  {
//    return id;
//  }
//
//  /**
//   * @see org.osid.shared.Id#isEqual(org.osid.shared.Id)
//   */
//  public boolean isEqual(org.osid.shared.Id id) throws SharedException
//  {
//    return this.equals(id);
//  }
//
//  /**
//   * @see java.lang.Object#equals(java.lang.Object)
//   */
//  public boolean equals(Object obj)
//  {
//    if (this == obj) return true;
//    if (!(obj instanceof Id)) return false;
//    Id other = (Id) obj;
//    return this.id.equals(other.id);
//  }
//
//  /**
//   * @see java.lang.Object#hashCode()
//   */
//  public int hashCode()
//  {
//    return id.hashCode();
//  }
//
//  /**
//   * @see java.lang.Object#toString()
//   */
//  public String toString()
//  {
//    return id;
//  }
}
