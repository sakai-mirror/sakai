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


package org.sakaiproject.tool.assessment.services;

import org.sakaiproject.tool.assessment.facade.SectionFacade;
import org.navigoproject.osid.impl.PersistenceService;

import java.io.*;
import javax.servlet.http.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;





/**
 * The SectionService calls persistent service locator to reach the
 * manager on the back end.
 */
public class SectionService
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionService.class);


  /**
   * Creates a new SectionService object.
   */
  public SectionService()
  {
  }


  /**
   * Get a particular item from the backend, with all questions.
   */
  public SectionFacade getSection(Long secId, String agentId)
  {
    SectionFacade section = null;
    try
    {
    System.out.println("***** section in delegate=" + secId);
      section = PersistenceService.getInstance().getSectionFacadeQueries().get(secId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return section;
  }


}
