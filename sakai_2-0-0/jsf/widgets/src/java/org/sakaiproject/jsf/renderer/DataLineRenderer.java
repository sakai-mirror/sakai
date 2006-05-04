/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets/src/java/org/sakaiproject/jsf/renderer/DataLineRenderer.java,v 1.3 2005/05/10 03:05:08 esmiley.stanford.edu Exp $
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
import java.util.Iterator;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;

/**
 * <p>Description: </p>
 * <p>Render a iterated data like a dataTable but without the table.</p>
 * <p> Based on example code by from the O'Reilley JSF book. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: DataLineRenderer.java,v 1.3 2005/05/10 03:05:08 esmiley.stanford.edu Exp $
 */

public class DataLineRenderer extends Renderer
{

  /**
   * This component renders its children
   * @return true
   */
  public boolean getRendersChildren()
  {
    return true;
  }

  /**
   * This is an output type component.
   * @param component
   * @return true if UIOutput
   */
  public boolean supportsComponentType(UIComponent component)
  {
    return (component instanceof UIOutput);
  }

  /**
   * no-op
   * @param context
   * @param component
   * @throws IOException
   */
  public void encodeBegin(FacesContext context,
    UIComponent component) throws IOException
  {
    ;
  }

  /**
   * We put all our processing in the encodeChildren method
   * @param context
   * @param component
   * @throws IOException
   */
  public void encodeChildren(FacesContext context, UIComponent component)
    throws IOException
  {
    if (!component.isRendered())
    {
      return;
    }

    String clientId = null;

    if (component.getId() != null &&
      !component.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
    {
      clientId = component.getClientId(context);
    }

    ResponseWriter writer = context.getResponseWriter();

    if (clientId != null)
    {
      writer.startElement("span", component);
      writer.writeAttribute("id", clientId, "id");
    }

    UIData data = (UIData) component;

    int first = data.getFirst();
    int rows = data.getRows();

    // this is a special separator attribute, not supported by UIData
    String separator = (String) RendererUtil.getAttribute(context, component, "separator");
    if (separator==null) separator=" | ";

    for (int i = first, n = 0; n < rows; i++, n++)
    {
      data.setRowIndex(i);
      if (!data.isRowAvailable())
      {
        break;
      }

      // between any two iterations add separator if there is one
      if (i!=first) writer.write(separator);

      Iterator iter = data.getChildren().iterator();
      while (iter.hasNext())
      {
        UIComponent column = (UIComponent) iter.next();
        if (!(column instanceof UIColumn))
        {
          continue;
        }
        RendererUtil.encodeRecursive(context, column);
        }
      }
      if (clientId != null)
        {
        writer.endElement("span");
      }

    }

    /**
     * no-op
     * @param context
     * @param component
     * @throws IOException
     */
  public void encodeEnd(FacesContext context, UIComponent component)
    throws IOException
  {
    ;
  }

}
