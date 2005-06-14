/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/archive/ImportMetadataImpl.java,v 1.1 2005/06/03 23:03:35 lance.indiana.edu Exp $
 *
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

package org.sakaiproject.component.legacy.archive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.archive.ImportMetadata;

/**
 * @author rshastri <a href="mailto:rshastri@iupui.edu ">Rashmi Shastri </a>
 * @version $Id$
 *  
 */
public class ImportMetadataImpl implements ImportMetadata
{
  private static final Log LOG = LogFactory.getLog(ImportMetadataImpl.class);

  private String id;
  private String legacyTool;
  private String sakaiTool;
  private String sakaiServiceName;
  private String fileName;
  private boolean mandatory = false;

  /**
   * Should only be constructed by ImportMetadataService.
   */
  ImportMetadataImpl()
  {
    LOG.debug("new ImportMetadata()");
  }

  /**
   * @return Returns the id.
   */
  public String getId()
  {
    return id;
  }

  /**
   * @param id
   *          The id to set.
   */
  public void setId(String id)
  {
    this.id = id;
  }

  /**
   * @return Returns the fileName.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * @param fileName
   *          The fileName to set.
   */
  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  /**
   * @return Returns the legacyTool.
   */
  public String getLegacyTool()
  {
    return legacyTool;
  }

  /**
   * @param legacyTool
   *          The legacyTool to set.
   */
  public void setLegacyTool(String legacyTool)
  {
    this.legacyTool = legacyTool;
  }

  /**
   * @return Returns the mandatory.
   */
  public boolean isMandatory()
  {
    return mandatory;
  }

  /**
   * @param mandatory
   *          The mandatory to set.
   */
  public void setMandatory(boolean mandatory)
  {
    this.mandatory = mandatory;
  }

  /**
   * @return Returns the sakaiServiceName.
   */
  public String getSakaiServiceName()
  {
    return sakaiServiceName;
  }

  /**
   * @param sakaiServiceName
   *          The sakaiServiceName to set.
   */
  public void setSakaiServiceName(String sakaiServiceName)
  {
    this.sakaiServiceName = sakaiServiceName;
  }

  /**
   * @return Returns the sakaiTool.
   */
  public String getSakaiTool()
  {
    return sakaiTool;
  }

  /**
   * @param sakaiTool
   *          The sakaiTool to set.
   */
  public void setSakaiTool(String sakaiTool)
  {
    this.sakaiTool = sakaiTool;
  }

}