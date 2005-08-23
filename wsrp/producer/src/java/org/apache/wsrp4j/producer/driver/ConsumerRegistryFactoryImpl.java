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


package org.apache.wsrp4j.producer.driver;

import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.producer.ConsumerRegistry;
import org.apache.wsrp4j.producer.ConsumerRegistryFactory;
import org.apache.wsrp4j.producer.provider.Provider;

/**
 * This class implements the ConsumerRegistryFactory-interface.
 *
 * @author  <a href="mailto:stefan.behl@de.ibm.com">Stefan Behl</a>
 *
 */
public class ConsumerRegistryFactoryImpl implements ConsumerRegistryFactory
{
    //holds an instance of a consumer registry
    private static ConsumerRegistryImpl consumerReg = null;

    /** 
     * Returns an instance of the ConsumerRegistryImpl-class.
     *
     * @param provider A provider interface.
     *
     * @return ConsumerRegistry An instance of ConsumerRegistryImpl.
     */
    public ConsumerRegistry getConsumerRegistry(Provider provider) throws WSRPException
    {

        if (consumerReg == null)
        {
            consumerReg = ConsumerRegistryImpl.create(provider);
        }

        return consumerReg;
    }

}
