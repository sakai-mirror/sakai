package org.sakaiproject.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

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
        return "SakaiRichTextEditArea";
    }
    
    public String getRendererType()
    {
        return "SakaiRichTextEditArea";
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
        setString(component, "value", value);
        setString(component, "width", width);
        setString(component, "height", height);
        setString(component, "toolbarButtonRows", toolbarButtonRows);
        setString(component, "javascriptLibrary", javascriptLibrary);
        setString(component, "autoConfig", autoConfig);
        setString(component, "columns", columns);
        setString(component, "rows", rows);
        setString(component, "justArea", justArea);
    }
    
    public void release()
    {
        super.release();
    }
    
    public static void setString(UIComponent component, String attributeName,
            String attributeValue)
    {
        if (attributeValue == null) return;
        if (UIComponentTag.isValueReference(attributeValue)) setValueBinding(
                component, attributeName, attributeValue);
        else
            component.getAttributes().put(attributeName, attributeValue);
    }
    
    public static void setValueBinding(UIComponent component,
            String attributeName, String attributeValue)
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        ValueBinding vb = app.createValueBinding(attributeValue);
        component.setValueBinding(attributeName, vb);
    }
}