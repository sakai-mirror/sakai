/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/ParameterParser.java,v 1.18.2.3 2005/03/21 15:19:09 lance.indiana.edu Exp $
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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import com.ibm.icu.text.Normalizer;

/**
 * ParameterParser is a wrapper over the request that provides
 * some convienience methods for the Sakai framework.  It expects
 * other code to take care of UTF-8 character
 * encoding and file uploads, except for a few Sakai-specific details.  
 * 
 * The parameter parser DOES do the following to parameters:
 *  * <xmp>Decode HTML numeric character references like "&#234;"</xmp>
 *  * Normalize Unicode characters
 * 
 * The other pieces that take care of UTF-8 and uploads are:
 *  * The Tomcat Connector setting URIEncoding="UTF-8" (in server.xml)
 *  * The SetCharacterEncodingFilter
 *  * The HandleFileUploadFilter
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.18.2.3 $
 */
public class ParameterParser
{
	/** The Java 2 documentation says that it is thread-safe to use this object across multiple threads! */
	protected static final Charset M_utfCharset = Charset.forName("UTF-8");

	/** A cache of names to parameters, used by the get() method for speed */
	protected Map m_paramCache = new HashMap();
	
	/** The pathInfo, properly translated into Unicode. */
	protected String m_path = null;

	/** Whether the path has already been decoded */ 	
	protected boolean m_alreadyDecodedPath = false;

	/** The request. */
	protected HttpServletRequest m_req = null;

	/**
	 * Construct with this request.  
	 * @param req The current request.
	 */
	public ParameterParser(HttpServletRequest req) throws Exception
	{
		m_req = req;
	}

	/**
	 * Access the parameter names.
	 * @return An Iterator of parameter names (String).
	 */
	public Iterator getNames()
	{
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
		rv = (String) m_paramCache.get(name);
		if (rv != null) return rv;
		
		rv = decode(m_req.getParameter(name));

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
		// not parsed, so get it direct - and take care of decoding
		String[] rv = m_req.getParameterValues(name);
		if (rv != null)
		{
			for (int i = 0; i < rv.length; i++)
			{
				rv[i] = decode(rv[i]);
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
		// wrap the Apache FileItem in our own homegrown FileItem
		Object o = m_req.getAttribute(name);
		if (o != null && o instanceof org.apache.commons.fileupload.FileItem)
		{
		    org.apache.commons.fileupload.FileItem item = (org.apache.commons.fileupload.FileItem) o;
		    return new FileItem(item.getName(), item.getContentType(), item.get());
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
	 * Access the pathInfo.
	 * @return The pathInfo.
	 */
	public String getPath()
	{
		String path = m_req.getPathInfo();
		if (!m_alreadyDecodedPath && path != null && !path.equals(m_path))
		{
		    // only decode the path ONCE
		    m_alreadyDecodedPath = true;
			path = decode(path);
		}
		m_path = path;
		return m_path;
	}

	
	/**
	* Decode the given value's HTML Numeric Character references, and normalize the Unicode.
	* Decode also the HTML Numeric Character References (&%D; and &#XH; see http://www.w3.org/TR/REC-html40/charset.html#entities)
	* @param value The string which should have HTML character references decoded and should be normalized.
	* @return The decoded value of the string.
	*/
	public static String decode(String value)
	{
		if (value == null) return null;
		if (value.length() == 0) return "";

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
		
	}   // decode
	
	/** 
	 * This method should ONLY be used in special cases where
	 * the servlet container doesn't handle a string as UTF-8
	 * automatically (as it should be configured to do).
	 */
	public static String convertFromRawBytesToUTF8(String value)
		throws UnsupportedEncodingException, CharacterCodingException
	{

		// use java.io canonnical name (see: http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html)
		final ByteBuffer strBytes = ByteBuffer.wrap(value.getBytes("ISO8859_1"));

		// use java.nio canonnical name (see: http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html)
		final CharsetDecoder decoder = M_utfCharset.newDecoder();
	
		final CharBuffer testChars = decoder.decode(strBytes);
	
//			if (DEBUG && Log.getLogger("chef").isDebugEnabled())
//			{
//				Logger.debug("decode: utf-8 detected in value: " + value + " len: " + value.length());
//				for (int i = 0; i < value.length(); i++)
//					Logger.debug(" ** " + i + " : " + Integer.toHexString(value.charAt(i)));
//			}

		return testChars.subSequence(0, testChars.length()).toString();
	}
	
}

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/ParameterParser.java,v 1.18.2.3 2005/03/21 15:19:09 lance.indiana.edu Exp $
*
**********************************************************************************/
