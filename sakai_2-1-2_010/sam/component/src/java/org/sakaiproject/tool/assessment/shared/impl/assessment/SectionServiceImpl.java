/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/sam/src/org/sakaiproject/tool/assessment/services/qti/QTIServiceException.java $
* $Id: QTIServiceException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.shared.impl.assessment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.facade.SectionFacade;
import org.sakaiproject.tool.assessment.services.SectionService;
import org.sakaiproject.tool.assessment.shared.api.assessment.AssessmentServiceException;
import org.sakaiproject.tool.assessment.shared.api.assessment.SectionServiceAPI;

/**
 * SectionServiceImpl implements a shared interface to get/set section
 * information.
 * @author Ed Smiley <esmiley@stanford.edu>
 */
public class SectionServiceImpl implements SectionServiceAPI
{
  private static Log log = LogFactory.getLog(SectionServiceImpl.class);
  public SectionServiceImpl()
  {
  }

  // our API just uses our internal service. SectionFacade implements
  // SectionDataIfc.  If we want, we can always replace this internal
  // service and use its implementation as our own.
  public SectionDataIfc getSection(Long secId, String agentId)
  {
    SectionFacade section = null;
    try
    {
      SectionService service = new SectionService();
      section = service.getSection(secId, agentId);
    }
    catch(Exception e)
    {
      throw new AssessmentServiceException(e);
    }

    return section;
  }

}