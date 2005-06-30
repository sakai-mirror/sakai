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

package org.sakaiproject.jsf.util;

import java.io.Serializable;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 * <p>
 * Place for common static utility methods for JSF widgets.
 * </p>
 */

public class JSFUtils
{

    /** This class is meant for static use only */
    private JSFUtils()
    {
    }

    /**
     * Sets component attribute value - if a ValueBinding exists for that
     * attribute, set through the binding; otherwise, set the value directly on
     * the component.
     * 
     * @param name
     * @param value
     */
    public static void setAttribute(FacesContext context, UIComponent component,
            String name, Object value)
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

    /**
     * Return the attribute value - handles getting the value from a
     * ValueBinding if necessary. This is necessary because of a difference in
     * the Sun JSF RI versus the MyFaces RI. The Sun RI
     * component.getAttributes().get(attrName) will automatically return value
     * bindings, whereas the MyFaces implmentation requires getting values from
     * ValueBinding seperately.
     * 
     * @param context
     * @param component
     * @param name
     * @return
     */
    public static Object getAttribute(FacesContext context, UIComponent component,
            String name)
    {
        // first check the attributes
        Object ret = component.getAttributes().get(name);
        if (ret != null) return ret;

        // next check the value bindings
        ValueBinding vb = component.getValueBinding(name);
        if (vb != null) ret = vb.getValue(context);

        return ret;
    }

    /**
     * Same as getAttribute, but if not found, we return a default value.
     * 
     * @param context
     * @param component
     * @param name
     * @param defaultValue
     * @return
     */
    public static Object getDefaultedAttribute(FacesContext context,
            UIComponent component, String name, Object defaultValue)
    {
        Object o = getAttribute(context, component, name);
        if (o == null) o = defaultValue;
        return o;
    }

    /**
     * Set a string value on a component - used by tags setProperties() method.
     * Handles value bindings.
     */
    public static void setString(UIComponent component, String name, String value)
    {
        if (value == null)
        {
            return;
        }
        if (UIComponentTag.isValueReference(value))
        {
            setValueBinding(component, name, value);
        }
        else
        {
            component.getAttributes().put(name, value);
        }
    }

    /**
     * Set a value binding on a component - used by tags setProperties() method.
     */
    public static void setValueBinding(UIComponent component, String name, String value)
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        ValueBinding vb = app.createValueBinding(value);
        component.setValueBinding(name, vb);
    }

    /**
     * Set a method binding on a component
     * 
     * @param name
     *            The attribute name to store the MethodBinding in
     * @param value
     *            The string name of the method binding
     * @param paramTypes
     *            An array of parameter types for the method
     */
    public static void setMethodBinding(UIComponent component, String name, String value,
            Class[] paramTypes)
    {
        if (value == null)
        {
            return;
        }
        if (UIComponentTag.isValueReference(value))
        {
            FacesContext context = FacesContext.getCurrentInstance();
            Application app = context.getApplication();
            MethodBinding mb = app.createMethodBinding(value, paramTypes);
            component.getAttributes().put(name, mb);
        }
    }

    /**
     * Set the action handler for the component.
     */
   public static void setAction(UIComponent component, String value)
    {
        if (value == null)
        {
            return;
        }
        if (UIComponentTag.isValueReference(value))
        {
            setMethodBinding(component, "action", value, new Class[] {});
        }
        else
        {
            FacesContext context = FacesContext.getCurrentInstance();
            Application app = context.getApplication();
            MethodBinding mb = new ActionMethodBinding(value);
            component.getAttributes().put("action", mb);
        }
    }

   /**
    * A shortcut MethodBinding which just returns a single string result - useful
    * when an action should just return a certain result, not call a method.
    * 
    */
    private static class ActionMethodBinding extends MethodBinding implements
            Serializable
    {
        private String result;

        public ActionMethodBinding(String result)
        {
            this.result = result;
        }

        public Object invoke(FacesContext context, Object params[])
        {
            return result;
        }

        public String getExpressionString()
        {
            return result;
        }

        public Class getType(FacesContext context)
        {
            return String.class;
        }
    }

}
