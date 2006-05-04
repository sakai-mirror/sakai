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
import org.apache.wsrp4j.persistence.xml.*;
import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import oasis.names.tc.wsrp.v1.types.PortletDescription;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
/***
import org.apache.wsrp4j.consumer.driver.ProducerImpl;
import org.apache.wsrp4j.consumer.driver.UserImpl;
import org.apache.wsrp4j.consumer.driver.WSRPPortletImpl;
***/
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.Registration;
import org.apache.wsrp4j.producer.driver.RegistrationImpl;
import org.apache.wsrp4j.producer.provider.driver.ConsumerConfiguredPortletImpl;
import org.apache.wsrp4j.producer.provider.driver.ConsumerPortletRegistrationImpl;
import org.apache.wsrp4j.persistence.driver.WSRPServiceDescription;

/**
 * This class holds the information for persistent file handling centrally. It
 * creates the persistent file store directory during construction and returns
 * PersistentFileInformation objects, filled with directory and file name
 * information, based on the request.
 *
 * The file system structure looks like:
 *
 * 1) the PROVIDER case:
 *
 * ./persistence/*.xml              (contains the mapping XML files)
 * ./persistence/portlets/*.xml     (contains object XML files)
 * ./persistence/descriptions/*.xml (contains object XML files)
 * ./persistence/portletStates/*.xml (contains object XML files)
 * ./persistence/registries/*.xml   (contains object XML files)
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 */
public class ServerPersistentInformationProviderImpl implements ServerPersistentInformationProvider
{

    private ServerPersistentInformationProvider _persistentInfoProvider = null;

    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    // sub directory for descriptions
    private static final String DESCRIPTIONS           = "descriptions";

    // sub directory for descriptions
    private static final String PORTLETS               = "portlets";

    // sub directory for descriptions
    private static final String PORTLETSTATES          = "portletstates";

    // sub directory for descriptions
    private static final String REGISTRIES             = "registries";

    // sub directory for users
    private static final String USERS                  = "users";

    // sub directory for producers
    private static final String PRODUCERS              = "producers";

    // user mapping file name
    private static final String USER_MAPPING           = "UserMapping";

    // producer mapping file name
    private static final String PRODUCER_MAPPING       = "ProducerMapping";

    // portlet mapping file name
    private static final String PORTLET_MAPPING        = "PortletMapping";

    // mapping file name of the servicedescription
    private static final String SERVICE_DESCRIPTION_MAPPING = "ServiceDescriptionMapping";

    // mapping file name of the registry
    private static final String REGISTRY_MAPPING       = "RegistryMapping";

    // mapping file name of the portlet (pluto)
    private static final String PLUTO_PORTLET_MAPPING  = "PlutoPortletMapping";

    // mapping file name of the portlet (pluto)
    private static final String SIMPLE_PORTLET_MAPPING = "SimplePortletMapping";

    // mapping file name of the portlet states
    private static final String PORTLET_STATE_MAPPING  = "PortletStateMapping";


    // This will be the root of all the persistent files
    // it should end up looking soemthing like this:
    // c:/Program Files/Tomcat 4.0/webapps/WEB-INF/persistence
    // depending on the installation
    private static String ROOT_DIR = null;

    // File extension of the portlet persistent file store files
    private static String FILE_EXTENSION = ".xml";

    // sub directory, sub-root of the persistent file store
    private static String PERSISTENT_DIR = "persistence";

    // Separator in the filename of the portlet persistent file store
    private static String SEPARATOR = "@";


    /**
     * Static construction of the PersistentInformationProvider
     */
    public static ServerPersistentInformationProvider create()
    {

        return new ServerPersistentInformationProviderImpl();

    }

    /**
     * @param offsetPath path for the store directory to be used
     *
     * Static construction of the PersistentInformationProvider
     */
    public static ServerPersistentInformationProvider create(String offsetPath)
    {

        return new ServerPersistentInformationProviderImpl(offsetPath);

    }

    /**
     * Private constructor
     */
    private ServerPersistentInformationProviderImpl()
    {

        logger.entry(Logger.TRACE_HIGH, "Constructor");

        try
        {
            if (ROOT_DIR == null)
            {
                // ROOT_DIR is static, so it only needs to be setup once for all
                // instances of PersistentStore objects

                // get the servlet context so we can figure out where to put the
                // files based on the context root of the web application
                // this is a bit of a hack since it will only work for servlet based
                // deployments, but this is how Axis computes the location of the server-config.wsdd,
                // so we can too
                String path = null;

                try
                {
                    MessageContext msgContext = AxisEngine.getCurrentMessageContext();
                    HttpServlet servlet = (HttpServlet)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLET);
                    ServletContext servletContext = servlet.getServletContext();
                    // use WEB-INF because http clients can't see it
                    path = servletContext.getRealPath("/WEB-INF") + File.separator;

                    if (logger.isLogging(Logger.TRACE_MEDIUM))
                    {
                        logger.text(Logger.TRACE_MEDIUM, "Constructor", "Current path of servletContext = " + path);
                    }

                } catch (Exception e)
                {
                    // ignore
                }

                if (path == null)
                {
                    ROOT_DIR = PERSISTENT_DIR;

                } else
                {
                    ROOT_DIR = path + PERSISTENT_DIR;
                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        logger.exit(Logger.TRACE_HIGH, "Constructor");
    }

    /**
     * Private constructor
     */
    public ServerPersistentInformationProviderImpl(String offsetPath)
    {

        logger.entry(Logger.TRACE_HIGH, "Constructor");

        try
        {
            if (ROOT_DIR == null)
            {
                // ROOT_DIR is static, so it only needs to be setup once for all
                // instances of PersistentStore objects
                ROOT_DIR = offsetPath + File.separator + PERSISTENT_DIR;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        logger.exit(Logger.TRACE_HIGH, "Constructor");
    }


    /**
     * Returns the store directory with the given sub directory
     *
     * @param  subDirectory String sub directory
     *
     * @return String store directory
     *
     */
    public String getStoreDirectory(String subDirectory)
    {
        return ROOT_DIR + File.separator + subDirectory;
    }

    /**
     * Creates the store directory with the given sub directory name
     *
     * @param subDirectory String sub directory
     *
     */
    public void makeStoreSubDir(String subDirectory)
    {

        // make sure the store directory exists
        File file = new File(ROOT_DIR + File.separator + subDirectory);

        if (!file.exists())
        {
            file.mkdir();
        }
    }

    /**
     * Compute the path to the mapping file from the root plus the name of the mapping file
     *
     * @param mappingBaseName String mapping file base name without extension
     *
     * @return String fully qualified mapping file name with extension
     */
    public String getMappingFile(String mappingBaseName)
    {
        String mappingFile = new String(ROOT_DIR + File.separator + mappingBaseName + FILE_EXTENSION);
        return mappingFile;
    }



    /**
     * Returns the persistent file information for the ServiceDescription
     *
     * @param serviceDescriptionList ServiceDescriptionList
     *
     * @return PersistentInformation
     */
    public PersistentInformation getPersistentInformation(ServiceDescriptionList serviceDescriptionList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (serviceDescriptionList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(DESCRIPTIONS));
            persistentInfo.setMappingFileName(getMappingFile(SERVICE_DESCRIPTION_MAPPING));
            WSRPServiceDescription serviceDescription = new WSRPServiceDescription();
            persistentInfo.setFilenameStub(serviceDescription.getClass().getName());
            persistentInfo.setFilename(
                persistentInfo.getStoreDirectory()
                    + File.separator
                    + serviceDescription.getClass().getName()
                    + FILE_EXTENSION);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            serviceDescriptionList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(DESCRIPTIONS);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;
    }

    /**
     * Returns the persistent file information for the PortletDescription
     *
     * @param portletDescriptionList PortletDescriptionList
     *
     * @return PersistentInformation
    **/
    public PersistentInformation getPersistentInformation(PortletDescriptionList portletDescriptionList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (portletDescriptionList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(DESCRIPTIONS));
            persistentInfo.setMappingFileName(null);
            PortletDescription portletDescription = new PortletDescription();
            persistentInfo.setFilenameStub(portletDescription.getClass().getName());
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            portletDescriptionList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(DESCRIPTIONS);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;
    }

    /**
     * Returns the persistent file information for the Registration
     *
     * @param registrationList RegistrationList
     *
     * @return PersistentInformation
    **/
    public PersistentInformation getPersistentInformation(RegistrationList registrationList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (registrationList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(REGISTRIES));
            persistentInfo.setMappingFileName(getMappingFile(REGISTRY_MAPPING));

            Registration registration = new RegistrationImpl();
            persistentInfo.setFilenameStub(registration.getClass().getName());
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            registrationList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(REGISTRIES);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;

    }

    /**
     * Returns the persistent file information for the ConsumerConfiguredPortlet
     *
     * @param cceList ConsumerConfiguredPortletList
     *
     * @return PersistentInformation
    **/
    public PersistentInformation getPersistentInformation(ConsumerConfiguredPortletList cceList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (cceList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(PORTLETS));
            persistentInfo.setMappingFileName(getMappingFile(PLUTO_PORTLET_MAPPING));

            ConsumerConfiguredPortletImpl cce = new ConsumerConfiguredPortletImpl();
            persistentInfo.setFilenameStub(cce.getClass().getName());
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            cceList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(PORTLETS);

        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;

    }

    /**
    * Returns the persistent file information for the producers
    *
    * @param producerList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(ProducerList producerList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (producerList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(PRODUCERS));
            persistentInfo.setMappingFileName(getMappingFile(PRODUCER_MAPPING));
            /***
            ProducerImpl producer = new ProducerImpl();
            persistentInfo.setFilenameStub(producer.getClass().getName());
            ***/
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            producerList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(PRODUCERS);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;
    }

    /**
    * Returns the persistent file information for the user
    *
    * @param userList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(UserList userList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (userList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(USERS));
            persistentInfo.setMappingFileName(getMappingFile(USER_MAPPING));
            /***
            UserImpl user = new UserImpl();
            persistentInfo.setFilenameStub(user.getClass().getName());
            ***/
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            userList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(USERS);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;
    }

    /**
    * Returns the persistent file information for the Portlet
    *
    * @param portletList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(PortletList portletList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (portletList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(PORTLETS));
            persistentInfo.setMappingFileName(getMappingFile(PORTLET_MAPPING));
            /***
            WSRPPortletImpl portlet = new WSRPPortletImpl();
            persistentInfo.setFilenameStub(portlet.getClass().getName());
            ***/
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            portletList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(PORTLETS);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;
    }

    /**
    * Returns the persistent file information for the ConsumerPortletRegistrationList
    *
    * @param regList
    *
    * @return PersistentInformation
    */
    public PersistentInformation getPersistentInformation(ConsumerPortletRegistrationList regList)
    {

        logger.entry(Logger.TRACE_HIGH, "getPersistentInformation");

        PersistentInformationXML persistentInfo = null;

        if (regList != null)
        {
            persistentInfo = new PersistentInformationImpl();
            persistentInfo.setStoreDirectory(getStoreDirectory(REGISTRIES));
            persistentInfo.setMappingFileName(null);
            ConsumerPortletRegistrationImpl cpr = new ConsumerPortletRegistrationImpl();
            persistentInfo.setFilenameStub(cpr.getClass().getName());
            persistentInfo.setFilename(null);
            persistentInfo.setExtension(FILE_EXTENSION);
            persistentInfo.setSeparator(SEPARATOR);
            regList.setPersistentInformation(persistentInfo);
            makeStoreSubDir(REGISTRIES);
        }

        logger.exit(Logger.TRACE_HIGH, "getPersistentInformation");

        return persistentInfo;
    }

}
