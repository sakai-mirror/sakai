/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/portal/presence/src/java/org/sakaiproject/tool/portal/PresenceTool.java $
* $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
**********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Set;
import java.util.Iterator;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
/**
 * A servlet to illustrate use of the RepositoryManager interface.
 * @author Massachusetts Institute of Technology
 * @version $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 *
 */
public class OSIDRepositoryTool extends HttpServlet {

	/**
	 * Access the Servlet's information display.
	 *
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Portal1";
	}

	/**
	 * Initialize the servlet.
	 *
	 * @param config
	 *        The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		super.destroy();
	}

	private void testStart(PrintWriter out,String theString)
	{
		out.println("<tr><td>&nbsp;</td></tr>\r\n");
		out.println("<tr><td>Starting Test "+theString+"</td></tr>\r\n");
	}
	private void testStep(PrintWriter out,String theString)
	{
		out.println("<tr><td>"+theString+"</td></tr>\r\n");
	}
	private void testPass(PrintWriter out,String theString)
	{
		out.println("<tr><td><font color=green>Passed: "+theString+"</font></td></tr>\r\n");
	}
	private void testFail(PrintWriter out,String theString)
	{
		out.println("<tr><td><font color=red>Failed: "+theString+"</font></td></tr>\r\n");
	}

	/**
	 * Respond to navigation / access requests.
	 *
	 * @param req
	 *        The servlet request.
	 * @param res
	 *        The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 *
	 * Get an instance of the Sakai OSID RepositoryManager using the Component Manager.
	 * Given the manager, get a list of registered repositories.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// start the response
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		out.println("<html>");
		out.println("<body>");
		out.println("<table>");

		try {
			testStart(out,"IdManager");
			ComponentManager.getInstance();
			Object o = ComponentManager.get("org.osid.id.IdManager");
			if (o != null) {
				org.osid.id.IdManager idManager = (org.osid.id.IdManager)o;
				org.osid.shared.Id theId = idManager.createId();
				testStep(out,"ID = "+theId.getIdString());
				testPass(out,"IdManager");
			} else {
				testFail(out,"ComponentManager could not find IdManager");
			}
		} catch(Throwable t) {
			testFail(out,"IdManager Exception "+t.toString());
			// TODO: throw(t);
		}

		try {
			testStart(out,"LoggingManager");
			ComponentManager.getInstance();
			Object o = ComponentManager.get("org.osid.logging.LoggingManager");
			if (o != null) {
				org.osid.logging.LoggingManager loggingManager = (org.osid.logging.LoggingManager)o;
				org.osid.shared.StringIterator iter = loggingManager.getLogNamesForWriting();
				testStep(out,"Writable Logs "+iter);
    				org.osid.logging.WritableLog log = loggingManager.getLogForWriting("info");
				log.appendLog("This message should go to info log");
				log = loggingManager.getLogForWriting("trace");
				log.appendLog("This message should go to trace log");
				log = loggingManager.getLogForWriting("fred");
				log.appendLog("This message should go to info log because if the log is unknown, info is assumed");
				testPass(out,"LoggingManager");
			} else {
				testFail(out,"ComponentManager could not find LoggingManager");
			}
		} catch(Throwable t) {
			testFail(out,"LoggingManager Exception "+t.toString());
			// TODO: throw(t);
		}


		
		try {
			testStart(out,"RepositoryManager");
			ComponentManager.getInstance();		
			Object o = ComponentManager.get("org.osid.repository.RepositoryManager");
			if (o != null) {
				// have a place to store Repositories for later use in a search
				java.util.Vector repositoryVector = new java.util.Vector();
				
				// initialize the repository manager and get its repositories
				org.osid.repository.RepositoryManager repositoryManager = (org.osid.repository.RepositoryManager)o;
				repositoryManager.assignOsidContext(new org.osid.OsidContext());
				repositoryManager.assignConfiguration(new java.util.Properties());
				org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
				out.println("<tr><td>List of OSID Repositories</td></tr>");
				while (repositoryIterator.hasNextRepository()) {
					// display each repository name in a line and store away the repository for a search
					org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
					out.println("<tr><td>" + nextRepository.getDisplayName() + "</td></tr>");
					repositoryVector.addElement(nextRepository);
				}
				
				// perform a search if a criteria was passed
				String criteria = req.getParameter("criteria");
				org.osid.shared.Type thumbnailType = new Type("mit.edu","partStructure","thumbnail");
				org.osid.shared.Type urlType = new Type("mit.edu","partStructure","URL");
				
				if (criteria != null) {
					// convert a vector of repositories to an array which the method call requires
					int size = repositoryVector.size();
					org.osid.repository.Repository repositories[] = new org.osid.repository.Repository[size];
					for (int i = 0; i < size; i++) {
						repositories[i] = (org.osid.repository.Repository)repositoryVector.elementAt(i);
					}
					
					// perform the search
					out.println("<tr><td>Results of searching all repositories for " + criteria + "</td></tr>");
					org.osid.repository.AssetIterator assetIterator = repositoryManager.getAssetsBySearch(repositories,
																										  criteria,
																										  new Type("mit.edu","search","keyword"),
																										  null);
					while (assetIterator.hasNextAsset()) {
						// show the name of the asset that was found
						org.osid.repository.Asset asset = assetIterator.nextAsset();
						out.println("<tr><td>Asset is " + asset.getDisplayName() + "</td></tr>");
						
						// look for a thumbnail or url part and show any
						org.osid.repository.RecordIterator recordIterator = asset.getRecords();
						while (recordIterator.hasNextRecord()) {
							org.osid.repository.PartIterator partIterator = recordIterator.nextRecord().getParts();
							while (partIterator.hasNextPart()) {
								org.osid.repository.Part part = partIterator.nextPart();
								org.osid.shared.Type partStructureType = part.getPartStructure().getType();
								if (partStructureType.isEqual(thumbnailType)) {
									out.println("<tr><td>Thumbnail is " + part.getValue() + "</td></tr>");
								} else if (partStructureType.isEqual(urlType)) {
									out.println("<tr><td>URL is " + part.getValue() + "</td></tr>");
								}
							}
						}
					}
				}
				
			}		
		}
		catch (Throwable t) {
			t.printStackTrace();
		}

		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
	}


	/**
	 * Respond to data posting requests.
	 *
	 * @param req
	 *        The servlet request.
	 * @param res
	 *        The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// start the response
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		out.println("<h1>Test</h1>");
	}

}
