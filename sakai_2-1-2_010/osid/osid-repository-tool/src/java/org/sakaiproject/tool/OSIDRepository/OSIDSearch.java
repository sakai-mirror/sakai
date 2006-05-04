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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.tool.util.DomUtils;

/*
 * public class Search extends ComponentServlet {
 */
public class OSIDSearch extends HttpServlet {
  /**
   * Servlet context
   */
  protected ServletContext    _context;

	/**
   * Cleanup
   */
  public void destroy() {
    super.destroy();
  }

  /**
   * Initialize
   */
  /*
  public void init(ServletConfig servletConfig) throws ServletException {
		String resource;

    super.init(servletConfig);
    _context = servletConfig.getServletContext();

 try {
	    resource = "WEB-INF/" + getInitParameter("source-configuration-file");
//	    SearchSource.populate(_context.getResourceAsStream(resource));
	    resource = "WEB-INF/" + getInitParameter("security-configuration-file");
	    SecuritySetup.initialize(_context.getResourceAsStream(resource));

	  } catch (Exception exception) {
	  	throw new ServletException(exception.toString());
	  }
  }
   */

  /**
   * Service a GET request
   * @param request Standard HTTP request object
   * @param response Standard HTTP response
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
                                       throws ServletException, IOException {
    doPost(request, response);
  }

  /**
   * Service a POST request
   * @param request Standard HTTP request object
   * @param response Standard HTTP response
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
                                        throws ServletException, IOException {
    String  criteria = request.getParameter("criteria");

    System.out.println("in OSID servlet " + criteria);

    PrintWriter       writer;

    try {
        response.setContentType("text/html; charset=" + DomUtils.ENCODING);
        writer = response.getWriter();
        writer.println("<html>");
        writer.println("foo");
        writer.println("</html>");
      try { writer.flush(); } catch (Exception ignore) { }
      try { writer.close(); } catch (Exception ignore) { }
    }
    catch (Exception ignore) {}
    }
}

