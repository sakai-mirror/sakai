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


package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import oasis.names.tc.wsrp.v1.types.ModelDescription;
import oasis.names.tc.wsrp.v1.types.PropertyList;

import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.provider.PortletPool;
import org.apache.wsrp4j.producer.provider.PortletState;
import org.apache.wsrp4j.producer.provider.PortletStateManager;
import org.apache.wsrp4j.producer.provider.Provider;

/**
   Implementation of the PortletStateManager interface
   
 * @author <a href="mailto:Peter.Fischer@de.ibm.com">Peter Fischer</a>
 * @see org.apache.wsrp4j.producer.provider.PortletStateManager
 */

public class PortletStateManagerImpl implements PortletStateManager
{
    //provider
    private Provider provider = null;

    // log and trace support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    /**
      Default constructor
    */
    private PortletStateManagerImpl()
    {
    }

    public static PortletStateManagerImpl create(Provider provider)
    {

        PortletStateManagerImpl portletStateManager = new PortletStateManagerImpl();
        portletStateManager.provider = provider;

        return portletStateManager;
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#get(String)
     */
    public PortletState get(String portletHandle)
    {
        PortletPool pool = provider.getPortletPool();
        try {
            SakaiPortlet portlet = (SakaiPortlet)pool.get(portletHandle);
            return portlet.getPortletState();
        } catch (WSRPException e) {
            logger.entry(Logger.ERROR, "Error returning portletState for handle " + portletHandle, e);
        }
        return null;
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#getAsString(String)
     */
    public String getAsString(String portletHandle)
    {
        PortletState es = get(portletHandle);
        if (es == null) return "";
        return es.getAsString();
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#getAsString(PortletState)
     */
    public String getAsString(PortletState state)
    {
        if (state != null) {
            return state.getAsString();
        }
        return "";
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#getAsPropertyList(PortletState)
     */
    public PropertyList getAsPropertyList(PortletState state)
    {
        if (state != null)
        {
            return state.getAsPropertyList();
        } else
        {
            return null;
        }
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#getAsPropertyList(String)
     */
    public PropertyList getAsPropertyList(String portletHandle)
    {
        PortletState es = get(portletHandle);
        if (es != null)
        {
            return es.getAsPropertyList();
        } else
        {
            return null;
        }
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#set(String, PortletState)
     */
    public void set(String portletHandle, PortletState state)
    {
        PortletState portletState = get(portletHandle);
        if (portletState != null) {
            portletState.setAsPropertyList(state.getAsPropertyList());
        }
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#setAsString(String, String)
     */
    public void setAsString(String portletHandle, String state)
    {
        PortletState portletState = get(portletHandle);        
        if (portletState != null) {
            portletState.setAsString(state);
        }
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#setAsPropertyList(String, PropertyList)
     */
    public void setAsPropertyList(String portletHandle, PropertyList state)
    {
        PortletState portletState = get(portletHandle);
        if (portletState != null) {
            portletState.setAsPropertyList(state);
        }
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#getAsPropertyList(String, String[])
     */
    public PropertyList getAsPropertyList(String portletHandle, String[] names)
    {
        PortletState portletState = get(portletHandle);
        if (portletState != null) {
            return portletState.getAsPropertyList(names);
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.wsrp4j.producer.provider.PortletStateManager#getAsString(String, String[])
     */
    public String getAsString(String portletHandle, String[] names)
    {
        PortletState es = get(portletHandle);
        if (es != null)
        {
            return es.getAsString(names);
        } else
        {
            return null;
        }
    }

    /**
      Destroy the portlet state information for a given portlet handle
      @param portletHandle portlet handle
    */
    public void destroy(String portletHandle)
    {
        PortletState es = get(portletHandle);
        if (es != null) {
           // TODO: Implement destroy
        }
    }

    /**
      Return the WSRP model description
      @param portletHandle portlet handle
      @param desiredLocales array of String representing desired locales
      @param sendAllLocales boolean indicating if all locales should be sent
    */
    public ModelDescription getModelDescription(String portletHandle, String[] desiredLocales, boolean sendAllLocales)
    {
        // ignore locale arguments for now
        PortletState es = null;
        if (es != null)
        {
            return es.getModelDescription();
        } else
        {
            return null;
        }
    }

}
