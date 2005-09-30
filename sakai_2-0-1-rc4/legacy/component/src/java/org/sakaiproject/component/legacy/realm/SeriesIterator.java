/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/realm/SeriesIterator.java,v 1.1 2005/04/06 02:42:37 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.realm;

// imports
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* <p>SeriesIterator is an iterator over a series of other iterators.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class SeriesIterator
	implements Iterator
{
	/** The enumeration over which this iterates. */
	protected Iterator[] m_iterators = null;

	/** The m_iterators index that is current. */
	protected int m_index = 0;

	/**
	* Construct to handle a series of two iterators.
	* @param one The first iterator.
	* @param two The second iterator.
	*/
	public SeriesIterator(Iterator one, Iterator two)
	{
		m_iterators = new Iterator[2];
		m_iterators[0] = one;
		m_iterators[1] = two;

	}	// SeriesIterator

	public Object next()
		throws NoSuchElementException
	{
		while (!m_iterators[m_index].hasNext())
		{
			m_index++;
			if (m_index >= m_iterators.length) throw new NoSuchElementException();
		}
		return m_iterators[m_index].next();
	}

	public boolean hasNext()
	{
		while (!m_iterators[m_index].hasNext())
		{
			m_index++;
			if (m_index >= m_iterators.length) return false;
		}
		return true;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}	// SeriesIterator

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/realm/SeriesIterator.java,v 1.1 2005/04/06 02:42:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
