package org.sakaiproject.tool.assessment.business.entity.constants;

/**
 * <p>Supported QTI Versions.</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2005 Sakai</p>
 * <p> </p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: QTIVersion.java,v 1.1 2005/01/27 21:40:09 esmiley.stanford.edu Exp $
 */

public class QTIVersion
{
  public static final int VERSION_1_2 = 1;
  public static final int VERSION_2_0 = 2;

  public QTIVersion()
  {
  }

  /**
   * @param q
   * @return true if
   * OK (QTI_VERSION_1_2 or QTI_VERSION_2_0)
   */
  public static boolean isValid(int q)
  {
    return (q == VERSION_1_2 || q == VERSION_2_0) ? true : false;
  }

}