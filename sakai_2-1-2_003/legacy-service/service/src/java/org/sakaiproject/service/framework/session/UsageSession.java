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
package org.sakaiproject.service.framework.session;

// imports
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>UsageSession models an end user's session.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface UsageSession
	extends Comparable
{
	/** String constants for major browser types */
	public static final String MAC_IE = "Mac-InternetExplorer";
	public static final String MAC_NN = "Mac-NetscapeNavigator";
	public static final String MAC_CM = "Mac-Camino";	
	public static final String MAC_SF = "Mac-Safari";	
	public static final String MAC_MZ = "Mac-Mozilla";
	public static final String WIN_IE = "Win-InternetExplorer";
	public static final String WIN_NN = "Win-NetscapeNavigator";
	public static final String WIN_MZ = "Win-Mozilla";
	public static final String UNKNOWN = "UnknownBrowser";
	
	/**
	 * Access the unique id for this session.
	 * @return the unique id for this session.
	 */
	String getId();

	/**
	 * Access the server id which is hosting this session.
	 * @return the server id which is hosting this session.
	 */
	String getServer();

	/**
	 * Access the user object for this session.
	 * @return the user object for this session.
	 */
	String getUserId();

	/**
	 * Access the IP Address from which this session originated.
	 * @return the IP Address from which this session originated.
	 */
	String getIpAddress();

	/**
	 * Access the User Agent string describing the browser used in this session.
	 * @return the User Agent string describing the browser used in this session.
	 */
	String getUserAgent();

	/**
	 * Access a short string describing the class of browser used in this session.
	 * @return the short ID describing the browser used in this session.
	 */
	String getBrowserId();

	/**
	 * Is this session closed?
	 * @return true if the session is closed, false if open.
	 */
	boolean isClosed();

	/**
	 * Access the start time of the session
	 * @return The time the session started.
	 */
	Time getStart();

	/**
	 * Access the end time of the session.
	 * @return The time the session ended.  If still going, this will .equals() the getStart() value.
	 */
	Time getEnd();
}



