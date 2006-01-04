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


package org.apache.wsrp4j.persistence.xml.driver;

import org.apache.wsrp4j.persistence.*;

import org.apache.wsrp4j.persistence.xml.ServerPersistentInformationProvider;

import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;


/**
 * This class is the server factory implementation for the persistence support.
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public class ServerPersistentFactoryImpl implements ServerPersistentFactory
{
    // holds this factory
    private static ServerPersistentFactory serverPersistentFactory = null;

    // holds the persistentHandler
    private static PersistentHandler persistentHandler = null;

    // holds the ServerPersistentInformationProvider
    private static ServerPersistentInformationProvider serverInfoProvider = null;

    // offset path for the persistent store
    private String path = null;

    // log and trace support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());



    /**
     * Returns a PersistentHandler 
     * 
     * @return persistentHandler
     */
    public PersistentHandler getPersistentHandler()
    {

        String MN = "getPersistentHandler";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        if (persistentHandler == null)
        {
            persistentHandler = PersistentHandlerImpl.create();

            if (logger.isLogging(Logger.TRACE_MEDIUM))
            {
                logger.text(Logger.TRACE_MEDIUM, MN, "PersistentHandler successfully created.");
            }
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return persistentHandler;
    }

    /**
     * @return ServerPersistentInformationProvider
     */
    public PersistentInformationProvider getPersistentInformationProvider()
    {

        String MN = "getServerPersistentInformationProvider";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        if(serverInfoProvider == null && path == null)
        {
            serverInfoProvider = ServerPersistentInformationProviderImpl.create();
        }
        else 
        {
            if(serverInfoProvider == null && path != null)
            {
                serverInfoProvider = ServerPersistentInformationProviderImpl.create(path);
            }
        }


        if (logger.isLogging(Logger.TRACE_MEDIUM))
        {
            logger.text(Logger.TRACE_MEDIUM, MN, "ServerPersistentInformationProvider successfully created.");
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return serverInfoProvider;
    }

    /**
     * Set the real offset path of the persistent store
     * 
     * @param path as string value
     */
    public void setPath(String path)
    {
        this.path = path;
    }


    /**
     * Returns the PortletList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getPortletList() throws WSRPException
    {

        String MN = "getPortletList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        PortletList pdo = null;

        try
        {
            pdo = (PortletList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.PortletList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);


        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }


    /**
     * Returns the ProducerList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getProducerList() throws WSRPException
    {

        String MN = "getProducerList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        ProducerList pdo = null;

        try
        {

            pdo = (ProducerList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.ProducerList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }


    /**
     * Returns the RegistrationList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getRegistrationList() throws WSRPException
    {

        String MN = "getRegistrationList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        RegistrationList pdo = null;

        try
        {

            pdo = (RegistrationList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.RegistrationList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }

    /**
     * Returns the UserList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getUserList() throws WSRPException
    {

        String MN = "getUserList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        UserList pdo = null;

        try
        {
            pdo = (UserList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.UserList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }

    /**
     * Returns the ConsumerConfiguredPortletList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getConsumerConfiguredPortletList() throws WSRPException
    {

        String MN = "getConsumerConfiguredPortletList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        ConsumerConfiguredPortletList pdo = null;

        try
        {
            pdo = (ConsumerConfiguredPortletList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.ConsumerConfiguredPortletList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }

    /**
     * Returns the PortletDescriptionList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getPortletDescriptionList() throws WSRPException
    {

        String MN = "getPortletDescriptionList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        PortletDescriptionList pdo = null;

        try
        {
            pdo = (PortletDescriptionList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.PortletDescriptionList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }

    /**
     * Returns the ServiceDescriptionList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getServiceDescriptionList() throws WSRPException
    {

        String MN = "getServiceDescriptionList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        ServiceDescriptionList pdo = null;

        try
        {
            pdo = (ServiceDescriptionList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.ServiceDescriptionList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }

    /**
     * Returns the ConsumerPortletRegistrationList
     * 
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getConsumerPortletRegistrationList() throws WSRPException
    {

        String MN = "getConsumerPortletRegistrationList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        ConsumerPortletRegistrationList pdo = null;

        try
        {
            pdo = (ConsumerPortletRegistrationList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.ConsumerPortletRegistrationList").newInstance();

            ((ServerPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

        } catch (Exception e)
        {
            // could not find class
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, 1003, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return pdo;
    }

}
