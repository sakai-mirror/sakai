/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/InputFileUploadRenderer.java,v 1.1 2005/05/12 16:13:50 janderse.umich.edu Exp $
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


package org.sakaiproject.jsf.renderer;

import java.io.File;
import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.sakaiproject.jsf.util.RendererUtil;

/**
 *
 * <p>Based on and modified from example code in  </p>
 * <p><i>Core JavaServer Faces</i>
 *  by Cay Horstmann, David Geary </p>
 * <p>Copyright:
 * Portions Copyright Sun Microsystems (c) 2005,
 * available for reuse.<br />
 * Copyright: Copyright  Sakai (c) 2005</p>
 * <p> </p>
 * @author Cay Horstmann,
 * @author David Geary
 * @author (modifications) Daisy Flemming
 * @author (modifications) Ed Smiley
 * @version $Id$
 */

public class InputFileUploadRenderer
  extends Renderer 
{
  private static final String UPLOAD = ".upload";

  public void encodeBegin(FacesContext context, UIComponent component)
    throws IOException 
  {
    if (!component.isRendered()) return;
    ResponseWriter writer = context.getResponseWriter();
    ExternalContext external = context.getExternalContext();
    HttpServletRequest request = (HttpServletRequest) external.getRequest();

    String clientId = component.getClientId(context);
    System.out.println("** encodeBegin, clientId ="+clientId);
    encodeUploadField(writer, clientId, component);
  }

  public void encodeUploadField(ResponseWriter writer, String clientId,
                                UIComponent component) throws IOException 
  {
    // write <input type=file> for browsing and upload
    writer.startElement("input", component);
    writer.writeAttribute("type","file","type");
    writer.writeAttribute("name",clientId + UPLOAD,"clientId");
    writer.endElement("input");
    writer.flush();
  }

  public void decode(FacesContext context, UIComponent component)
  {
    System.out.println("** decode =");

    ExternalContext external = context.getExternalContext();
    HttpServletRequest request = (HttpServletRequest) external.getRequest();
    String clientId = component.getClientId(context);
    FileItem item = (FileItem) request.getAttribute(clientId+UPLOAD);

    System.out.println("clientId ="+ clientId);
    System.out.println("fileItem ="+ item);


    Object target = RendererUtil.getAttribute(context, component, "target");

    if (target != null)
    {
      File dir = new File("/tmp/" + target.toString()); //directory where file would be saved
      if (!dir.exists())
        dir.mkdirs();
      String filename = item.getName();
      filename = filename.replace('\\','/'); // replace c:\filename to c:/filename
      filename = filename.substring(filename.lastIndexOf("/")+1);
      File file = new File(dir.getPath()+"/"+filename);
      System.out.println("**1. filename="+file.getPath());
      try 
      {
        item.write(file);
        // change value so we can evoke the listener
        ((EditableValueHolder) component).setSubmittedValue(file.getPath());
      }
      catch (Exception ex){
        throw new FacesException(ex);
      }
    }
  }
}