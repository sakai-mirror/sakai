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

package org.sakaiproject.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.sakaiproject.jsf.util.JSFUtils;

public class RichTextEditArea extends UIComponentTag
{
    private String value;
    private String width;
    private String height;
    private String toolbarButtonRows;
    private String javascriptLibrary;
    private String autoConfig;
    private String columns;
    private String rows;
    private String justArea;

    public String getComponentType()
    {
        return "org.sakaiproject.RichTextEditArea";
    }

    public String getRendererType()
    {
        return "org.sakaiproject.RichTextEditArea";
    }

    // getters and setters for component properties

    public void setValue(String newValue) { value = newValue; }
    public String getValue() { return value; }
    public void setWidth(String newWidth) { width = newWidth; }
    public String getWidth() { return width; }
    public void setHeight(String newHeight) { height = newHeight; }
    public String getHeight() { return height; }
    public void setToolbarButtonRows(String str) { toolbarButtonRows = str; }
    public String getToolbarButtonRows() { return toolbarButtonRows; }
    public void setJavascriptLibrary(String str) { javascriptLibrary = str; }
    public String getJavascriptLibrary() { return javascriptLibrary; }
    public void setAutoConfig(String str) { autoConfig = str; }
    public String getAutoConfig() { return autoConfig; }
    public void setColumns(String newC) { columns = newC; }
    public String getColumns() { return columns; }
    public void setRows(String newRows) { rows = newRows; }
    public String getRows() { return rows; }
    public void setJustArea(String newJ) { justArea = newJ; }
    public String getJustArea() { return justArea; }

    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        JSFUtils.setString(component, "value", value);
        JSFUtils.setString(component, "width", width);
        JSFUtils.setString(component, "height", height);
        JSFUtils.setString(component, "toolbarButtonRows", toolbarButtonRows);
        JSFUtils.setString(component, "javascriptLibrary", javascriptLibrary);
        JSFUtils.setString(component, "autoConfig", autoConfig);
        JSFUtils.setString(component, "columns", columns);
        JSFUtils.setString(component, "rows", rows);
        JSFUtils.setString(component, "justArea", justArea);
    }

    public void release()
    {
        super.release();
        
        value = null;
        width = null;
        height = null;
        toolbarButtonRows = null;
        javascriptLibrary = null;
        autoConfig = null;
        columns = null;
        rows = null;
        justArea = null;      
    }
}

/**********************************************************************************
* $URL$
* $Id$
**********************************************************************************/
