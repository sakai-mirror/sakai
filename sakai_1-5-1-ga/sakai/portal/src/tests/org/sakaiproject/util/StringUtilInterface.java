/**********************************************************************************
*
* $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/util/StringUtilInterface.java,v 1.1 2004/11/20 21:30:25 dlhaines.umich.edu Exp $
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
package org.sakaiproject.util;

// imports
import java.util.Vector;

/**
* <p>StringUtil collects together some string utility classes.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public interface StringUtilInterface
{
	/**
	* Like jdk1.4's String.split...
	*/
	public String[] split(String source, String splitter);
	

	/**
	* Split the source into two strings at the first occurrence of the splitter
	* Subsequent occurrences are not treated specially, and may be part of the second string.
	* @param source The string to split
	* @param splitter The string that forms the boundary between the two strings returned.
	* @return An array of two strings split from source by splitter.
	*/
	public  String[] splitFirst(String source, String splitter);
	
	/**
	* Compute the reference path (i.e. the container) for a given reference.
	* @param ref The reference string.
	* @return The reference root for the given reference.
	*/
	public  String referencePath(String ref);
	
	/**
	* Create a full reference from a relative reference merged with a root reference.
	* @param root The root reference, which is the root of the full reference.
	* @param relative The relative reference, to add to the path of the root.
	* @return A full reference from the root and relative references.
	*/
	public  String fullReference(String root, String relative);


	/**
	* Trim blanks, and if nothing left, make null.
	* @param value The string to trim.
	* @return value trimmed of blanks, and if nothing left, made null.
	*/
	public  String trimToNull(String value);

	/**
	* Trim blanks, and if nothing left, make null, else lowercase.
	* @param value The string to trim.
	* @return value trimmed of blanks, lower cased, and if nothing left, made null.
	*/
	public  String trimToNullLower(String value);


	/**
	* Trim blanks, and assure there is a value, and it's not null.
	* @param value The string to trim.
	* @return value trimmed of blanks, assuring it not to be null.
	*/
	public  String trimToZero(String value);

	/**
	* Trim blanks, and assure there is a value, and it's not null, then lowercase.
	* @param value The string to trim.
	* @return value trimmed of blanks, lower cased, assuring it not to be null.
	*/
	public  String trimToZeroLower(String value);

	/**
	* Check if the target contains the substring anywhere, ignore case.
	* @param target The string to check.
	* @param substring The value to check for.
	* @return true of the target contains the substring anywhere, ignore case, or false if it does not.
	*/
	public  boolean containsIgnoreCase(String target, String substring);

	/**
	* Compare two strings for differences, either may be null
	* @param a One String.
	* @param b The other String.
	* @return true if the strings are different, false if they are the same.
	*/
	public  boolean different(String a, String b);

	/**
	* Compare two String[] for differences, either may be null
	* @param a One String[].
	* @param b The other String[].
	* @return true if the String[]s are different, false if they are the same.
	*/
	public  boolean different(String[] a, String[] b);

	/**
	* Compare two byte[] for differences, either may be null
	* @param a One byte[].
	* @param b The other byte[].
	* @return true if the byte[]s are different, false if they are the same.
	*/
	public  boolean different(byte[] a, byte[] b);


}	// StringUtil

/**********************************************************************************
*
* $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/util/StringUtilInterface.java,v 1.1 2004/11/20 21:30:25 dlhaines.umich.edu Exp $
*
**********************************************************************************/
