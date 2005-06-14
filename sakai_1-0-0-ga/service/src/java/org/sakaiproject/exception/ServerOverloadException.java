/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/exception/ServerOverloadException.java,v 1.1 2004/09/09 01:58:24 ggolden.umich.edu Exp $
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
package org.sakaiproject.exception;

/**
* <p>ServerOverloadException is thrown whenever a request cannot be completed at this time due to
* some critical resource of the server being in too short supply.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/

public class ServerOverloadException extends Exception
{
	private String m_id = null;

	public ServerOverloadException(String id)
	{
		m_id = id;
	}

	public String getId()
	{
		return m_id;
	}

	public String toString()
	{
		return super.toString() + " id=" + m_id;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/exception/ServerOverloadException.java,v 1.1 2004/09/09 01:58:24 ggolden.umich.edu Exp $
*
**********************************************************************************/
