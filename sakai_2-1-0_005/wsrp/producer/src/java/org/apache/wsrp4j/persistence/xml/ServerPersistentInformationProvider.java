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


package org.apache.wsrp4j.persistence.xml;

import org.apache.wsrp4j.persistence.*;
import org.apache.wsrp4j.persistence.xml.driver.*;


/**
 * Contains the persistent file store information for the castor
 * xml file support used by server applications e.g. producers.
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 */
public interface ServerPersistentInformationProvider extends PersistentInformationProvider
{


    /**
     * Returns the persistent file information for the ServiceDescription
     *
     * @param serviceDescriptionList ServiceDescriptionList
     *
     * @return PersistentInformation
     */
    public PersistentInformation getPersistentInformation(ServiceDescriptionList serviceDescriptionList);

    /**
     * Returns the persistent file information for the PortletDescription
     *
     * @param portletDescriptionList PortletDescriptionList
     *
     * @return PersistentInformation
    **/
    public PersistentInformation getPersistentInformation(PortletDescriptionList portletDescriptionList);

    /**
     * Returns the persistent file information for the Registration
     *
     * @param registrationList RegistrationList
     *
     * @return PersistentInformation
    **/
    public PersistentInformation getPersistentInformation(RegistrationList registrationList);

    /**
     * Returns the persistent file information for the ConsumerConfiguredPortlet
     *
     * @param cceList ConsumerConfiguredPortletList
     *
     * @return PersistentInformation
    **/
    public PersistentInformation getPersistentInformation(ConsumerConfiguredPortletList cceList);

    /**
    * Returns the persistent file information for the producers
    *
    * @param producerList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(ProducerList producerList);

    /**
    * Returns the persistent file information for the user
    *
    * @param userList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(UserList userList);

    /**
    * Returns the persistent file information for the Portlet
    *
    * @param portletList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(PortletList portletList);

    /**
    * Returns the persistent file information for the ConsumerPortletRegistrationList 
    *
    * @param regList ConsumerPortletRegistrationList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(ConsumerPortletRegistrationList regList);


}
