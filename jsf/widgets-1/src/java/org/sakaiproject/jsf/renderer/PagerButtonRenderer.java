/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/PagerButtonRenderer.java,v 1.3 2005/05/10 03:01:28 esmiley.stanford.edu Exp $
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
import javax.faces.component.UIForm;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.render.Renderer;

/**
 * <p>Render a next/previous control for a pager attached to a dataTable.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id$
 */

public class PagerButtonRenderer extends Renderer
{
  // some IO stuff
  private final String numItemsValues[] = {"2", "5", "10", "20", "30", "50", "100" };
  private final String DISABLED_ATTRIB = " disabled=\"disabled\"";
  // this needs to be internationalized, tricky because of fragments
  private final String OF = "of";
  private final String VIEWING = "Viewing";
  private final String SHOW = "Show";
  private final String ITEMS = "items";
  private final String ITEMS_PER = "items per page";
  private final String NEXT_TEXT_DEFAULT = "Next";
  private final String PREV_TEXT_DEFAULT = "Previous";


  public boolean supportsComponentType(UIComponent component)
  {
    return (component instanceof UIOutput);
  }

  public void decode(FacesContext context, UIComponent component)
  {
      Map parameters = context.getExternalContext().getRequestParameterMap();
      String formId = calcFormId(context, component);

      String dataTableId = (String) component.getAttributes().get("dataTableId");

      String newNumItemsStr = (String) parameters.get(dataTableId+"_"+formId+"__pager_button_control_select");
      if (newNumItemsStr != null)
      {
          Integer newNumItems = new Integer(newNumItemsStr);
          int oldNumItems = getInt(component, "numItems", 0);
          if (newNumItems.intValue() != oldNumItems)
          {
              setAttributeValue(context, component, "numItems", newNumItems);
              calcIndexes(context, component, 0);
          }
      }

      Object prevButtonPushed = parameters.get(dataTableId+"_"+formId+"__pager_button_control_prev_btn");
      if (prevButtonPushed != null)
      {
          calcIndexes(context, component, -1);
      }

      Object nextButtonPushed = parameters.get(dataTableId+"_"+formId+"__pager_button_control_next_btn");
      if (nextButtonPushed != null)
      {
          calcIndexes(context, component, 1);
      }
  }

  /** Return the formId of the form that the pager controls are part of.
   * If the component formId attribute is present, use that - otherwise search
   * for a UIForm enclosing this component.
   * @param context
   * @param component
   */
  private String calcFormId(FacesContext context, UIComponent component)
  {
      String formId = (String) component.getAttributes().get("formId");
      if (formId != null) return formId;

      // search for an enclosing form
      while (component != null)
      {
          if (component instanceof UIForm)
          {
              formId = ((UIForm) component).getId();
              return formId;
          }
          component = component.getParent();
      }

      return null;
  }


 /**
 * Calculate new firstItem and lastItem using scrolling logic (and set them if they've changed).
 * @param direction The direction to scroll - may be 1 to scroll forward, -1 to scroll back, and 0 to not scroll.
 */
protected void calcIndexes(FacesContext context, UIComponent component, int direction)
{
    // scrolling logic
    // scroll the displayed items in the given direction.
    int firstItem = getInt(component, "firstItem", 0);
    int lastItem = getInt(component, "lastItem", 0);
    int numItems = getInt(component, "numItems", 0);
    int totalItems = getInt(component, "totalItems", 0);

     int newFirstItem = firstItem + direction * numItems;
    if (newFirstItem + numItems > totalItems)
    {
        // don't scroll past the beginning of the last page of items
        newFirstItem = totalItems - numItems + 1;
    }

    if (newFirstItem < 1)
    {
        // don't scroll before the 1st item
        newFirstItem = 1;
    }

    int newLastItem = newFirstItem + numItems - 1;

    if (newLastItem > totalItems)
    {
        // don't scroll off the end of the last item
        newLastItem = totalItems;
    }

    setAttributeValue(context, component, "prevDisabled", new Boolean(newFirstItem == 1));
    setAttributeValue(context, component, "nextDisabled", new Boolean(newLastItem == totalItems));

    // set the newly calculated attributes (if they've changed)
    if (newFirstItem != firstItem) setAttributeValue(context, component, "firstItem", new Integer(newFirstItem));
    if (newLastItem != lastItem) setAttributeValue(context, component, "lastItem", new Integer(newLastItem));
}

/** Get an integer attribute value from the given component.
 * Convert to int if the value is a String.
 */
private int getInt(UIComponent component, String name, int defaultValue)
{
    Object obj = component.getAttributes().get(name);
    if (obj == null) return defaultValue;

    if (obj instanceof Number) return ((Number)obj).intValue();
    if ("".equals(obj)) return defaultValue;
    if (obj instanceof String) return Integer.parseInt((String)obj);

    return defaultValue;
}

/** Get an boolean attribute value from the given component.
 * Convert to boolean if the value is a String.
 */
private boolean getBoolean(UIComponent component, String name, boolean defaultValue)
{
    Object obj = component.getAttributes().get(name);
    if (obj == null) return defaultValue;

    if (obj instanceof Boolean) return ((Boolean)obj).booleanValue();
    if ("".equals(obj)) return defaultValue;
    if (obj instanceof String) return Boolean.getBoolean((String)obj);

    return defaultValue;
}

/** Sets component attribute value - if a ValueBinding exists for that
 * attribute, set through the binding; otherwise, set the value directly on the component.
 * @param name
 * @param value
 */
private void setAttributeValue(FacesContext context, UIComponent component, String name, Object value)
{
    ValueBinding binding = component.getValueBinding(name);
    if (binding != null)
    {
        try
        {
            binding.setValue(context, value);
        }
        catch (IllegalArgumentException e)
        {
            // try setting the value as a String
            binding.setValue(context, String.valueOf(value));
        }
    }
    else
    {
        component.getAttributes().put(name, value);
    }
}
public void encodeChildren(FacesContext context,
    UIComponent component) throws IOException
  {
    ;
  }

  public void encodeBegin(FacesContext context,
    UIComponent component) throws IOException
  {
    ;
  }

  /**
   * <p>Faces render output method .</p>
   * <p>Method Generator: org.sakaiproject.tool.assessment.devtoolsRenderMaker</p>
   *
   *  @param context   <code>FacesContext</code> for the current request
   *  @param component <code>UIComponent</code> being rendered
   *
   * @throws IOException if an input/output error occurs
   */
  public void encodeEnd(FacesContext context,
    UIComponent component) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    String formId = calcFormId(context, component);
    int firstItem = getInt(component, "firstItem", 0);
    int lastItem = getInt(component, "lastItem", 0);
    int numItems = getInt(component, "numItems", 0);
    int totalItems = getInt(component, "totalItems", 0);

    String dataTableId = (String) component.getAttributes().get("dataTableId");
    String prevText = (String) component.getAttributes().get("prevText");
    String nextText = (String) component.getAttributes().get("nextText");

    boolean prevDisabled = getBoolean(component, "prevDisabled", false);
    boolean nextDisabled = getBoolean(component, "nextDisabled", false);

    String prevDisabledAttr = "";
    String nextDisabledAttr = "";
    if (prevDisabled)
    {
      prevDisabledAttr = DISABLED_ATTRIB;
    }
    if (nextDisabled)
    {
      nextDisabledAttr = DISABLED_ATTRIB;
    }

    if (totalItems == 0)
    {
        // don't render the buttons when there are no items
        return;
    }

    String nextButtonId = dataTableId + "_" + formId + "__pager_button_control_prev_btn";

    writer.write("  <span class=\"instruction\">"+ VIEWING +" " + firstItem +
      " - " +
      lastItem + " " + OF + " " + totalItems + " " +ITEMS +"</span>");
    writer.write("  <br />");
    writer.write("  <input type=\"submit\"");
    writer.write("    name=\"" + nextButtonId +"\"");
//    writer.write("    onclick=\""+ fillInValueAndSubmit(formId, nextButtonId, nextButtonId) +"\"");
    writer.write("    value=\"&lt; " + prevText + " " + numItems + "\"");
    writer.write("    " + prevDisabledAttr + "/>");

    String select = dataTableId + "_" + formId +  "__pager_button_control_select";

    writeSelectList(writer, numItems, select, formId);

    String prevButtonId = dataTableId + "_" + formId + "__pager_button_control_next_btn";

    writer.write("  <input type=\"submit\"");
    writer.write("    name=\"" + prevButtonId + "\"");
//    writer.write("    onclick=\"" + fillInValueAndSubmit(formId, prevButtonId, prevButtonId) + "\"");
    writer.write("    value=\"" + nextText + " " + numItems + " &gt;\"");
    writer.write("		" + nextDisabledAttr + "/>");
    writer.write("  <br />");

  }

  /**
  *
  * @param writer ResponseWriter
  * @param component UIComponent
  * @param formId String
  * @param id String
  * @param value String
  * @param styleClass String
  * @throws IOException

 private void writeLink(ResponseWriter writer, UIComponent component,
   String formId, String id, String value,
   String styleClass) throws IOException
 {
   writer.writeText(" ", null);
   writer.startElement("a", component);
   writer.writeAttribute("href", "#", null);
   writer.writeAttribute("onclick", onclickCode(formId, id, value), null);
   if (styleClass != null)
   {
     writer.writeAttribute("class", styleClass, "styleClass");
   }
   writer.writeText(value, null);
   writer.endElement("a");
 }
*/

 /**
  *
  * @param formId String
  * @param id String
  * @param value String
  * @return String
  */
 private String fillInValueAndSubmit(String formId, String id, String value)
 {
   StringBuffer buffer = new StringBuffer();
   buffer.append("javascript:document.forms[");
   buffer.append("'");
   buffer.append(formId);
   buffer.append("'");
   buffer.append("]['");
   buffer.append(id);
   buffer.append("'].value='");
   buffer.append(value);
   buffer.append("';");
   buffer.append(" document.forms[");
   buffer.append("'");
   buffer.append(formId);
   buffer.append("'");
   buffer.append("].submit()");
   buffer.append("; return false;");
   return buffer.toString();
 }

  /**
   *
   * @param writer for output
   * @param numItems number of items to show
   * @param selectId the name to give the HTML select control
   * @param formId the form to post onchange events to
   * @throws IOException
   */
  private void writeSelectList(ResponseWriter writer, int numItems,
    String selectId, String formId) throws IOException
  {
    writer.write("  <select ");
    writer.write("    onchange=\"javascript:document.forms['" + formId +
      "'].submit(); return false;\"");
    writer.write("    name=\"" + selectId + "\">");

    for (int i = 0; i < numItemsValues.length; i++) {
      String currentVal = numItemsValues[i];
      writer.write("    <option ");
      writer.write(" value=\"" + currentVal +"\"");
      if (currentVal.equals(Integer.toString(numItems)))
      {
        writer.write(" selected=\"selected\" ");
      }
      writer.write(">"+ SHOW +" " + currentVal + " " + ITEMS_PER + "</option>");
    }

    writer.write("  </select>");
  }

}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/PagerButtonRenderer.java,v 1.3 2005/05/10 03:01:28 esmiley.stanford.edu Exp $
*
**********************************************************************************/
