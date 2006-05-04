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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oasis.names.tc.wsrp.v1.types.RegistrationContext;
import oasis.names.tc.wsrp.v1.types.RegistrationData;

import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;

import org.apache.wsrp4j.persistence.PersistentDataObject;
import org.apache.wsrp4j.persistence.ServerPersistentFactory;
import org.apache.wsrp4j.persistence.PersistentHandler;
import org.apache.wsrp4j.persistence.driver.PersistentAccess;

import org.apache.wsrp4j.producer.ConsumerRegistry;
import org.apache.wsrp4j.producer.Registration;
import org.apache.wsrp4j.producer.provider.DescriptionHandler;
import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.util.HandleGenerator;
import org.apache.wsrp4j.util.HandleGeneratorFactoryImpl;

/**
 * <p>This class implements the ConsumerRegistry interface. All registrations
 * are kept within a hashmap associating registration handles (keys) with
 * registration-objects (values). Provides different access methods.</p>
 * <p>TODO:
 * <ul>
 *   <li>Synchronization aspects are not considered so far. This could be achieved
 *       by using a hashtable instead of a hashmap.</li>
 * </li>
 * </p>
 *
 * @author  <a href="mailto:stefan.behl@de.ibm.com">Stefan Behl</a>
 *
 * @see     ConsumerRegistry
 *
 */
public class ConsumerRegistryImpl implements ConsumerRegistry
{

    //initialized handle generator factory
    private HandleGeneratorFactoryImpl genFactory = null;
    //initialized handle factory
    private HandleGenerator generator = null;

    //persistence
    private PersistentDataObject persistentDataObject = null;//container for stored objects (information+data)
    private PersistentHandler    persistentHandler    = null;//generic persistence mechansim (store/restore)

    //indicates whether the consumer requires registration
    private boolean requiresRegistration = false;
    //refernce to the provider
    private Provider provider = null;

    //holds the actual consumer information
    private Map registrations = null;

    // log and trace support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    /**
     * Private constructor of ConsumerRegistryImpl.
       @param provider a Provider
     */
    private ConsumerRegistryImpl(Provider provider) throws WSRPException
    {

        super();

        String MN = "Constructor";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        this.provider = provider;

        if (provider != null)
        {

            DescriptionHandler descrHandler = null;

            if ((descrHandler = provider.getDescriptionHandler()) != null)
            {
                requiresRegistration = descrHandler.isRegistrationRequired();
            }
        }

        genFactory = new HandleGeneratorFactoryImpl();
        generator = genFactory.getHandleGenerator();

        registrations = new HashMap();

        // restore registration objects from persistent file store

        try
        {
            ServerPersistentFactory persistentFactory = PersistentAccess.getServerPersistentFactory();
            persistentHandler = persistentFactory.getPersistentHandler();
            persistentDataObject = persistentFactory.getRegistrationList();

            restore();
        } catch (Exception e)
        {

            // write warning to log
            if (logger.isLogging(Logger.WARN))
            {
                logger.text(Logger.WARN, MN, "Could not restore all registered consumers. Check persistent store.");
            }
        }

        if (logger.isLogging(Logger.TRACE_MEDIUM))
        {
            logger.text(Logger.TRACE_MEDIUM, MN, "ConsumerRegistry successfully constructed.");
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

    }

    /**
     * Creates a new ConsumerRegistryImpl-object. To be called within
     * an implementation of the abstract factory ConsumerRegistryFactory.
       @param provider the provider interface
     */
    public static ConsumerRegistryImpl create(Provider provider) throws WSRPException
    {
        return new ConsumerRegistryImpl(provider);
    }

    /**
     * Provides information about whether this producer requires
     * registration or not. Queries the DescriptionHandler to figure this out.
     *
     * @return A boolean indicating whether registration is required or not
     */
    public boolean isRegistrationRequired()
    {

        return this.requiresRegistration;

    }

    /**
     * Creates a new registration-object for a certain consumer,
     * adds it to the hashmap and returns it.
     *
     * @param  registrationData RegistrationData-object
     *
     * @return Registration     Registration-object
     * 
     * @throws WSRPException
     */
    public Registration register(RegistrationData registrationData) throws WSRPException
    {

        String MN = "register";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Registration newRegistration = new RegistrationImpl();

        RegistrationContext newContext = new RegistrationContext();

        newContext.setRegistrationHandle(generator.generateHandle());
        newContext.setRegistrationState(null);
        newContext.setExtensions(null);

        // set RegistrationData, RegistrationContext etc.
        newRegistration.setRegistrationData(registrationData);
        newRegistration.setRegistrationContext(newContext);

        // add new registration to hashmap
        registrations.put(newContext.getRegistrationHandle(), newRegistration);

        // store new registration to persistent file
        persistentDataObject.clear();
        persistentDataObject.addObject(newRegistration);
        try
        {

            store(persistentDataObject);

        } catch (WSRPException e)
        {
            // cleanup on error
            registrations.remove(newContext.getRegistrationHandle());

            throw e;
        }

        if (logger.isLogging(Logger.TRACE_MEDIUM))
        {
            logger.text(
                Logger.TRACE_MEDIUM,
                MN,
                "Consumer with registration handle: " + newContext.getRegistrationHandle() + " is registered");
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return newRegistration;
    }

    /**
     * Returns a certain registration identified by regHandle.
     *
     * @param  regHandle    String representing the regHandle.
     *
     * @return Registration Registration-object identified by regHandle.
     */
    public Registration get(String regHandle)
    {
        return (Registration)registrations.get(regHandle);
    }

    /**
     * Returns all registrations (of all consumers) currently stored
     * in the hashmap.
     *
     * @return Iterator of an registration collection containing all
     *         registrations.
     */
    public Iterator getAll()
    {
        return registrations.values().iterator();
    }

    /**
     * Deletes the registration of a certain consumer (identified by regHandle)
     * from the hashmap.
     *
     * @param regHandle  String representing the regHandle.
     */
    public void deregister(String regHandle)
    {

        String MN = "deregister";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        persistentDataObject.clear();
        persistentDataObject.addObject(registrations.get(regHandle));
        delete(persistentDataObject);
        registrations.remove(regHandle);

        if (logger.isLogging(Logger.TRACE_MEDIUM))
        {
            logger.text(
                Logger.TRACE_MEDIUM,
                MN,
                "Consumer with registration handle: " + regHandle + " is now deregistered.");
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }

    /**
     * Evaluates whether a registration with regHandle exists or not.
     * Returns true if registration exists, else false.
     *
     * @param regHandle  String representing the regHandle.
     *
     * @return Returns true if registration exists, else false. 
     */
    public boolean check(String regHandle)
    {

        String MN = "check";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        // check registration
        boolean regExists = false;
        if (registrations.get(regHandle) != null)
        {
            regExists = true;

            if (logger.isLogging(Logger.TRACE_MEDIUM))
            {
                logger.text(
                    Logger.TRACE_MEDIUM,
                    MN,
                    "Consumer with registration handle: " + regHandle + " is registered");
            }
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
        return regExists;
    }

    /**
     * Store the given Registration to persistent store
     * 
     * @param persistentDataObject
     */
    private void store(PersistentDataObject persistentDataObject) throws WSRPException
    {

        String MN = "store";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        // store single object
        persistentHandler.store(persistentDataObject);

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }

    /**
     * Restore all registrations from persistent store
     */
    private void restore() throws WSRPException
    {

        String MN = "restore";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        // clear the collection
        registrations.clear();

        try
        {
            persistentDataObject = persistentHandler.restoreMultiple(persistentDataObject);
            Iterator iterator = persistentDataObject.getObjects();

            while (iterator.hasNext())
            {
                Registration reg = (Registration)iterator.next();
                registrations.put(reg.getRegistrationContext().getRegistrationHandle(), reg);
            }
        } catch (WSRPException e)
        {

            // cleanup on error
            registrations.clear();

            throw e;
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }

    /**
     * Delete the given Registration from persistent store
     * 
     * @param persistentDataObject
     */
    private void delete(PersistentDataObject persistentDataObject)
    {

        String MN = "delete";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }
        // delete object
        persistentHandler.delete(persistentDataObject);

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }

}
