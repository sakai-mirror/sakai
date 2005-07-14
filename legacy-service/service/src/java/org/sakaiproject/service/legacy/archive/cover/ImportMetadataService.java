/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.service.legacy.archive.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
 * @author rshastri <a href="mailto:rshastri@iupui.edu ">Rashmi Shastri</a>
 * @version $Id$
 *  
 */

public class ImportMetadataService
{

  private static org.sakaiproject.service.legacy.archive.ImportMetadataService m_instance = null;

  public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.archive.ImportMetadataService.SERVICE_NAME;

  /**
   * Access the component instance: special cover only method.
   * @return the component instance.
   */
  public static org.sakaiproject.service.legacy.archive.ImportMetadataService getInstance()
  {
    if (ComponentManager.CACHE_SINGLETONS)
    {
      if (m_instance == null)
          m_instance = (org.sakaiproject.service.legacy.archive.ImportMetadataService) ComponentManager
              .get(org.sakaiproject.service.legacy.archive.ImportMetadataService.class);
      return m_instance;
    }
    else
    {
      return (org.sakaiproject.service.legacy.archive.ImportMetadataService) ComponentManager
          .get(org.sakaiproject.service.legacy.archive.ImportMetadataService.class);
    }
  }

  public static org.sakaiproject.service.legacy.archive.ImportMetadata getImportMapById(
      String id)
  {
    org.sakaiproject.service.legacy.archive.ImportMetadataService service = getInstance();
    if (service == null) return null;

    return service.getImportMapById(id);
  }

  public static java.util.List getImportMetadataElements(
      org.w3c.dom.Document doc)
  {
    org.sakaiproject.service.legacy.archive.ImportMetadataService service = getInstance();
    if (service == null) return null;

    return service.getImportMetadataElements(doc);
  }

  /**
   * @param username
   * @param siteDoc
   * @return
   */
  public static boolean hasMaintainRole(String username,
      org.w3c.dom.Document siteDoc)
  {
    org.sakaiproject.service.legacy.archive.ImportMetadataService service = getInstance();
    if (service == null) return false;

    return service.hasMaintainRole(username, siteDoc);
  }
}

