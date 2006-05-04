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

import java.io.InputStream;
import java.util.Properties;

import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;

import org.apache.wsrp4j.persistence.*;

/**
 * This class provides a static method to access the client and server
 * persistent factories.It reads in the file "WSRPServices.properties" 
 * for the server and the file "SwingConsumer.properties" for a client.
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public class PersistentAccess
{

    // the name of the .properties file for the server
    private static String WSRP_SERVICES = "WSRPServices.properties";

    // the name of the .properties file for the client
    private static String CLIENT = "SwingConsumer.properties";

    // property name of the server factory
    private static String SERVER_PERSISTENT_FACTORY = "server.persistent.factory";

    // property name of the client factory
    private static String CLIENT_PERSISTENT_FACTORY = "client.persistent.factory";

    // the content of the properties file
    private static Properties pFactories = null;

    // holds the instance of the server factory after initializing
    private static ServerPersistentFactory serverPersistentFactory = null;

    // holds the instance of the client factory after initializing
    private static ClientPersistentFactory clientPersistentFactory = null;

    // log and trace support
    private static Logger logger = LogManager.getLogManager().getLogger(getThisClass());

    /**
     * Fetches a server factory-instance.
     *
     * @return ServerPersistentFactory
     * @throws WSRPException
     */
    public static ServerPersistentFactory getServerPersistentFactory() throws WSRPException
    {
        String MN = "getServerPersistentFactory";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        if (serverPersistentFactory == null)
        {
            serverPersistentFactory = (ServerPersistentFactory)getFactory(SERVER_PERSISTENT_FACTORY,WSRP_SERVICES);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return serverPersistentFactory;
    }

    /**
     * Fetches a client factory-instance.
     *
     * @return ClientPersistentFactory
     * @throws WSRPException
     */
    public static ClientPersistentFactory getClientPersistentFactory() throws WSRPException
    {
        String MN = "getClientPersistentFactory";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        if (clientPersistentFactory == null)
        {
            clientPersistentFactory = (ClientPersistentFactory)getFactory(CLIENT_PERSISTENT_FACTORY,CLIENT);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return clientPersistentFactory;
    }


    /**
     * Returns the factory loaded from the properties file.
     * 
     * @param type of the factory as string value
     * @param propertyFile name of the property file as string value
     * @throws WSRPException
     */
    private static Object getFactory(String type,String propertyFile) throws WSRPException
    {
        String MN = "getFactory";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Object obj = null;

        try
        {
            loadPropertyFile(propertyFile);

            String factoryName = (String)pFactories.get(type);

            Class cl = Class.forName(factoryName);

            if (logger.isLogging(Logger.TRACE_HIGH))
            {
                logger.exit(Logger.TRACE_HIGH, MN);
            }

            obj = cl.newInstance();

        } catch (Exception e)
        {
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.PERSISTENT_FACTORY_NOT_FOUND);
        }

        return obj;
    }

    /**
     * Loads the content of a properties file into a private Properties object. The properties
     * file to load contains the factory information.
     * 
     * @param propertyFile name of the property file as string value
     * @throws WSRPException
     */
    private static void loadPropertyFile(String propertyFile) throws WSRPException
    {

        String MN = "loadPropertyFile";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        try
        {
            // read in .properties-file
            InputStream in = getThisClass().getClassLoader().getResourceAsStream(propertyFile);
            pFactories = new Properties();
            pFactories.load(in);

        } catch (Exception e)
        {
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.PROPERTY_FILE_NOT_FOUND);
        }
    }


    /**
     * Returns the class object of PersistentrAccess
     * 
     * @return java.lang.Class
     */
    private static Class getThisClass()
    {

        return PersistentAccess.class;

    }
}
