
/*
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
 */
package org.sakaiproject.tool.assessment.ui.servlet.delivery;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.data.dao.grading.MediaData;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: ShowMediaServlet.java,v 1.1 2005/01/14 19:16:04 daisyf.stanford.edu Exp $
 */

public class ShowMediaServlet extends HttpServlet
{

  public ShowMediaServlet()
  {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    doPost(req,res);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    String mediaId = req.getParameter("mediaId");

    System.out.println("**mediaId = "+mediaId);
    GradingService gradingService = new GradingService();
    MediaData mediaData = gradingService.getMedia(mediaId);
    byte[] media = mediaData.getMedia();
    System.out.println("** media length"+media.length);
    ServletOutputStream outputStream = res.getOutputStream();
    BufferedOutputStream buf_outputStream = new BufferedOutputStream(outputStream);
    for (int i=0; i<media.length;i++){
      buf_outputStream.write(media[i]);
      System.out.print(media[i]);
    }

    String displayType="attachment";
    res.setHeader("Content-Disposition", displayType+";filename=\""+mediaData.getFilename()+"\";");
    res.setContentLength(media.length);
    if (mediaData.getMimeType()!=null)
      res.setContentType(mediaData.getMimeType());
    else
      res.setContentType("application/octet-stream");
    res.flushBuffer();
    buf_outputStream.close();
  }
}
