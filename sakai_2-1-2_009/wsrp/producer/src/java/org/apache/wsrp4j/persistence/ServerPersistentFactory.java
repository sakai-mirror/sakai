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


package org.apache.wsrp4j.persistence;

import org.apache.wsrp4j.persistence.PersistentDataObject;
import org.apache.wsrp4j.exception.WSRPException;

/**
 * This class is the interface definition for the server persistence
 * support factory class. E.g. a Producer uses server persistent 
 * support. 
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public interface ServerPersistentFactory extends PersistentFactory
{

     /**
     * Returns the RegistrationList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getRegistrationList() throws WSRPException;


    /**
     * Returns the ConsumerConfiguredPortletList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getConsumerConfiguredPortletList() throws WSRPException;

    /**
     * Returns the PortletDescriptionList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getPortletDescriptionList() throws WSRPException;

    /**
     * Returns the ServiceDescriptionList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getServiceDescriptionList() throws WSRPException;

    /**
     * Returns the ConsumerPortletRegistrationList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getConsumerPortletRegistrationList() throws WSRPException;
 

}
