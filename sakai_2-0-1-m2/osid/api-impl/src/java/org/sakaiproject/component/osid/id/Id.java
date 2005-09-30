/**********************************************************************************
 *
 * $Header: /cvs/sakai2/osid/api-impl/src/java/org/sakaiproject/component/osid/id/Id.java,v 1.2 2005/05/21 02:48:37 ggolden.umich.edu Exp $
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

package org.sakaiproject.component.osid.id;

// Simply Call the Legacy Service to get the Id

import org.sakaiproject.service.legacy.id.cover.IdService;

public class Id implements org.osid.shared.Id
{
	private String idString = null;

	private void log(String entry) throws org.osid.shared.SharedException
	{
		System.out.println("Id:" + entry);
	}

	protected Id() throws org.osid.shared.SharedException
	{
		idString = IdService.getUniqueId();
	}

	protected Id(String idString) throws org.osid.shared.SharedException
	{
		if (idString == null)
		{
			throw new org.osid.shared.SharedException(org.osid.id.IdException.NULL_ARGUMENT);
		}
		this.idString = idString;
	}

	public String getIdString() throws org.osid.shared.SharedException
	{
		return this.idString;
	}

	public boolean isEqual(org.osid.shared.Id id) throws org.osid.shared.SharedException
	{
		return id.getIdString().equals(this.idString);
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/osid/api-impl/src/java/org/sakaiproject/component/osid/id/Id.java,v 1.2 2005/05/21 02:48:37 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
