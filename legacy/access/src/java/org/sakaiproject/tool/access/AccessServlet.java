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
package org.sakaiproject.tool.access;

// imports
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageHeader;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.assignment.cover.AssignmentService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.PrintFileGenerator;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.web.Web;

/**
 * 
 * <p>AccessServlet is ...</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision$
 */
public class AccessServlet
	extends VmServlet
{
	
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("access");
	
	/** stream content requests if true, read all into memory and send if false. */
	protected static final boolean STREAM_CONTENT = true;

	/** The chunk size used when streaming (100k). */
	protected static final int STREAM_BUFFER_SIZE = 102400;
	
	/** delimiter for form multiple values */
	protected static final String FORM_VALUE_DELIMETER = "^";

	/** used to id a log message */
	public static String ME = "Access:"; //rb.getString("java.access");

	/** set to true when init'ed. */
	protected boolean m_ready = false;
	
	/** copyright path -- MUST have same value as ResourcesAction.COPYRIGHT_PATH */
	public static final String COPYRIGHT_PATH = Entity.SEPARATOR + "copyright";
	
	private static final String MIME_TYPE_DOCUMENT_PLAINTEXT= "text/plain";

	/** init thread - so we don't wait in the actual init() call */
	public class AccessServletInit
		extends Thread
	{
		/**
		* construct and start the init activity
		*/
		public AccessServletInit()
		{
			m_ready = false;
			start();

		}   // AccessServletInit

		/**
		* run the init
		*/
		public void run()
		{
			m_ready = true;

		}   // run

	}   // class AccessServletInit

	/**
	* initialize the AccessServlet servlet
	*
	* @param config the servlet config parameter
	* @exception ServletException in case of difficulties
	*/
	public void init(ServletConfig config)
		throws ServletException
	{
		super.init(config);
		startInit();

	}   // init

	/**
	* Start the initialization process
	*/
	public void startInit()
	{
		new AccessServletInit();

	}   // startInit

	/**
	* respond to an HTTP GET request
	*
	* @param req HttpServletRequest object with the client request
	* @param res HttpServletResponse object back to the client
	* @exception ServletException in case of difficulties
	* @exception IOException in case of difficulties
	*/
	public void doGet(  HttpServletRequest req,
						HttpServletResponse res)
		throws ServletException, IOException
	{
		// catch the login helper requests
		String option = req.getPathInfo();
		String[] parts = option.split("/");
		if ((parts.length == 2) && ((parts[1].equals("login"))))
		{
			doLogin(req, res, null);
		}

		else
		{
			dispatch(req, res);
		}

	}   // doGet

	/**
	* respond to an HTTP POST request
	*
	* @param req HttpServletRequest object with the client request
	* @param res HttpServletResponse object back to the client
	* @exception ServletException in case of difficulties
	* @exception IOException in case of difficulties
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		// catch the login helper posts
		String option = req.getPathInfo();
		String[] parts = option.split("/");
		if ((parts.length == 2) && ((parts[1].equals("login"))))
		{
			doLogin(req, res, null);
		}

		else
		{
			dispatch(req, res);
		}

	}   // doPost

	/**
	* handle get and post communication from the user
	* @param req HttpServletRequest object with the client request
	* @param res HttpServletResponse object back to the client
	*/
	public void dispatch(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		ParameterParser params = (ParameterParser) req.getAttribute(ATTR_PARAMS);

		// get the path info
		String path = params.getPath();
		if (path == null) path = "";
		
		boolean copyrightAlert = false;

		if (!m_ready)
		{
			respondNotReady(req, res);
			return;
		}

		if(COPYRIGHT_PATH.equals(path))
		{
			respondCopyrightAlertDemo(req, res);
			return;
		}
		
		// get the incoming information
		AccessServletInfo info = newInfo(req);

		RunData data = null;
		try
		{
			// what is being requested?
			Reference ref = EntityManager.newReference(path);

			// reject anything we don't want to handle
			boolean willHandle = false;
			boolean permitted = false;
			if (ContentHostingService.SERVICE_NAME.equals(ref.getType()))
			{
				// we will reject collections, which end with a SEPARATOR
				if (!ref.getId().endsWith(Entity.SEPARATOR))
				{
					willHandle = true;
					permitted = ContentHostingService.allowGetResource(ref.getId());
				}
			}
			else if (AssignmentService.SERVICE_NAME.equals(ref.getType()))
			{
				if (Log.getLogger("chef").isDebugEnabled())
						Log.debug("chef", this + ":dispatch type=assignment service ref subtype=" + ref.getSubType());
				// we serve only subtypes "submissions" and "grades"
				if (	(AssignmentService.REF_TYPE_SUBMISSIONS.equals(ref.getSubType()))
					||	(AssignmentService.REF_TYPE_GRADES.equals(ref.getSubType())))
				{
					willHandle = true;
					permitted = AssignmentService.allowUpdateAssignment(ref.getReference());
				}
			}
			else if (SiteService.SERVICE_NAME.equals(ref.getType()))
			{
				willHandle = true;
				permitted = true;
			}

			else if (AnnouncementService.SERVICE_NAME.equals(ref.getType()))
			{
				// we handle messages
				if (MessageService.REF_TYPE_MESSAGE.equals(ref.getSubType()))
				{
					willHandle = true;
					permitted = (ref.getEntity() != null);
				}
			}

 			else if (PrintFileGenerator.PRINT_REQUEST_NAME.equals(path))
			{
				willHandle = true;
				permitted = true;
			}

			if (!willHandle)
			{
				respondInvalid(req, res, ref.getReference());
				return;
			}

			// if not permitted, and the user is the anon user, let them login
			if (!permitted && (SessionManager.getCurrentSessionUserId() == null))
			{
				doLogin(req, res, path);
				return;
			}

			// make the response
			try
			{
				// content
				if (ContentHostingService.SERVICE_NAME.equals(ref.getType()))
				{
					// we will reject collections, which end with a SEPARATOR
					if (!ref.getId().endsWith(Entity.SEPARATOR))
					{
						doContent(ref.getId(), copyrightAlert, req, res);
					}
				}

				// assignment
				else if (AssignmentService.SERVICE_NAME.equals(ref.getType()))
				{
					if (AssignmentService.REF_TYPE_SUBMISSIONS.equals(ref.getSubType()))
					{
						doAssignmentSubmissions(ref, res);
					}

					else if (AssignmentService.REF_TYPE_GRADES.equals(ref.getSubType()))
					{
						doAssignmentGrades(ref, res);
					}
				}

				// site (the description, please)
				else if (SiteService.SERVICE_NAME.equals(ref.getType()))
				{
					doSite(ref, res);
				}

				// announcement
				else if (AnnouncementService.SERVICE_NAME.equals(ref.getType()))
				{
					doAnnouncement(ref, req, res);
				}

 				// Printing (PDF Generation)
				else if (PrintFileGenerator.PRINT_REQUEST_NAME.equals(path))
				{
					doPrintingRequest(info, req, res);
				}
			}

			catch (IdUnusedException e)
			{
				respondError(req, res, ref.getReference(), rb.getString("java.theresource"));
			}

			catch (PermissionException e)
			{
				respondError(req, res, ref.getReference(), rb.getString("java.youdonot"));
			}

			catch (NoBodyException e)
			{
				respondError(req, res, ref.getReference(), rb.getString("java.miss"));
			}

			catch (ServerOverloadException e)
			{
				Logger.info(this + ".dispatch(): ref: " + ref.getReference() + e);
				respondError(req, res, ref.getReference(), rb.getString("java.busy"));
			}

			catch (java.net.SocketException e) {}

			catch (Exception e)
			{
				Log.warn("chef", this + ".dispatch(): ref: " + ref.getReference(), e);
				respondError(req, res, ref.getReference(), e.toString());
			}
		}
		catch (Exception e)
		{
			Log.warn("chef", this + ".dispatch(): exception: ", e);
			respondError(req, res, null, e.toString());
		}
		finally
		{
			// log
			log(req, params, info);
		}

	}   // dispatch

	/** log a request processed */
	protected void log(HttpServletRequest req, ParameterParser params, AccessServletInfo info)
	{
		if (Log.getLogger("chef").isDebugEnabled())
			Log.debug("chef", ME
						+ " from:" + req.getRemoteAddr()
						+ " path:" + params.getPath()
						+ " options: " +  info.optionsString()
						+ " time: " + info.getElapsedTime()
						);

	}   // log

	/**
	* Make the "not ready" response.
	* @param req HttpServletRequest object with the client request.
	* @param res HttpServletResponse object back to the client.
	*/
	protected void respondNotReady(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		includeVm("/vm/access/not_ready.vm", req, res);

	}	// respondNotReady
	
	/**
	* Make the Sample Copyright Alert response.
	* @param req HttpServletRequest object with the client request.
	* @param res HttpServletResponse object back to the client.
	*/
	protected void respondCopyrightAlertDemo(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		//the context wraps our real vm attribute set
		setVmReference("validator", new Validator(), req);
		setVmReference("sample", Boolean.TRUE.toString(), req);
		setVmReference("tlang",rb,req);
		includeVm("/vm/access/copyrightAlert.vm", req, res);

	}	// respondCopyrightAlertDemo

	/**
	* Make the "invalid" response for things we don't handle.
	* @param req HttpServletRequest object with the client request.
	* @param res HttpServletResponse object back to the client.
	* @param ref The reference requested
	*/
	protected void respondInvalid(HttpServletRequest req, HttpServletResponse res, String ref) throws ServletException
	{
		setVmReference("ref", ref, req);
		setVmReference("tlang",rb,req);
		includeVm("/vm/access/invalid.vm", req, res);

	}	// respondInvalid

	/**
	* Make the "error" response for problems processing the request.
	* @param req HttpServletRequest object with the client request.
	* @param res HttpServletResponse object back to the client.
	* @param ref The request reference string.
	* @param msg The error message.
	*/
	protected void respondError(HttpServletRequest req, HttpServletResponse res, String ref, String msg) throws ServletException
	{
		setVmReference("msg", msg, req);
		setVmReference("ref", ref, req);
		setVmReference("tlang",rb,req);
		includeVm("/vm/access/error.vm", req, res);

	}	// respondError

	/**
	* Make a redirect to the login url.
	* @param req HttpServletRequest object with the client request.
	* @param res HttpServletResponse object back to the client.
	* @param path The current request path
	*/
	protected void doLogin(HttpServletRequest req, HttpServletResponse res, String path)
	{
		// get the Sakai session
		Session session = SessionManager.getCurrentSession();

		// setup for the helper if needed (Note: in session, not tool session, special for Login helper)
		if (session.getAttribute(Tool.HELPER_DONE_URL) == null)
		{
			// where to go after
			session.setAttribute(Tool.HELPER_DONE_URL, Web.returnUrl(req, path));
		}

		// map the request to the helper, leaving the path after ".../options" for the helper
		ActiveTool tool = ActiveToolManager.getActiveTool("sakai.login");
		String context = req.getContextPath() + req.getServletPath() + "/login";
		tool.help(req, res, context, "/login");

	}	// respondRedirectToLogin

	/**
	* Handle requests for content, collection or resource
	* @param id The local resource id.
	* @param res The http servlet response object.
	*/
	protected void doContent(String id, boolean copyrightAlert, HttpServletRequest req, HttpServletResponse res)
		throws Exception
	{
		boolean accessedFromEmail=false;
		if (id.endsWith("&copyrightAlert"))
		{
			copyrightAlert = true;
			id = id.substring(0, id.indexOf("&copyrightAlert"));
		}
		if (id.endsWith("&copyrightAlertEmail"))
		{
			copyrightAlert = true;
			id = id.substring(0, id.indexOf("&copyrightAlertEmail"));
			accessedFromEmail = true;
		}
		
		// handle resources only
		ContentResource resource = ContentHostingService.getResource(id);
		
		ResourceProperties properties = resource.getProperties();

		if (copyrightAlert)
		{
			//the context wraps our real vm attribute set
			setVmReference("validator", new Validator(), req);
			setVmReference("resource", resource, req);
			setVmReference("resourceUrl", ContentHostingService.getUrl(resource.getId()), req);
			setVmReference("tlang",rb,req);
			if (accessedFromEmail)
			{
				setVmReference("accessedFromEmail", Boolean.TRUE, req);
			}
			includeVm("/vm/access/copyrightAlert.vm", req, res);
		}
		else
		{
			// changed to int from long because res.setContentLength won't take long param -- JE
			int len = resource.getContentLength();
			String contentType = resource.getContentType();
	
			// for url content type, encode a redirect to the body URL
			if (contentType.equalsIgnoreCase(ResourceProperties.TYPE_URL))
			{		
				byte[] content = resource.getContent();
				if ((content == null) || (content.length == 0))
				{
					throw new NoBodyException();
				}

				String one = new String(content);
				String two = "";
				for (int i=0; i < one.length(); i++)
				{
					if (one.charAt(i) == '+')
					{
						two += "%2b";
					}
					else
					{
						two += one.charAt(i);
					}
				}
				res.sendRedirect(two);
			}
	
			else
			{
				// use the last part, the file name part of the id, for the download file name
				String fileName = Validator.getFileName(id);
				fileName = Validator.escapeResourceName(fileName);
	
				String disposition = null;
				if (Validator.letBrowserInline(contentType))
				{
					disposition = "inline; filename=\"" + fileName+"\"";
				}
				else
				{
					disposition = "attachment; filename=\"" + fileName +"\"";
				}
	
				// NOTE:  Only set the encoding on the content we have to.
				// Files uploaded by the user may have been created with different encodings, such as ISO-8859-1;
				// rather than (sometimes wrongly) saying its UTF-8, let the browser auto-detect the encoding.
				// If the content was created through the WYSIWYG editor, the encoding does need to be set (UTF-8).
				String encoding = resource.getProperties().getProperty(ResourceProperties.PROP_CONTENT_ENCODING);
				if (encoding != null && encoding.length() > 0)
				{
					contentType = contentType + "; charset="+encoding;
				}
				
				// stream the content using a small buffer to keep memory managed
				if (STREAM_CONTENT)
				{
					InputStream content = null;
					OutputStream out = null;
	
					try
					{
						content = resource.streamContent();
						if (content == null)
						{
							throw new IdUnusedException(id);
						}
	
						res.setContentType(contentType);
						res.addHeader("Content-Disposition", disposition);
						res.setContentLength(len);
	
						// set the buffer of the response to match what we are reading from the request
						if (len < STREAM_BUFFER_SIZE)
						{						
							res.setBufferSize(len);
						}
						else
						{
							res.setBufferSize(STREAM_BUFFER_SIZE);
						}
	
						out = res.getOutputStream();
	
						// chunk
						byte[] chunk = new byte[STREAM_BUFFER_SIZE];
						int lenRead;
						while ((lenRead = content.read(chunk)) != -1)
						{
							out.write(chunk, 0, lenRead);					
						}
					}
					catch (ServerOverloadException e)
					{
						throw e;
					}
					catch (Throwable ignore)
					{
					}
					finally
					{
						// be a good little program and close the stream - freeing up valuable system resources
						if (content != null)
						{
							content.close();
						}
	
						if (out != null)
						{
							try
							{
								out.close();
							}
							catch (Throwable ignore)
							{
							}
						}
					}
				}
	
				// read the entire content into memory and send it from there
				else
				{
					byte[] content = resource.getContent();
					if (content == null)
					{
						throw new IdUnusedException(id);
					}
	
					res.setContentType(contentType);
					res.addHeader("Content-Disposition", disposition);
					res.setContentLength(len);
	
					// Increase the buffer size for more speed. - don't - we don't want a 20 meg buffer size,right? -ggolden
					//res.setBufferSize(len);
	
					OutputStream out = null;
					try
					{
						out = res.getOutputStream();
						out.write(content);
						out.flush();
						out.close();
					}
					catch (Throwable ignore)
					{
					}
					finally
					{
						if (out != null)
						{
							try
							{
								out.close();
							}
							catch (Throwable ignore)
							{
							}
						}
					}
				}
			}
		}

		// track event
		EventTrackingService.post(EventTrackingService.newEvent(ContentHostingService.EVENT_RESOURCE_READ, resource.getReference(), false));

	}   // doContent

	/**
	* Handle requests for grades spreadsheet from assignment.
	* @param ref The reference.
	* @param res The http servlet response object.
	*/
	protected void doAssignmentGrades(Reference ref, HttpServletResponse res)
		throws Exception
	{
		// get the grades spreadsheet blob
		byte[] spreadsheet = AssignmentService.getGradesSpreadsheet(ref.getReference());
		
		if (spreadsheet != null)
		{
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-Disposition", "attachment; filename = export_grades_file.xls");
	
			OutputStream out = null;
			try
			{
				out = res.getOutputStream();
				out.write(spreadsheet);
				out.flush();
				out.close();
			}
			catch (Throwable ignore)
			{
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (Throwable ignore)
					{
					}
				}
			}
		}

	}   // doAssignmentGrades

	/**
	* Handle requests for submissions zip from assignment.
	* @param ref The reference.
	* @param res The http servlet response object.
	*/
	protected void doAssignmentSubmissions(Reference ref, HttpServletResponse res)
		throws Exception
	{
		// get the submissions zip blob
		byte[] zip = AssignmentService.getSubmissionsZip(ref.getReference());
		
		if (zip != null)
		{
			res.setContentType("application/zip");
			res.setHeader("Content-Disposition", "attachment; filename = bulk_download.zip");
	
			OutputStream out = null;
			try
			{
				out = res.getOutputStream();
				out.write(zip);
				out.flush();
				out.close();
			}
			catch (Throwable ignore)
			{
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (Throwable ignore)
					{
					}
				}
			}
		}

	}   // doAssignmentSubmissions

	/**
	* Handle requests for printing (or generating a format suitable for printing.
	* @param ref The reference.
	* @param res The http servlet response object.
	*/
	protected void doPrintingRequest(AccessServletInfo info, HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		try
		{
			// We need to write to a temporary stream for better speed, plus
			// so we can get a byte count.  Internet Explorer has problems
			// if we don't make the setContentLength() call.
			ByteArrayOutputStream outByteStream=new ByteArrayOutputStream();
			StringBuffer contentType = new StringBuffer();

			try
			{
				PrintFileGenerator.printSchedule(
				info.m_options,
				contentType,
				getServletContext(),
				outByteStream);
				
				// Set the mime type for a PDF
				res.addHeader("Content-Disposition", "inline; filename=\"schedule.pdf\"");
				res.setContentType(contentType.toString());
				res.setContentLength(outByteStream.size());

				if (outByteStream.size() > 0)
				{
					// Increase the buffer size for more speed.
					res.setBufferSize(outByteStream.size());
				}

				OutputStream out = null;
				try
				{
					out = res.getOutputStream();
					if (outByteStream.size() > 0)
					{
						outByteStream.writeTo(out);
					}
					out.flush();
					out.close();
				}
				catch (Throwable ignore)
				{
				}
				finally
				{
					if (out != null)
					{
						try
						{
							out.close();
						}
						catch (Throwable ignore)
						{
						}
					}
				}
			}
			catch (PermissionException e)
			{
				String msg = "You have no permissions to access the calendar(s).";

				// read the entire content into memory and send it from there
				byte[] content = msg.getBytes();

				res.setContentType(MIME_TYPE_DOCUMENT_PLAINTEXT);
				res.addHeader("Content-Disposition", "inline;");
				res.setContentLength(msg.length());

				OutputStream out = null;
				try
				{
					out = res.getOutputStream();
					out.write(content);
					out.flush();
					out.close();
				}
				catch (Throwable ignore)
				{
				}
				finally
				{
					if (out != null)
					{
						try
						{
							out.close();
						}
						catch (Throwable ignore)
						{
						}
					}
				}
			}
		}

		catch (NumberFormatException e)
		{
			Log.warn("chef", this + ".doPrintingRequest()", e);
			respondError(req, res, null, rb.getString("java.unable"));
			return;
		}
	}

	/**
	* Handle requests for site - give the description text.
	* @param ref The reference.
	* @param res The http servlet response object.
	*/
	protected void doSite(Reference ref, HttpServletResponse res)
		throws Exception
	{
		Site site = (Site) ref.getEntity();

		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		//out.println("<html><head></head><body>");
		out.println("<html><head><style type=" + "\"" + "text/css" + "\"" + ">body{margin:	0px;padding:1em;font-family:Verdana,Arial,Helvetica,sans-serif;font-size:80%;}</style></head><body>");
		// get the description - if missing, use the site title
		String description = site.getDescription();
		if (description == null)
		{
			description = site.getTitle();
		}

		// make it safe for html
		description = Validator.escapeHtml(description);

		out.println(description);

		out.println("</body></html>");

	}   // doSite

	/**
	* Handle requests for an announcement.
	* @param ref The reference.
	* @param res The http servlet response object.
	*/
	protected void doAnnouncement(Reference ref, HttpServletRequest req, HttpServletResponse res)
		throws Exception
	{
		AnnouncementMessage msg = (AnnouncementMessage) ref.getEntity();
		if (ref == null)
		{
			respondError(req, res, ref.getReference(), rb.getString("java.message"));
			return;
		}

		AnnouncementMessageHeader hdr = (AnnouncementMessageHeader) msg.getAnnouncementHeader();

		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();
		out.println("<html><head></head><body>");

		// header
		out.println("<p>"+rb.getString("java.from") +" " + Validator.escapeHtml(hdr.getFrom().getDisplayName()) + "<br />");
		out.println(rb.getString("java.date")+" " + Validator.escapeHtml(hdr.getDate().toStringLocalFull()) + "<br />");
		out.println(rb.getString("java.subject")+" " + Validator.escapeHtml(hdr.getSubject()) + "</p>");

		// body
		out.println("<p>" + Validator.escapeHtmlFormattedText(msg.getBody()) + "</p>");

		// attachments
		List attachments = hdr.getAttachments();
		if (attachments.size() > 0)
		{
			out.println("<p>"+rb.getString("java.attach")+"</p><p>");
			for (Iterator iAttachments = attachments.iterator(); iAttachments.hasNext(); )
			{
				Reference attachment = (Reference) iAttachments.next();
				out.println("<a href=\"" + Validator.escapeHtml(attachment.getUrl()) + "\">" + Validator.escapeHtml(attachment.getUrl()) + "</a><br />");
			}
			out.println("</p>");
		}

		out.println("</body></html>");

	}   // doAnnouncement

	/**
	* Send out the standard html start.
	* @param out The PrintWriter on the response.
	*/
	protected void startHtml(PrintWriter out)
	{
		String webappRoot = ServerConfigurationService.getServerUrl();

		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html><head>");
		out.println("<link href=\"" + webappRoot + "/css/default.css\" type=\"text/css\" rel=\"stylesheet\" media=\"screen\" />");
		out.println("</head><body>");
		//out.println("<hr class=\"PageNavSpacer\" />");
		out.println("<div style=\"padding: 16px\">");

	}   // startHtml

	/**
	* Send out the standard html end.
	* @param out The PrintWriter on the response.
	*/
	protected void endHtml(PrintWriter out)
	{
		String webappRoot = ServerConfigurationService.getServerUrl();

		/*
		out.println("</div><p>&nbsp;</p>");
		out.println("<div align=\"center\" class=\"SmallPrint\">");
		out.println("&nbsp;<br/>");
		out.println("|");
		String[] links = TurbineResources.getStringArray("bottomnav.link");
		for (int i = 0; i < links.length; i++)
		{
			out.println(links[i] + " |");
		}
		out.println("<br/>&nbsp;");
		out.println("</div>");
		out.println("<div align=\"center\" class=\"TinyPrint\">");
		out.println("<a href=\"http://chefproject.org\" target=\"_blank\"><img border=\"0\" src=\"" + webappRoot + "/images/logoC_powered.gif\"></a>");
		out.println("<br/>");
		out.println("&copy; The Regents of the University of Michigan. All rights reserved. Copyright 2002, 2003<br/>");
		out.println("CHEF v" + TurbineResources.getString("chef_version") + ", Jetspeed v" + TurbineResources.getString("jetspeed_version"));
		*/
		out.println("</div>");
		out.println("</body></html>");

	}   // endHtml

	/** create the info */
	public AccessServletInfo newInfo(HttpServletRequest req)
	{
		return new AccessServletInfo(req);

	}   // newInfo

	public class AccessServletInfo
	{
		// elapsed time start
		protected long m_startTime = System.currentTimeMillis();
		public long getStartTime() { return m_startTime; }
		public long getElapsedTime()
		{
			return System.currentTimeMillis() - m_startTime;
		}

		// all properties from the request
		protected Properties m_options = null;
		public Properties getOptions() { return m_options; }

		/** construct from the req */
		// TODO: is this used? what about the parameter parser? -ggolden
		public AccessServletInfo(HttpServletRequest req)
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

		}   // AccessServletInfo

		/** return the m_options as a string - obscure any "password" fields */
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
					if (key.equals("password"))
					{
						buf.append("*****");
					}
					else
					{
						buf.append(o.toString());
					}
					buf.append("&");
				}
			}

			return buf.toString();

		}   // optionsString

	}   // AccessServletInfo

	class NoBodyException extends Exception
	{
	}

}   // AccessServlet



