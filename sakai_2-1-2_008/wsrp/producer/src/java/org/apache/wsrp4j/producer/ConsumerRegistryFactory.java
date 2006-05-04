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


package org.apache.wsrp4j.producer;

import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.producer.provider.Provider;

/**
 * This class provides an interface to any ConsumerRegistry.
 *
 * @author  <a href="mailto:stefan.behl@de.ibm.com">Stefan Behl</a>
 */
public interface ConsumerRegistryFactory
{

    /**
     * Returns an instance of ConsumerRegistry by calling the constructor
     * of the corresponding class implementing the ConsumerRegistry-Interface
     *
     * @return ConsumerRegistry An instance of any class implementing the
     *                          ConsumerRegistry.
     */
    public ConsumerRegistry getConsumerRegistry(Provider provider) throws WSRPException;

}