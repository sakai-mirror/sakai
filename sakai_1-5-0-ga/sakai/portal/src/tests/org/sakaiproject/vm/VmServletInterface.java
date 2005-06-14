/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.vm;

import javax.servlet.http.HttpServletRequest;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface VmServletInterface {
    /**
     * Access the object set in the velocity context for this name, if any.
     * @param name The reference name.
     * @param request The request.
     * @return The reference value object, or null if none
     */
    public abstract Object getVmReference(String name,
            HttpServletRequest request);

    /**
     * Add a reference object to the velocity context by name - if it's not already defined
     * @param name The reference name.
     * @param value The reference value object.
     * @param request The request.
     */
    public abstract void setVmReference(String name, Object value,
            HttpServletRequest request);
}