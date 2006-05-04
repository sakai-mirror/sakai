package org.sakaiproject.component.osid.logging;

/**********************************************************************************
* $URL: $
* $Id: $
**********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

public class ReadableLog implements org.osid.logging.ReadableLog
{
  org.osid.OsidContext context = null;
  java.util.Map configuration = null;
  java.io.BufferedReader file = null;
  private String fileExtension = null;
  private String displayName = null;
  private org.osid.shared.Id id = null;

  public String getDisplayName() throws org.osid.logging.LoggingException
  {
    return this.displayName;
  }

  public org.osid.shared.Id getId() throws org.osid.logging.LoggingException
  {
    return this.id;
  }

  public void initialize() throws org.osid.logging.LoggingException
  {
    if (configuration == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.CONFIGURATION_ERROR);
    }
    fileExtension = (String) configuration.get("FileExtension");
    if (fileExtension == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.CONFIGURATION_ERROR);
    }
  }

  public ReadableLog(org.osid.OsidContext context, java.util.Map configuration,
      String logName) throws org.osid.logging.LoggingException
  {
    if (logName == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.NULL_ARGUMENT);
    }
    this.context = context;
    this.configuration = configuration;
    initialize();
    try
    {
      this.displayName = logName;
      openFile();
    }
    catch (org.osid.OsidException oex)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.OPERATION_FAILED);
    }
  }

  public org.osid.logging.EntryIterator getEntries(
      org.osid.shared.Type formatType, org.osid.shared.Type priorityType)
      throws org.osid.logging.LoggingException
  {
    try
    {
      java.util.Vector v = new java.util.Vector();
      String line = "";
      openFile();

      while ((line = file.readLine()) != null)
      {
        org.osid.logging.Entry entry = new Entry(line);
        if ((formatType.isEqual(entry.getFormatType()))
            && (priorityType.isEqual(entry.getPriorityType())))
        {
          v.addElement(entry);
        }
      }
      file.close();
      return (new EntryIterator(v));
    }
    catch (Exception oex)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.OPERATION_FAILED);
    }
  }

  private void openFile() throws org.osid.logging.LoggingException
  {
    try
    {
      try
      {
        file = new java.io.BufferedReader(new java.io.FileReader(
            this.displayName + fileExtension));
      }
      catch (java.io.FileNotFoundException fnfex)
      {
        throw new org.osid.logging.LoggingException(
            org.osid.logging.LoggingException.UNKNOWN_NAME);
      }
    }
    catch (org.osid.OsidException oex)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.OPERATION_FAILED);
    }
  }
}
