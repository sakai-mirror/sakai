/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/component/sample/crud/CrudObjectImpl.java,v 1.3 2004/06/22 03:10:39 ggolden Exp $
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

package org.sakaiproject.component.sample.crud;

import java.util.Date;

import org.sakaiproject.service.sample.crud.CrudObject;

/**
 * <p>CrudObjectImpl implements the CrudObject for the CrudServiceImpl.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.3 $
 */
public class CrudObjectImpl implements CrudObject
{
	/** The id. */
	protected String m_id = null;

	/** The version. */
	protected Date m_version = null;

	/** The "name". */
	protected String m_name = "";

	/** The "rank". */
	protected String m_rank = "";

	/** The "serial number". */
	protected String m_serialNumber = "";

	/**
	 * Construct.
	 */
	protected CrudObjectImpl()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * Set the id - should be used only by the storage layer, not by end users!
	 * @param id The object id.
	 */
	public void setId(String id)
	{
		m_id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getVersion()
	{
		return m_version;
	}

	/**
	 * Set the version - should be used only by the storage layer, not by end users!
	 * @param version The object version.
	 */
	public void setVersion(Date version)
	{
		m_version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName()
	{
		return m_name;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setName(String name)
	{
		m_name = name;
		if (m_name == null)
			m_name = "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRank()
	{
		return m_rank;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRank(String rank)
	{
		m_rank = rank;
		if (m_rank == null)
			m_rank = "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSerialNumber()
	{
		return m_serialNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSerialNumber(String sn)
	{
		m_serialNumber = sn;
		if (m_serialNumber == null)
			m_serialNumber = "";
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object o)
	{
		if (!(o instanceof CrudObject))
			throw new ClassCastException();

		// if the object are the same, say so
		if (o == this)
			return 0;

		// start the compare by comparing their names
		int compare = getName().compareTo(((CrudObject) o).getName());

		// if these are the same
		if (compare == 0)
		{
			// compare rank
			compare = getRank().compareTo(((CrudObject) o).getRank());

			// if these are the same
			if (compare == 0)
			{
				// compare serial number
				compare = getSerialNumber().compareTo(((CrudObject) o).getSerialNumber());
			}
		}

		return compare;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CrudObject))
			return false;

		return ((CrudObject) obj).getId().equals(getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return getId().hashCode();
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/component/sample/crud/CrudObjectImpl.java,v 1.3 2004/06/22 03:10:39 ggolden Exp $
*
**********************************************************************************/
