/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/FileItem.java,v 1.1 2004/07/11 01:54:47 ggolden.umich.edu Exp $
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

package org.sakaiproject.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>FileItem is ...</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.1 $
 */
public class FileItem
{
	/** Body stored in memory, filled in using this stream. */
	protected ByteArrayOutputStream m_body = new ByteArrayOutputStream();

	/** file name. */
	protected String m_name = null;

	/** file type. */
	protected String m_type = null;

	/**
	 * Construct
	 * @param fileName The file name.
	 * @param fileType The file type.
	 */
	public FileItem(String fileName, String fileType)
	{
		if (fileName != null)
			m_name = fileName.trim();
		if (fileType != null)
			m_type = fileType.trim();
	}

	/**
	 * @return
	 */
	public String getFileName()
	{
		return m_name;
	}

	/**
	 * @return
	 */
	public String getContentType()
	{
		return m_type;
	}

	/**
	 * Access the body as a String.
	 */
	public String getString()
	{
		String rv = null;
		try
		{
			// this should give us byte for byte translation, no encoding/decoding
			rv = m_body.toString("ISO8859_1");
		}
		catch (UnsupportedEncodingException ignore) {}

		m_body = null;

		return rv;
	}

	/**
	 * Access the body as a byte array.  This consumes the entry.
	 * @return
	 */
	public byte[] get()
	{
		byte[] content = m_body.toByteArray();
		m_body = null;

		return content;
	}

	/**
	 * @return
	 */
	OutputStream getOutputStream()
	{
		return m_body;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/FileItem.java,v 1.1 2004/07/11 01:54:47 ggolden.umich.edu Exp $
*
**********************************************************************************/
