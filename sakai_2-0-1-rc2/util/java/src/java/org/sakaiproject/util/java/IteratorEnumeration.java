/**********************************************************************************
 *
 * $Header: /cvs/sakai2/util/java/src/java/org/sakaiproject/util/java/IteratorEnumeration.java,v 1.1 2005/05/12 01:39:07 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
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

// package
package org.sakaiproject.util.java;

// imports
import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>
 * IteratorEnumeration is an enumeration over an iterator.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class IteratorEnumeration implements Enumeration
{
	/** The iterator over which this enumerates. */
	protected Iterator m_iterator = null;

	public IteratorEnumeration(Iterator i)
	{
		m_iterator = i;
	}

	public boolean hasMoreElements()
	{
		return m_iterator.hasNext();
	}

	public Object nextElement()
	{
		return m_iterator.next();
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/util/java/src/java/org/sakaiproject/util/java/IteratorEnumeration.java,v 1.1 2005/05/12 01:39:07 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
