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


package org.apache.wsrp4j.persistence.driver;

import oasis.names.tc.wsrp.v1.types.CookieProtocol;
import oasis.names.tc.wsrp.v1.types.ServiceDescription;

/**
 * This class extends the ServiceDescription class for  persistent purpose 
 * and maps the CookieProtocol attribute in the servicedescription to the 
 * corresponding string representation. 
 * 
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 * 
 * @see ServiceDescription
 *
 * @version 1.0
 */
public class WSRPServiceDescription extends ServiceDescription
{

    /**
     * String representation of the CookieProtocol object, contained
     * in the servicedescription and retured by method getRequiresInitCookie()
     */
    private String initCookieRequired = null;

    /**
     * Default Constructor
     */
    public WSRPServiceDescription()
    {

    }

    /**
     * Constructs a WSRPServiceDescription from a ServiceDescription
     * 
     * @param serviceDescription
     */
    public WSRPServiceDescription(ServiceDescription serviceDescription)
    {

        this.setRequiresRegistration(serviceDescription.isRequiresRegistration());
        this.setOfferedPortlets(serviceDescription.getOfferedPortlets());
        this.setUserCategoryDescriptions(serviceDescription.getUserCategoryDescriptions());
        this.setRequiresInitCookie(serviceDescription.getRequiresInitCookie());
        this.setRegistrationPropertyDescription(serviceDescription.getRegistrationPropertyDescription());
        this.setLocales(serviceDescription.getLocales());
        this.setResourceList(serviceDescription.getResourceList());
        this.setExtensions(serviceDescription.getExtensions());

        this.initCookieRequired = this.getRequiresInitCookie().toString();
    }

    /**
     * String representation of the CookieProtocol object, contained
     * in the servicedescription and retured by method getRequiresInitCookie()
     * 
     * @return String representation from CookieProtocol 
     */
    public String getInitCookieRequired()
    {
        return this.initCookieRequired;
    }

    /**
     * Creates a CookieProtocol objects from a string representation and
     * stores the instance.
     * 
     * @param requiresInitCookie as string
     */
    public void setInitCookieRequired(String requiresInitCookie)
    {

        this.initCookieRequired = requiresInitCookie;
        CookieProtocol cookieProtocol = CookieProtocol.fromString(requiresInitCookie);
        setRequiresInitCookie(cookieProtocol);
    }

    /**
     * @return serviceDescription from this WSRPServiceDescription
     */
    public ServiceDescription toServiceDescription()
    {
        return this;
    }

}
