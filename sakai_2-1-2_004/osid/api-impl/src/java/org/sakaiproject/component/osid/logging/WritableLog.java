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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WritableLog implements org.osid.logging.WritableLog
{
  private static final Log LOG = LogFactory.getLog(WritableLog.class);

  org.osid.shared.Type formatType = null;
  org.osid.shared.Type priorityType = null;
  private String logName = null;
  private org.osid.shared.Id id = null;

  public String getDisplayName() throws org.osid.logging.LoggingException
  {
    return this.logName;
  }

  public org.osid.shared.Id getId() throws org.osid.logging.LoggingException
  {
    return this.id;
  }

  public void initialize() throws org.osid.logging.LoggingException
  {
  }

  public WritableLog(org.osid.OsidContext context, java.util.Map configuration,
      String logName) throws org.osid.logging.LoggingException
  {
    if (logName == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.NULL_ARGUMENT);
    }

    initialize();
    this.logName = logName;
  }

  public void appendLog(java.io.Serializable entryItem)
      throws org.osid.logging.LoggingException
  {

    try {
	if ( "trace".equalsIgnoreCase(logName) ) {
		if ( LOG.isTraceEnabled() ) LOG.trace(entryItem.toString());
	} else if ( "debug".equalsIgnoreCase(logName) ) {
		if ( LOG.isDebugEnabled() ) LOG.debug(entryItem.toString());
	} else if ( "info".equalsIgnoreCase(logName) ) {
		LOG.info(entryItem.toString());
	} else if ( "warn".equalsIgnoreCase(logName) ) {
		LOG.warn(entryItem.toString());
	} else if ( "error".equalsIgnoreCase(logName) ) {
		LOG.error(entryItem.toString());
	} else if ( "fatal".equalsIgnoreCase(logName) ) {
		LOG.fatal(entryItem.toString());
	} else {
		LOG.info(entryItem.toString());
	}

    } catch(Throwable t) {
	// Ignore - not much to do...
        System.out.println("Sakai OSID Log:"+t.toString());
        System.out.println(this+"Entry="+entryItem);
    }

  }

  public void appendLogWithTypes(java.io.Serializable entryItem,
      org.osid.shared.Type formatType, org.osid.shared.Type priorityType)
      throws org.osid.logging.LoggingException
  {
    if (formatType == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.FORMAT_TYPE_NOT_SET);
    }
    if (priorityType == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.PRIORITY_TYPE_NOT_SET);
    }
    if (entryItem == null)
    {
      throw new org.osid.logging.LoggingException(
          org.osid.logging.LoggingException.NULL_ARGUMENT);
    }
    appendLog(entryItem);
  }

  public void assignPriorityType(org.osid.shared.Type priorityType)
      throws org.osid.logging.LoggingException
  {
    this.priorityType = priorityType;
  }

  public void assignFormatType(org.osid.shared.Type formatType)
      throws org.osid.logging.LoggingException
  {
    this.formatType = formatType;
  }
}
