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

public class LoggingManager implements org.osid.logging.LoggingManager
{
  org.osid.OsidContext context = null;
  java.util.Properties configuration = null;
  private org.osid.shared.Type plainLoggingFormatType = new Type("sakaiproject.org","log","plain","Plain Logging Type");
  private org.osid.shared.Type infoLoggingPriorityType = new Type("sakaiproject.org","log","priority","Plain Logging Type");
  private java.io.PrintWriter writeFile;
  private String fileExtension = null;

  public org.osid.OsidContext getOsidContext()
      throws org.osid.logging.LoggingException
  {
    return context;
  }

  public void assignOsidContext(org.osid.OsidContext context)
      throws org.osid.logging.LoggingException
  {
    this.context = context;
  }

  public void assignConfiguration(java.util.Properties configuration)
      throws org.osid.logging.LoggingException
  {
    this.configuration = configuration;
  }

  public void initialize() throws org.osid.logging.LoggingException
  {
  }

  public org.osid.shared.TypeIterator getFormatTypes()
      throws org.osid.logging.LoggingException
  {
    java.util.Vector vector = new java.util.Vector();
    vector.addElement(plainLoggingFormatType);
    try
    {
      return (new TypeIterator(vector));
    }
    catch (org.osid.shared.SharedException sex)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.OPERATION_FAILED);
    }
  }

  public org.osid.shared.TypeIterator getPriorityTypes()
      throws org.osid.logging.LoggingException
  {
    java.util.Vector vector = new java.util.Vector();
    vector.addElement(infoLoggingPriorityType);
    try
    {
      return (new TypeIterator(vector));
    }
    catch (org.osid.shared.SharedException sex)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.OPERATION_FAILED);
    }
  }

  public org.osid.shared.StringIterator getLogNamesForWriting()
      throws org.osid.logging.LoggingException
  {
    initialize();
    java.util.Vector v = new java.util.Vector();

    v.addElement("trace");
    v.addElement("debug");
    v.addElement("info");
    v.addElement("warn");
    v.addElement("error");
    v.addElement("fatal");

    try
    {
      return (new StringIterator(v));
    }
    catch (org.osid.shared.SharedException sex)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.OPERATION_FAILED);
    }
  }

  public org.osid.logging.WritableLog createLog(String logName)
      throws org.osid.logging.LoggingException
  {
    if (logName == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.NULL_ARGUMENT);
    }
    return (new WritableLog(context, configuration, logName));
  }

  public void deleteLog(String logName)
      throws org.osid.logging.LoggingException
  {
    if (logName == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.NULL_ARGUMENT);
    }
  }

  public org.osid.logging.WritableLog getLogForWriting(String logName)
      throws org.osid.logging.LoggingException
  {
    if (logName == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.NULL_ARGUMENT);
    }
    return (new WritableLog(context, configuration, logName));
  }


  // No logs are currently readable
  public org.osid.shared.StringIterator getLogNamesForReading()
      throws org.osid.logging.LoggingException
  {
    throw new org.osid.logging.LoggingException(
        org.osid.logging.LoggingException.OPERATION_FAILED);
  }

  public org.osid.logging.ReadableLog getLogForReading(String logName)
      throws org.osid.logging.LoggingException
  {
    throw new org.osid.logging.LoggingException(
        org.osid.logging.LoggingException.OPERATION_FAILED);
  }

  public boolean supportsReading() throws org.osid.logging.LoggingException
  {
    return true;
  }

  public void osidVersion_2_0() throws org.osid.logging.LoggingException
  {
  }
}
