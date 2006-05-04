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

public class Entry implements org.osid.logging.Entry
{
  private java.io.Serializable item = null;

  public java.io.Serializable getItem()
      throws org.osid.logging.LoggingException
  {
    return this.item;
  }

  public Entry(String line) throws org.osid.logging.LoggingException
  {
    this.item = line;
  }

  public long getTimestamp() throws org.osid.logging.LoggingException
  {
    return 0;
  }

  public org.osid.shared.Type getFormatType()
      throws org.osid.logging.LoggingException
  {
      return new Type("sakaiproject.org","log","plain","Plain Logging Type");
  }

  public org.osid.shared.Type getPriorityType()
      throws org.osid.logging.LoggingException
  {
      return new Type("sakaiproject.org","log","priority","Priority Logging Type");
  }
}
