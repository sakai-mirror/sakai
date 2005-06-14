/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/service/sample/crud/CrudObject.java,v 1.4 2004/06/22 03:10:39 ggolden Exp $
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

package org.sakaiproject.service.sample.crud;

import java.util.Date;

/**
 * <p>CrudObject is ... something with an id and a version (name, rank, serial number - why not!).</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.4 $
 */
public interface CrudObject extends Comparable
{
	/**
	 * Access the unique id. 
	 * @return The unique id.
	 */
	String getId();

	/**
	 * Access the object's version time stamp.
	 * @return The object's version time stamp.
	 */
	Date getVersion();

	/**
	 * Access "name".
	 * @return "name".
	 */
	String getName();

	/**
	 * Set the "name".
	 * @param name The new "name".
	 */
	void setName(String name);

	/**
	 * Access "rank".
	 * @return "rank".
	 */
	String getRank();

	/**
	 * Set the "rank".
	 * @param rank The new "rank".
	 */
	void setRank(String rank);

	/**
	 * Access "serial number".
	 * @return "serial number".
	 */
	String getSerialNumber();

	/**
	 * Set the "serial number".
	 * @param sn The new "serial number".
	 */
	void setSerialNumber(String sn);
}

/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/service/sample/crud/CrudObject.java,v 1.4 2004/06/22 03:10:39 ggolden Exp $
*
**********************************************************************************/
