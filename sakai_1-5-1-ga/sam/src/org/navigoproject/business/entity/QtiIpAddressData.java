/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

/*
 * Created on Nov 21, 2003
 */
package org.navigoproject.business.entity;

import org.apache.ojb.broker.PersistenceBroker;
import org.navigoproject.data.imsglobal.qti.QtiIpAddressDataBean;

/**
 * DOCUMENT ME!
 *
 * @author chmaurer
 */
public class QtiIpAddressData
{
	private QtiIpAddressDataBean bean;
	private static final org.apache.log4j.Logger LOG =
		org.apache.log4j.Logger.getLogger(QtiIpAddressData.class);

	/**
	 *
	 */
	public QtiIpAddressData()
	{
		LOG.debug("new QtiIpAddressData()");
		bean = new QtiIpAddressDataBean();
	}

	/**
	 * Creates a new QtiIpAddressData object.
	 *
	 * @param bean DOCUMENTATION PENDING
	 *
	 * @throws IllegalArgumentException DOCUMENTATION PENDING
	 */
	public QtiIpAddressData(QtiIpAddressDataBean bean)
	{
		if(bean == null)
		{
			throw new IllegalArgumentException(
				"Illegal QtiIpAddressDataBean argument" + bean);
		}

		this.bean = bean;
	}

	/**
	 * Creates a new QtiIpAddressData object.
	 *
	 * @param id DOCUMENTATION PENDING
	 * @param startIp DOCUMENTATION PENDING
	 * @param endIp DOCUMENTATION PENDING
	 *
	 * @throws IllegalArgumentException DOCUMENTATION PENDING
	 */
	public QtiIpAddressData(String id, String startIp, String endIp)
		throws IllegalArgumentException
	{
		if(id == null)
		{
			throw new IllegalArgumentException("Illegal String argument" + id);
		}

		bean = new QtiIpAddressDataBean();
		setId(id);
		setStartIp(startIp);
		setEndIp(endIp);
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	public String getId()
	{
		return bean.getId();
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param id DOCUMENTATION PENDING
	 *
	 * @throws IllegalArgumentException DOCUMENTATION PENDING
	 */
	public void setId(String id)
		throws IllegalArgumentException
	{
		if((id == null) || (id.length() < 1))
		{
			throw new IllegalArgumentException("Illegal String argument: " + id);
		}

		bean.setId(id);
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	public String getStartIp()
	{
		return bean.getStartIp();
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param startIp DOCUMENTATION PENDING
	 */
	public void setStartIp(String startIp)
	{
		bean.setStartIp(getPaddedIp(startIp));
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	public String getEndIp()
	{
		return bean.getEndIp();
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param endIp DOCUMENTATION PENDING
	 */
	public void setEndIp(String endIp)
	{
		bean.setEndIp(getPaddedIp(endIp));
	}

	/**
	 * DOCUMENTATION PENDING
	 */
	public void store()
	{
		bean.store();
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param broker DOCUMENTATION PENDING
	 */
	public void store(PersistenceBroker broker)
	{
		bean.store(broker);
	}

	/**
	 * DOCUMENTATION PENDING
	 */
	public void delete()
	{
		bean.delete();
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param broker DOCUMENTATION PENDING
	 */
	public void delete(PersistenceBroker broker)
	{
		bean.delete(broker);
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	public QtiIpAddressDataBean getBean()
	{
		return bean;
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	protected QtiIpAddressDataBean getQtiIpAddressDataBean()
	{
		return bean;
	}

	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param ip DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	private String getPaddedIp(String ip)
	{
		String retIp = "";
		String[] subIp = null;
		String tempIp = "";

		subIp = ip.split("\\.");
		for(int i = 0; i < subIp.length; i++)
		{
			tempIp = "000" + subIp[i];
			retIp =
				retIp.concat(tempIp.substring(tempIp.length() - 3, tempIp.length()));
			retIp = retIp.concat(".");
		}

		return retIp.substring(0, retIp.length() - 1);
	}
}
