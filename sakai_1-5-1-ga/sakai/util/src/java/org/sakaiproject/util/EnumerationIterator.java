/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/EnumerationIterator.java,v 1.4 2004/06/22 03:15:26 ggolden Exp $
*
***********************************************************************************
*
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
*
**********************************************************************************/

// package
package org.sakaiproject.util;

// imports
import java.util.Enumeration;
import java.util.Iterator;

/**
* <p>EnumerationIterator is an iterator over an enumeration.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public class EnumerationIterator
	implements Iterator
{
	/** The enumeration over which this iterates. */
	protected Enumeration m_enum = null;

	public EnumerationIterator(Enumeration e)
	{
		m_enum = e;

	}	// EnumerationIterator

	public Object next() {  return m_enum.nextElement(); }

	public boolean hasNext() { return m_enum.hasMoreElements(); }

	public void remove() {throw new UnsupportedOperationException();}

}	// EnumerationIterator

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/EnumerationIterator.java,v 1.4 2004/06/22 03:15:26 ggolden Exp $
*
**********************************************************************************/
