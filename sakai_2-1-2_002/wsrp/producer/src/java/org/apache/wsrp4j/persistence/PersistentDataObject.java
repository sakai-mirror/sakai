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

import java.util.Iterator;

import org.apache.wsrp4j.persistence.PersistentInformation;

/**
 * This class is the generic interface for the PersistentDataObject 
 * implementations used to store and retrieve objects from a persistent
 * store. 
 * 
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public interface PersistentDataObject
{

    /**
     * Sets the PersistentInformation object for this PersistentDataObject
     *
     * @param persistentInfo 
     * @see PersistentInformation
     */
    public void setPersistentInformation(PersistentInformation persistentInfo);

    /**
     * Get the PersistentInformation for this PersistentDataObject
     *
     * @return PersistentInformation
     * @see PersistentInformation
     */
    public PersistentInformation getPersistentInformation();

    /**
     * Clear the internal object collection 
     */
    public void clear();

    /**
     * Returns all objects in the object map as an iterator
     * 
     * @return Iterator
     */
    public Iterator getObjects();

    /**
     * Add an element to the internal map
     * 
     * @param object Object as element
     * 
     */
    public void addObject(Object object);

    /**
     * Returns the last element added
     * 
     * @return Object as last element
     */
    public Object getLastElement();

}
