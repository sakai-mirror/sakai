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
import org.apache.wsrp4j.persistence.xml.ClientPersistentInformationProvider;

import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;

/**
 * This class is the client factory implementation for the persistence support.
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public class ClientPersistentFactoryImpl implements ClientPersistentFactory
{

    // holds this factory
    private static ClientPersistentFactory persistentFactory = null;

    // holds the PersistentHandler
    private static PersistentHandler persistentHandler = null;

    // holds the ClientPersistentInformationProvider
    private static ClientPersistentInformationProvider clientInfoProvider = null;

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
     * Returns a ServerPersistentInformationrovider
     * 
     * @return ServerPersistentInformationProvider
     */
    public PersistentInformationProvider getPersistentInformationProvider()
    {

        String MN = "getClientPersistentInformationProvider";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        if(clientInfoProvider == null)
        {
            clientInfoProvider = ClientPersistentInformationProviderImpl.create();
        }


        if (logger.isLogging(Logger.TRACE_MEDIUM))
        {
            logger.text(Logger.TRACE_MEDIUM, MN, "ClientPersistentInformationProvider successfully created.");
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return clientInfoProvider;
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

            ((ClientPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

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
     * Returns the ConsumerPortletContextList
     *
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getConsumerPortletContextList() throws WSRPException
    {

        String MN = "getConsumerPortletContextList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        ConsumerPortletContextList pdo = null;

        try
        {

            pdo = (ConsumerPortletContextList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.ConsumerPortletContextList").newInstance();

            ((ClientPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

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

            ((ClientPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

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
     * Returns the PageList
     *
     * @return PersistentDataObject
     * @throws WSRPException
     */
    public PersistentDataObject getPageList() throws WSRPException
    {

        String MN = "getPageList";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        PageList pdo = null;

        try
        {

            pdo = (PageList) Class.forName("org.apache.wsrp4j.persistence.xml.driver.PageList").newInstance();

            ((ClientPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);

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

            ((ClientPersistentInformationProvider)getPersistentInformationProvider()).getPersistentInformation(pdo);


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
