/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.facade;

import java.util.Collection;
import java.util.Iterator;

/**
 * A AssessmentTemplate iterator implementation.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class AssessmentTemplateIteratorFacade
  //implements AssessmentTemplateIterator
{
  private Iterator assessmentTemplateIter;
  private int size = 0;

  /**
   * Creates a new AssessmentTemplateIteratorImpl object.
   *
   * @param passessmentTemplates DOCUMENTATION PENDING
   */
  public AssessmentTemplateIteratorFacade(Collection passessmentTemplates)
  {
    assessmentTemplateIter = passessmentTemplates.iterator();
    this.size = passessmentTemplates.size();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DataFacadeException DOCUMENTATION PENDING
   */
  public boolean hasNextAssessmentTemplate()
    throws DataFacadeException
  {
    try{
      return assessmentTemplateIter.hasNext();
    }
    catch(Exception e){
      throw new DataFacadeException("No objects to return.");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DataFacadeException DOCUMENTATION PENDING
   */
  public AssessmentTemplateFacade nextAssessmentTemplate()
    throws DataFacadeException
  {
    try
    {
      return (AssessmentTemplateFacade) assessmentTemplateIter.next();
    }
    catch(Exception e)
    {
      throw new DataFacadeException("No objects to return.");
    }
  }

  public int getSize(){
    return size;
  }

}