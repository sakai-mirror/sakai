/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 

 */


package org.apache.wsrp4j.producer.provider.sakaiproject;

import oasis.names.tc.wsrp.v1.types.PortletContext;
import oasis.names.tc.wsrp.v1.types.RegistrationContext;
import oasis.names.tc.wsrp.v1.types.RuntimeContext;
import oasis.names.tc.wsrp.v1.types.UserContext;

import org.apache.wsrp4j.util.Modes;
import org.apache.wsrp4j.util.WindowStates;

/**
 * Provides access to information required for portlet rendering. Provides two
 * additional constants representing attributes to be inserted into the servlet
 * request needed to instantiate the DynamicInformationProvider.
 *
 * @author  <a href="mailto:stefan.behl@de.ibm.com">Stefan Behl</a>
 */
public interface WSRPDynamicInformationProvider
{
    /**
     * String constant representing an attribute name of a servlet request.
     * Serves to store the getMarkup-, performInteraction- or performBlockingInteraction-
     * request within the current ServletRequest.
     */
    public static final String REQUEST = "wsrp.request";

    /**
     * String constant representing an attribute name of a servlet request.
     * Serves to store the Provider within the current ServletRequest.
     */
    public static final String PROVIDER = "wsrp.provider";
    
    /** String constant representing an attribute name of a servlet request.
     * Serves to store the render params within the current ServletRequest
     */
	public static final String RENDER_PARAMS = "wsrp.renderParams";

    public static final String INFO_PROVIDER = "wsrp.dynamic.information.provider";


    /**
     * Returns the content type the portlet should use in its response
     * The content type only includes the content type, not the character set.
     *
     * @return the content type to use for the response
     */
    public String getResponseContentType();

    /**
     * Gets a list of mime types which the portal accepts for the response.
     * This list is ordered with the most preferable types listed first.
     * <p>
     * The content type only includes the content type, not the
     * character set.
     * 
     * @return an java.util.Iterator of content types for the response
     */ 
    public java.util.Iterator getResponseContentTypes();

   /**
    * Returns the current <CODE>PortletMode</CODE> of the given portlet window.
    *
    * @param portletWindow    the portlet Window
    * @return the portlet mode
    */
   public Modes getPortletMode();

   /**
    * Returns the <CODE>WindowState</CODE> of the given portlet window.
    *
    * @param portletWindow    the portlet window
    * @return the portlet window state
    */
   public WindowStates getWindowState();

   /**
    * Returns true if the portal supports the requested
    * portlet mode.
    *
    * @param mode portlet mode requested to support
    *
    * @return  true, if the portal support requested portlet mode
    */

   public boolean isPortletModeAllowed(Modes mode);


       /**
     * @return Returns the portletContext.
     */
    public PortletContext getPortletContext();
    
    /**
     * @return Returns the regContext.
     */
    public RegistrationContext getRegistrationContext();
    
    /**
     * @return Returns the runtimeContext.
     */
    public RuntimeContext getRuntimeContext();
    
    /**
     * @return Returns the userContext.
     */
    public UserContext getUserContext();
    
    /**
     * @return Returns the isAction.
     */
    public boolean isAction();
    
    /**
     * @return Returns the isSecure.
     */
    public boolean isSecure();
    
    /**
    * Returns true if the portal supports the requested
    * window state.
    *
    * @param state window state requested to support
    *
    * @return  true, if the portal support requested window state
    */

   public boolean isWindowStateAllowed(WindowStates state);
 }
