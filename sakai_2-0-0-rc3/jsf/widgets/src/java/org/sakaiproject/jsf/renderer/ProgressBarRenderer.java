/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/ProgressBarRenderer.java,v 1.2 2005/05/10 03:05:08 esmiley.stanford.edu Exp $
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

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.sakaiproject.jsf.util.ConfigurationResource;

/**
 * <p>Description:<br />
 * This class is the class that renders the <code>timerBar</code>
 * custom tag.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @author (JavaScript) Brian Gosselin of http://scriptasylum.com
 * @version $Id: ProgressBarRenderer.java,v 1.2 2005/05/10 03:05:08 esmiley.stanford.edu Exp $
 */

public class ProgressBarRenderer
    extends Renderer
{
  private static final String SCRIPT_PATH;
  private static final String RESOURCE_PATH;


  // we have static resources for our script path
  static {
    ConfigurationResource cr = new ConfigurationResource();
    SCRIPT_PATH = cr.get("timerBarScript");
    RESOURCE_PATH = cr.get("resources");
  }

  public boolean supportsComponentType(UIComponent component)
  {
    return (component instanceof UIOutput);
  }

  public void decode(FacesContext context, UIComponent component)
  {
  }

  public void encodeChildren(FacesContext context, UIComponent component)
    throws IOException
  {
    ;
  }

  /**
   * <p>Faces render output method .</p>
   *  @param context   <code>FacesContext</code> for the current request
   *  @param component <code>UIComponent</code> being rendered
   *
   * @throws IOException if an input/output error occurs
   */
    public void encodeEnd(FacesContext context, UIComponent component)
      throws IOException {

       if (!component.isRendered())
       {
         return;
        }

        ResponseWriter writer = context.getResponseWriter();

        String clientId = null;

        if (component.getId() != null &&
          !component.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
        {
          clientId = component.getClientId(context);
        }

        if (clientId != null)
        {
          writer.startElement("span", component);
          writer.writeAttribute("id", clientId, "id");
        }

        Map attrMap = component.getAttributes();

        writer.write("\n");
        writer.write("\n<script language=\"javascript\">");
        writer.write("\n// Timer Bar - Version 1.0");
        writer.write(
            "\n// Based on Script by Brian Gosselin of http://scriptasylum.com");
        writer.write(
            "\n  var loadedcolor='blue' ;            // PROGRESS BAR COLOR");
        writer.write(
            "\n  var unloadedcolor='white';         // COLOR OF UNLOADED AREA");
        writer.write(
            "\n  var bordercolor='white';            // COLOR OF THE BORDER");
        writer.write("\n  var barheight = 45; // HEIGHT OF PROGRESS BAR IN PIXELS");
        writer.write("\n  var barwidth = 300; // WIDTH OF THE BAR IN PIXELS");
        writer.write("\n  var waitTime = " + attrMap.get("wait") +
                     "; // NUMBER OF SECONDS FOR PROGRESSBAR");
        writer.write("\n  var loaded = 0; // TENTHS OF A SECOND ELAPSED");
        writer.write(
            "\n// THE FUNCTION BELOW CONTAINS THE ACTION(S) TAKEN ONCE BAR REACHES 100.");
        writer.write("\n");
        writer.write("\n  var action = function()");
        writer.write("\n {");
        writer.write("\n }");
        writer.write("\n</script>");
        writer.write("\n<script language=\"javascript\" src=\"" +
             "/" + RESOURCE_PATH + "/" + SCRIPT_PATH + "\"></script>");
        writer.write("\n");

        if (clientId != null) {
          writer.endElement("span");
        }
    }

}
