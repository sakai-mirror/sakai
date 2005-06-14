/*
 * Copyright 2003, Trustees of Indiana University
 */

package org.navigoproject.osid;

import java.util.Iterator;

import osid.assessment.AssessmentException;
import osid.assessment.AssessmentTaken;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @author <a href="mailto:jlannan@iupui.edu">Jarrod Lannan</a>
 * @version $Id: AssessmentTakenIterator.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class AssessmentTakenIterator
  extends AbstractIterator
  implements osid.assessment.AssessmentTakenIterator
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentTakenIterator.class);

  /**
   * DOCUMENT ME!
   *
   * @param i
   */
  public AssessmentTakenIterator(Iterator i)
  {
    super(i);
  }

  /* (non-Javadoc)
   * @see osid.authorization.AuthorizationIterator#hasNext()
   */
  public boolean hasNext()
    throws AssessmentException
  {
    return super.abstractHasNext();
  }

  /* (non-Javadoc)
   * @see osid.authorization.AuthorizationIterator#next()
   */
  public AssessmentTaken next()
    throws AssessmentException
  {
    try
    {
      return (AssessmentTaken) super.abstractNext();
    }
    catch(RuntimeException e)
    {
      LOG.error(e.getMessage(), e);
      throw new AssessmentException(e.getMessage());
    }
  }
}
