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


package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oasis.names.tc.wsrp.v1.types.GetMarkup;
import oasis.names.tc.wsrp.v1.types.MarkupParams;
import oasis.names.tc.wsrp.v1.types.MarkupType;
import oasis.names.tc.wsrp.v1.types.PerformBlockingInteraction;
import oasis.names.tc.wsrp.v1.types.PortletContext;
import oasis.names.tc.wsrp.v1.types.PortletDescription;
import oasis.names.tc.wsrp.v1.types.RegistrationContext;
import oasis.names.tc.wsrp.v1.types.RuntimeContext;
import oasis.names.tc.wsrp.v1.types.UserContext;

import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.producer.provider.sakaiproject.WSRPDynamicInformationProvider;
import org.apache.wsrp4j.util.Modes;
import org.apache.wsrp4j.util.WindowStates;

/**
  Provides access to information required for portlet rendering
*/
public class DynamicInformationProviderImpl implements WSRPDynamicInformationProvider
{

    //references the servlet request
    private HttpServletRequest servletRequest = null;

    //references the provider
    private Provider provider = null;

    //references the registration context
    private RegistrationContext regContext = null;

    // reference to the runtime context of the request
    private RuntimeContext runtimeContext = null;

    //  reference to the user context of the request
    private UserContext userContext = null;

	// default mode
	private Modes mode = Modes.view;

	// default window state
	private WindowStates winState = WindowStates.normal;

	//indicating if this is a secure request between Browser and Consumer has been issued
	private boolean isSecure = false;

	//portlet context
    private PortletContext portletContext = null;

    //mime types
    private String[] mimeTypes = null;

    // indicating that this is a performBlockingInteraction request
    private boolean isAction = false;
    
    /**
      Constructor
      Creates a new DynamicInformationProviderImpl using the information provided by
      the servlet request and servlet config.
      
      @param request HttpServletRequest
    */
    public DynamicInformationProviderImpl(HttpServletRequest request)
    {
		this.servletRequest = request;

        this.provider = (Provider)request.getAttribute(WSRPDynamicInformationProvider.PROVIDER);

        if (provider == null)
        {
            // TODO: Exception ??
        }

        Object wsrpRequest = request.getAttribute(WSRPDynamicInformationProvider.REQUEST);

        // getMarkup was called
        if (wsrpRequest instanceof GetMarkup)
        {
            GetMarkup markupRequest = (GetMarkup)wsrpRequest;

            this.portletContext = markupRequest.getPortletContext();
            this.regContext = markupRequest.getRegistrationContext();
            this.runtimeContext = markupRequest.getRuntimeContext();
            this.userContext = markupRequest.getUserContext();

            MarkupParams markupParams = markupRequest.getMarkupParams();
            
			// get the mode requested
			this.mode = Modes.fromValue(markupParams.getMode());

			this.winState = WindowStates.fromString(markupParams.getWindowState());

            String[] currentMimeTypes = markupParams.getMimeTypes();
            if (currentMimeTypes != null)
            {
                this.mimeTypes = currentMimeTypes;
            }

			this.isSecure = markupParams.isSecureClientCommunication();
        }
        // performBlockingInteraction was called
        else if (wsrpRequest instanceof PerformBlockingInteraction)
        {
            PerformBlockingInteraction blockingInteractionRequest = (PerformBlockingInteraction)wsrpRequest;

            this.portletContext = blockingInteractionRequest.getPortletContext();
            this.regContext = blockingInteractionRequest.getRegistrationContext();
            this.runtimeContext = blockingInteractionRequest.getRuntimeContext();
            this.userContext = blockingInteractionRequest.getUserContext();

            MarkupParams markupParams = blockingInteractionRequest.getMarkupParams();

			// get the mode requested
			this.mode = Modes.fromValue(markupParams.getMode());

			// get the window state requested
			this.winState = WindowStates.fromString(markupParams.getWindowState());

            String[] currentMimeTypes = markupParams.getMimeTypes();
            if (currentMimeTypes != null)
            {
                this.mimeTypes = currentMimeTypes;
            }
            this.isSecure = markupParams.isSecureClientCommunication();
            this.isAction = true;
            
        } else
        {
            // TODO Exception ??
        }
    }

    /**
      Set the portlet mode
      @param mode portlte mode
    */
    public void changePortletMode(Modes mode)
    {
        this.mode = mode;
    }

    /**
      Set the portlet window state
      @param state portlet window state
    */
    public void changePortletWindowState(WindowStates state)
    {
        this.winState = state;
    }

    /**
      Return the default MIME type of the request
      @return String representing a MIME type
    */
    public String getRequestMimeType()
    {
        return this.mimeTypes[0];
    }

    /**
      Return the default MIME type of the response
      @return String representing a MIME type
    */
    public String getResponseMimeType()
    {
        return this.mimeTypes[0];
    }

    /**
      Return all MIME type of the request
      @return java.util.Enumeration
    */
    public Iterator getResponseMimeTypes()
    {
        Vector vector = new Vector();
        vector.copyInto(this.mimeTypes);

        return vector.iterator();
    }

    /**
      Return the portlet mode
      @param portletWindow portlet window
      @return PortletMode
    */
    public Modes getPortletMode()
    {
		return this.mode;
    }

    /**
      Return the previous portlet mode
      @param portletWindow portlet window
      @return PortletMode
    */
    public Modes getPreviousPortletMode()
    {
		return this.mode;
    }

    /**
      Return the portlet window state
      @param portletWindow portlet window
      @return WindowState
    */
    public WindowStates getWindowState()
    {
        return this.winState;
    }

    /**
      Return the previous portlet window state
      @param portletWindow portlet window
      @return WindowState
    */
    public WindowStates getPreviousWindowState()
    {
        return this.winState;
    }

	/** Return the supported Portlet Modes.
	 * These depend on the mime type of the request
	 * @return Iterator
	 */
	public Iterator getSupportedPortletModes() {
		ArrayList theModes = new ArrayList();
		try {
			// use the DescriptionHandler to obtain the PortletDescription
			PortletDescription pDesc = provider.getDescriptionHandler().getPortletDescription(portletContext.getPortletHandle(),null,null, null);
			MarkupType[] markupTypes = pDesc.getMarkupTypes();
			// search for matching mimeType
			int i = 0;
			boolean found = false;
			while ( markupTypes[i] != null && found == false) {
				if ( markupTypes[i].getMimeType().equalsIgnoreCase(mimeTypes[0]) ) {
					found = true;
					// get the modes, convert them to JSR modes
					String[] modes = markupTypes[i].getModes();
					for (int j = 0; j < modes.length; j++) {
						theModes.add(Modes.fromString(modes[j]));
					}
				}	
				i++;
			}
		}
		catch(Exception e) {
			//TODO: handle this one, rethrow			
		}		
        return theModes.iterator();
	}

	/** Return the supported Window States
	 * @return Iterator
	 */
	public Iterator getSupportedWindowStates() {
		ArrayList theStates = new ArrayList();
		try {
			// use the DescriptionHandler to obtain the PortletDescription
			PortletDescription pDesc = provider.getDescriptionHandler().getPortletDescription(portletContext.getPortletHandle(),null,null, null);
			MarkupType[] markupTypes = pDesc.getMarkupTypes();
			// search for matching mimeType
			int i = 0;
			boolean found = false;
			while ( markupTypes[i] != null && found == false) {
				if ( markupTypes[i].getMimeType().equalsIgnoreCase(mimeTypes[0]) ) {
					found = true;
					// get the window states, convert them to JSR windows states
					String[] winStates = markupTypes[i].getWindowStates();
					for (int j = 0; j < winStates.length; j++) {
						theStates.add(WindowStates.fromString(winStates[j]));
					}
				}
				i++;	
			}
		}
		catch(Exception e) {
			//TODO: handle this one, rethrow			
		}
		return theStates.iterator();
	}

	/** Check if a mode is allowed
	 * As of now we consider the the mode allowed if it is part of the portlet definition
	 * @return boolean
	 */
	public boolean isPortletModeAllowed(Modes portletMode) {
		try {
			// use the DescriptionHandler to obtain the PortletDescription
			PortletDescription pDesc = provider.getDescriptionHandler().getPortletDescription(portletContext.getPortletHandle(),null,null, null);
			MarkupType[] markupTypes = pDesc.getMarkupTypes();
			for (int i = 0; i < markupTypes.length; i++) {
				if ( markupTypes[i].getMimeType().equalsIgnoreCase(mimeTypes[0]) ) {
					String[] modes = markupTypes[i].getModes();
					// see if we find that mode
					Modes wsrpMode = null;
					for (int j = 0; j < modes.length; j++)
					{
						wsrpMode = Modes.fromString(modes[j]);
						if ( wsrpMode != null ) {
							// found a supported WSRP mode
							if (wsrpMode.equals(mode))
							{
								return true;    
							}
						}
					}
				}	
			}
		}
		catch(Exception e) {
			//TODO: handle this one, rethrow			
		}
		
		return false;
	}

	/** Check if a window state is allowed
	 * As of now we consider the the window state allowed if it is part of the portlet definition
	 * @return boolean
	 */
	public boolean isWindowStateAllowed(WindowStates windowState) {
		try {
			// use the DescriptionHandler to obtain the PortletDescription
			PortletDescription pDesc = provider.getDescriptionHandler().getPortletDescription(portletContext.getPortletHandle(),null,null, null);
			MarkupType[] markupTypes = pDesc.getMarkupTypes();
			for (int i = 0; i < markupTypes.length; i++) {
				if ( markupTypes[i].getMimeType().equalsIgnoreCase(mimeTypes[0]) ) {
					String[] winStates = markupTypes[i].getWindowStates();
					// see if we find that window state
					for (int j = 0; j < winStates.length; j++) {
						if ( WindowStates.fromString(winStates[j]).equals(windowState) ) {
							return true;	
						}
					}
				}	
			}
		}
		catch(Exception e) {
			//TODO: handle this one, rethrow			
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.pluto.services.information.DynamicInformationProvider#getResponseContentType()
	 */
	public String getResponseContentType()
	{
		return this.mimeTypes[0];
	}

	/* (non-Javadoc)
	 * @see org.apache.pluto.services.information.DynamicInformationProvider#getResponseContentTypes()
	 */
	public Iterator getResponseContentTypes()
	{
		ArrayList arrayList = new ArrayList(this.mimeTypes.length);
		for(int x = 0; x < arrayList.size(); x++)
		{
			arrayList.add(mimeTypes[x]);
		}
		return arrayList.iterator();
	}

    
    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPDynamicInformationProvider#isAction()
     */
    public boolean isAction() {
        return isAction;
    }
    

    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPDynamicInformationProvider#isSecure()
     */
    public boolean isSecure() {
        return isSecure;
    }
        
    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPDynamicInformationProvider#getPortletContext()
     */
    public PortletContext getPortletContext() {
        return portletContext;
    }
    

    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPDynamicInformationProvider#getRegistrationContext()
     */
    public RegistrationContext getRegistrationContext() {
        return regContext;
    }
    

    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPDynamicInformationProvider#getRuntimeContext()
     */
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }
    

    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.WSRPDynamicInformationProvider#getUserContext()
     */
    public UserContext getUserContext() {
        return userContext;
    }
    

}
