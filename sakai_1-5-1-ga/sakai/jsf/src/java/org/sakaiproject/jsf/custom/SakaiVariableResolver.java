/**********************************************************************************
 *
 * $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiVariableResolver.java,v 1.14 2004/12/03 23:45:27 janderse.umich.edu Exp $
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


/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.sakaiproject.jsf.custom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;

import com.sun.faces.RIConstants;
//import com.sun.faces.application.ApplicationAssociate;


/**
 * <p>Concrete implementation of <code>VariableResolver</code>.</p>
 */

public class SakaiVariableResolver extends VariableResolver {
    
    private static final Log log = LogFactory.getLog(
            SakaiVariableResolver.class);
    
    //
    // Relationship Instance Variables
    // 
    
    // Specified by javax.faces.el.VariableResolver.resolveVariable()
    public Object resolveVariable(FacesContext context, String name)
    throws EvaluationException {
        
        ExternalContext ec = context.getExternalContext();
        
        if (RIConstants.APPLICATION_SCOPE.equals(name)) {
            return (ec.getApplicationMap());
        } else if (RIConstants.COOKIE_IMPLICIT_OBJ.equals(name)) {
            return (ec.getRequestCookieMap());
        } else if (RIConstants.FACES_CONTEXT_IMPLICIT_OBJ.equals(name)){
            return (context);
        } else if (RIConstants.HEADER_IMPLICIT_OBJ.equals(name)) {
            return (ec.getRequestHeaderMap());
        } else if (RIConstants.HEADER_VALUES_IMPLICIT_OBJ.equals(name)){
            return (ec.getRequestHeaderValuesMap());
        } else if (RIConstants.INIT_PARAM_IMPLICIT_OBJ.equals(name)) {
            return (ec.getInitParameterMap());
        } else if (RIConstants.PARAM_IMPLICIT_OBJ.equals(name)) {
            return (ec.getRequestParameterMap());
        } else if (RIConstants.PARAM_VALUES_IMPLICIT_OBJ.equals(name)) {
            return (ec.getRequestParameterValuesMap());
        } else if (RIConstants.REQUEST_SCOPE.equals(name)) {
            return (ec.getRequestMap());
        } else if (RIConstants.SESSION_SCOPE.equals(name)) {
            return (ec.getSessionMap());
        } else if (RIConstants.VIEW_IMPLICIT_OBJ.equals(name)) {
            return (context.getViewRoot());
        } else if ("toolConfig".equals(name)) {
            
            HttpServletRequest req = (HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName());
            if (req != null)
            {
                String pid = req.getParameter("pid");
                if (pid != null)
                {
                    ToolConfiguration tool = SiteService.findTool(pid);
                    return (tool);
                }
            }
            return null;
        } else if ("toolScope".equals(name)) {
            return (PortalService.getCurrentToolState());
        } else {
            // do the scoped lookup thing
            Object value = null;
            
            SessionState toolstate = null;
            
            if (null == (value = ec.getRequestMap().get(name))) {
                // check the tool scope (pid based) next
                toolstate = PortalService.getCurrentToolState();
                if ((null == toolstate) || (null == (value = toolstate.getAttribute(name)))) {
                    if (null == (value = ec.getSessionMap().get(name))) {
                        if (null == (value = ec.getApplicationMap().get(name))) {
                            // if it's a managed bean try and create it
                            // We call the method ApplicationAssociate.createAndMaybeStoreManagedBeans
                            // INDIRECTLY, since we get ClassCastException when calling directly.
                            Object associate = context.getExternalContext().getApplicationMap().get("com.sun.faces.ApplicationAssociate");
                            Class[] methParamTypes =  new Class[] { FacesContext.class, String.class };
                            Object[] methParamValues = new Object[] {context, name};
                            try
                            {
                                Method meth = associate.getClass().getMethod("createAndMaybeStoreManagedBeans", methParamTypes);
                                
                                // calls associate.createAndMaybeStoreManagedBeans(context, name) by reflection
                                value = meth.invoke(associate, methParamValues);
                                
                                if (toolstate != null && isToolScopedBean(context, name, value))
                                {
                                    // the bean is marked as a tool-scoped bean, now make sure 
                                    // that it was registered as a request-scoped bean.  If it 
                                    // is in some other scope other than request, that is an error.
                                    Object theBean = ec.getRequestMap().get(name);
                                    if (theBean == null || theBean != value)
                                    {
                                        throw new EvaluationException("The managed bean named '"+name+"' " +
                                                "is marked as a tool bean by the ToolBean marker interface.\n" +
                                                "It should also be registered in faces-config.xml " +
                                                "in request scope.\nPlease change faces-config.xml " +
                                                "so that the bean is registered in request scope.");
                                    }
                                    
                                    // move the tool-scoped bean to the tool scope (out of request scope).
                                    ec.getRequestMap().remove(name);
                                    
                                    // save the tool-scoped bean in the tool state (for next time)
                                    // since JSF isn't aware of our custom tool-scope, so we need to save 
                                    // tool-scoped beans here
                                    toolstate.setAttribute(name, value);
                                    
                                }   
                            }
                            catch (InvocationTargetException e)
                            {
                                // convert the exception to an EvaluationException
                                // e.printStackTrace();
                                //System.out.println("CAUSED BY: ");
                                //e.getCause().printStackTrace();
                                throw (e.getCause() instanceof EvaluationException) ? (EvaluationException) e.getCause() : new EvaluationException(e.getCause());
                            }
                            
                            catch (Exception e)
                            {
                                // convert the exception to an EvaluationException
                                //e.printStackTrace();
                                throw (e instanceof EvaluationException) ? (EvaluationException) e : new EvaluationException(e);
                            }
                        }
                    }
                }
            }
            
            
            
            if (log.isDebugEnabled()) {
                log.debug("resolveVariable: Resolved variable:" + value);
            }
            return (value);
        }
    }
    
    /**
     * @param context
     * @param name
     * @param value
     */
    private boolean isToolScopedBean(FacesContext context, String name, Object value)
    {
        // TODO: How SHOULD we indicate that the bean should be in the implicit "tool" scope???
        return value instanceof org.sakaiproject.jsf.ToolBean;
    }
    
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiVariableResolver.java,v 1.14 2004/12/03 23:45:27 janderse.umich.edu Exp $
 *
 **********************************************************************************/
