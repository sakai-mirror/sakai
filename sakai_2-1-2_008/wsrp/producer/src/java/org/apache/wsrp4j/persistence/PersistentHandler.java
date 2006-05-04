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

import org.apache.wsrp4j.exception.WSRPException;

/**
 * This class is the generic interface for the PersistentHandler implementation.
 * A PersistentHandler is used to store and retrieve objects from a persistent
 * store.
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public interface PersistentHandler
{

    /**
     * Store a single object from the PersistentDataObject to persistent
     * store.
     * 
     * @param persistentDataObject
     * @throws WSRPException
     */
    public void store(PersistentDataObject persistentDataObject) throws WSRPException;

    /**
     * Restore a single object into the persistentDataObject from the persistent store
     * 
     * @param persistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject restore(PersistentDataObject persistentDataObject) throws WSRPException;

    /**
     * Restore all known objects into the persistentDataObject from the persistent store
     * 
     * @param persistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject restoreMultiple(PersistentDataObject persistentDataObject) throws WSRPException;

    /**
     * Delete a object from the persistent store
     */
    public void delete(PersistentDataObject persistentDataObject);

}
