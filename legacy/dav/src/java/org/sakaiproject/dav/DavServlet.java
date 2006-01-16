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

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */


package org.sakaiproject.dav;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Vector;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.util.DOMWriter;
import org.apache.catalina.util.MD5Encoder;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.XMLWriter;
import org.sakaiproject.api.common.authentication.Authentication;
import org.sakaiproject.api.common.authentication.AuthenticationException;
import org.sakaiproject.api.common.authentication.Evidence;
import org.sakaiproject.api.common.authentication.cover.AuthenticationManager;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.IdPwEvidence;
import org.sakaiproject.util.LoginUtil;
import org.sakaiproject.util.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Servlet which adds support for WebDAV level 2. All the basic HTTP requests
 * are handled by the DefaultServlet.
 *
 * @author Remy Maucherat
 * @version $Revision$ $Date$
 */

public class DavServlet
	extends HttpServlet {
//    extends DefaultServlet {


	// -------------------------------------------------------------- Constants


	private static final String METHOD_HEAD = "HEAD";
	private static final String METHOD_PROPFIND = "PROPFIND";
	private static final String METHOD_PROPPATCH = "PROPPATCH";
	private static final String METHOD_OPTIONS = "OPTIONS";
	private static final String METHOD_MKCOL = "MKCOL";
	private static final String METHOD_COPY = "COPY";
	private static final String METHOD_MOVE = "MOVE";
	private static final String METHOD_LOCK = "LOCK";
	private static final String METHOD_UNLOCK = "UNLOCK";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_DELETE = "DELETE";

	/**
	 * Default depth is infite.
	 */
	private static final int INFINITY = 3; // To limit tree browsing a bit

	/**
	 * The debugging detail level for this servlet.
	 */
	protected int debug = 0;


	/**
	 * PROPFIND - Specify a property mask.
	 */
	private static final int FIND_BY_PROPERTY = 0;


	/**
	 * PROPFIND - Display all properties.
	 */
	private static final int FIND_ALL_PROP = 1;


	/**
	 * PROPFIND - Return property names.
	 */
	private static final int FIND_PROPERTY_NAMES = 2;


	/**
	 * Create a new lock.
	 */
	private static final int LOCK_CREATION = 0;


	/**
	 * Refresh lock.
	 */
	private static final int LOCK_REFRESH = 1;


	/**
	 * Default lock timeout value.
	 */
	private static final int DEFAULT_TIMEOUT = 3600;


	/**
	 * Maximum lock timeout.
	 */
	private static final int MAX_TIMEOUT = 604800;

	/**
	 * Read only flag. By default, it's set to true.
	 */
	protected boolean readOnly = true;

	/**
	 * Default namespace.
	 */
	protected static final String DEFAULT_NAMESPACE = "DAV:";

	/** delimiter for form multiple values */
	static final String FORM_VALUE_DELIMETER = "^";

	/** used to id a log message */
	public static String ME = DavServlet.class.getName();

	/**
	 * Simple date format for the creation date ISO representation (partial).
	 */
	protected static final SimpleDateFormat creationDateFormat =
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		creationDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Simple date format for the HTTP Date
	 */
	protected static final SimpleDateFormat HttpDateFormat =
		new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z");


	static {
		HttpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	};

	/**
	 * The set of SimpleDateFormat formats to use in getDateHeader().
	 */
	protected static final SimpleDateFormat formats[] = {
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
		new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
		new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
	};


	// ----------------------------------------------------- Instance Variables


	/**
	 * Repository of the locks put on single resources.
	 * <p>
	 * Key : path <br>
	 * Value : LockInfo
	 */
	private Hashtable resourceLocks = new Hashtable();


	/**
	 * Repository of the lock-null resources.
	 * <p>
	 * Key : path of the collection containing the lock-null resource<br>
	 * Value : Vector of lock-null resource which are members of the
	 * collection. Each element of the Vector is the path associated with
	 * the lock-null resource.
	 */
	private Hashtable lockNullResources = new Hashtable();


	/**
	 * Vector of the heritable locks.
	 * <p>
	 * Key : path <br>
	 * Value : LockInfo
	 */
	private Vector collectionLocks = new Vector();


	/**
	 * Secret information used to generate reasonably secure lock ids.
	 */
	private String secret = "catalina";


	/**
	 * Don't show directories starting with "protected" to non-owner
	 * This defaults off because it requires corresponding changes
	 * in AccessServlet, which currently aren't present.
	 */
        private boolean doProtected = false;

	/**
	 * The MD5 helper object for this class.
	 */
	protected static final MD5Encoder md5Encoder = new MD5Encoder();

	/**
	 * MD5 message digest provider.
	 */
	protected static MessageDigest md5Helper;

	/**
	 * Array of file patterns we are not supposed to accept on PUT
	 */
	private String [] ignorePatterns = null;

	// --------------------------------------------------------- Public Methods


	/**
	 * Initialize this servlet.
	 */
	public void init()
		throws ServletException {

		// Set our properties from the initialization parameters
		String value = null;
		try {
			value = getServletConfig().getInitParameter("debug");
			debug = Integer.parseInt(value);
		} catch (Throwable t) {
			;
		}

		try {
			value = getServletConfig().getInitParameter("readonly");
			if (value != null)
			    readOnly = (new Boolean(value)).booleanValue();
		} catch (Throwable t) {
			;
		}

		try {
			value = getServletConfig().getInitParameter("secret");
			if (value != null)
			    secret = value;
		} catch (Throwable t) {
			;
		}

		try {
			value = getServletConfig().getInitParameter("doprotected");
			if (value != null)
			    doProtected = (new Boolean(value)).booleanValue();
		} catch (Throwable t) {
			;
		}

		// load up the ignorePatterns from properties
		ignorePatterns = ServerConfigurationService.getStrings("webdav.ignore");
		if ( ignorePatterns != null )
		{
			String outVal = "";
			for ( int i = 0; i< ignorePatterns.length; i++ )
			{
				if ( outVal.length() > 0 ) outVal = outVal + " : ";
				outVal = outVal + ignorePatterns[i];
			}
			Log.info("sakai", ME + " ignore patterns:" + outVal);
		}

		// Load the MD5 helper used to calculate signatures.
        try {
            md5Helper = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
	}

	/** create the info */
	public SakaidavServletInfo newInfo(HttpServletRequest req)
	{
		return new SakaidavServletInfo(req);

	}   // newInfo

	public class SakaidavServletInfo
	{
		// all properties from the request
		protected Properties m_options = null;
		public Properties getOptions() { return m_options; }

		/** construct from the req */
		public SakaidavServletInfo(HttpServletRequest req)
		{
			m_options = new Properties();
			String type = req.getContentType();

			Enumeration e = req.getParameterNames();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				String [] values = req.getParameterValues(key);
				if (values.length == 1) 
				{
					m_options.put(key, values[0]);
				}
				else 
				{
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < values.length; i++)
					{
						buf.append(values[i] + FORM_VALUE_DELIMETER);
					}
					m_options.put(key, buf.toString());
				}
			}

		}   // SakaidavServletInfo

		/** return the m_options as a string */
		public String optionsString()
		{
			StringBuffer buf = new StringBuffer(1024);
			Enumeration e = m_options.keys();
			while (e.hasMoreElements())
			{
				String key = (String)e.nextElement();
				Object o = m_options.getProperty(key);
				if (o instanceof String)
				{
					buf.append(key);
					buf.append("=");
					buf.append(o.toString());
					buf.append("&");
				}
			}

			return buf.toString();

		}   // optionsString
		
	}   // SakaidavServletInfo



// From DefaultServlet

	/**
	 * Show HTTP header information.
	 */
	protected void showRequestInfo(HttpServletRequest req) {

		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","DefaultServlet Request Info");

		// Show generic info
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Encoding : " + req.getCharacterEncoding());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Length : " + req.getContentLength());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Type : " + req.getContentType());

		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Parameters");

		Enumeration parameters = req.getParameterNames();

		while (parameters.hasMoreElements()) {
			String paramName = (String) parameters.nextElement();
			String[] values = req.getParameterValues(paramName);
			System.out.print(paramName + " : ");
			for (int i = 0; i < values.length; i++) {
			    System.out.print(values[i] + ", ");
			}
		}

		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Protocol : " + req.getProtocol());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Address : " + req.getRemoteAddr());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Host : " + req.getRemoteHost());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Scheme : " + req.getScheme());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Server Name : " + req.getServerName());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Server Port : " + req.getServerPort());

		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Attributes");

		Enumeration attributes = req.getAttributeNames();

		while (attributes.hasMoreElements()) {
			String attributeName = (String) attributes.nextElement();
			System.out.print(attributeName + " : ");
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai",req.getAttribute(attributeName).toString());
		}

		// Show HTTP info
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","HTTP Header Info");

		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Authentication Type : " + req.getAuthType());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","HTTP Method : " + req.getMethod());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Path Info : " + req.getPathInfo());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Path translated : " + req.getPathTranslated());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Query string : " + req.getQueryString());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Remote user : " + req.getRemoteUser());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Requested session id : "
			               + req.getRequestedSessionId());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Request URI : " + req.getRequestURI());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Context path : " + req.getContextPath());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Servlet path : " + req.getServletPath());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","User principal : " + req.getUserPrincipal());
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Headers : ");

		Enumeration headers = req.getHeaderNames();

		while (headers.hasMoreElements()) {
			String headerName = (String) headers.nextElement();
			System.out.print(headerName + " : ");
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai",req.getHeader(headerName));
		}
	}


	/**
	 * Return the relative path associated with this servlet.
	 *
	 * @param request The servlet request we are processing
	 */
	protected String getRelativePath(HttpServletRequest request) {

		// Are we being processed by a RequestDispatcher.include()?
		if (request.getAttribute("javax.servlet.include.request_uri")!=null) {
			String result = (String)
			    request.getAttribute("javax.servlet.include.path_info");
			if (result == null)
			    result = (String)
			        request.getAttribute("javax.servlet.include.servlet_path");
			if ((result == null) || (result.equals("")))
			    result = "/";
			return (result);
		}

		// Are we being processed by a RequestDispatcher.forward()?
		if (request.getAttribute("javax.servlet.forward.request_uri")!=null) {
			String result = (String)
				request.getAttribute("javax.servlet.forward.path_info");
			if (result == null)
				result = (String)
					request.getAttribute("javax.servlet.forward.servlet_path");
			if ((result == null) || (result.equals("")))
				result = "/";
			return (result);
		}

		// No, extract the desired path directly from the request
		String result = request.getPathInfo();
		if (result == null) {
			result = request.getServletPath();
		}
		if ((result == null) || (result.equals(""))) {
			result = "/";
		}
		return normalize(result);

	}


	/**
	 * Return a context-relative path, beginning with a "/", that represents
	 * the canonical version of the specified path after ".." and "." elements
	 * are resolved out.  If the specified path attempts to go outside the
	 * boundaries of the current context (i.e. too many ".." path elements
	 * are present), return <code>null</code> instead.
	 *
	 * @param path Path to be normalized
	 */
	protected String normalize(String path) {

		if (path == null)
			return null;

		// Create a place for the normalized path
		String normalized = path;

		/*
		 * Commented out -- already URL-decoded in StandardContextMapper
		 * Decoding twice leaves the container vulnerable to %25 --> '%'
		 * attacks.
		 *
		 * if (normalized.indexOf('%') >= 0)
		 *     normalized = RequestUtil.URLDecode(normalized, "UTF8");
		 */

		if (normalized == null)
			return (null);

		if (normalized.equals("/."))
			return "/";

		// Normalize the slashes and add leading slash if necessary
		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
			    break;
			normalized = normalized.substring(0, index) +
			    normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
			    break;
			normalized = normalized.substring(0, index) +
			    normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
			    break;
			if (index == 0)
			    return (null);  // Trying to go outside our context
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) +
			    normalized.substring(index + 3);
		}

		// Return the normalized path that we have completed
		return (normalized);

	}


	/**
	 * URL rewriter.
	 *
	 * @param path Path which has to be rewiten
	 */
	protected String rewriteUrl(String path) {

		/**
		 * Note: This code portion is very similar to URLEncoder.encode.
		 * Unfortunately, there is no way to specify to the URLEncoder which
		 * characters should be encoded. Here, ' ' should be encoded as "%20"
		 * and '/' shouldn't be encoded.
		 */

		int maxBytesPerChar = 10;
		int caseDiff = ('a' - 'A');
		StringBuffer rewrittenPath = new StringBuffer(path.length());
		ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(buf, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
			writer = new OutputStreamWriter(buf);
		}

		for (int i = 0; i < path.length(); i++) {
			int c = (int) path.charAt(i);
			if (safeCharacters.get(c)) {
			    rewrittenPath.append((char)c);
			} else {
			    // convert to external encoding before hex conversion
			    try {
			        writer.write(c);
			        writer.flush();
			    } catch(IOException e) {
			        buf.reset();
			        continue;
			    }
			    byte[] ba = buf.toByteArray();
			    for (int j = 0; j < ba.length; j++) {
			        // Converting each byte in the buffer
			        byte toEncode = ba[j];
			        rewrittenPath.append('%');
			        int low = (int) (toEncode & 0x0f);
			        int high = (int) ((toEncode & 0xf0) >> 4);
			        rewrittenPath.append(hexadecimal[high]);
			        rewrittenPath.append(hexadecimal[low]);
			    }
			    buf.reset();
			}
		}

		return rewrittenPath.toString();

	}

	/**
	 * Array containing the safe characters set.
	 */
	protected static BitSet safeCharacters;


	protected static final char[] hexadecimal =
	{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	 'A', 'B', 'C', 'D', 'E', 'F'};


	// ----------------------------------------------------- Static Initializer


	static {
		safeCharacters = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			safeCharacters.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			safeCharacters.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			safeCharacters.set(i);
		}
		safeCharacters.set('-');
		safeCharacters.set('_');
		safeCharacters.set('.');
		safeCharacters.set('*');
		safeCharacters.set('/');
	}



	// ------------------------------------------------------ Protected Methods


	/**
	 * Return JAXP document builder instance.
	 */
	protected DocumentBuilder getDocumentBuilder()
		throws ServletException {
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = 
			    DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			throw new ServletException
			    ("Sakaidavservlet.jaxpfailed");
		}
		return documentBuilder;
	}

	/**
	 * Setup and cleanup around this request.
	 * @param req HttpServletRequest object with the client request
	 * @param res HttpServletResponse object back to the client
	 */
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException
	{
		SakaidavServletInfo info = newInfo(req);

		// RequestFilter will give us any session with this username,
		// even if there's no cookie (which will normally be the case)
		// Thus we can't assume that just because there's a session,
		// we're authenticated. So we always check. In theory if we
		// can verify that this is a proper cookie-based session
		// we could skip the test. Note that there's nothing in the
		// DAV RFC about cookies, so it's actually non-standard to
		// use them, and I think most clients don't.
		// See kernel/request/src/java/org/sakaiproject/util/RequestFilter.java

		Session session = SessionManager.getCurrentSession();

		// try to authenticate based on a Principal (one of ours) in the req
		Principal prin = req.getUserPrincipal();

		if ((prin != null) && (prin instanceof DavPrincipal))
		    {
			String eid = prin.getName();
			String pw = ((DavPrincipal) prin).getPassword();
			Evidence e = new IdPwEvidence(eid, pw);
			
			// authenticate
			try
			    {
				if ((eid.length() == 0) || (pw.length() == 0))
				    {
					throw new AuthenticationException("missing required fields");
				    }
				
				Authentication a = AuthenticationManager.authenticate(e);
				
				// login the user if needed. RequestFilter
				// may have found a session with the right 
				// userid. If so there's no need for a login
				// as long as it's the right userid. 
				// getUserID could be our id, null, or the
				// wrong one. The wrong one should be rare.

				if (session.getUserId() != eid)
				    LoginUtil.login(a, req);
			    }
			catch (AuthenticationException ex)
			    {
				// not authenticated
				res.sendError(401);
				return;
			    }
		    } 
		else
		    {
			// user name missing, so can't authenticate
			res.sendError(401);
			return;
		    }

// Setup... ?

		try
		{
			doDispatch(info, req, res);
		}
		finally
		{
			log(req, info);
		}
	}

	/** log a request processed */
	public void log(HttpServletRequest req, SakaidavServletInfo info)
	{
		Log.debug("sakai", ME + " from:" + req.getRemoteAddr() + " path:" + req.getPathInfo() + " options: " +  info.optionsString());

	}   // log

	/**
	 * Handles the special Webdav methods
	 */
	protected void doDispatch(SakaidavServletInfo info, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		String method = req.getMethod();

		if (debug > 0) {
			String path = getRelativePath(req);
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIDAV doDispatch [" + method + "] " + path);
		}

		String remoteUser = req.getRemoteUser();
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIDAV remoteuser = " + remoteUser );
		if ( remoteUser == null ) {
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIDAV Requires Authorization");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (method.equals(METHOD_PROPFIND)) {
			doPropfind(req, resp);
		} else if (method.equals(METHOD_PROPPATCH)) {
			doProppatch(req, resp);
		} else if (method.equals(METHOD_MKCOL)) {
			doMkcol(req, resp);
		} else if (method.equals(METHOD_COPY)) {
			doCopy(req, resp);
		} else if (method.equals(METHOD_MOVE)) {
			doMove(req, resp);
		} else if (method.equals(METHOD_LOCK)) {
			doLock(req, resp);
		} else if (method.equals(METHOD_UNLOCK)) {
			doUnlock(req, resp);
		} else if (method.equals(METHOD_GET)) {
			doGet(req, resp);
		} else if (method.equals(METHOD_PUT)) {
			doPut(req, resp);
		} else if (method.equals(METHOD_POST)) {
			doPost(req, resp);
		} else if (method.equals(METHOD_HEAD)) {
			doHead(req, resp);
		} else if (method.equals(METHOD_OPTIONS)) {
			doOptions(req, resp);
		} else if (method.equals(METHOD_DELETE)) {
			doDelete(req, resp);
		} else {
			Log.warn("sakai","SAKAIDAV:Request not supported");
			resp.sendError(SakaidavStatus.SC_NOT_IMPLEMENTED);
			// showRequestInfo(req);
		}

	}

	/**
	 * Determine if this path is one of the prefixes that we have been 
	 * requested to ignore by the properties settings
	 *
	 * @param request The servlet request we are processing
	 */
	protected boolean isFileNameAllowed(HttpServletRequest req)
	{
		if ( ignorePatterns == null ) return true;

		String sakaiPath = getRelativePathSAKAI(req);
		for ( int i = 0; i< ignorePatterns.length; i++ )
		{
			if ( sakaiPath.lastIndexOf(ignorePatterns[i]) > 0 ) return false; 
		}
		return true;
	}

	/**
	 * Process a HEAD request for the specified resource.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet-specified error occurs
	 */
	protected void doHead(HttpServletRequest request,
			              HttpServletResponse response)
		throws IOException, ServletException {

		// Call helper
		processHead(request, response);
	}

	/**
	 * Helper to handle set Header information
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet-specified error occurs

	 * @return boolean false if there was an error, true if the head variable all were set
	 */
	private boolean processHead(HttpServletRequest request,
			              HttpServletResponse response)
		throws IOException, ServletException {

		String path = getRelativePathSAKAI(request);

		if ((path == null) ||
			path.toUpperCase().startsWith("/WEB-INF") ||
			path.toUpperCase().startsWith("/META-INF")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
			return false;
		}

		// Retrieve the resources
		DirContextSAKAI resources = getResourcesSAKAI();
		ResourceInfoSAKAI resourceInfo = new ResourceInfoSAKAI(path, resources);

		if (!resourceInfo.exists) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
			return false;
		}

		if ( !resourceInfo.collection) {
			response.setDateHeader("Last-Modified", resourceInfo.date);
		}

		// Find content type by looking at the file suffix
		// We should probably make this something which is done within the service
		// rather than each tool

		String contentType =
			getServletContext().getMimeType(resourceInfo.path);

		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Default serveResource contentType = " + contentType);
		if (contentType != null) {
			response.setContentType(contentType);
		}

		long contentLength = resourceInfo.length;
		if ((!resourceInfo.collection) && (contentLength >= 0)) {
			response.setContentLength((int) contentLength);
		}
		return true;
	}

	/**
	 * OPTIONS Method.
	 */
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		String path = getRelativePathSAKAI(req);

		resp.addHeader("DAV", "1,2");
		String methodsAllowed = null;

		// Retrieve the resources
		DirContextSAKAI resources = getResourcesSAKAI();

		if (resources == null) {
			Log.warn("sakai","SAKAIDAV doOptions ERROR Resources is null");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		ResourceInfoSAKAI resourceInfo = new ResourceInfoSAKAI(path, resources);

		boolean exists = true;
		Object object = null;
		try {
			object = resources.lookup(path);
		} catch (NamingException e) {
			exists = false;
		}

		if (!exists) {
			methodsAllowed = "OPTIONS, MKCOL, PUT, LOCK, UNLOCK";
			resp.addHeader("Allow", methodsAllowed);
			return;
		}

		methodsAllowed = "OPTIONS, GET, HEAD, POST, DELETE, PROPFIND, COPY, MOVE, LOCK, UNLOCK";

// don't know why, but this instanceof test doesn't work
//		if (!(object instanceof DirContext)) {
	        if (! resourceInfo.collection) {
			methodsAllowed += ", PUT";
		} else {
			methodsAllowed += ", MKCOL";
		}

		resp.addHeader("Allow", methodsAllowed);

		resp.addHeader("MS-Author-Via", "DAV");

	}


	// Wrappers to make SAKAI look almost like a DirContext - This may be replaced as
	// we move this to an OKI style framework

   public class DirContextSAKAI {
	protected String path;
	private DirContext myDC;
	public boolean isCollection;
	private ContentCollection collection;
	private boolean isRootCollection;

	public Object lookup(String id) 
	throws NamingException
	{
		path = id;

		// resource or collection? check the properties (also finds bad id and checks permissions)
		isCollection = false;
		isRootCollection = false;
		try
		{
			ResourceProperties props;

			path = fixDirPathSAKAI(path);
			
			// Do not allow access to /attachments

			if (path.startsWith("/attachments"))
			{
				Log.info("sakai","DirContextSAKAI.lookup - You do not have permission to view this area "+path);
				throw new NamingException();
			}

			props = ContentHostingService.getProperties(path);

			isCollection = props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);

			if ( isCollection ) {
				// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","DirContextSAKAI.lookup getting collection");
				collection = ContentHostingService.getCollection(path);
				isRootCollection = ContentHostingService.isRootCollection(path);
			}
		}
		catch (PermissionException e) { 
			Log.debug("sakai","DirContextSAKAI.lookup - You do not have permission to view this resource "+path);
			throw new NamingException();
		}
		catch (IdUnusedException e) {  
			Log.debug("sakai","DirContextSAKAI.lookup - This resource does not exist: " + id);
			throw new NamingException(); 
		}
		catch (EmptyException e) {  
			Log.warn("sakai","DirContextSAKAI.lookup - This resource is empty: " + id);
			throw new NamingException(); 
		}
		catch (TypeException e) {  
			Log.warn("sakai","DirContextSAKAI.lookup - This resource has a type exception: " + id);
			throw new NamingException();
		}

		// for resources

		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","DirContextSAKAI.lookup is collection " + path);
		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai"," isCollection = " + isCollection);
		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai"," isRootCollection = " + isRootCollection);
		return myDC;
	}

	public Iterator list(String id) {
		try {
			Object object = lookup(id);
		}
		catch(Exception e) { return null; }
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","DirContextSAKAI.list getting collection members and iterator");
		List members = collection.getMemberResources();
		Iterator it = members.iterator();
		return it;
	}
	}

	public class ResourceInfoSAKAI {
	private DirContextSAKAI resources;
	private ResourceProperties props = null;
	private String path;
	
	public boolean collection;
	public boolean exists;
	public long length;
	public String httpDate;
	public long creationDate;
	public String MIMEType;
	public long modificationDate;
	public long date;  // From DirContext
	public String displayName;
	public String resourceName;     // The "non-display" name
	public String resourceLink;     // The resource link (within SAKAI)
	public String eTag;		// The eTag

	public ResourceInfoSAKAI(String our_path, DirContextSAKAI parent_resources)
	{
		path = our_path;
		exists = false;

		resources = parent_resources;
		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","ResourceInfoSAKAI Constructor path = " + path);

		// resource or collection? check the properties (also finds bad id and checks permissions)
		collection = false;
		try
		{
			ResourceProperties props;
			org.sakaiproject.service.legacy.entity.Entity mbr;

			path = fixDirPathSAKAI(path);  // Add slash as necessary

			props = ContentHostingService.getProperties(path);

			collection = props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);

			resourceName = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
			displayName = props.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
			exists = true;

			if (!collection)
			{				
				mbr = (org.sakaiproject.service.legacy.entity.Entity) ContentHostingService.getResource(path);
				// Props for a file is OK from above
				length = ((ContentResource) mbr).getContentLength();
				MIMEType = ((ContentResource) mbr).getContentType();
				eTag = ((ContentResource) mbr).getId();
			}
			else
			{
				mbr = (org.sakaiproject.service.legacy.entity.Entity) ContentHostingService.getCollection(path);
				props = mbr.getProperties();
				eTag = our_path;
			}
			modificationDate = props.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE).getTime();
			eTag = modificationDate + "+" + eTag;
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Path="+path+" eTag="+eTag);
			creationDate = props.getTimeProperty(ResourceProperties.PROP_CREATION_DATE).getTime();
			resourceLink = mbr.getUrl();
				String resourceName = getResourceNameSAKAI(mbr);

		}
		catch (PermissionException e) { 
			Log.debug("sakai","ResourceInfoSAKAI - You do not have permission to view this resource "+path);
		}
		catch (IdUnusedException e) {  
			Log.debug("sakai","ResourceInfoSAKAI - This resource does not exist "+path); 
		}
		catch (EmptyException e) {  
			Log.warn("sakai","ResourceInfoSAKAI - This resource is empty"+path);
		}
		catch (TypeException e) {  
			Log.warn("sakai","ResourceInfoSAKAI - Type Exception "+path);
		}

		httpDate = getHttpDate(modificationDate);
		if (creationDate == 0 ) creationDate = modificationDate;
		date = modificationDate;
/*
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","ResourceInfoSAKAI path="+path);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  collection=" + collection);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  length=" + length);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  ISOCreationDate=" + creationDate + " ISO= " + getISOCreationDate(creationDate));
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  ModificationDate=" + modificationDate + " ISO= " + getISOCreationDate(modificationDate));
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  MIMEType=" + MIMEType);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  httpDate=" + httpDate);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  resourceName="+resourceName);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  resourceLink="+resourceLink);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  displayName=" + displayName);
*/
	}
	}

	public DirContextSAKAI getResourcesSAKAI() {
	return new DirContextSAKAI();
	}

	public String fixDirPathSAKAI(String path) 
	{

	String tmpPath = path;

	ResourceProperties props;
	try
	{
		props = ContentHostingService.getProperties(tmpPath);
	}
	catch (IdUnusedException e) {  
		if (! tmpPath.endsWith("/")) { // We will add a slash and try again
			String newPath = tmpPath + "/";
			try
			{
				props = ContentHostingService.getProperties(newPath);
				tmpPath = newPath;
			}
			catch(Exception x) { } // Ignore everything
		}
	}
	catch (Exception e) { }  // Ignore all other exceptions
	return tmpPath;
   }

	/**
	 * POST Method.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {


		String path = getRelativePathSAKAI(req);

		doContent(path,req,resp);
	}


	/**
	 * GET Method.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {


		String path = getRelativePathSAKAI(req);

		doContent(path,req,resp);
	}


        protected int countSlashes(String s)
        {
	    int count = 0;
	    int loc = s.indexOf ('/');

	    while (loc >= 0) {
		count ++;
		loc ++;
		loc = s.indexOf ('/', loc);
	    }

	    return count;
	}

        // id is known to be a collection
        private void doDirectory(String id, HttpServletRequest req, HttpServletResponse res)
	{
	    // OK, it's a collection and we can read it. Do a listing.
	    // System.out.println("got to final check");

	    String uri = req.getRequestURI();
	    PrintWriter out = null;

	    // don't set the writer until we verify that
	    // getallresources is going to work.

	    try {
		ContentCollection x = 
		    ContentHostingService.getCollection(id);

		// I want to use relative paths in the listing,
		// so we need to redirect if there's no trailing /
		// for the usual reasons.
		if (!uri.endsWith("/")) {
		    // System.out.println("need redirect");
		    try {
			res.sendRedirect(uri + "/");
			// System.out.println("redirect ok");
			return;
		    } catch (IOException ignore) {
			// System.out.println("redirect failed");
			return;
		    }
		}

		// can't do directory on any folder starting with protected
		// Case-indepedent, based on display name.
		if (doProtected) {
		    ResourceProperties pl = x.getProperties();
		    if (pl.getProperty(ResourceProperties.PROP_DISPLAY_NAME).toLowerCase().indexOf("protected") == 0) {
			if (! ContentHostingService.allowAddCollection(id)) {
			    return;
			}
		    }
		}

		List xl = x.getMembers();
		Collections.sort(xl);
		Iterator xi = xl.iterator();

		res.setContentType("text/html; charset=UTF-8");

		out = res.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		out.println("<html><head>");
		String webappRoot = ServerConfigurationService.getServerUrl();
		out.println("<link href=\"" + webappRoot + "/css/default.css\" type=\"text/css\" rel=\"stylesheet\" media=\"screen\" />");
		out.println("<STYLE type=\"text/css\">");
		out.println("<!--");
		out.println("td {padding-right: .5em}");
		out.println("-->");
		out.println("</STYLE>");
		out.println("</head><body>");
		out.println("<div style=\"padding: 16px\">");
		out.println("<h2>Contents of /dav" + id + "</h2>");
		out.println("<table>");

		// show .. if not already there. we don't get aliases
		// so this is 
		//  /group/db5a4d0c-3dfd-4d10-8018-41db42ac7c8b/
		//  /user/hedrick/

		int slashes = countSlashes(id);

		if (slashes > 3) {
		    // go up a level
		    String uplev = id.substring(0, id.length() - 1);
		    uplev = uplev.substring(0, uplev.lastIndexOf('/')+1);
		    out.println("<tr><td><a href=\"..\">Up one level</a></td><td><b>Folder</b>" +
				"</td><td>" +
				"</td><td>" +
				"</td><td>" +
				"</td></tr>");
		    
		}

		while (xi.hasNext()) {
		    String xs = (String) xi.next ();
		    String xss = xs.substring(id.length());
		    
		    if (xss.endsWith("/")) {
			ContentCollection nextres = ContentHostingService.getCollection(xs);
			ResourceProperties properties = nextres.getProperties();
			if (doProtected && properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME).toLowerCase().indexOf("protected") == 0) {
			    if (! ContentHostingService.allowAddCollection(xs)) {
				continue;
			    }
			}
			out.println("<tr><td><a href=\"" + Validator.escapeUrl(xss) + "\">" + Validator.escapeHtml(xss) + "</a></td><td><b>Folder</b>" +
				    "</td><td>" +
				    "</td><td>" +
				    "</td><td>" +
				    "</td></tr>");
		    } else try {
			ContentResource nextres = ContentHostingService.getResource(xs);
			ResourceProperties properties = nextres.getProperties();

			long filesize = ((nextres.getContentLength() - 1) / 1024) + 1;
			String createdBy = properties.getUserProperty(ResourceProperties.PROP_CREATOR).getDisplayName();
			Time modTime = properties.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
			String modifiedTime = modTime.toStringLocalShortDate() + " " + modTime.toStringLocalShort();
			String filetype = nextres.getContentType();
			out.println("<tr><td><a href=\"" + Validator.escapeUrl(xss) + "\">" + Validator.escapeHtml(xss) + "</a></td><td>" +
				    filesize + "</td><td>" +
				    createdBy + "</td><td>" +
				    filetype + "</td><td>" +
				    modifiedTime + "</td></tr>");
		    } catch (Throwable ignore) {
			    out.println("<tr><td><a href=\"" + Validator.escapeUrl(xss) + "\">" + Validator.escapeHtml(xss) + "</a></td><td>" +
					"</td><td>" +
					"</td><td>" +
					"</td><td>" +
					"</td></tr>");

		    }
		}
	    } catch (Throwable ignore) {
	    } 
	    if (out != null) 
		out.println("</table></div></body></html>");
	}

 	/**
	* Handle requests for content, resources ONLY
	* @param id The local resource id.
	* @param res The http servlet response object.
	* @return any error message, or null if all went well.
	*/
	private String doContent(String id, HttpServletRequest req, HttpServletResponse res)
	{
		// resource or collection? check the properties (also finds bad id and checks permissions)
		boolean isCollection = false;
		try
		{
			ResourceProperties props = ContentHostingService.getProperties(id);
			isCollection = props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
		}
		catch (PermissionException e) { return "You do not have permission to view this resource"; }
		catch (IdUnusedException e) { return "This resource does not exist"; }
		catch (EmptyException e) { return "This resource does not exist"; }
		catch (TypeException e) { return "This resource does not exist"; }

		// for resources
		if (!isCollection)
		{
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIAccess doContent is resource " + id);
			try
			{
				ContentResource resource = ContentHostingService.getResource(id);
				long len = resource.getContentLength();
				String contentType = resource.getContentType();
				byte[] content = resource.getContent();

				if (content == null)
				{
					return "Empty resource";
				}

				// for URL content type, encode a redirect to the body URL
				if (contentType.equalsIgnoreCase(ResourceProperties.TYPE_URL))
				{
					res.sendRedirect(new String(content));
				}
				else
				{
					if ( ! processHead(req, res) ) return "Error setting header values";
					OutputStream out = res.getOutputStream();
					// now set in processHead
					// res.setContentType(contentType);
					out.write(content);
					out.flush();
				}
			}
			catch (Throwable e)
			{
				// Log.warn("sakai", this + ".doContent(): exception: id: " + id + " : " + e.toString());
				return e.toString();
			}
		}

		// for collections
		else
		{
		    doDirectory(id, req, res);
		}
		
		// no errors
		return null;

	}	// doContent

	// Sometimes we are the root applet and other times, we are a sub-applet
	// We have to trim off the part of the path which gets to us
	// Also we have to deal with the fact that SAKAI likes collections with trailing slashes
	// while http like collections without trailing slashes
	// So, we take a quick look to see if the directory does not exist without the slash
	// and if it does not exist without the slash, we peek to see if it exists with the slash.
	// if so, we tack the slash on.

	public String getRelativePathSAKAI(HttpServletRequest req)
	{
		String path = req.getPathInfo();
//		if (path != null)
//		{
//		    try
//		    {
//		        path = ParameterParser.convertFromRawBytesToUTF8(path);
//		    }
//		    catch (UnsupportedEncodingException e)
//		    {
//		        Log.warn("sakai", e);
//		    }
//		    catch (CharacterCodingException e)
//		    {
//		        Log.warn("sakai", e);
//		    }
//		}

		if (path == null) path = "/";
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","getRelativePathSAKAI = " + path);
		return path;

/*		String tmpPath = getRelativePath(req);

	// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","getRelativePathSAKAI = " + tmpPath);

	// Remove the parent path
	if ( tmpPath.length() >= 4 ) {
		if(tmpPath.equalsIgnoreCase("/dav")) {
			tmpPath = "/";
		} else if (tmpPath.substring(0,4).equalsIgnoreCase("/dav") ) {
	   		tmpPath = "/" + tmpPath.substring(4);
		}
		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","New Relative Path = " + tmpPath);
	}
	return tmpPath;
*/
	}	// getRelativePathSAKAI

	/**
	 * getResourceNameSAKAI - Needs to become a method of resource
	 *    returns the internal name for a resource.
	 */
	
	public String getResourceNameSAKAI(org.sakaiproject.service.legacy.entity.Entity mbr) {
	String idx = mbr.getId();
	ResourceProperties props = mbr.getProperties();
	String resourceName = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

	if (idx.startsWith("/") && idx.endsWith("/") && idx.length() > 3) {
		int lastSlash = idx.lastIndexOf("/",idx.length()-2);
		if (lastSlash > 0 && lastSlash+1 <= idx.length()-2) {
			//if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","ls1="+(lastSlash+1)+" idl="+(idx.length()-1));
			resourceName = idx.substring(lastSlash+1,idx.length()-1);
		}

	} else if (idx.startsWith("/") && ! idx.endsWith("/") && idx.length() > 2) {
		int lastSlash = idx.lastIndexOf("/");
		if (lastSlash > -1 ) {
			// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","ls="+lastSlash);
			resourceName = idx.substring(lastSlash+1);	
		}
	}
	
	// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","getResourceNameSAKAI resourceName="+resourceName);
	// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai"," idx="+idx);
	// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai"," displayName = " + props.getProperty(props.PROP_DISPLAY_NAME));

	return resourceName;
	}


	/**
	 * PROPFIND Method.
	 */
	protected void doPropfind(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		String path = getRelativePathSAKAI(req);

		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		if ((path.toUpperCase().startsWith("/WEB-INF")) ||
			(path.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		// Properties which are to be displayed.
		Vector properties = null;
		// Propfind depth
		int depth = INFINITY;
		// Propfind type
		int type = FIND_ALL_PROP;

		String depthStr = req.getHeader("Depth");

		if (depthStr == null) {
			depth = INFINITY;
		} else {
			if (depthStr.equals("0")) {
			    depth = 0;
			} else if (depthStr.equals("1")) {
			    depth = 1;
			} else if (depthStr.equals("infinity")) {
			    depth = INFINITY;
			}
		}

		Node propNode = null;

		DocumentBuilder documentBuilder = getDocumentBuilder();

	// Parse the input XML to see what they really want
		try {
			InputStream is = req.getInputStream();

			if (is.available() == 0) throw new Exception("Zero bytes");
			Document document = documentBuilder.parse
			    (new InputSource(is));

			// Get the root element of the document
			Element rootElement = document.getDocumentElement();
			NodeList childList = rootElement.getChildNodes();

			for (int i=0; i < childList.getLength(); i++) {
			    Node currentNode = childList.item(i);
			    switch (currentNode.getNodeType()) {
			    case Node.TEXT_NODE:
			        break;
			    case Node.ELEMENT_NODE:
			        if (currentNode.getNodeName().endsWith("prop")) {
			            type = FIND_BY_PROPERTY;
			            propNode = currentNode;
			        }
			        if (currentNode.getNodeName().endsWith("propname")) {
			            type = FIND_PROPERTY_NAMES;
			        }
			        if (currentNode.getNodeName().endsWith("allprop")) {
			            type = FIND_ALL_PROP;
			        }
			        break;
			    }
			}
		} catch(Exception e) {
			// Most likely there was no content : we use the defaults.
			// TODO : Enhance that !
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIDAV doPropfind exception documentBuilder");
		}


		if (type == FIND_BY_PROPERTY) {
			properties = new Vector();
			NodeList childList = propNode.getChildNodes();

			for (int i=0; i < childList.getLength(); i++) {
			    Node currentNode = childList.item(i);
			    switch (currentNode.getNodeType()) {
			    case Node.TEXT_NODE:
			        break;
			    case Node.ELEMENT_NODE:
			        String nodeName = currentNode.getNodeName();
			        String propertyName = null;
			        if (nodeName.indexOf(':') != -1) {
			            propertyName = nodeName.substring
			                (nodeName.indexOf(':') + 1);
			        } else {
			            propertyName = nodeName;
			        }
			        // href is a live property which is handled differently
			        properties.addElement(propertyName);
			        break;
			    }
			}

		}

		// Retrieve the resources
		DirContextSAKAI resources = getResourcesSAKAI();

		if (resources == null) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

	// Point the resource object at a particular path and catch the error if necessary.

		boolean exists = true;
		Object object = null;
		try {
			object = resources.lookup(path);
		} catch (NamingException e) {
			exists = false;
			int slash = path.lastIndexOf('/');
			if (slash != -1) {
			    String parentPath = path.substring(0, slash);
			    Vector currentLockNullResources =
			        (Vector) lockNullResources.get(parentPath);
			    if (currentLockNullResources != null) {
			        Enumeration lockNullResourcesList =
			            currentLockNullResources.elements();
			        while (lockNullResourcesList.hasMoreElements()) {
			            String lockNullPath = (String)
			                lockNullResourcesList.nextElement();
			            if (lockNullPath.equals(path)) {
			                resp.setStatus(SakaidavStatus.SC_MULTI_STATUS);
			                resp.setContentType("text/xml; charset=UTF-8");
			                // Create multistatus object
			                XMLWriter generatedXML = 
			                    new XMLWriter(resp.getWriter());
			                generatedXML.writeXMLHeader();
			                generatedXML.writeElement
			                    (null, "multistatus"
			                     + generateNamespaceDeclarations(),
			                     XMLWriter.OPENING);
			                parseLockNullProperties
			                    (req, generatedXML, lockNullPath, type,
			                     properties);
			                generatedXML.writeElement(null, "multistatus",
			                                          XMLWriter.CLOSING);
			                generatedXML.sendData();
			                return;
			            }
			        }
			    }
			}
		}

		if (!exists) {
		    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "/dav" + path);
		    return;
		}

		resp.setStatus(SakaidavStatus.SC_MULTI_STATUS);

		resp.setContentType("text/xml; charset=UTF-8");

		// Create multistatus object
		XMLWriter generatedXML = new XMLWriter(resp.getWriter());
		generatedXML.writeXMLHeader();

		generatedXML.writeElement(null, "multistatus"
			                      + generateNamespaceDeclarations(),
			                      XMLWriter.OPENING);

		if (depth == 0) {
			parseProperties(req, resources, generatedXML, path, type,
			                properties);
		} else {
			// The stack always contains the object of the current level
			Stack stack = new Stack();
			stack.push(path);

			// Stack of the objects one level below
			Stack stackBelow = new Stack();

			while ((!stack.isEmpty()) && (depth >= 0)) {

			    String currentPath = (String) stack.pop();

			    try {
			// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Lookup currentPath="+currentPath);
			        object = resources.lookup(currentPath);
			    } catch (NamingException e) {
			        continue;
			    }

			    parseProperties(req, resources, generatedXML, currentPath,
			                    type, properties);

			    if ((resources.isCollection) && (depth > 0)) {

			        Iterator it = resources.list(currentPath);
			while (it.hasNext())
			{
			org.sakaiproject.service.legacy.entity.Entity mbr = (org.sakaiproject.service.legacy.entity.Entity) it.next();
				String resourceName = getResourceNameSAKAI(mbr);

			           	String newPath = currentPath;
			           	if (!(newPath.endsWith("/")))
			           	    newPath += "/";
			           	newPath += resourceName;
			           	stackBelow.push(newPath);
			// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAI found resource " + newPath);
			        }

			        // Displaying the lock-null resources present in that
			        // collection
			        String lockPath = currentPath;
			        if (lockPath.endsWith("/"))
			            lockPath = 
			                lockPath.substring(0, lockPath.length() - 1);
			        Vector currentLockNullResources =
			            (Vector) lockNullResources.get(lockPath);
			        if (currentLockNullResources != null) {
			            Enumeration lockNullResourcesList =
			                currentLockNullResources.elements();
			            while (lockNullResourcesList.hasMoreElements()) {
			                String lockNullPath = (String)
			                    lockNullResourcesList.nextElement();

			                parseLockNullProperties
			                    (req, generatedXML, lockNullPath, type,
			                     properties);
			            }
			        }

			    }

			    if (stack.isEmpty()) {
			        depth--;
			        stack = stackBelow;

			        stackBelow = new Stack();
			    }
		// if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIDAV.propfind() " + generatedXML.toString());
			    generatedXML.sendData();
			}
		}

		generatedXML.writeElement(null, "multistatus",
			                      XMLWriter.CLOSING);
		//	if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","SAKAIDAV.propfind() at end:" + generatedXML.toString());
		generatedXML.sendData();

	}


	/**
	 * PROPPATCH Method.
	 */
	protected void doProppatch(HttpServletRequest req,
			                   HttpServletResponse resp)
		throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

	}

	protected String justName(String str)
	{
		try
		{
			// Note: there may be a trailing separator
			int pos = str.lastIndexOf("/", str.length() - 2);
			String rv = str.substring(pos + 1);
			if (rv.endsWith("/"))
			{
				rv = rv.substring(0, rv.length()-1);
			}
			return rv;
		}
		catch (Throwable t)
		{
			return str;
		}
	}

	/**
	 * MKCOL Method.
	 */
	protected void doMkcol(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		String path = getRelativePathSAKAI(req);
		if ((path.toUpperCase().startsWith("/WEB-INF")) ||
			(path.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		String name = justName(path);

		if ((name.toUpperCase().startsWith("/WEB-INF")) ||
			(name.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

	// Check to see if collection already exists
	try
	{
		boolean isCollection = ContentHostingService.getProperties (path).getBooleanProperty (ResourceProperties.PROP_IS_COLLECTION);
		
		// if the path already exists and it is a collection there is nothing to do
		// if it exists and is a resource, we remove it to make room for the collection	
		if (isCollection) {
			return;
		}
		else
		{
			ContentHostingService.removeResource (path);
		}
	}
	catch (PermissionException e)
	{
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);
				return;
	}
	catch (InUseException e)
	{
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);	// %%%
				return;
	}
	catch (IdUnusedException e)
	{
		// Resource not found (this is actually the normal case
	}
	catch (EmptyException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doMkcol() - EmptyException "+path);
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);
				return;
	}
	catch (TypeException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doMkcol() - TypeException "+path);
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);
				return;
	}

	// Add the collection
							
	ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties();
			
	try
	{
		User user = UserDirectoryService.getCurrentUser();

		TimeBreakdown timeBreakdown = TimeService.newTime().breakdownLocal();
		String mycopyright = "copyright (c)" + " " + timeBreakdown.getYear () +", " + user.getDisplayName() + ". All Rights Reserved. ";

		resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, name);
		resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);

		ContentCollection collection = ContentHostingService.addCollection (path, resourceProperties);
	}


	catch (IdUsedException e)
	{
		// Should not happen because if this esists, we either return or delete above
	}
	catch (IdInvalidException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doMkcol() IdInvalid:" + e.getMessage());
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);
				return;
	}
	catch (PermissionException e)
	{
		// This is normal
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);
				return;
	}
	catch (InconsistentException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doMkcol() InconsistentException:" + e.getMessage());
				resp.sendError(SakaidavStatus.SC_FORBIDDEN);
				return;
	}

	// Removing any lock-null resource which would be present
		lockNullResources.remove(path);

	}


	/**
	 * DELETE Method.
	 */
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		deleteResource(req, resp);

	}


	/**
	 * Process a PUT request for the specified resource.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet-specified error occurs
	 */
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		// Do not allow files which match patterns specified in properties
		if ( ! isFileNameAllowed(req) ) return;

		ResourceProperties oldProps = null;

		boolean newfile = true;

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		String path = getRelativePathSAKAI(req);

		if ((path.toUpperCase().startsWith("/WEB-INF")) ||
			(path.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		// Looking for a Content-Range header
		if (req.getHeader("Content-Range") != null) {
			// No content range header is supported
			resp.sendError(SakaidavStatus.SC_NOT_IMPLEMENTED);
		}


		String name = justName(path);

		// Database max for id field is 255. If we allow longer, odd things happen
		if (path.length() > 254) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if ((name.toUpperCase().startsWith("/WEB-INF")) ||
			(name.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}
	
	// Try to delete the resource
	try
	{
		//The existing document may be a collection or a file.
		boolean isCollection = ContentHostingService.getProperties (path).getBooleanProperty (ResourceProperties.PROP_IS_COLLECTION);
			
		if (isCollection)
		{
			ContentHostingService.removeCollection (path);
		}
		else
		{
		        // save original properties; we're just updating the file
			oldProps = ContentHostingService.getProperties(path);
			newfile = false;
			ContentHostingService.removeResource (path);
		}
	}
	catch (PermissionException e)
	{
		// Normal situation
			    resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			    return;
	}
	catch (InUseException e)
	{
		// Normal situation
			    resp.sendError(SakaidavStatus.SC_FORBIDDEN); // %%%
			    return;
	}
	catch (IdUnusedException e)
	{
		// Normal situation - nothing to do
	}
	catch (EmptyException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doMkcol() - EmptyException "+path);
			    resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			    return;
	}
	catch (TypeException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doMkcol() - TypeException "+path);
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			    return;
	}
	catch (ServerOverloadException e)
	{
		Log.warn("sakai", "SAKAIDavServlet.doMkcol() - ServerOverloadException " + path);
		resp.sendError(SakaidavStatus.SC_SERVICE_UNAVAILABLE);
	    return;
	}

	// Add the resource

	String contentType = "";
	String collectionId = "";
	InputStream inputStream = req.getInputStream();
	contentType = req.getContentType();
	int contentLength = req.getContentLength();
	if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","  req.contentType() ="+contentType+" len="+contentLength);

	if (contentLength < 0 ) {
		Log.warn("sakai","SAKAIDavServlet.doPut() content length ("+contentLength+") less than zero "+ path);
	   	     	resp.sendError(HttpServletResponse.SC_CONFLICT);
			    return;
	}
	
	// Convert to byte array for the content service
	byte[] byteContent = new byte[contentLength]; 

	if ( contentLength > 0 ) {
		try
		{
			int lenRead = 0;
			while (lenRead < contentLength)
			{
				int read = inputStream.read(byteContent, lenRead, contentLength-lenRead);
				if (read <= 0) break;
				lenRead += read;
			}	
		}
		catch (IOException e) {
			Log.warn("sakai","SAKAIDavServlet.doPut() IOException "+ path);
	   	     		resp.sendError(HttpServletResponse.SC_CONFLICT);
			        return;
		}
	}
	
	if ( contentType == null ) {
		contentType = getServletContext().getMimeType(path);
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Lookup contentType ="+contentType);
	}
	if (contentType == null ) {
		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Unable to determine contentType");
		contentType = "";  // Still cannot figure it out
	}

	try
	{
		User user = UserDirectoryService.getCurrentUser();

		TimeBreakdown timeBreakdown = TimeService.newTime().breakdownLocal();
		String mycopyright = "copyright (c)" + " " + timeBreakdown.getYear () +", " + user.getDisplayName() + ". All Rights Reserved. ";

		// use this code rather than the long form of addResource
		// because it doesn't add an extension. Delete doesn't, so we have
		// to match, and I'd just as soon be able to create items with no extension anyway
		org.sakaiproject.service.legacy.content.ContentResourceEdit
		   edit = ContentHostingService.addResource(path);
		edit.setContentType(contentType);
		edit.setContent(byteContent);
		ResourcePropertiesEdit p = edit.getPropertiesEdit();

		// copy old props, if any
		if (oldProps != null) {
		    Iterator it = oldProps.getPropertyNames();

		    while (it.hasNext()) {
			String pname = (String) it.next();

			// skip any live properties
			if (!oldProps.isLiveProperty(pname)) {
				p.addProperty(pname, oldProps.getProperty(pname));
			}
		    }
		}

		if (newfile) {
		    p.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);
		    p.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
		}

		// commit the change
		ContentHostingService.commitResource(edit, NotificationService.NOTI_NONE);

	}
	catch (IdUsedException e)
	{
		// Should not happen because we deleted above (unless two requests at same time)
		Log.warn("sakai","SAKAIDavServlet.doPut() IdUsedException:" + e.getMessage());

				resp.sendError(HttpServletResponse.SC_CONFLICT);
			    return;
	}
	catch (IdInvalidException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doPut() IdInvalidException:" + e.getMessage());
				resp.sendError(HttpServletResponse.SC_CONFLICT);
			    return;
	}
	catch (PermissionException e)
	{
		// Normal
			    resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			    return;
	}
	catch (OverQuotaException e)
	{
		// Normal %%% what's the proper response for over-quota?
			    resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			    return;
	}
	catch (InconsistentException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doPut() InconsistentException:" + e.getMessage());
	   	     	resp.sendError(HttpServletResponse.SC_CONFLICT);
			    return;
	}
	catch (ServerOverloadException e)
	{
		Log.warn("sakai","SAKAIDavServlet.doPut() ServerOverloadException:" + e.getMessage());
		resp.setStatus(SakaidavStatus.SC_SERVICE_UNAVAILABLE);
		return;
	}
		resp.setStatus(HttpServletResponse.SC_CREATED);

		// Removing any lock-null resource which would be present
		lockNullResources.remove(path);

	}

	/**
	 * COPY Method.
	 */
	protected void doCopy(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		// System.out.println("doCopy called");

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		copyResource(req, resp);

	}


	/**
	 * MOVE Method.
	 */
	protected void doMove(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		String path = getRelativePath(req);

		if (copyResource(req, resp)) {
			deleteResource(path, req, resp);
		}

/*
		String destinationPath = getDestinationPath(req);

		if (destinationPath == null) {
			resp.sendError(SakaidavStatus.SC_BAD_REQUEST);
			return;
		}
		//

		path = fixDirPathSAKAI(path);	
		destinationPath = fixDirPathSAKAI(destinationPath);	

		System.out.println("doMove source="+path+" dest="+destinationPath);

		try
		{
			ContentHostingService.rename(path,destinationPath);
		}
		catch (PermissionException e)
		{
			resp.sendError(SakaidavStatus.SC_BAD_REQUEST);
			return;
		}
		catch (InUseException e)
		{
			resp.sendError(SakaidavStatus.SC_BAD_REQUEST);
			return;
		}
		catch (IdUnusedException e)
		{
			// Resource not found (this is actually the normal case)
		}
		catch (TypeException e)
		{
			Log.warn("sakai","SAKAIDavServlet.doMove() - TypeException "+path);
			resp.sendError(SakaidavStatus.SC_BAD_REQUEST);
			return;
		}

*/

	}


	/**
	 * LOCK Method.
	 */
	protected void doLock(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		LockInfo lock = new LockInfo();

		// Parsing lock request

		// Parsing depth header

		String depthStr = req.getHeader("Depth");

		if (depthStr == null) {
			lock.depth = INFINITY;
		} else {
			if (depthStr.equals("0")) {
			    lock.depth = 0;
			} else {
			    lock.depth = INFINITY;
			}
		}

		// Parsing timeout header

		int lockDuration = DEFAULT_TIMEOUT;
		String lockDurationStr = req.getHeader("Timeout");
		if (lockDurationStr == null) {
			lockDuration = DEFAULT_TIMEOUT;
		} else {
			if (lockDurationStr.startsWith("Second-")) {
			    lockDuration =
			        (new Integer(lockDurationStr.substring(7))).intValue();
			} else {
			    if (lockDurationStr.equalsIgnoreCase("infinity")) {
			        lockDuration = MAX_TIMEOUT;
			    } else {
			        try {
			            lockDuration =
			                (new Integer(lockDurationStr)).intValue();
			        } catch (NumberFormatException e) {
			            lockDuration = MAX_TIMEOUT;
			        }
			    }
			}
			if (lockDuration == 0) {
			    lockDuration = DEFAULT_TIMEOUT;
			}
			if (lockDuration > MAX_TIMEOUT) {
			    lockDuration = MAX_TIMEOUT;
			}
		}
		lock.expiresAt = System.currentTimeMillis() + (lockDuration * 1000);

		int lockRequestType = LOCK_CREATION;

		Node lockInfoNode = null;

		DocumentBuilder documentBuilder = getDocumentBuilder();

		try {
			Document document = documentBuilder.parse(new InputSource
			    (req.getInputStream()));

			// Get the root element of the document
			Element rootElement = document.getDocumentElement();
			lockInfoNode = rootElement;
		} catch(Exception e) {
			lockRequestType = LOCK_REFRESH;
		}

		if (lockInfoNode != null) {

			// Reading lock information

			NodeList childList = lockInfoNode.getChildNodes();
			StringWriter strWriter = null;
			DOMWriter domWriter = null;

			Node lockScopeNode = null;
			Node lockTypeNode = null;
			Node lockOwnerNode = null;

			for (int i=0; i < childList.getLength(); i++) {
			    Node currentNode = childList.item(i);
			    switch (currentNode.getNodeType()) {
			    case Node.TEXT_NODE:
			        break;
			    case Node.ELEMENT_NODE:
			        String nodeName = currentNode.getNodeName();
			        if (nodeName.endsWith("lockscope")) {
			            lockScopeNode = currentNode;
			        }
			        if (nodeName.endsWith("locktype")) {
			            lockTypeNode = currentNode;
			        }
			        if (nodeName.endsWith("owner")) {
			            lockOwnerNode = currentNode;
			        }
			        break;
			    }
			}

			if (lockScopeNode != null) {

			    childList = lockScopeNode.getChildNodes();
			    for (int i=0; i < childList.getLength(); i++) {
			        Node currentNode = childList.item(i);
			        switch (currentNode.getNodeType()) {
			        case Node.TEXT_NODE:
			            break;
			        case Node.ELEMENT_NODE:
			            String tempScope = currentNode.getNodeName();
			            if (tempScope.indexOf(':') != -1) {
			                lock.scope = tempScope.substring
			                    (tempScope.indexOf(':') + 1);
			            } else {
			                lock.scope = tempScope;
			            }
			            break;
			        }
			    }

			    if (lock.scope == null) {
			        // Bad request
			        resp.setStatus(SakaidavStatus.SC_BAD_REQUEST);
			    }

			} else {
			    // Bad request
			    resp.setStatus(SakaidavStatus.SC_BAD_REQUEST);
			}

			if (lockTypeNode != null) {

			    childList = lockTypeNode.getChildNodes();
			    for (int i=0; i < childList.getLength(); i++) {
			        Node currentNode = childList.item(i);
			        switch (currentNode.getNodeType()) {
			        case Node.TEXT_NODE:
			            break;
			        case Node.ELEMENT_NODE:
			            String tempType = currentNode.getNodeName();
			            if (tempType.indexOf(':') != -1) {
			                lock.type =
			                    tempType.substring(tempType.indexOf(':') + 1);
			            } else {
			                lock.type = tempType;
			            }
			            break;
			        }
			    }

			    if (lock.type == null) {
			        // Bad request
			        resp.setStatus(SakaidavStatus.SC_BAD_REQUEST);
			    }

			} else {
			    // Bad request
			    resp.setStatus(SakaidavStatus.SC_BAD_REQUEST);
			}

			if (lockOwnerNode != null) {

			    childList = lockOwnerNode.getChildNodes();
			    for (int i=0; i < childList.getLength(); i++) {
			        Node currentNode = childList.item(i);
			        switch (currentNode.getNodeType()) {
			        case Node.TEXT_NODE:
			            lock.owner += currentNode.getNodeValue();
			            break;
			        case Node.ELEMENT_NODE:
			            strWriter = new StringWriter();
			            domWriter = new DOMWriter(strWriter, true);
			            domWriter.print(currentNode);
			            lock.owner += strWriter.toString();
			            break;
			        }
			    }

			    if (lock.owner == null) {
			        // Bad request
			        resp.setStatus(SakaidavStatus.SC_BAD_REQUEST);
			    }
			    
			    // contribute feeds us an owner that looks
			    // like <A:href>...</A:href>. Since we'll put it
			    // back with a different namespace prefix, we
			    // don't want to save it that way.

			    lock.owner = lock.owner.replaceAll("<(/?)[^>]+:([hH][rR][eE][fF])>","<$1$2>");
			    // System.out.println("lock.owner: " + lock.owner);

			} else {
			    lock.owner = new String();
			}

		}

		String path = getRelativePath(req);
		String lockToken = null;

		lock.path = path;

                // We don't want to allow just anyone to lock a resource.
                // It seems reasonable to allow it only for someone who
                // is allowed to modify it.
                if (!ContentHostingService.allowUpdateResource (path)) {
                    resp.sendError(SakaidavStatus.SC_FORBIDDEN, path);
                    return;
                }

		// Retrieve the resources
		// DirContext resources = getResources();
		DirContextSAKAI resources = getResourcesSAKAI();

		if (resources == null) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		boolean exists = true;
		Object object = null;
		try {
			object = resources.lookup(path);
		} catch (NamingException e) {
			exists = false;
		}

		Enumeration locksList = null;

		if (lockRequestType == LOCK_CREATION) {

			// Generating lock id
			String lockTokenStr = req.getServletPath() + "-" + lock.type + "-"
			    + lock.scope + "-" + req.getUserPrincipal() + "-"
			    + lock.depth + "-" + lock.owner + "-" + lock.tokens + "-"
			    + lock.expiresAt + "-" + System.currentTimeMillis() + "-"
			    + secret;
			lockToken = md5Encoder.encode(md5Helper.digest(lockTokenStr.getBytes()));

			if ( (exists) && (object instanceof DirContext) &&
			     (lock.depth == INFINITY) ) {

			    // Locking a collection (and all its member resources)

			    // Checking if a child resource of this collection is
			    // already locked
			    Vector lockPaths = new Vector();
			    locksList = collectionLocks.elements();
			    while (locksList.hasMoreElements()) {
			        LockInfo currentLock = (LockInfo) locksList.nextElement();
			        if (currentLock.hasExpired()) {
			            resourceLocks.remove(currentLock.path);
			            continue;
			        }
			        if ( (currentLock.path.startsWith(lock.path)) &&
			             ((currentLock.isExclusive()) ||
			              (lock.isExclusive())) ) {
			            // A child collection of this collection is locked
			            lockPaths.addElement(currentLock.path);
			        }
			    }
			    locksList = resourceLocks.elements();
			    while (locksList.hasMoreElements()) {
			        LockInfo currentLock = (LockInfo) locksList.nextElement();
			        if (currentLock.hasExpired()) {
			            resourceLocks.remove(currentLock.path);
			            continue;
			        }
			        if ( (currentLock.path.startsWith(lock.path)) &&
			             ((currentLock.isExclusive()) ||
			              (lock.isExclusive())) ) {
			            // A child resource of this collection is locked
			            lockPaths.addElement(currentLock.path);
			        }
			    }

			    if (!lockPaths.isEmpty()) {

			        // One of the child paths was locked
			        // We generate a multistatus error report

			        Enumeration lockPathsList = lockPaths.elements();

			        resp.setStatus(SakaidavStatus.SC_CONFLICT);

			        XMLWriter generatedXML = new XMLWriter();
			        generatedXML.writeXMLHeader();

			        generatedXML.writeElement
			            (null, "multistatus" + generateNamespaceDeclarations(),
			             XMLWriter.OPENING);

			        while (lockPathsList.hasMoreElements()) {
			            generatedXML.writeElement(null, "response",
			                                      XMLWriter.OPENING);
			            generatedXML.writeElement(null, "href",
			                                      XMLWriter.OPENING);
			            generatedXML
			                .writeText((String) lockPathsList.nextElement());
			            generatedXML.writeElement(null, "href",
			                                      XMLWriter.CLOSING);
			            generatedXML.writeElement(null, "status",
			                                      XMLWriter.OPENING);
			            generatedXML
			                .writeText("HTTP/1.1 " + SakaidavStatus.SC_LOCKED
			                           + " " + SakaidavStatus
			                           .getStatusText(SakaidavStatus.SC_LOCKED));
			            generatedXML.writeElement(null, "status",
			                                      XMLWriter.CLOSING);

			            generatedXML.writeElement(null, "response",
			                                      XMLWriter.CLOSING);
			        }

			        generatedXML.writeElement(null, "multistatus",
			                              XMLWriter.CLOSING);

			        Writer writer = resp.getWriter();
			        writer.write(generatedXML.toString());
			        writer.close();

			        return;

			    }

			    boolean addLock = true;

			    // Checking if there is already a shared lock on this path
			    locksList = collectionLocks.elements();
			    while (locksList.hasMoreElements()) {

			        LockInfo currentLock = (LockInfo) locksList.nextElement();
			        if (currentLock.path.equals(lock.path)) {

			            if (currentLock.isExclusive()) {
			                resp.sendError(SakaidavStatus.SC_LOCKED);
			                return;
			            } else {
			                if (lock.isExclusive()) {
			                    resp.sendError(SakaidavStatus.SC_LOCKED);
			                    return;
			                }
			            }

			            currentLock.tokens.addElement(lockToken);
			            lock = currentLock;
			            addLock = false;

			        }

			    }

			    if (addLock) {
			        lock.tokens.addElement(lockToken);
			        collectionLocks.addElement(lock);
			    }

			} else {

			    // Locking a single resource

			    // Retrieving an already existing lock on that resource
			    LockInfo presentLock = (LockInfo) resourceLocks.get(lock.path);
			    if (presentLock != null) {

			        if ((presentLock.isExclusive()) || (lock.isExclusive())) {
			            // If either lock is exclusive, the lock can't be
			            // granted
			            resp.sendError(SakaidavStatus.SC_PRECONDITION_FAILED);
			            return;
			        } else {
			            presentLock.tokens.addElement(lockToken);
			            lock = presentLock;
			        }

			    } else {

			        lock.tokens.addElement(lockToken);
			        resourceLocks.put(lock.path, lock);

			        // Checking if a resource exists at this path
			        exists = true;
			        try {
			            object = resources.lookup(path);
			        } catch (NamingException e) {
			            exists = false;
			        }
			        if (!exists) {

			            // "Creating" a lock-null resource
			            int slash = lock.path.lastIndexOf('/');
			            String parentPath = lock.path.substring(0, slash);

			            Vector lockNulls =
			                (Vector) lockNullResources.get(parentPath);
			            if (lockNulls == null) {
			                lockNulls = new Vector();
			                lockNullResources.put(parentPath, lockNulls);
			            }

			            lockNulls.addElement(lock.path);

			        }

			    }

			}

		}

		if (lockRequestType == LOCK_REFRESH) {

			String ifHeader = req.getHeader("If");
			if (ifHeader == null)
			    ifHeader = "";

			// Checking resource locks

			LockInfo toRenew = (LockInfo) resourceLocks.get(path);
			Enumeration tokenList = null;
			if (lock != null) {

			    // At least one of the tokens of the locks must have been given

			    tokenList = toRenew.tokens.elements();
			    while (tokenList.hasMoreElements()) {
			        String token = (String) tokenList.nextElement();
			        if (ifHeader.indexOf(token) != -1) {
			            toRenew.expiresAt = lock.expiresAt;
			            lock = toRenew;
			        }
			    }

			}

			// Checking inheritable collection locks

			Enumeration collectionLocksList = collectionLocks.elements();
			while (collectionLocksList.hasMoreElements()) {
			    toRenew = (LockInfo) collectionLocksList.nextElement();
			    if (path.equals(toRenew.path)) {

			        tokenList = toRenew.tokens.elements();
			        while (tokenList.hasMoreElements()) {
			            String token = (String) tokenList.nextElement();
			            if (ifHeader.indexOf(token) != -1) {
			                toRenew.expiresAt = lock.expiresAt;
			                lock = toRenew;
			            }
			        }

			    }
			}

		}

		// Set the status, then generate the XML response containing
		// the lock information
		XMLWriter generatedXML = new XMLWriter();
		generatedXML.writeXMLHeader();
		generatedXML.writeElement(null, "prop"
			                      + generateNamespaceDeclarations(),
			                      XMLWriter.OPENING);

		generatedXML.writeElement(null, "lockdiscovery",
			                      XMLWriter.OPENING);

		lock.toXML(generatedXML, true);

		generatedXML.writeElement(null, "lockdiscovery",
			                      XMLWriter.CLOSING);

		generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);

		/* the RFC requires this header in response to lock creation */

		if (lockRequestType == LOCK_CREATION)
		    resp.addHeader("Lock-Token", "opaquelocktoken:" 
				                 + lockToken);
		resp.setStatus(SakaidavStatus.SC_OK);
		resp.setContentType("text/xml; charset=UTF-8");
		Writer writer = resp.getWriter();
		writer.write(generatedXML.toString());
		writer.close();

	}


	/**
	 * UNLOCK Method.
	 */
	protected void doUnlock(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		if (readOnly) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return;
		}

		if (isLocked(req)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return;
		}

		String path = getRelativePath(req);

		String lockTokenHeader = req.getHeader("Lock-Token");
		if (lockTokenHeader == null)
			lockTokenHeader = "";

                // Only allow lock/unlock for someone who can do update
                // NB: we don't check that the person unlocking is the same as
                // the one locking. Experience with Contribute says that
                // significant size groups have a big problem with people
                // leaving resources inadvertently locked. No one will use
                // the system if they continually have to find a privileged
                // user to unscramble things. Contribute does check who owns
                // the lock, so to bypass the lock you have to run a copy of
                // DAVExplorer and unlock it manually. That seems like a
                // good compromise. At any rate, there needs to be some
                // check here, which there wasn't originally.
                if (!ContentHostingService.allowUpdateResource (path)) {
                    resp.sendError(SakaidavStatus.SC_FORBIDDEN);
                    return;
                }

		// Checking resource locks

		LockInfo lock = (LockInfo) resourceLocks.get(path);
		Enumeration tokenList = null;
		if (lock != null) {

			// At least one of the tokens of the locks must have been given

			tokenList = lock.tokens.elements();
			while (tokenList.hasMoreElements()) {
			    String token = (String) tokenList.nextElement();
			    if (lockTokenHeader.indexOf(token) != -1) {
			        lock.tokens.removeElement(token);
			    }
			}

			if (lock.tokens.isEmpty()) {
			    resourceLocks.remove(path);
			    // Removing any lock-null resource which would be present
			    lockNullResources.remove(path);
			}

		}

		// Checking inheritable collection locks

		Enumeration collectionLocksList = collectionLocks.elements();
		while (collectionLocksList.hasMoreElements()) {
			lock = (LockInfo) collectionLocksList.nextElement();
			if (path.equals(lock.path)) {

			    tokenList = lock.tokens.elements();
			    while (tokenList.hasMoreElements()) {
			        String token = (String) tokenList.nextElement();
			        if (lockTokenHeader.indexOf(token) != -1) {
			            lock.tokens.removeElement(token);
			            break;
			        }
			    }

			    if (lock.tokens.isEmpty()) {
			        collectionLocks.removeElement(lock);
			        // Removing any lock-null resource which would be present
			        lockNullResources.remove(path);
			    }

			}
		}

		resp.setStatus(SakaidavStatus.SC_NO_CONTENT);

	}


	// -------------------------------------------------------- Private Methods


	/**
	 * Generate the namespace declarations.
	 */
	private String generateNamespaceDeclarations() {
		return " xmlns=\"" + DEFAULT_NAMESPACE + "\"";
	}


	/**
	 * Check to see if a resource is currently write locked. The method
	 * will look at the "If" header to make sure the client
	 * has give the appropriate lock tokens.
	 *
	 * @param req Servlet request
	 * @return boolean true if the resource is locked (and no appropriate
	 * lock token has been found for at least one of the non-shared locks which
	 * are present on the resource).
	 */
	private boolean isLocked(HttpServletRequest req) {

		String path = getRelativePath(req);

		String ifHeader = req.getHeader("If");
		if (ifHeader == null)
			ifHeader = "";

		String lockTokenHeader = req.getHeader("Lock-Token");
		if (lockTokenHeader == null)
			lockTokenHeader = "";

		return isLocked(path, ifHeader + lockTokenHeader);

	}


	/**
	 * Check to see if a resource is currently write locked.
	 *
	 * @param path Path of the resource
	 * @param ifHeader "If" HTTP header which was included in the request
	 * @return boolean true if the resource is locked (and no appropriate
	 * lock token has been found for at least one of the non-shared locks which
	 * are present on the resource).
	 */
	private boolean isLocked(String path, String ifHeader) {

		// Checking resource locks

		LockInfo lock = (LockInfo) resourceLocks.get(path);
		Enumeration tokenList = null;
		if ((lock != null) && (lock.hasExpired())) {
			resourceLocks.remove(path);
		} else if (lock != null) {

			// At least one of the tokens of the locks must have been given

			tokenList = lock.tokens.elements();
			boolean tokenMatch = false;
			while (tokenList.hasMoreElements()) {
			    String token = (String) tokenList.nextElement();
			    if (ifHeader.indexOf(token) != -1)
			        tokenMatch = true;
			}
			if (!tokenMatch)
			    return true;

		}

		// Checking inheritable collection locks

		Enumeration collectionLocksList = collectionLocks.elements();
		while (collectionLocksList.hasMoreElements()) {
			lock = (LockInfo) collectionLocksList.nextElement();
			if (lock.hasExpired()) {
			    collectionLocks.removeElement(lock);
			} else if (path.startsWith(lock.path)) {

			    tokenList = lock.tokens.elements();
			    boolean tokenMatch = false;
			    while (tokenList.hasMoreElements()) {
			        String token = (String) tokenList.nextElement();
			        if (ifHeader.indexOf(token) != -1)
			            tokenMatch = true;
			    }
			    if (!tokenMatch)
			        return true;

			}
		}

		return false;

	}

	/**
	 * Get the destination path from the header
	 */

	private String getDestinationPath(HttpServletRequest req)
	{
		// Parsing destination header

		String destinationPath = req.getHeader("Destination");

		if (destinationPath == null) {
			return null;
		}

		int protocolIndex = destinationPath.indexOf("://");
		if (protocolIndex >= 0) {
			// if the Destination URL contains the protocol, we can safely
			// trim everything upto the first "/" character after "://"
			int firstSeparator =
			    destinationPath.indexOf("/", protocolIndex + 4);
			if (firstSeparator < 0) {
			    destinationPath = "/";
			} else {
			    destinationPath = destinationPath.substring(firstSeparator);
			}
		} else {
			String hostName = req.getServerName();
			if ((hostName != null) && (destinationPath.startsWith(hostName))) {
			    destinationPath = destinationPath.substring(hostName.length());
			}

			int portIndex = destinationPath.indexOf(":");
			if (portIndex >= 0) {
			    destinationPath = destinationPath.substring(portIndex);
			}

			if (destinationPath.startsWith(":")) {
			    int firstSeparator = destinationPath.indexOf("/");
			    if (firstSeparator < 0) {
			        destinationPath = "/";
			    } else {
			        destinationPath = 
			            destinationPath.substring(firstSeparator);
			    }
			}
		}

		String contextPath = req.getContextPath();
		if ((contextPath != null) &&
			(destinationPath.startsWith(contextPath))) {
			destinationPath = destinationPath.substring(contextPath.length());
		}

		String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String servletPath = req.getServletPath();
			if ((servletPath != null) &&
			    (destinationPath.startsWith(servletPath))) {
			    destinationPath = destinationPath
			        .substring(servletPath.length());
			}
		}

		destinationPath = 
			RequestUtil.URLDecode(normalize(destinationPath), "UTF8");

		return destinationPath;

	} // getDestinationPath

	/**
	 * Copy a resource.
	 *
	 * @param req Servlet request
	 * @param resp Servlet response
	 * @return boolean true if the copy is successful
	 */
	private boolean copyResource(HttpServletRequest req,
			                     HttpServletResponse resp)
		throws ServletException, IOException {

		String destinationPath = getDestinationPath(req);

		if (destinationPath == null) {
			resp.sendError(SakaidavStatus.SC_BAD_REQUEST);
			return false;
		}

		if (debug > 0)
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Dest path :" + destinationPath);

		if ((destinationPath.toUpperCase().startsWith("/WEB-INF")) ||
			(destinationPath.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return false;
		}

		String path = getRelativePath(req);

		if ((path.toUpperCase().startsWith("/WEB-INF")) ||
			(path.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return false;
		}

		if (destinationPath.equals(path)) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return false;
		}

		// Parsing overwrite header

		boolean overwrite = true;
		String overwriteHeader = req.getHeader("Overwrite");

		if (overwriteHeader != null) {
			if (overwriteHeader.equalsIgnoreCase("T")) {
			    overwrite = true;
			} else {
			    overwrite = false;
			}
		}

		// Overwriting the destination

		// Retrieve the resources
		// DirContext resources = getResources();
		DirContextSAKAI resources = getResourcesSAKAI();

		if (resources == null) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return false;
		}

		boolean exists = true;
		try {
			resources.lookup(destinationPath);
		} catch (NamingException e) {
			exists = false;
		}

		if (overwrite) {

			// Delete destination resource, if it exists
			if (exists) {
			    if (!deleteResource(destinationPath, req, resp)) {
			        return false;
			    } else {
			        resp.setStatus(SakaidavStatus.SC_NO_CONTENT);
			    }
			} else {
			    resp.setStatus(SakaidavStatus.SC_CREATED);
			}

		} else {

			// If the destination exists, then it's a conflict
			if (exists) {
			    resp.sendError(SakaidavStatus.SC_PRECONDITION_FAILED);
			    return false;
			}

		}

		// Copying source to destination

		Hashtable errorList = new Hashtable();

		boolean result = copyResource(resources, errorList,
			                          path, destinationPath);

		if ((!result) || (!errorList.isEmpty())) {

			sendReport(req, resp, errorList);
			return false;

		}

		// Removing any lock-null resource which would be present at
		// the destination path
		lockNullResources.remove(destinationPath);

		return true;

	}

	/**
	 * Copy a collection.
	 *
	 * @param resources Resources implementation to be used
	 * @param errorList Hashtable containing the list of errors which occurred
	 * during the copy operation
	 * @param source Path of the resource to be copied
	 * @param dest Destination path
	 */
	private boolean copyResource(DirContextSAKAI resources, Hashtable errorList,
			                     String source, String dest) {
		
/*
		Object object = null;
		try {
			object = resources.lookup(source);
		} catch (NamingException e) {
		}

		if (object instanceof DirContext) {

			try {
			    resources.createSubcontext(dest);
			} catch (NamingException e) {
			    errorList.put
			        (dest, new Integer(SakaidavStatus.SC_CONFLICT));
			    return false;
			}

			try {
			    NamingEnumeration enumer = resources.list(source);
			    while (enumer.hasMoreElements()) {
			        NameClassPair ncPair = (NameClassPair) enumer.nextElement();
			        String childDest = dest;
			        if (!childDest.equals("/"))
			            childDest += "/";
			        childDest += ncPair.getName();
			        String childSrc = source;
			        if (!childSrc.equals("/"))
			            childSrc += "/";
			        childSrc += ncPair.getName();
			        copyResource(resources, errorList, childSrc, childDest);
			    }
			} catch (NamingException e) {
			    errorList.put
			        (dest, new Integer(SakaidavStatus.SC_INTERNAL_SERVER_ERROR));
			    return false;
			}

		} else {

			if (object instanceof Resource) {
			    try {
			        resources.bind(dest, object);
			    } catch (NamingException e) {
			        errorList.put
			            (source,
			             new Integer(SakaidavStatus.SC_INTERNAL_SERVER_ERROR));
			        return false;
			    }
			} else {
			    errorList.put
			        (source,
			         new Integer(SakaidavStatus.SC_INTERNAL_SERVER_ERROR));
			    return false;
			}

		}

		return true;
*/
		if (debug > 1)
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","Copy: " + source + " To: " + dest);

		errorList.put
		   (source,
			new Integer(SakaidavStatus.SC_INTERNAL_SERVER_ERROR));

		source = fixDirPathSAKAI(source);	
		dest = fixDirPathSAKAI(dest);	

		// System.out.println("copyResource source="+source+" dest="+dest);

		try
		{
			ContentHostingService.copy(source,dest);
		}
		catch (PermissionException e)
		{
				return false;
		}
		catch (InUseException e)
		{
				return false;
		}
		catch (IdUnusedException e)
		{
			return false;
		}
		catch (IdUsedException e)
		{
			// Resource not found (this is actually the normal case)
		}
		catch (OverQuotaException e)
		{
			Log.warn("sakai","SAKAIDavServlet.copyResource() - OverQuota "+source);
			return false;
		}
		catch (TypeException e)
		{
			Log.warn("sakai","SAKAIDavServlet.copyResource() - TypeException "+source);
			return false;
		}
		catch (ServerOverloadException e)
		{
			Log.warn("sakai","SAKAIDavServlet.copyResource() ServerOverloadException:" + source);
			return false;
		}

		// We did not have an error
		errorList.clear();
		return true;

	}


	/**
	 * Delete a resource.
	 *
	 * @param req Servlet request
	 * @param resp Servlet response
	 * @return boolean true if the copy is successful
	 */
	private boolean deleteResource(HttpServletRequest req,
			                       HttpServletResponse resp)
		throws ServletException, IOException {

		String path = getRelativePathSAKAI(req);

		return deleteResource(path, req, resp);

	}


	/**
	 * Delete a resource.
	 *
	 * @param path Path of the resource which is to be deleted
	 * @param req Servlet request
	 * @param resp Servlet response
	 */
	private boolean deleteResource(String path, HttpServletRequest req,
			                       HttpServletResponse resp)
		throws ServletException, IOException {

		if ((path.toUpperCase().startsWith("/WEB-INF")) ||
			(path.toUpperCase().startsWith("/META-INF"))) {
			resp.sendError(SakaidavStatus.SC_FORBIDDEN);
			return false;
		}

		String ifHeader = req.getHeader("If");
		if (ifHeader == null)
			ifHeader = "";

		String lockTokenHeader = req.getHeader("Lock-Token");
		if (lockTokenHeader == null)
			lockTokenHeader = "";

		if (isLocked(path, ifHeader + lockTokenHeader)) {
			resp.sendError(SakaidavStatus.SC_LOCKED);
			return false;
		}

	path = fixDirPathSAKAI(path);	// In case we are a directory

	boolean isCollection = false;
	try
	{
		isCollection = ContentHostingService.getProperties (path).getBooleanProperty (ResourceProperties.PROP_IS_COLLECTION);
			
		if (isCollection)
		{
			ContentHostingService.removeCollection (path);
		}
		else
		{
			ContentHostingService.removeResource (path);
		}
	}
	catch (PermissionException e)
	{
				return false;
	}
	catch (InUseException e)
	{
				return false;
	}
	catch (IdUnusedException e)
	{
		// Resource not found (this is actually the normal case)
	}
	catch (EmptyException e)
	{
		Log.warn("sakai","SAKAIDavServlet.deleteResource() - EmptyException "+path);
				return false;
	}
	catch (TypeException e)
	{
		Log.warn("sakai","SAKAIDavServlet.deleteResource() - TypeException "+path);
				return false;
	}
	catch (ServerOverloadException e)
	{
		Log.warn("sakai", "SAKAIDavServlet.deleteResource() - ServerOverloadException " + path);
		return false;
	}
	return true;

	}


	/**
	 * Deletes a collection.
	 *
	 * @param resources Resources implementation associated with the context
	 * @param path Path to the collection to be deleted
	 * @param errorList Contains the list of the errors which occurred
	 */
	private void deleteCollection(HttpServletRequest req,
			                      DirContext resources,
			                      String path, Hashtable errorList) {

		if (debug > 1)
			if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","deleteCollection:" + path);

		if ((path.toUpperCase().startsWith("/WEB-INF")) ||
			(path.toUpperCase().startsWith("/META-INF"))) {
			errorList.put(path, new Integer(SakaidavStatus.SC_FORBIDDEN));
			return;
		}

		String ifHeader = req.getHeader("If");
		if (ifHeader == null)
			ifHeader = "";

		String lockTokenHeader = req.getHeader("Lock-Token");
		if (lockTokenHeader == null)
			lockTokenHeader = "";

		Enumeration enumer = null;
		try {
			enumer = resources.list(path);
		} catch (NamingException e) {
			errorList.put(path, new Integer
			    (SakaidavStatus.SC_INTERNAL_SERVER_ERROR));
			return;
		}

		while (enumer.hasMoreElements()) {
			NameClassPair ncPair = (NameClassPair) enumer.nextElement();
			String childName = path;
			if (!childName.equals("/"))
			    childName += "/";
			childName += ncPair.getName();

			if (isLocked(childName, ifHeader + lockTokenHeader)) {

			    errorList.put(childName, new Integer(SakaidavStatus.SC_LOCKED));

			} else {

			    try {
			        Object object = resources.lookup(childName);
			        if (object instanceof DirContext) {
			            deleteCollection(req, resources, childName, errorList);
			        }

			        try {
			            resources.unbind(childName);
			        } catch (NamingException e) {
			            if (!(object instanceof DirContext)) {
			                // If it's not a collection, then it's an unknown
			                // error
			                errorList.put
			                    (childName, new Integer
			                        (SakaidavStatus.SC_INTERNAL_SERVER_ERROR));
			            }
			        }
			    } catch (NamingException e) {
			        errorList.put
			            (childName, new Integer
			                (SakaidavStatus.SC_INTERNAL_SERVER_ERROR));
			    }
			}

		}

	}


	/**
	 * Send a multistatus element containing a complete error report to the
	 * client.
	 *
	 * @param req Servlet request
	 * @param resp Servlet response
	 * @param errorList List of error to be displayed
	 */
	private void sendReport(HttpServletRequest req, HttpServletResponse resp,
			                Hashtable errorList)
		throws ServletException, IOException {

		resp.setStatus(SakaidavStatus.SC_MULTI_STATUS);

		String absoluteUri = req.getRequestURI();
		String relativePath = getRelativePath(req);

		XMLWriter generatedXML = new XMLWriter();
		generatedXML.writeXMLHeader();

		generatedXML.writeElement(null, "multistatus"
			                      + generateNamespaceDeclarations(),
			                      XMLWriter.OPENING);

		Enumeration pathList = errorList.keys();
		while (pathList.hasMoreElements()) {

			String errorPath = (String) pathList.nextElement();
			int errorCode = ((Integer) errorList.get(errorPath)).intValue();

			generatedXML.writeElement(null, "response", XMLWriter.OPENING);

			generatedXML.writeElement(null, "href", XMLWriter.OPENING);
			String toAppend = errorPath.substring(relativePath.length());
			if (!toAppend.startsWith("/"))
			    toAppend = "/" + toAppend;
			generatedXML.writeText(absoluteUri + toAppend);
			generatedXML.writeElement(null, "href", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML
			    .writeText("HTTP/1.1 " + errorCode + " "
			               + SakaidavStatus.getStatusText(errorCode));
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "response", XMLWriter.CLOSING);

		}

		generatedXML.writeElement(null, "multistatus", XMLWriter.CLOSING);

		Writer writer = resp.getWriter();
		writer.write(generatedXML.toString());
		writer.close();

	}


	/**
	 * Propfind helper method.
	 *
	 * @param resources Resources object associated with this context
	 * @param generatedXML XML response to the Propfind request
	 * @param path Path of the current resource
	 * @param type Propfind type
	 * @param propertiesVector If the propfind type is find properties by
	 * name, then this Vector contains those properties
	 */
	private void parseProperties(HttpServletRequest req,
			                     DirContextSAKAI resources, XMLWriter generatedXML,
			                     String path, int type,
			                     Vector propertiesVector) {

		// Exclude any resource in the /WEB-INF and /META-INF subdirectories
		// (the "toUpperCase()" avoids problems on Windows systems)
		if (path.toUpperCase().startsWith("/WEB-INF") ||
			path.toUpperCase().startsWith("/META-INF"))
			return;

		ResourceInfoSAKAI resourceInfo = new ResourceInfoSAKAI(path, resources);

		generatedXML.writeElement(null, "response", XMLWriter.OPENING);
		String status = new String("HTTP/1.1 " + SakaidavStatus.SC_OK + " "
			                       + SakaidavStatus.getStatusText
			                       (SakaidavStatus.SC_OK));

		// Generating href element
		generatedXML.writeElement(null, "href", XMLWriter.OPENING);

		 String href = (String) req.getAttribute("javax.servlet.forward.servlet_path");
		 if (href == null)
		 {
			 href = (String) req.getAttribute("javax.servlet.include.servlet_path");
		 }
		 if (href == null)
		 {
			 href = req.getContextPath();
		 }
		
		 if ((href.endsWith("/")) && (path.startsWith("/")))
			 href += path.substring(1);
		 else
			 href += path;
		 if ((resourceInfo.collection) && (!href.endsWith("/")))
			 href += "/";

		if (Log.getLogger("sakai").isDebugEnabled()) Log.debug("sakai","parserProperties href="+href);

		generatedXML.writeText(rewriteUrl(href));

		generatedXML.writeElement(null, "href", XMLWriter.CLOSING);

		String resourceName = justName(path);

		switch (type) {

		case FIND_ALL_PROP :

			generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			generatedXML.writeProperty
			    (null, "creationdate",
			     getISOCreationDate(resourceInfo.creationDate));
			generatedXML.writeElement(null, "displayname", XMLWriter.OPENING);
			generatedXML.writeData(resourceName);
			generatedXML.writeElement(null, "displayname", XMLWriter.CLOSING);
			generatedXML.writeProperty(null, "getcontentlanguage",
			                           Locale.getDefault().toString());
			if (!resourceInfo.collection) {
			    generatedXML.writeProperty
			        (null, "getlastmodified", resourceInfo.httpDate);
			    generatedXML.writeProperty
			        (null, "getcontentlength",
			         String.valueOf(resourceInfo.length));
			    generatedXML.writeProperty
			        (null, "getcontenttype",
			               resourceInfo.MIMEType);
//                   getServletContext().getMimeType(resourceInfo.path));
  	                    generatedXML.writeProperty(null, "getetag",
                                         resourceInfo.eTag);
//                                       getETagValue(resourceInfo, true));
			    generatedXML.writeElement(null, "resourcetype",
			                              XMLWriter.NO_CONTENT);
			} else {
			    generatedXML.writeElement(null, "resourcetype",
			                              XMLWriter.OPENING);
			    generatedXML.writeElement(null, "collection",
			                              XMLWriter.NO_CONTENT);
			    generatedXML.writeElement(null, "resourcetype",
			                              XMLWriter.CLOSING);
			}

			generatedXML.writeProperty(null, "source", "");

			String supportedLocks = "<lockentry>"
			    + "<lockscope><exclusive/></lockscope>"
			    + "<locktype><write/></locktype>"
			    + "</lockentry>" + "<lockentry>"
			    + "<lockscope><shared/></lockscope>"
			    + "<locktype><write/></locktype>"
			    + "</lockentry>";
			generatedXML.writeElement(null, "supportedlock",
			                          XMLWriter.OPENING);
			generatedXML.writeText(supportedLocks);
			generatedXML.writeElement(null, "supportedlock",
			                          XMLWriter.CLOSING);

			generateLockDiscovery(path, generatedXML);

			generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			break;

		case FIND_PROPERTY_NAMES :

			generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			generatedXML.writeElement(null, "creationdate",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "displayname",
			                          XMLWriter.NO_CONTENT);
			if (!resourceInfo.collection) {
			    generatedXML.writeElement(null, "getcontentlanguage",
			                              XMLWriter.NO_CONTENT);
			    generatedXML.writeElement(null, "getcontentlength",
			                              XMLWriter.NO_CONTENT);
			    generatedXML.writeElement(null, "getcontenttype",
			                             XMLWriter.NO_CONTENT);
			    generatedXML.writeElement(null, "getetag",
			                             XMLWriter.NO_CONTENT);
			    generatedXML.writeElement(null, "getlastmodified",
			                              XMLWriter.NO_CONTENT);
			}
			generatedXML.writeElement(null, "resourcetype",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "source", XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "lockdiscovery",
			                          XMLWriter.NO_CONTENT);

			generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			break;

		case FIND_BY_PROPERTY :


			Vector propertiesNotFound = new Vector();

			// Parse the list of properties

			generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			Enumeration properties = propertiesVector.elements();

			while (properties.hasMoreElements()) {

			    String property = (String) properties.nextElement();

			    if (property.equals("creationdate")) {
			        generatedXML.writeProperty
			            (null, "creationdate",
			             getISOCreationDate(resourceInfo.creationDate));
			    } else if (property.equals("displayname")) {
			        generatedXML.writeElement
			            (null, "displayname", XMLWriter.OPENING);

			generatedXML.writeData(resourceInfo.displayName);
			        generatedXML.writeElement
			            (null, "displayname", XMLWriter.CLOSING);
			    } else if (property.equals("getcontentlanguage")) {
			        if (resourceInfo.collection) {
			            propertiesNotFound.addElement(property);
			        } else {
			            generatedXML.writeProperty
			                (null, "getcontentlanguage",
			                 Locale.getDefault().toString());
			        }
			    } else if (property.equals("getcontentlength")) {
			        if (resourceInfo.collection) {
			            propertiesNotFound.addElement(property);
			        } else {
			            generatedXML.writeProperty
			                (null, "getcontentlength",
			                 (String.valueOf(resourceInfo.length)));
			        }
			    } else if (property.equals("getcontenttype")) {
			        if (resourceInfo.collection) {
			            propertiesNotFound.addElement(property);
			        } else {
			            generatedXML.writeProperty
			                (null, "getcontenttype",
			                 getServletContext().getMimeType
			                 (resourceInfo.path));
			        }
			    } else if (property.equals("getetag")) {
                                if (resourceInfo.collection) {
                                    propertiesNotFound.addElement(property);
                                } else {
                                    generatedXML.writeProperty
                                        (null, "getetag",
                                         resourceInfo.eTag);
//                                       getETagValue(resourceInfo, true));
                               }
			    } else if (property.equals("getlastmodified")) {
			        if (resourceInfo.collection) {
			            propertiesNotFound.addElement(property);
			        } else {
			            generatedXML.writeProperty
			                (null, "getlastmodified", resourceInfo.httpDate);
			        }
			    } else if (property.equals("resourcetype")) {
			        if (resourceInfo.collection) {
			            generatedXML.writeElement(null, "resourcetype",
			                                      XMLWriter.OPENING);
			            generatedXML.writeElement(null, "collection",
			                                      XMLWriter.NO_CONTENT);
			            generatedXML.writeElement(null, "resourcetype",
			                                      XMLWriter.CLOSING);
			        } else {
			            generatedXML.writeElement(null, "resourcetype",
			                                      XMLWriter.NO_CONTENT);
			        }
// iscollection is an MS property. Contribute uses it but seems to be
// able to get along without it
//			    } else if (property.equals("iscollection")) {
//				generatedXML.writeProperty
//			                (null, "iscollection", 
//					 resourceInfo.collection ? "1" : "0");
			    } else if (property.equals("source")) {
			        generatedXML.writeProperty(null, "source", "");
			    } else if (property.equals("supportedlock")) {
			        supportedLocks = "<lockentry>"
			            + "<lockscope><exclusive/></lockscope>"
			            + "<locktype><write/></locktype>"
			            + "</lockentry>" + "<lockentry>"
			            + "<lockscope><shared/></lockscope>"
			            + "<locktype><write/></locktype>"
			            + "</lockentry>";
			        generatedXML.writeElement(null, "supportedlock",
			                                  XMLWriter.OPENING);
			        generatedXML.writeText(supportedLocks);
			        generatedXML.writeElement(null, "supportedlock",
			                                  XMLWriter.CLOSING);
			    } else if (property.equals("lockdiscovery")) {
			        if (!generateLockDiscovery(path, generatedXML))
			            propertiesNotFound.addElement(property);
			    } else {
			        propertiesNotFound.addElement(property);
			    }
			}

			generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			Enumeration propertiesNotFoundList = propertiesNotFound.elements();

			if (propertiesNotFoundList.hasMoreElements()) {
			    status = new String("HTTP/1.1 " + SakaidavStatus.SC_NOT_FOUND
			                        + " " + SakaidavStatus.getStatusText
			                        (SakaidavStatus.SC_NOT_FOUND));

			    generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			    generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			    while (propertiesNotFoundList.hasMoreElements()) {
			        generatedXML.writeElement
			            (null, (String) propertiesNotFoundList.nextElement(),
			             XMLWriter.NO_CONTENT);
			    }

			    generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			    generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			    generatedXML.writeText(status);
			    generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			    generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			}
			break;

		}

		generatedXML.writeElement(null, "response", XMLWriter.CLOSING);

	}


	/**
	 * Propfind helper method. Dispays the properties of a lock-null resource.
	 *
	 * @param resources Resources object associated with this context
	 * @param generatedXML XML response to the Propfind request
	 * @param path Path of the current resource
	 * @param type Propfind type
	 * @param propertiesVector If the propfind type is find properties by
	 * name, then this Vector contains those properties
	 */
	private void parseLockNullProperties(HttpServletRequest req,
			                             XMLWriter generatedXML,
			                             String path, int type,
			                             Vector propertiesVector) {

		// Exclude any resource in the /WEB-INF and /META-INF subdirectories
		// (the "toUpperCase()" avoids problems on Windows systems)
		if (path.toUpperCase().startsWith("/WEB-INF") ||
			path.toUpperCase().startsWith("/META-INF"))
			return;

		// Retrieving the lock associated with the lock-null resource
		LockInfo lock = (LockInfo) resourceLocks.get(path);

		if (lock == null)
			return;

		generatedXML.writeElement(null, "response", XMLWriter.OPENING);
		String status = new String("HTTP/1.1 " + SakaidavStatus.SC_OK + " "
			                       + SakaidavStatus.getStatusText
			                       (SakaidavStatus.SC_OK));

		// Generating href element
		generatedXML.writeElement(null, "href", XMLWriter.OPENING);

		String absoluteUri = req.getRequestURI();
		String relativePath = getRelativePath(req);
		String toAppend = path.substring(relativePath.length());
		if (!toAppend.startsWith("/"))
			toAppend = "/" + toAppend;

		generatedXML.writeText(rewriteUrl(normalize(absoluteUri + toAppend)));

		generatedXML.writeElement(null, "href", XMLWriter.CLOSING);

		String resourceName = justName(path);

		switch (type) {

		case FIND_ALL_PROP :

			generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			generatedXML.writeProperty
			    (null, "creationdate",
			     getISOCreationDate(lock.creationDate.getTime()));
			generatedXML.writeElement
			    (null, "displayname", XMLWriter.OPENING);
			generatedXML.writeData(resourceName);
			generatedXML.writeElement
			    (null, "displayname", XMLWriter.CLOSING);
			generatedXML.writeProperty(null, "getcontentlanguage",
			                           Locale.getDefault().toString());
			generatedXML.writeProperty(null, "getlastmodified",
			                           formats[0].format(lock.creationDate));
			generatedXML.writeProperty
			    (null, "getcontentlength", String.valueOf(0));
			generatedXML.writeProperty(null, "getcontenttype", "");
			generatedXML.writeProperty(null, "getetag", "");
			generatedXML.writeElement(null, "resourcetype",
			                          XMLWriter.OPENING);
			generatedXML.writeElement(null, "lock-null", XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "resourcetype",
			                          XMLWriter.CLOSING);

			generatedXML.writeProperty(null, "source", "");

			String supportedLocks = "<lockentry>"
			    + "<lockscope><exclusive/></lockscope>"
			    + "<locktype><write/></locktype>"
			    + "</lockentry>" + "<lockentry>"
			    + "<lockscope><shared/></lockscope>"
			    + "<locktype><write/></locktype>"
			    + "</lockentry>";
			generatedXML.writeElement(null, "supportedlock",
			                          XMLWriter.OPENING);
			generatedXML.writeText(supportedLocks);
			generatedXML.writeElement(null, "supportedlock",
			                          XMLWriter.CLOSING);

			generateLockDiscovery(path, generatedXML);

			generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			break;

		case FIND_PROPERTY_NAMES :

			generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			generatedXML.writeElement(null, "creationdate",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "displayname",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "getcontentlanguage",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "getcontentlength",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "getcontenttype",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "getetag",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "getlastmodified",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "resourcetype",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "source",
			                          XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "lockdiscovery",
			                          XMLWriter.NO_CONTENT);

			generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			break;

		case FIND_BY_PROPERTY :

			Vector propertiesNotFound = new Vector();

			// Parse the list of properties

			generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			Enumeration properties = propertiesVector.elements();

			while (properties.hasMoreElements()) {

			    String property = (String) properties.nextElement();

			    if (property.equals("creationdate")) {
			        generatedXML.writeProperty
			            (null, "creationdate",
			             getISOCreationDate(lock.creationDate.getTime()));
			    } else if (property.equals("displayname")) {
			        generatedXML.writeElement
			            (null, "displayname", XMLWriter.OPENING);
			        generatedXML.writeData(resourceName);
			        generatedXML.writeElement
			            (null, "displayname", XMLWriter.CLOSING);
			    } else if (property.equals("getcontentlanguage")) {
			        generatedXML.writeProperty
			            (null, "getcontentlanguage",
			             Locale.getDefault().toString());
			    } else if (property.equals("getcontentlength")) {
			        generatedXML.writeProperty
			            (null, "getcontentlength", (String.valueOf(0)));
			    } else if (property.equals("getcontenttype")) {
			        generatedXML.writeProperty
			            (null, "getcontenttype", "");
			    } else if (property.equals("getetag")) {
			        generatedXML.writeProperty(null, "getetag", "");
			    } else if (property.equals("getlastmodified")) {
			        generatedXML.writeProperty
			            (null, "getlastmodified",
			             formats[0].format(lock.creationDate));
			    } else if (property.equals("resourcetype")) {
			        generatedXML.writeElement(null, "resourcetype",
			                                  XMLWriter.OPENING);
			        generatedXML.writeElement(null, "lock-null",
			                                  XMLWriter.NO_CONTENT);
			        generatedXML.writeElement(null, "resourcetype",
			                                  XMLWriter.CLOSING);
			    } else if (property.equals("source")) {
			        generatedXML.writeProperty(null, "source", "");
			    } else if (property.equals("supportedlock")) {
			        supportedLocks = "<lockentry>"
			            + "<lockscope><exclusive/></lockscope>"
			            + "<locktype><write/></locktype>"
			            + "</lockentry>" + "<lockentry>"
			            + "<lockscope><shared/></lockscope>"
			            + "<locktype><write/></locktype>"
			            + "</lockentry>";
			        generatedXML.writeElement(null, "supportedlock",
			                                  XMLWriter.OPENING);
			        generatedXML.writeText(supportedLocks);
			        generatedXML.writeElement(null, "supportedlock",
			                                  XMLWriter.CLOSING);
			    } else if (property.equals("lockdiscovery")) {
			        if (!generateLockDiscovery(path, generatedXML))
			            propertiesNotFound.addElement(property);
			    } else {
			        propertiesNotFound.addElement(property);
			    }

			}

			generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			Enumeration propertiesNotFoundList = propertiesNotFound.elements();

			if (propertiesNotFoundList.hasMoreElements()) {

			    status = new String("HTTP/1.1 " + SakaidavStatus.SC_NOT_FOUND
			                        + " " + SakaidavStatus.getStatusText
			                        (SakaidavStatus.SC_NOT_FOUND));

			    generatedXML.writeElement(null, "propstat", XMLWriter.OPENING);
			    generatedXML.writeElement(null, "prop", XMLWriter.OPENING);

			    while (propertiesNotFoundList.hasMoreElements()) {
			        generatedXML.writeElement
			            (null, (String) propertiesNotFoundList.nextElement(),
			             XMLWriter.NO_CONTENT);
			    }

			    generatedXML.writeElement(null, "prop", XMLWriter.CLOSING);
			    generatedXML.writeElement(null, "status", XMLWriter.OPENING);
			    generatedXML.writeText(status);
			    generatedXML.writeElement(null, "status", XMLWriter.CLOSING);
			    generatedXML.writeElement(null, "propstat", XMLWriter.CLOSING);

			}

			break;

		}

		generatedXML.writeElement(null, "response", XMLWriter.CLOSING);

	}


	/**
	 * Print the lock discovery information associated with a path.
	 *
	 * @param path Path
	 * @param generatedXML XML data to which the locks info will be appended
	 * @return true if at least one lock was displayed
	 */
	private boolean generateLockDiscovery
		(String path, XMLWriter generatedXML) {

		LockInfo resourceLock = (LockInfo) resourceLocks.get(path);
		Enumeration collectionLocksList = collectionLocks.elements();

		boolean wroteStart = false;

		if (resourceLock != null) {
			wroteStart = true;
			generatedXML.writeElement(null, "lockdiscovery",
			                          XMLWriter.OPENING);
			resourceLock.toXML(generatedXML);
		}

		while (collectionLocksList.hasMoreElements()) {
			LockInfo currentLock =
			    (LockInfo) collectionLocksList.nextElement();
			if (path.startsWith(currentLock.path)) {
			    if (!wroteStart) {
			        wroteStart = true;
			        generatedXML.writeElement(null, "lockdiscovery",
			                                  XMLWriter.OPENING);
			    }
			    currentLock.toXML(generatedXML);
			}
		}

		if (wroteStart) {
			generatedXML.writeElement(null, "lockdiscovery",
			                          XMLWriter.CLOSING);
		} else {
			return false;
		}

		return true;

	}


	/**
	 * Get creation date in ISO format.
	 */
	private String getISOCreationDate(long creationDate) {
		StringBuffer creationDateValue = new StringBuffer
			(creationDateFormat.format
			 (new Date(creationDate)));
		/*
		int offset = Calendar.getInstance().getTimeZone().getRawOffset()
			/ 3600000; // FIXME ?
		if (offset < 0) {
			creationDateValue.append("-");
			offset = -offset;
		} else if (offset > 0) {
			creationDateValue.append("+");
		}
		if (offset != 0) {
			if (offset < 10)
			    creationDateValue.append("0");
			creationDateValue.append(offset + ":00");
		} else {
			creationDateValue.append("Z");
		}
		*/
		return creationDateValue.toString();
	}

	/**
	 * Get a date in HTTP format.
	 */
	private String getHttpDate(long dateMS) {
	return HttpDateFormat.format(new Date(dateMS));
	}


	// --------------------------------------------------  LockInfo Inner Class


	/**
	 * Holds a lock information.
	 */
	private class LockInfo {


		// -------------------------------------------------------- Constructor


		/**
		 * Constructor.
		 *
		 * @param pathname Path name of the file
		 */
		public LockInfo() {

		}


		// ------------------------------------------------- Instance Variables


		String path = "/";
		String type = "write";
		String scope = "exclusive";
		int depth = 0;
		String owner = "";
		Vector tokens = new Vector();
		long expiresAt = 0;
		Date creationDate = new Date();


		// ----------------------------------------------------- Public Methods


		/**
		 * Get a String representation of this lock token.
		 */
		public String toString() {

			String result =  "Type:" + type + "\n";
			result += "Scope:" + scope + "\n";
			result += "Depth:" + depth + "\n";
			result += "Owner:" + owner + "\n";
			result += "Expiration:" +
			    formats[0].format(new Date(expiresAt)) + "\n";
			Enumeration tokensList = tokens.elements();
			while (tokensList.hasMoreElements()) {
			    result += "Token:" + tokensList.nextElement() + "\n";
			}
			return result;

		}


		/**
		 * Return true if the lock has expired.
		 */
		public boolean hasExpired() {
			return (System.currentTimeMillis() > expiresAt);
		}


		/**
		 * Return true if the lock is exclusive.
		 */
		public boolean isExclusive() {

			return (scope.equals("exclusive"));

		}


		/**
		 * Get an XML representation of this lock token. This method will
		 * append an XML fragment to the given XML writer.
		 */
                // originally this called toXML( ... false). That causes
                // the system to show a dummy lock name. That breaks 
                // Contribute. It also violates the RFC. Contribute uses
                // PROPFIND to find the lock name. Furthermore, the RFC
                // specifically prohibits hiding the lock name this way.
                // Rather than treating the lock name as secret, it's
                // better to check permissions, as I now do.
		public void toXML(XMLWriter generatedXML) {
			toXML(generatedXML, true);
		}


		/**
		 * Get an XML representation of this lock token. This method will
		 * append an XML fragment to the given XML writer.
		 */
		public void toXML(XMLWriter generatedXML, boolean showToken) {

			generatedXML.writeElement(null, "activelock", XMLWriter.OPENING);

			generatedXML.writeElement(null, "locktype", XMLWriter.OPENING);
			generatedXML.writeElement(null, type, XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "locktype", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "lockscope", XMLWriter.OPENING);
			generatedXML.writeElement(null, scope, XMLWriter.NO_CONTENT);
			generatedXML.writeElement(null, "lockscope", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "depth", XMLWriter.OPENING);
			if (depth == INFINITY) {
			    generatedXML.writeText("Infinity");
			} else {
			    generatedXML.writeText("0");
			}
			generatedXML.writeElement(null, "depth", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "owner", XMLWriter.OPENING);
			generatedXML.writeText(owner);
			generatedXML.writeElement(null, "owner", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "timeout", XMLWriter.OPENING);
			long timeout = (expiresAt - System.currentTimeMillis()) / 1000;
			generatedXML.writeText("Second-" + timeout);
			generatedXML.writeElement(null, "timeout", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "locktoken", XMLWriter.OPENING);
			if (showToken) {
			    Enumeration tokensList = tokens.elements();
			    while (tokensList.hasMoreElements()) {
			        generatedXML.writeElement(null, "href", XMLWriter.OPENING);
			        generatedXML.writeText("opaquelocktoken:"
			                               + tokensList.nextElement());
			        generatedXML.writeElement(null, "href", XMLWriter.CLOSING);
			    }
			} else {
			    generatedXML.writeElement(null, "href", XMLWriter.OPENING);
			    generatedXML.writeText("opaquelocktoken:dummytoken");
			    generatedXML.writeElement(null, "href", XMLWriter.CLOSING);
			}
			generatedXML.writeElement(null, "locktoken", XMLWriter.CLOSING);

			generatedXML.writeElement(null, "activelock", XMLWriter.CLOSING);

		}


	}


	// --------------------------------------------------- Property Inner Class


	private class Property {

		public String name;
		public String value;
		public String namespace;
		public String namespaceAbbrev;
		public int status = SakaidavStatus.SC_OK;

	}


};


// --------------------------------------------------------  SakaidavStatus Class


/**
 * Wraps the HttpServletResponse class to abstract the
 * specific protocol used.  To support other protocols
 * we would only need to modify this class and the
 * SakaidavRetCode classes.
 *
 * @author              Marc Eaddy
 * @version             1.0, 16 Nov 1997
 */
class SakaidavStatus {


	// ----------------------------------------------------- Instance Variables


	/**
	 * This Hashtable contains the mapping of HTTP and Sakaidav
	 * status codes to descriptive text.  This is a static
	 * variable.
	 */
	private static Hashtable mapStatusCodes = new Hashtable();


	// ------------------------------------------------------ HTTP Status Codes


	/**
	 * Status code (200) indicating the request succeeded normally.
	 */
	public static final int SC_OK = HttpServletResponse.SC_OK;


	/**
	 * Status code (201) indicating the request succeeded and created
	 * a new resource on the server.
	 */
	public static final int SC_CREATED = HttpServletResponse.SC_CREATED;


	/**
	 * Status code (202) indicating that a request was accepted for
	 * processing, but was not completed.
	 */
	public static final int SC_ACCEPTED = HttpServletResponse.SC_ACCEPTED;


	/**
	 * Status code (204) indicating that the request succeeded but that
	 * there was no new information to return.
	 */
	public static final int SC_NO_CONTENT = HttpServletResponse.SC_NO_CONTENT;


	/**
	 * Status code (301) indicating that the resource has permanently
	 * moved to a new location, and that future references should use a
	 * new URI with their requests.
	 */
	public static final int SC_MOVED_PERMANENTLY =
		HttpServletResponse.SC_MOVED_PERMANENTLY;


	/**
	 * Status code (302) indicating that the resource has temporarily
	 * moved to another location, but that future references should
	 * still use the original URI to access the resource.
	 */
	public static final int SC_MOVED_TEMPORARILY =
		HttpServletResponse.SC_MOVED_TEMPORARILY;


	/**
	 * Status code (304) indicating that a conditional GET operation
	 * found that the resource was available and not modified.
	 */
	public static final int SC_NOT_MODIFIED =
		HttpServletResponse.SC_NOT_MODIFIED;


	/**
	 * Status code (400) indicating the request sent by the client was
	 * syntactically incorrect.
	 */
	public static final int SC_BAD_REQUEST =
		HttpServletResponse.SC_BAD_REQUEST;


	/**
	 * Status code (401) indicating that the request requires HTTP
	 * authentication.
	 */
	public static final int SC_UNAUTHORIZED =
		HttpServletResponse.SC_UNAUTHORIZED;


	/**
	 * Status code (403) indicating the server understood the request
	 * but refused to fulfill it.
	 */
	public static final int SC_FORBIDDEN = HttpServletResponse.SC_FORBIDDEN;


	/**
	 * Status code (404) indicating that the requested resource is not
	 * available.
	 */
	public static final int SC_NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;


	/**
	 * Status code (500) indicating an error inside the HTTP service
	 * which prevented it from fulfilling the request.
	 */
	public static final int SC_INTERNAL_SERVER_ERROR =
		HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


	/**
	 * Status code (501) indicating the HTTP service does not support
	 * the functionality needed to fulfill the request.
	 */
	public static final int SC_NOT_IMPLEMENTED =
		HttpServletResponse.SC_NOT_IMPLEMENTED;


	/**
	 * Status code (502) indicating that the HTTP server received an
	 * invalid response from a server it consulted when acting as a
	 * proxy or gateway.
	 */
	public static final int SC_BAD_GATEWAY =
		HttpServletResponse.SC_BAD_GATEWAY;


	/**
	 * Status code (503) indicating that the HTTP service is
	 * temporarily overloaded, and unable to handle the request.
	 */
	public static final int SC_SERVICE_UNAVAILABLE =
		HttpServletResponse.SC_SERVICE_UNAVAILABLE;


	/**
	 * Status code (100) indicating the client may continue with
	 * its request.  This interim response is used to inform the
	 * client that the initial part of the request has been
	 * received and has not yet been rejected by the server.
	 */
	public static final int SC_CONTINUE = 100;


	/**
	 * Status code (405) indicating the method specified is not
	 * allowed for the resource.
	 */
	public static final int SC_METHOD_NOT_ALLOWED = 405;


	/**
	 * Status code (409) indicating that the request could not be
	 * completed due to a conflict with the current state of the
	 * resource.
	 */
	public static final int SC_CONFLICT = 409;


	/**
	 * Status code (412) indicating the precondition given in one
	 * or more of the request-header fields evaluated to false
	 * when it was tested on the server.
	 */
	public static final int SC_PRECONDITION_FAILED = 412;


	/**
	 * Status code (413) indicating the server is refusing to
	 * process a request because the request entity is larger
	 * than the server is willing or able to process.
	 */
	public static final int SC_REQUEST_TOO_LONG = 413;


	/**
	 * Status code (415) indicating the server is refusing to service
	 * the request because the entity of the request is in a format
	 * not supported by the requested resource for the requested
	 * method.
	 */
	public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;


	// -------------------------------------------- Extended Sakaidav status code


	/**
	 * Status code (207) indicating that the response requires
	 * providing status for multiple independent operations.
	 */
	public static final int SC_MULTI_STATUS = 207;
	// This one colides with HTTP 1.1
	// "207 Parital Update OK"


	/**
	 * Status code (418) indicating the entity body submitted with
	 * the PATCH method was not understood by the resource.
	 */
	public static final int SC_UNPROCESSABLE_ENTITY = 418;
	// This one colides with HTTP 1.1
	// "418 Reauthentication Required"


	/**
	 * Status code (419) indicating that the resource does not have
	 * sufficient space to record the state of the resource after the
	 * execution of this method.
	 */
	public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
	// This one colides with HTTP 1.1
	// "419 Proxy Reauthentication Required"


	/**
	 * Status code (420) indicating the method was not executed on
	 * a particular resource within its scope because some part of
	 * the method's execution failed causing the entire method to be
	 * aborted.
	 */
	public static final int SC_METHOD_FAILURE = 420;


	/**
	 * Status code (423) indicating the destination resource of a
	 * method is locked, and either the request did not contain a
	 * valid Lock-Info header, or the Lock-Info header identifies
	 * a lock held by another principal.
	 */
	public static final int SC_LOCKED = 423;


	// ------------------------------------------------------------ Initializer


	static {
		// HTTP 1.0 tatus Code
		addStatusCodeMap(SC_OK, "OK");
		addStatusCodeMap(SC_CREATED, "Created");
		addStatusCodeMap(SC_ACCEPTED, "Accepted");
		addStatusCodeMap(SC_NO_CONTENT, "No Content");
		addStatusCodeMap(SC_MOVED_PERMANENTLY, "Moved Permanently");
		addStatusCodeMap(SC_MOVED_TEMPORARILY, "Moved Temporarily");
		addStatusCodeMap(SC_NOT_MODIFIED, "Not Modified");
		addStatusCodeMap(SC_BAD_REQUEST, "Bad Request");
		addStatusCodeMap(SC_UNAUTHORIZED, "Unauthorized");
		addStatusCodeMap(SC_FORBIDDEN, "Forbidden");
		addStatusCodeMap(SC_NOT_FOUND, "Not Found");
		addStatusCodeMap(SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
		addStatusCodeMap(SC_NOT_IMPLEMENTED, "Not Implemented");
		addStatusCodeMap(SC_BAD_GATEWAY, "Bad Gateway");
		addStatusCodeMap(SC_SERVICE_UNAVAILABLE, "Service Unavailable");
		addStatusCodeMap(SC_CONTINUE, "Continue");
		addStatusCodeMap(SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
		addStatusCodeMap(SC_CONFLICT, "Conflict");
		addStatusCodeMap(SC_PRECONDITION_FAILED, "Precondition Failed");
		addStatusCodeMap(SC_REQUEST_TOO_LONG, "Request Too Long");
		addStatusCodeMap(SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
		// dav Status Codes
		addStatusCodeMap(SC_MULTI_STATUS, "Multi-Status");
		addStatusCodeMap(SC_UNPROCESSABLE_ENTITY, "Unprocessable Entity");
		addStatusCodeMap(SC_INSUFFICIENT_SPACE_ON_RESOURCE,
			             "Insufficient Space On Resource");
		addStatusCodeMap(SC_METHOD_FAILURE, "Method Failure");
		addStatusCodeMap(SC_LOCKED, "Locked");
	}


	// --------------------------------------------------------- Public Methods


	/**
	 * Returns the HTTP status text for the HTTP or WebDav status code
	 * specified by looking it up in the static mapping.  This is a
	 * static function.
	 *
	 * @param   nHttpStatusCode [IN] HTTP or WebDAV status code
	 * @return  A string with a short descriptive phrase for the
	 *                  HTTP status code (e.g., "OK").
	 */
	public static String getStatusText(int nHttpStatusCode) {
		Integer intKey = new Integer(nHttpStatusCode);

		if (!mapStatusCodes.containsKey(intKey)) {
			return "";
		} else {
			return (String) mapStatusCodes.get(intKey);
		}
	}


	// -------------------------------------------------------- Private Methods


	/**
	 * Adds a new status code -> status text mapping.  This is a static
	 * method because the mapping is a static variable.
	 *
	 * @param   nKey    [IN] HTTP or WebDAV status code
	 * @param   strVal  [IN] HTTP status text
	 */
	private static void addStatusCodeMap(int nKey, String strVal) {
		mapStatusCodes.put(new Integer(nKey), strVal);
	}

};

