/**********************************************************************************
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
package org.sakaiproject.tool.search;

import org.sakaiproject.tool.util.*;

/**
 * Encapsulate one matching search item.  The rendering process depends
 * on these.
 */
public interface MatchItemInterface {

	/**
	 * Fetch the URL of the "preview" view of the matching item
	 * @return A fully qualified URL value
	 */
	public String getPreviewUrl();

	/**
	 * Fetch the text associated with the "preview" URL
	 * @return Text
	 */
	public String getPreviewText();

	/**
	 * Fetch the image source associated with the "preview" URL
	 * @return IMG tag SRC attribute
	 */
	public String getPreviewImage();

	/**
	 * Fetch any additional, descriptive text
	 * @return Additional text
	 */
	public String getDescription();

	/**
	 * Fetch the URL for the persistent reference
	 * @return A fully qualified URL value (minus any arguments)
	 */
	public String getPersistentUrl();

	/**
	 * Fetch simple parameters for the persistent reference
	 * @return Parameters (<code>?name1=value1&name2=value2</code>)
	 */
	public String getPersistentUrlParameters();

	/**
	 * Fetch any persistent reference parameters that <i>must be</i> URL encoded.
	 *<p>
	 * Note: the HTML editor parses encoded text, replacing entity sequences
	 * and the like with the "real" characters - in rare cases, this can cause
	 * the resultant URL to fail.  As a work around, the query handler can
	 * specifiy any parameters that need to be wrapped in a JavaScript escape()
	 * at URL "click time".
	 *
	 * @return Parameters (<code>?name3=value3&name4=value4</code>)
	 */
	public String getPersistentUrlParametersForEncoding();

	/**
	 * Fetch the link text associated with the persistent reference
	 * @return Anchor text
	 */
	public String getPersistentText();
}