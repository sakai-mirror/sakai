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
package org.sakaiproject.tool.OSIDRepository;

import javax.servlet.http.HttpSession;

import org.sakaiproject.tool.util.StringUtils;

/**
 * Expose the servlet-level session context block
 */
public class SessionContext implements SessionContextInterface {
	/**
	 * Base name for stored session name/value pairs
	 */
	private static final String BASENAME 		  = "edu.indiana.lib.twinpeaks.";
	/**
	 * Name of the stored SessionContext object
	 */
	public static final String CONTEXTNAME 	  = "SessionContext";

  /*
   * Servlet session state
   */
  private HttpSession		_session;

 /**
   * private constructor
   */
  private SessionContext() {
  }

  /**
   * Constructor
   * @param session HttpSession object
   */
	private SessionContext(HttpSession session) {
		_session = session;
	}

  /**
   * Get a Session Context object instance
   * @param session HttpSession object
   */
  public static SessionContext getInstance(HttpSession session) {
  	System.out.println("\r\n-->> NEW SESSION? " + session.isNew());
    SessionContext sc = (SessionContext) session.getAttribute(CONTEXTNAME);

		if (sc != null) System.out.println("\r\n-->> Existing SessionContext = " + sc);
    if (sc == null) {
    	sc = new SessionContext(session);
    	session.setAttribute(CONTEXTNAME, sc);
		  System.out.println("\r\n-->> New SessionContext = " + sc);
    }
    return sc;
  }

	/**
	 * Form full context parameter name
	 * @param name Parameter name
	 * @return BASENAME + name
	 */
	private static String fullName(String name) {
		StringBuffer nameBuffer = new StringBuffer(BASENAME);

		nameBuffer.append(name);
		return nameBuffer.toString();
	}

  /**
   * Set a name=value pair
   * @param name Attribute name
   * @param value Attribute value
   */
  public void put(String name, Object value) {
    _session.setAttribute(fullName(name), value);
  }

  /**
   * Fetch a value
   * @param name Attribute name
   * @return Requested value
   */
  public Object get(String name) {
    return _session.getAttribute(fullName(name));
  }

  /**
   * Delete a name=value pair
   * @param name Attribute name
   */
  public void remove(String name) {
    _session.removeAttribute(fullName(name));
  }

  /**
   * Get the servlet session block associated with this SessionContext
   * @return HttpServlet object for the SessionContext
   */
  public HttpSession getSession() {
  	return _session;
  }

  /**
   * Construct a session-wide unique name (unique within a browser session)
   * @parameter The parent (calling) Object
   * @param additionalDetail Additional name description
   */
	public static String uniqueSessionName(Object parent) {
    return StringUtils.replace(parent.getClass().getName(), "\\.", ":");
  }

  /**
   * Construct a unique name
   * @parameter The parent (calling) Object
   * @param additionalDetail Additional name description
   */
	public String uniqueName(Object parent) {

    return  uniqueSessionName(parent)
          + "["
          + Integer.toHexString(parent.hashCode())
          + ":"
          + Long.toHexString(System.currentTimeMillis())
          + "]";
  }

}