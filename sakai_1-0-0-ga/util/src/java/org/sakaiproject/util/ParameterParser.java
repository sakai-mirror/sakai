/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/ParameterParser.java,v 1.11 2004/09/22 01:13:30 janderse.umich.edu Exp $
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
// some of this code (parseRequest) is adapted from the package org.apache.turbine.services.upload under the Apache 1.1 License:
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.sakaiproject.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.sakaiproject.util.FormattedText;

import com.ibm.icu.text.Normalizer;

/**
 * <p>ParameterParser parses the incoming parameters in an HTTP request.
 * It knows about files.  It trims names and values.
 * Parameters with empty values are registered in the set.
 * Parameters from the URL are mixed with those from multi-part forms.
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.11 $
 */
public class ParameterParser
{
	/** The request size limit in bytes - 20 megs. */
	protected static final int REQUEST_SIZE_LIMIT = 20 * 1024 * 1024;

	/** The Java 2 documentation says that it is thread-safe to use this object across multiple threads! */
	protected static final Charset M_utfCharset = Charset.forName("UTF-8");

	/** If we are pre-parsed, this is a map of names to parameters. */
	protected Map m_params = null;

	protected Map m_paramCache = new HashMap();
	
	/**
	 * A maximum lenght of a single header line that will be
	 * parsed. (1024 bytes).
	 */
	public static final int MAX_HEADER_SIZE = 1024;

	/**
	 * HTTP header.
	 */
	public static final String CONTENT_TYPE = "Content-type";

	/**
	 * HTTP header.
	 */
	public static final String CONTENT_DISPOSITION = "Content-disposition";

	/**
	 * HTTP header base type.
	 */
	public static final String MULTIPART = "multipart";

	/**
	 * HTTP header base type modifier.
	 */
	public static final String FORM_DATA = "form-data";

	/**
	 * HTTP header base type modifier.
	 */
	public static final String MIXED = "mixed";

	/**
	 * HTTP header.
	 */
	public static final String MULTIPART_FORM_DATA = MULTIPART + '/' + FORM_DATA;

	/**
	 * HTTP header.
	 */
	public static final String MULTIPART_MIXED = MULTIPART + '/' + MIXED;

	/** The pathInfo, properly translated into Unicode. */
	protected String m_path = null;

	/**
	 * <p> Processes an <a href="http://rf.cx/rfc1867.html">RFC
	 * 1867</a> compliant <code>multipart/form-data</code> stream.
	 * @param req The servlet request to be parsed.
	 */
	protected void parseRequest(HttpServletRequest req) throws Exception
	{
		String contentType = req.getHeader(CONTENT_TYPE);
		int requestSize = req.getContentLength();
		if (requestSize == -1)
		{
			return;
		}

		// reject the request without parsing if it's over the limit		
		if (requestSize > REQUEST_SIZE_LIMIT)
		{
			return;
		}

		byte[] boundary = contentType.substring(contentType.indexOf("boundary=") + 9).getBytes();
		InputStream input = (InputStream) req.getInputStream();

//// <view the raw request> (Note: consumes the request, too, so use only to debug! -ggolden
//			StringBuffer buf = new StringBuffer();
//			do
//			{
//				int i = input.read();
//				if (i != -1)
//				{
//					if ((i != 10) && (i != 13))
//					{
//						System.out.print(Integer.toHexString(i) + " " + (char)i + " ");
//					}
//					else
//					{
//						System.out.print(Integer.toHexString(i) + " ");
//					}
//					buf.append((char)i);
//				}
//				else
//				{
//					break;
//				}
//			} while (true);
//			System.out.println();
//			System.out.println(buf.toString());
//			if (true) return;
//// </view the raw request>

		MultipartStream multi = new MultipartStream(input, boundary);
		boolean nextPart = multi.skipPreamble();
		while (nextPart)
		{
			Map headers = parseHeaders(multi.readHeaders());
			String fieldName = getFieldName(headers);
			if (fieldName != null)
			{
				String subContentType = getHeader(headers, CONTENT_TYPE);
				if (subContentType != null && subContentType.startsWith(MULTIPART_MIXED))
				{
					// Multiple files.
					byte[] subBoundary = subContentType.substring(subContentType.indexOf("boundary=") + 9).getBytes();
					multi.setBoundary(subBoundary);
					boolean nextSubPart = multi.skipPreamble();
					while (nextSubPart)
					{
						headers = parseHeaders(multi.readHeaders());
						String fileName = getFileName(headers);
						if (fileName != null)
						{
							FileItem item = new FileItem(fileName, getHeader(headers, CONTENT_TYPE));
							OutputStream os = item.getOutputStream();
							try
							{
								multi.readBodyData(os);
							}
							finally
							{
								os.close();
							}
							append(fieldName, item);
						}
						else
						{
							// Ignore anything but files inside
							// multipart/mixed.
							multi.discardBodyData();
						}
						nextSubPart = multi.readBoundary();
					}
					multi.setBoundary(boundary);
				}
				else
				{
					String fileName = getFileName(headers);
					if (fileName != null)
					{
						// A single file.
						FileItem item = new FileItem(fileName, getHeader(headers, CONTENT_TYPE));
						OutputStream os = item.getOutputStream();
						try
						{
							multi.readBodyData(os);
						}
						finally
						{
							os.close();
						}
						append(fieldName, item);
					}
					else
					{
						// A form field.
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						try
						{
							multi.readBodyData(os);
						}
						finally
						{
							os.close();
						}

						// the bytes are raw - not escaped, not encoded... take them literally
						// use java.io canonnical name (see: http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html)
						String value = os.toString("ISO8859_1");
						value = decodeUtf8(value);// TODO:
						append(fieldName, value);
					}
				}
			}
			else
			{
				// Skip this part.
				multi.discardBodyData();
			}
			nextPart = multi.readBoundary();
		}
	}

	/**
	 * Add a string parameter.  Trims names and values.  Blank values are ok.
	 * @param name The parameter name.
	 * @param value The parameter value.
	 */
	protected void append(String name, String value)
	{
		if ((name == null) || (value == null))
			return;
		name = name.trim();
		value = value.trim();
		if (name.length() == 0)
			return;

		// leave blank values
		//		if (value.length() == 0)
		//			return;

		Object prior = m_params.get(name);
		if (prior != null)
		{
			if (prior instanceof String[])
			{
				// add another value to the array
				String[] saPrior = (String[]) prior;
				String[] values = new String[saPrior.length + 1];
				System.arraycopy(saPrior, 0, values, 0, saPrior.length);
				values[saPrior.length] = value;
				m_params.put(name, values);
			}
			else
			{
				// create an array with these values
				String[] values = new String[2];
				values[0] = (String) prior;
				values[1] = value;
				m_params.put(name, values);
			}
		}
		else
		{
			m_params.put(name, value);
		}
	}

	/**
	 * Add a FileItem parameter.  Trims the name.  If the file has no name, this parameter is ignored.
	 * @param name The parameter name.
	 * @param item The parameter value.
	 */
	protected void append(String name, FileItem item)
	{
		if ((name == null) || (item == null))
			return;
		name = name.trim();
		if ((name.length() == 0) || (item.getFileName().length() == 0))
			return;

		m_params.put(name, item);
	}

	/**
	 * <p> Retrieves file name from <code>Content-disposition</code> header.
	 *
	 * @param headers The HTTP request headers.
	 * @return A the file name for the current <code>encapsulation</code>.
	 */
	protected String getFileName(Map headers)
	{
		String fileName = null;
		String cd = getHeader(headers, CONTENT_DISPOSITION);
		if (cd.startsWith(FORM_DATA) || cd.startsWith("attachment"))
		{
			int start = cd.indexOf("filename=\"");
			int end = cd.lastIndexOf('"');
			if ((start != -1) && (end != -1) && (end >= (start + 10)))
			{
				String str = cd.substring(start + 10, end).trim();
				fileName = str;
			}
		}

		if (fileName != null)
		{
			fileName = decodeUtf8(fileName);// TODO:
		}

		return fileName;
	}

	/**
	 * <p> Retrieves field name from <code>Content-disposition</code> header.
	 *
	 * @param headers The HTTP request headers.
	 * @return The field name for the current <code>encapsulation</code>.
	 */
	protected String getFieldName(Map headers)
	{
		String fieldName = null;
		String cd = getHeader(headers, CONTENT_DISPOSITION);
		if (cd != null && cd.startsWith(FORM_DATA))
		{
			int start = cd.indexOf("name=\"");
			int end = cd.indexOf('"', start + 6);
			if (start != -1 && end != -1)
			{
				fieldName = cd.substring(start + 6, end);
			}
		}
		return fieldName;
	}

	/**
	 * <p> Parses the <code>header-part</code> and returns as key/value
	 * pairs.
	 *
	 * <p> If there are multiple headers of the same names, the name
	 * will map to a comma-separated list containing the values.
	 *
	 * @param headerPart The <code>header-part</code> of the current
	 * <code>encapsulation</code>.
	 * @return The parsed HTTP request headers.
	 */
	protected Map parseHeaders(String headerPart)
	{
		Map headers = new HashMap();
		char buffer[] = new char[MAX_HEADER_SIZE];
		boolean done = false;
		int j = 0;
		int i;
		String header, headerName, headerValue;
		try
		{
			while (!done)
			{
				i = 0;
				// Copy a single line of characters into the buffer,
				// omitting trailing CRLF.
				while (i < 2 || buffer[i - 2] != '\r' || buffer[i - 1] != '\n')
				{
					buffer[i++] = headerPart.charAt(j++);
				}
				header = new String(buffer, 0, i - 2);
				if (header.equals(""))
				{
					done = true;
				}
				else
				{
					if (header.indexOf(':') == -1)
					{
						// This header line is malformed, skip it.
						continue;
					}
					headerName = header.substring(0, header.indexOf(':')).trim().toLowerCase();
					headerValue = header.substring(header.indexOf(':') + 1).trim();
					if (getHeader(headers, headerName) != null)
					{
						// More that one heder of that name exists,
						// append to the list.
						headers.put(headerName, getHeader(headers, headerName) + ',' + headerValue);
					}
					else
					{
						headers.put(headerName, headerValue);
					}
				}
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			// Headers were malformed. continue with all that was
			// parsed.
		}
		return headers;
	}

	/**
	 * <p> Returns a header with specified name.
	 *
	 * @param headers The HTTP request headers.
	 * @param name The name of the header to fetch.
	 * @return The value of specified header, or a comma-separated
	 * list if there were multiple headers of that name.
	 */
	protected String getHeader(Map headers, String name)
	{
		return (String) headers.get(name.toLowerCase());
	}

	/** The request. */
	protected HttpServletRequest m_req = null;

	/**
	 * Construct with this request.  Parse parameters, combining URL and multi-part form if needed.
	 * @param req The current request.
	 */
	public ParameterParser(HttpServletRequest req) throws Exception
	{
		m_req = req;

		// if this is from a multipart, we need to parse the request
		String contentType = req.getHeader(CONTENT_TYPE);
		if ((contentType != null) && (contentType.startsWith(MULTIPART_FORM_DATA)))
		{
			m_params = new HashMap();
			parseRequest(req);

			// add any parameters that were in the URL - make sure to get multiples, and deal with decoding
			for (Enumeration e = m_req.getParameterNames(); e.hasMoreElements();)
			{
				String name = (String) e.nextElement();
				String[] values = m_req.getParameterValues(name);
				if (values != null)
				{
					for (int i = 0; i < values.length; i++)
					{
						append(name, decodeUtf8(values[i]));						
					}
				}
			}
		}
		
		m_path = req.getPathInfo();
		if (m_path != null)
		{
			m_path = decodeUtf8(m_path, true);
		}
	}

	/**
	 * Access the parameter names.
	 * @return An Iterator of parameter names (String).
	 */
	public Iterator getNames()
	{
		if (m_params != null)
		{
			return m_params.keySet().iterator();
		}

		return new EnumerationIterator(m_req.getParameterNames());
	}

	/**
	 * Get a (String) parameter by name.
	 * @param name The parameter name.
	 * @return The parameter value, or null if it's not defined.
	 */
	public String get(String name)
	{
		String rv = null;
		if (m_params != null)
		{
			// if it's an array, return the first value
			Object orv = m_params.get(name);
			if (orv != null)
			{
				if (orv instanceof String[])
				{
					rv = ((String[]) orv)[0];
				}
				else
				{
					rv = (String) orv;
				}
			}
		}
		else
		{
			rv = (String) m_paramCache.get(name);
			if (rv != null) return rv;
			
			rv = decodeUtf8(m_req.getParameter(name));

			// trim
			if (rv != null)
			{
				rv = rv.trim();
				//				if (rv.length() == 0)
				//				{
				//					rv = null;
				//				}
			}
			m_paramCache.put(name, rv);
		}

		return rv;
	}

	/**
	 * Get a (String) parameter by name.
	 * @param name The parameter name.
	 * @return The parameter value, or null if it's not defined.
	 */
	public String getString(String name)
	{
		return get(name);
	}

	/**
	 * Get a (String[]) multi-valued parameter by name.
	 * @param name The parameter name.
	 * @return The parameter values array (of String), or null if it's not defined.
	 */
	public String[] getStrings(String name)
	{
		if (m_params != null)
		{
			String[] rv = null;
			Object value = m_params.get(name);
			if (value != null)
			{
				if (value instanceof String[])
				{
					rv = (String[]) value;
				}
				else
				{
					// make an array for the single value
					rv = new String[1];
					rv[0] = (String) value;
				}
			}
			return rv;
		}

		// not parsed, so get it direct - and take care of decoding
		String[] rv = m_req.getParameterValues(name);
		if (rv != null)
		{
			for (int i = 0; i < rv.length; i++)
			{
				rv[i] = decodeUtf8(rv[i]);
			}
		}
		
		return rv;
	}

	/**
	 * Get a boolean parameter by name.
	 * @param name The parameter name.
	 * @return The parameter boolean value, or false if it's not defined.
	 */
	public boolean getBoolean(String name)
	{
		return "true".equalsIgnoreCase(get(name));
	}

	/**
	 * Get an int parameter by name, with default.
	 * @param name The parameter name.
	 * @param deflt The default value.
	 * @return The parameter int value, or the default if it's not defined or not int.
	 */
	public int getInt(String name, int deflt)
	{
		try
		{
			return Integer.parseInt(get(name));
		}
		catch (Throwable t)
		{
			return deflt;
		}
	}

	/**
	 * Get an int parameter by name.
	 * @param name The parameter name.
	 * @return The parameter int value, or 0 if it's not defined or not int.
	 */
	public int getInt(String name)
	{
		return getInt(name, 0);
	}

	/**
	 * Get a FileItem parameter by name.
	 * @param name The parameter name.
	 * @return The parameter FileItem value, or null if it's not defined.
	 */
	public FileItem getFileItem(String name)
	{
		if (m_params != null)
		{
			Object o = m_params.get(name);
			if (o instanceof FileItem)
			{
				return (FileItem) o;
			}
		}

		return null;
	}

	/**
	* Clean the user input string of strange newlines, etc.
	* @param value The user input string.
	* @return value cleaned of string newlines, etc.
	*/
	public String getCleanString(String name)
	{
		String value = getString(name);
		if (value == null) return null;
		if (value.length() == 0) return value;

		final int len = value.length();
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < len; i++)
		{
			char c = value.charAt(i);
			char next = 0;
			if (i+1 < len) next = value.charAt(i+1);

			switch (c)
			{
				case '\r':
				{
					// detect CR LF, make it a \n
					if (next == '\n')
					{
						buf.append('\n');
						// eat the next character
						i++;
					}
					else
					{
						buf.append(c);
					}

				}	break;
				
				default:
				{
					buf.append(c);
				}
			}
		}

		if (buf.charAt(buf.length()-1) == '\n')
		{
			buf.setLength(buf.length()-1);
		}

		return buf.toString();
	}

	/**
	 * Access the pathInfo, properly translated into Unicode.
	 * @return The pathInfo, properly translated into Unicode.
	 */
	public String getPath()
	{
		return m_path;
	}

	/**
	* Test the string - if it has Utf-8 encoding, decode it and return the decoded value,
	* else just return the string.
	* Decode also the HTML Numeric Character References (&%D; and &#XH; see http://www.w3.org/TR/REC-html40/charset.html#entities)
	* @param value The possibly encoded string.
	* @return The definitely decoded value of the string.
	*/
	public static String decodeUtf8(String value)
	{
		return decodeUtf8(value, true);
	}
	
	/**
	* Test the string - if it has Utf-8 encoding, decode it and return the decoded value,
	* else just return the string.
	* Decode also the HTML Numeric Character References (&%D; and &#XH; see http://www.w3.org/TR/REC-html40/charset.html#entities)
	* @param value The possibly encoded string.
	* @param decodeIso8859_1 Whether to convert from ISO8859_1 encoding first; if the string is already UTF-8 set this to false
	* @return The definitely decoded value of the string.
	*/
	private static String decodeUtf8(String value, boolean decodeIso8859_1)
	{
		if (value == null) return null;

		if (decodeIso8859_1)
		{
			try
			{
				// use java.io canonnical name (see: http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html)
				final ByteBuffer strBytes = ByteBuffer.wrap(value.getBytes("ISO8859_1"));
	
				// use java.nio canonnical name (see: http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html)
				final CharsetDecoder decoder = M_utfCharset.newDecoder();
			
				final CharBuffer testChars = decoder.decode(strBytes);
			
	//			if (DEBUG && Log.getLogger("chef").isDebugEnabled())
	//			{
	//				Logger.debug("decodeUtf8: utf-8 detected in value: " + value + " len: " + value.length());
	//				for (int i = 0; i < value.length(); i++)
	//					Logger.debug(" ** " + i + " : " + Integer.toHexString(value.charAt(i)));
	//			}
	
				String value2 = testChars.subSequence(0, testChars.length()).toString();
				value = value2;	
			}
			catch (Exception e)
			{
			}
		}
		
		value = FormattedText.decodeNumericCharacterReferences(value);

		// normalize the Unicode characters
		//
		// this makes sure that the characters used are more compatible with more browsers.
		// Multi-character sequences that have semantically-equivalent single character
		// sequences are replaced.  For example, an "n" with a "tilde" might come in
		// as two Unicode characters, "n" and "tilde".  Normalizing will replace
		// the two characters with a single character that represents "n tilde".  
		// See
		// http://www.unicode.org/reports/tr15/
		// and
		// http://oss.software.ibm.com/icu/userguide/normalization.html
		String normalized = Normalizer.normalize(value, Normalizer.NFKC);
	
		return normalized;		
		
	}   // decodeUtf8
	
}

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/ParameterParser.java,v 1.11 2004/09/22 01:13:30 janderse.umich.edu Exp $
*
**********************************************************************************/
