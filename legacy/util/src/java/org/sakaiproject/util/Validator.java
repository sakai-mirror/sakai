/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.util;

// imports
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;

/**
* <p>Validator is utility class that helps to validate stuff for CHEF.</p>
* <p>It is also a Turbine PULL tool so it can be used in a .vm file</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class Validator
{
	/** These characters are not allowed in a resource id */
	protected static final String INVALID_CHARS_IN_RESOURCE_ID = "^/\\%*?#\n\r\t\b\f";
	
	/** These characters are allowed; but if escapeResourceName() is called, they are escaped (actually, removed) 
	 * Certain characters cause problems with filenames in certain OSes - so get rid of these characters in filenames
	 * 
	 */
	protected static final String ESCAPE_CHARS_IN_RESOURCE_ID = ";'\"";
	
	protected static final String INVALID_CHARS_IN_ZIP_ENTRY = "/\\%:*?'\"";
	protected static final String INVALID_MSG = " cannot contain any of the following characters: / \\ % * ? #";
	protected static final String BLANK_MSG = " cannot be blank";

	/** These characters are escaped when making a URL */
//	protected static final String ESCAPE_URL = "#%?&='\"+ ";
	// not '/' as that is assumed to be part of the path
	protected static final String ESCAPE_URL = "$&+,:;=?@ '\"<>#%{}|\\^~[]`";
	
	/** These can't be encoded in URLs safely even using %nn notation, so encode them using our own
	 * custom URL encoding, which the ParameterParser decodes */
	protected static final String ESCAPE_URL_SPECIAL = "^?;";

	/** Valid special email local id characters (- those that are invalid resource ids) */
	protected static final String VALID_EMAIL = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ!#$&'*+-=?^_`{|}~.";


	/**
	* Escape a plaintext string so that it can be output as part of an HTML document.
	* Amperstand, greater-than, less-than, newlines, etc, will be escaped so that they 
	* display (instead of being interpreted as formatting).
	* @param value The string to escape.
	* @return value fully escaped for HTML.
	*/
	public static String escapeHtml(String value)
	{
		return FormattedText.escapeHtml(value, true);
	}

	
	/**
	* Escape a plaintext string so that it can be output as part of an HTML document,
	* except that newlines are NOT escaped and therefore are treated as whitespace instead
	* of displaying as line-breaks.
	* Amperstand, greater-than, less-than, etc, will be escaped so that they 
	* display (instead of being interpreted as formatting).
	* @param value The string to escape.
	* @return value fully escaped for HTML.
	*/
	public static String escapeHtmlSupressNewlines(String value)
	{
		return FormattedText.escapeHtml(value, false);
	}	
	
	
	/**
	* Escape plaintext for display inside a plain textarea.
	*/
	public static String escapeHtmlTextarea(String value)
	{
		return FormattedText.escapeHtml(value, false);
	}
	
	
	/** 
	 * Escape HTML-formatted text in preparation to include it in an HTML document.
	 */
	public static String escapeHtmlFormattedText(String value)
	{
		return FormattedText.escapeHtmlFormattedText(value);
	}
	
	/** 
	 * Escape HTML-formatted text in preparation to include it in an HTML document,
	 * except that HTML line breaks ("&lt;br /&gt;") will be supressed (removed).
	 */
	public static String escapeHtmlFormattedTextSupressNewlines(String value)
	{
		return FormattedText.escapeHtmlFormattedTextSupressNewlines(value);
	}
	
	/**
	 * Escapes the given HTML-formatted text for editing within the WYSIWYG editor.
	 * All HTML meta-characters in the string (such as amperstand, less-than, etc), will be escaped.  
	 * @param value The formatted text to escape
	 * @return The string to use as the value of the formatted textarea widget
	 */	
	public static String escapeHtmlFormattedTextarea(String value)
	{
		return FormattedText.escapeHtmlFormattedTextarea(value);
	}

	/**
	* escapeHtml(), but also fix the case where we start with &#169; and treat it as copyright (c)
	* Note: ResourcesAction used to (before 1.1.05) place this as the copyright symbol. -ggolden
	*/
	public static String escapeHtmlFixCopyright(String value)
	{
		if (value.startsWith("&#169;"))
		{
			value = "copyright (c)" + value.substring(6);
		}
		
		return escapeHtml(value);

	}	// escapeHtmlFixCopyright	
	
	/**
	* Return a string based on id that is fully escaped using URL rules,
	* using a UTF-8 underlying encoding.
	* @param id The string to escape.
	* @return id fully escaped using URL rules.
	*/
	public static String escapeUrl(String id)
	{
		if (id == null) return "";
		try
		{
			// convert the string to bytes in UTF-8
			byte[] bytes = id.getBytes("UTF-8");
			
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < bytes.length; i++)
			{
				byte b = bytes[i];
				// escape ascii control characters, ascii high bits, specials
				if (ESCAPE_URL_SPECIAL.indexOf((char)b) != -1)
				{
					buf.append("^^x"); // special funky way to encode bad URL characters - ParameterParser will decode it
					buf.append(toHex(b));
					buf.append('^');
				}
				else if (	(ESCAPE_URL.indexOf((char)b) != -1)
					||	(b <= 0x1F)
					||	(b == 0x7F)
					||	(b >= 0x80))
				{
					buf.append("%");
					buf.append(toHex(b));
				}
				else
				{
					buf.append((char)b);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeUrl: ", e);
			return id;
		}

	}	// escapeUrl

	/**
	* Return a string based on id that is fully escaped using Resource name validity rules.
	* @param id The string to escape.
	* @return id fully escaped using Resource name validity rules.
	*/
	public static String escapeResourceName(String id)
	{
		if (id == null) return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < id.length(); i++)
			{
				char c = id.charAt(i);
				if (INVALID_CHARS_IN_RESOURCE_ID.indexOf(c) != -1 || ESCAPE_CHARS_IN_RESOURCE_ID.indexOf(c) != -1)
				{
					buf.append('_');
				}
				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeResourceName: ", e);
			return id;
		}

	}	// escapeResourceName
	
	/**
	* Return a string based on id that is fully escaped the question mark.
	* @param id The string to escape.
	* @return id fully escaped question mark.
	*/
	public static String escapeQuestionMark(String id)
	{
		if (id == null) return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < id.length(); i++)
			{
				char c = id.charAt(i);
				if (c == '?')
				{
					buf.append('_');
				}
				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeQuestionMark: ", e);
			return id;
		}

	}	// escapeQuestionMark
	
	/**
	* Return a string based on id that is fully escaped to create a zip entry
	* @param id The string to escape.
	* @return id fully escaped to create a zip entry
	*/
	public static String escapeZipEntry(String id)
	{
		if (id == null) return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < id.length(); i++)
			{
				char c = id.charAt(i);
				if (INVALID_CHARS_IN_ZIP_ENTRY.indexOf(c) != -1)
				{
					buf.append('_');
				}
				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeZipEntry: ", e);
			return id;
		}

	}	// escapeZipEntry

	
	/**
	* Return a string based on value that is safe to place into a javascript value that is in single quiotes.
	* @param value The string to escape.
	* @return value escaped.
	*/
	public static String escapeJsQuoted(String value)
	{
		if (value == null) return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < value.length(); i++)
			{
				char c = value.charAt(i);
				
				// a single quote must be escaped with a leading backslash
				if (c == '\'')
				{
					buf.append("\\'");
				}

				// a backslash must be escaped with another backslash
				else if (c == '\\')
				{
					buf.append("\\\\");
				}

				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeJsQuoted: ", e);
			return value;
		}

	}	// escapeJsQuoted

	
	/**
	* Return a string based on value that is safe to place into a sql statement:
	* sql statements use the single quote, and this must be doubled as an escape.
	* @param value The string to escape.
	* @return value escaped.
	*/
	public static String escapeSql(String value)
	{
		if (value == null) return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < value.length(); i++)
			{
				char c = value.charAt(i);
				if (c == '\'')
				{
					buf.append("''");
				}
				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeSql: ", e);
			return value;
		}

	}	// escapeSql
	
	/**
	* Return a string based on value that is safe to place into a javascript / html identifier:
	* anything not alphanumeric change to 'x'. If the first character is not alphabetic, a
	* letter 'i' is prepended.
	* @param value The string to escape.
	* @return value fully escaped using javascript / html identifier rules.
	*/
	public static String escapeJavascript(String value)
	{
		if (value == null || value == "") return "";
		try
		{
			StringBuffer buf = new StringBuffer();
			
			// prepend 'i' if first character is not a letter
			if(! java.lang.Character.isLetter(value.charAt(0)))
			{
				buf.append("i");
			}
			
			// change non-alphanumeric characters to 'x'
			for (int i = 0; i < value.length(); i++)
			{
				char c = value.charAt(i);
				if (! java.lang.Character.isLetterOrDigit(c))
				{
					buf.append("x");
				}
				else
				{
					buf.append(c);
				}
			}
			
			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			Log.warn("chef", "Validator.escapeJavascript: ", e);
			return value;
		}

	}	// escapeJavascript
	
	
	/**
	* Check for a valid resource id.
	* @exception IdInvalidException if the id is invalid.
	*/
	public static void checkResourceId(String id)
		throws IdInvalidException
	{
		// the rules:
		//	Null is rejected
		//	all blank is rejected
		//	INVALID_CHARS_IN_RESOURCE_ID characters are rejected

		if (id == null) throw new IdInvalidException(BLANK_MSG);
		if (id.trim().length() == 0) throw new IdInvalidException(BLANK_MSG);
		
		// we must reject certain characters that we cannot even escape and get into Tomcat via a URL
		for (int i = 0; i < id.length(); i++)
		{
			if (INVALID_CHARS_IN_RESOURCE_ID.indexOf(id.charAt(i)) != -1)
				throw new IdInvalidException(INVALID_MSG);
		}

	}	// checkResourceId

	/**
	* Check for a valid resource reference.
	* @exception IdInvalidException if the id is invalid.
	*/
	public static void checkResourceRef(String ref)
		throws IdInvalidException
	{
		// the rules:
		//	Null is rejected
		//	all blank is rejected
		//	INVALID_CHARS_IN_RESOURCE_ID characters are rejected
		
		Reference r = EntityManager.newReference(ref);
		
		// just check the id... %%% need more? -ggolden
		String id = r.getId();

		if (id == null) throw new IdInvalidException(BLANK_MSG);
		if (id.trim().length() == 0) throw new IdInvalidException(BLANK_MSG);

		// we must reject certain characters that we cannot even escape and get into Tomcat via a URL
		for (int i = 0; i < id.length(); i++)
		{
			if (INVALID_CHARS_IN_RESOURCE_ID.indexOf(id.charAt(i)) != -1)
				throw new IdInvalidException(INVALID_MSG);
		}

	}	// checkResourceRef

	/**
	* Is this a valid local part of an email id?
	*/
	public static boolean checkEmailLocal(String id)
	{
		// rules based on rfc2882, but a bit more conservative

		for (int i = 0; i < id.length(); i++)
		{
			if (VALID_EMAIL.indexOf(id.charAt(i)) == -1)
				return false;
		}

		return true;

	}	// checkEmailLocal
	
	
	/**
	* Isolate and return just the file name part of a full drive and path file name.
	* @param fullName The full file name from a local os file system (mac, unix, windoze)
	* @return Just the name (and extension) of the file, without the drive or path.
	*/
	public static String getFileName(String fullName)
	{
		// examples:	windows:	c:\this\that\me.doc
		//				unix:		/usr/local/dev/test.txt
		//				mac:?		one:two:three:four
		// so... just take the last characters back till we see a \ or / or :
		StringBuffer buf = new StringBuffer();
		int index = fullName.length()-1;
		while (index >= 0)
		{
			char c = fullName.charAt(index--);
			if ((c == '\\') || (c == '/') || (c == ':')) break;
			buf.insert(0, c);
		}
		
		return buf.toString();

	}	// getFileName

	/**
	* Put the dividor (comma) inside the size string, for example, 1,003 for 1003
	* @param size The string of size number
	* @return The size string with the dividor added
	*/
	public static String getFileSizeWithDividor (String size)
	{
		StringBuffer newSize = new StringBuffer(size);

		int length = size.length ();
		int index = size.length ();

		while (index > 3)
		{
			index = index - 3;
			newSize.insert (index, ",");
		}

		return newSize.toString ();
	}
	
	/**
	* Isolate and return just the file extension part of a full drive and path file name.
	* @param fullName The full file name from a local os file system (mac, unix, windoze)
	* @return Just the extension of the file, to the right of the dot, not including the dot, or blank if none.
	*/
	public static String getFileExtension(String fullName)
	{
		// just take from the last dot to the end, or return "" if there's no dot.
		int index = fullName.lastIndexOf('.');
		if (index == -1) return "";
		
		return fullName.substring(index+1);

	}	// getFileExtension
	
	/**
	* Determine whether a file resource should be opened in the current window
	* or a new window.
	* @param properties The properties for a file resource
	* @return A string identifying the window in which to open the resource: "_self" 
	* to open the resource in the current window, "_blank" for a new window, or an
	* empty string if the resource is not a file.
	*/
	public static String getResourceTarget(ResourceProperties properties)
	{
		// we will open a new window unless...
		String rv = "_blank";
		
		// get the resource's type
		String contentType = properties.getProperty(ResourceProperties.PROP_CONTENT_TYPE);
		if(contentType != null)
		{
			// if the browser will not inline, but mark as attachments, let's not open a new window
			if (!letBrowserInline(contentType))
			{
				rv = "_self";
			}
		}

		return rv;

	}	// getResourceTarget

	/**
	* Is this a mime type that the browser can handle inline, in a browser window?
	* If so, links to this type should be to a _blank, and content-disposition should be inline.
	* If not, links to this type should be to _self, and content-disposition should be attachment.
	* @param type The mime type to check.
	* @return true if this type of resource the browser can handle in line, false if not.
	*/
	public static boolean letBrowserInline(String type)
	{
		if (type == null) return false;
		
		String lType = type.toLowerCase();

		if (lType.startsWith("text/")) return true;
		if (lType.startsWith("image/")) return true;
		if (lType.equals("application/pdf")) return true;
		// checks for VRML file MIME types:x-world/x-vrml, model/vrml, application/x-blaxxunCC3D, application/x-blaxxunCC3Dpro, application/x-CC3D
		// need to check for any other MIME types which can be opened by browser plug-ins? %%%zqian
		if (lType.indexOf("vrml") != -1 || lType.indexOf("CC3D") != -1) return true;

		return false;
	
	}	// letBrowserInline

	/**
	* Limit the string to a certain number of characters, adding "..." if it was truncated.
	* @param value The string to limit.
	* @param the length to limit to (as an Integer).
	* @return The limited string.
	*/
	public static String limit(String value, Integer length)
	{
		return limit(value, length.intValue());

	}	// limit

	/**
	* Limit the string to a certain number of characters, adding "..." if it was truncated
	* @param value The string to limit.
	* @param the length to limit to (as an int).
	* @return The limited string.
	*/
	public static String limit(String value, int length)
	{
		StringBuffer buf = new StringBuffer(value);
		if (buf.length() > length)
		{
			buf.setLength(length);
			buf.append("...");
		}
		
		return buf.toString();

	}	// limit
	
	/**
	* Limit the formatted to a certain number of DISPLAYED characters, adding "..." if it was truncated.
	* For example, <xmp>trim("Hello \n<b>World</b>!", 7)</xmp> returns <xmp>"Hello \n<b>W</b>..."</xmp>
	* @param value The formatted text to limit.
	* @param the length to limit to (as an int).
	* @return The limited string.
	*/
	public static String limitFormattedText(String value, int length)
	{
		StringBuffer ret = new StringBuffer();
		value = FormattedText.escapeHtmlFormattedTextSupressNewlines(value);
		boolean didTrim = FormattedText.trimFormattedText(value, length, ret);
		if (didTrim) ret.append("...");
		return ret.toString();
	}

	/**
	* Clean the user input string of strange newlines, etc.
	* @param value The user input string.
	* @return value cleaned of string newlines, etc.
	*/
	public static String cleanInput(String value)
	{
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

	}	// cleanInput

	/**
	* Clean the string parameter of all newlines (replace with space character)
	* and trim leading and trailing spaces
	* @param value The user input string.
	* @return value cleaned of newlines, etc.
	*/
	public static String stripAllNewlines(String value)
	{
		if (value == null) return null;
		value = value.trim();
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
				case '\n':
				case '\r':
				{
					buf.append(' ');
				}	break;
				
				default:
				{
					buf.append(c);
				}
			}
		}

		return buf.toString();

	}	// stripAllNewlines

	/**
	* Returns a hex representation of a byte.
	* @param b The byte to convert to hex.
	* @return The 2-digit hex value of the supplied byte.
	*
	*/
	private static final String toHex(byte b) {

		char ret[] = new char[2];

		ret[0] = hexDigit((b >>> 4) & (byte)0x0F);
		ret[1] = hexDigit((b >>> 0) & (byte)0x0F);

		return new String(ret);
	}

	/**
	* Returns the hex digit cooresponding to a number between 0 and 15.
	* @param i The number to get the hex digit for.
	* @return The hex digit cooresponding to that number.
	* @exception java.lang.IllegalArgumentException If supplied digit
	* is not between 0 and 15 inclusive.
	*
	*/
	private static final char hexDigit(int i) {

		switch (i) {
		case 0:
			return '0';
		case 1:
			return '1';
		case 2:
			return '2';
		case 3:
			return '3';
		case 4:
			return '4';
		case 5:
			return '5';
		case 6:
			return '6';
		case 7:
			return '7';
		case 8:
			return '8';
		case 9:
			return '9';
		case 10:
			return 'A';
		case 11:
			return 'B';
		case 12:
			return 'C';
		case 13:
			return 'D';
		case 14:
			return 'E';
		case 15:
			return 'F';
		}

		throw new IllegalArgumentException("Invalid digit:" + i);
	}

}	// Validator



