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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

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

}
