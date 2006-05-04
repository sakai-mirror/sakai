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

public class StringIterator implements org.osid.shared.StringIterator
{
  private java.util.Vector vector = new java.util.Vector();
  private int i = 0;

  public StringIterator(java.util.Vector vector)
      throws org.osid.shared.SharedException
  {
    this.vector = vector;
  }

  public String toString() 
  {
    String retval = super.toString();
    for ( int j = 0; j < vector.size(); j++ )
    {
	retval = retval + " " + vector.elementAt(j);
    }
    return retval;
  }

  public boolean hasNextString() throws org.osid.shared.SharedException
  {
    return i < vector.size();
  }

  public String nextString() throws org.osid.shared.SharedException
  {
    if (i < vector.size())
    {
      return (String) vector.elementAt(i++);
    }
    else
    {
      throw new org.osid.shared.SharedException(
          org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS);
    }
  }
}
