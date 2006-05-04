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

import javax.servlet.http.HttpServletRequest;

import org.apache.wsrp4j.producer.provider.DescriptionHandler;
import org.apache.wsrp4j.producer.provider.PortletInvoker;
import org.apache.wsrp4j.producer.provider.PortletPool;
import org.apache.wsrp4j.producer.provider.PortletRegistrationFilter;
import org.apache.wsrp4j.producer.provider.PortletRegistrationFilterWriter;
import org.apache.wsrp4j.producer.provider.PortletSessionManager;
import org.apache.wsrp4j.producer.provider.PortletStateManager;
import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.producer.provider.URLComposer;
import org.apache.wsrp4j.producer.provider.driver.PortletRegistrationFilterImpl;
import org.apache.wsrp4j.producer.provider.driver.URLComposerImpl;
import org.apache.wsrp4j.producer.provider.sakaiproject.PortletURLProvider;

public class ProviderImpl implements Provider
{
    //portlet pool
    private PortletPool portletPool = null;

    //description handler
    private DescriptionHandler descriptionHandler = null;

    //portlet state manager
    private PortletStateManager portletStateManager = null;

    //portlet invoker
    private PortletInvoker portletInvoker = null;

    //url composer
    private URLComposer urlComposer = null;

    private PortletSessionManagerImpl sessionManager;

    /**
     * Private constructor
     */
    private ProviderImpl()
    {
    }

    /**
     * Creates a new ProviderImpl-object. To be called within
     * an implementation of the abstract factory ProviderFactory.
     */
    public static ProviderImpl create()
    {
        ProviderImpl provider = new ProviderImpl();
        return provider;
    }


    /** 
     * Returns an instance of the DescriptionHandlerImpl-class.
     *
     * @return DescriptionHandler  An instance of DescriptionHandlerImpl.
     */
    public DescriptionHandler getDescriptionHandler()
    {
        if (descriptionHandler == null)
        {
            descriptionHandler = DescriptionHandlerImpl.create(this);
        }
        return descriptionHandler;
    }

    /** 
     * Returns an instance of the PortletInvokerImpl-class.
     *
     * @return PortletInvoker  An instance of PortletInvokerImpl.
     */
    public PortletInvoker getPortletInvoker()
    {
        if (portletInvoker == null)
        {
            portletInvoker = PortletInvokerImpl.create(this);
        }
        return portletInvoker;
    }

    /** 
     * Returns an instance of the PortletPoolImpl-class.
     *
     * @return PortletPool  An instance of PortletPoolImpl.
     */
    public PortletPool getPortletPool()
    {
        if (portletPool == null)
        {
            portletPool = PortletPoolImpl.create(this);
        }
        return portletPool;
    }

    /** 
     * Returns an instance of the PortletStateManagerImpl-class.
     *
     * @return PortletStateManager  An instance of PortletStateManagerImpl.
     */
    public PortletStateManager getPortletStateManager()
    {
        if (portletStateManager == null)
        {
            portletStateManager = PortletStateManagerImpl.create(this);
        }
        return portletStateManager;
    }


    /**
     * Returns an instance of the PortletRegistrationFilterWriter
     * 
     * @return PortletRegistrationFilterWriter
     */
    public PortletRegistrationFilterWriter getPortletRegistrationFilterWriter()
    {
        return PortletRegistrationFilterImpl.createWriter();
    }


    /**
     * Returns an instance of the PortletRegistrationFilter
     * 
     * @return PortletRegistrationFilter
     */
    public PortletRegistrationFilter getPortletRegistrationFilter()
    {
        return PortletRegistrationFilterImpl.createReader();
    }


    /** 
     * Returns an instance of the URLComposerImpl-class.
     *
     * @return URLComposer  An instance of URLComposerImpl.
     */
    public URLComposer getURLComposer()
    {
        if (urlComposer == null)
        {
            urlComposer = URLComposerImpl.getInstance(this);
        }
        return urlComposer;
    }

    public PortletSessionManager getSessionManager() 
    {
        if (sessionManager == null)
        {
            sessionManager = PortletSessionManagerImpl.create(this);
        }
        return sessionManager;
    }

    public PortletURLProvider getPortletURLProvider(HttpServletRequest request) {
        return new RenderURL(request);
    }
}
