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
 * This class is the interface definition for the client persistence
 * support factory class. E.g. a consumer uses client persistent 
 * support. 
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public interface ClientPersistentFactory extends PersistentFactory
{

    /**
     * Returns a PageList
     *
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getPageList() throws WSRPException;


     /**
     * Returns the ConsumerPortletContextList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getConsumerPortletContextList() throws WSRPException;


}
