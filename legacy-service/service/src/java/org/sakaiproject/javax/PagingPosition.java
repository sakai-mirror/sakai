/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/javax/PagingPosition.java,v 1.1 2005/05/12 15:45:41 ggolden.umich.edu Exp $
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

package org.sakaiproject.javax;

/**
* <p>PagingPosition models a current position in a paging display, with a first and last item value, 1 based.</p>
* <p>Implementation note: the default Object.equals() is fine for this class.</p>
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class PagingPosition implements Cloneable
{
	/** The first item position on the current page, 1 based. */
	protected int m_first = 1;

	/** The last item position on the current page, 1 based. */
	protected int m_last = 1;

	/** If true, paging is ebabled, otherwise all items should be used. */
	protected boolean m_paging = true;

	/**
	 * Construct, setting position to select all possible items.
	 */
	public PagingPosition()
	{
		m_first = 1;
		m_last = 1;
	}

	/**
	 * Construct, setting the first and last.
	 * @param first The first item position, 1 based.
	 * @param last The last item position, 1 based.
	 */
	public PagingPosition(int first, int last)
	{
		m_first = first;
		m_last = last;
		validate();
	}

	/**
	 * Adjust the first and list item position by distance, positive or negative.
	 * @param distance The positive or negative distance to move the first and last item positions.
	 */
	public void adjustPostition(int distance)
	{
		m_first += distance;
		m_last += distance;
		validate();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ignore)
		{
			throw new Error("Assertion failure");
		}
	}

	/**
	 * Access the first item position, 1 based.
	 * @return the first item position, 1 based.
	 */
	public int getFirst()
	{
		return m_first;
	}

	/**
	 * Access the last item position, 1 based.
	 * @return the last item position, 1 based.
	 */
	public int getLast()
	{
		return m_last;
	}

	/**
	 * Check if we have paging enabled.
	 * @return true if paging is enabled, false if not.
	 */
	public boolean isPaging()
	{
		return m_paging;
	}

	/**
	 * Set the paging enabled value.
	 * @param paging the new paging enabled value.
	 */
	public void setPaging(boolean paging)
	{
		m_paging = paging;
	}

	/**
	 * Set the first and last positions.
	 * @param first The new first item position, 1 based.
	 * @param last The new last item position, 1 based.
	 */
	public void setPosition(int first, int last)
	{
		m_first = first;
		m_last = last;
		validate();
	}
	/**
	 * Adjust the first and last to be valid.
	 */
	protected void validate()
	{
		if (m_first < 0)
			m_first = 1;
		if (m_last < m_first)
			m_last = m_first;
	}

	/**
	 * Adjust the first and last to be valid and within the range 1..biggestLast
	 * @param biggestLast The largest valid value for last
	 */
	public void validate(int biggestLast)
	{
		if (m_first < 0)
			m_first = 1;
		if (m_last > biggestLast)
			m_last = biggestLast;
		if (m_last < m_first)
			m_last = m_first;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/javax/PagingPosition.java,v 1.1 2005/05/12 15:45:41 ggolden.umich.edu Exp $
*
**********************************************************************************/
