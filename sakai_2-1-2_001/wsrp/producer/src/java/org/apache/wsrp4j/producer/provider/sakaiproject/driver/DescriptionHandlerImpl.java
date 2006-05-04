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


package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import oasis.names.tc.wsrp.v1.types.CookieProtocol;
import oasis.names.tc.wsrp.v1.types.LocalizedString;
import oasis.names.tc.wsrp.v1.types.MarkupType;
import oasis.names.tc.wsrp.v1.types.PortletDescription;
import oasis.names.tc.wsrp.v1.types.RegistrationContext;
import oasis.names.tc.wsrp.v1.types.ServiceDescription;
import oasis.names.tc.wsrp.v1.types.UserContext;

import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.persistence.PersistentDataObject;
import org.apache.wsrp4j.persistence.PersistentHandler;
import org.apache.wsrp4j.persistence.ServerPersistentFactory;
import org.apache.wsrp4j.persistence.driver.PersistentAccess;
import org.apache.wsrp4j.persistence.driver.WSRPServiceDescription;
import org.apache.wsrp4j.producer.provider.DescriptionHandler;
import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.util.Constants;
import org.apache.wsrp4j.util.Modes;
import org.apache.wsrp4j.util.Utility;
import org.apache.wsrp4j.util.WindowStates;

/**
 *
 * @author <a href="mailto:Peter.Fischer@de.ibm.com">Peter Fischer</a>
 * @author <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>* 
 *
 * @version 1.9
 *
 * @see     DescriptionHandler
 *
 */
public class DescriptionHandlerImpl implements DescriptionHandler
{

    private Provider provider;
    
    //persistence
    private PersistentDataObject persistentDataObject = null;
    private PersistentHandler persistentHandler = null;

    // ServiceDescription
    private ServiceDescription serviceDescription;       
    
    // name of the properties which is read
    private static final String WSRP_SERVICE_PROP = "WSRPServices.properties"; 
        
    //defines an internal property name/key
    private static final String URL_TEMPLATE_PROCESSING = "provider.doesurltemplateprocessing";

    //indicates if a producer supports URL template processing
    private Boolean doesUrlTemplateProcessing = Boolean.FALSE;

    //holds all portlet descriptions
    private Map portletDescs = new HashMap();

    // for logging and exception support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    /**
     * Default constructor of DescriptionHandler implementation
     *
     * @see DescriptionHandler
     */
    private DescriptionHandlerImpl()
    {
        String MN = "Constructor";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        try
        {
            ServerPersistentFactory persistentFactory = PersistentAccess.getServerPersistentFactory();
            persistentHandler = persistentFactory.getPersistentHandler();
            persistentDataObject = persistentFactory.getServiceDescriptionList();

            // restore the service description from persistent store
            restore();

        } catch (WSRPException e)
        {

            if (logger.isLogging(Logger.ERROR))
            {
                logger.text(
                    Logger.ERROR,
                    MN,
                    "Restore ServiceDescription failed. Check persistent XML file and content");
            }
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }

    /**
     * Creates a new DescriptionHandlerImpl-object. To be called within
     * an implementation of the abstract factory PortletPoolFactory.
     *
     * @param    provider  The provider for this DescriptionHandlerImpl
     *
     * @return   DescriptionHandler
     */
    public static DescriptionHandler create(Provider provider)
    {

        DescriptionHandlerImpl descriptionHandler = new DescriptionHandlerImpl();
        descriptionHandler.provider = provider;
        
        try {

            Properties props = Utility.loadPropertiesFromFile(WSRP_SERVICE_PROP);
            String urlTemplProcStr = props.getProperty(DescriptionHandlerImpl.URL_TEMPLATE_PROCESSING,"false");
            descriptionHandler.doesUrlTemplateProcessing =Boolean.valueOf(urlTemplProcStr);
        
        } catch(WSRPException e) {
            
            // keep default
            
        }
        return descriptionHandler;

    }

    /**
     *
     * @param regContext RegistrationContext
     * @param desiredLocales String[] 
     *
     *
     */
    public ServiceDescription getServiceDescription(RegistrationContext regContext, String[] desiredLocales) throws WSRPException
    {
        String MN = "get";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        if (serviceDescription != null)
        {

            // check if full description is required    
            if (regContext != null | !isRegistrationRequired())
            {

                // get all portlet descriptions from portlet pool
                serviceDescription.setOfferedPortlets(
                    getProducerOfferedPortletDescriptions(regContext, desiredLocales));

                serviceDescription.setRequiresInitCookie(CookieProtocol.perUser);

            }

        } else
        {

            // serviceDescription not available
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.LOAD_SERVICEDESCRIPTION_FAILED);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return serviceDescription;

        // TODO: return descriptions according to configuration

    }

    /**
     * Returns true, if registration is required on this producer
     * 
     * @return true -  registration is required
     *         false - registration is not required
     * 
     */
    public boolean isRegistrationRequired() throws WSRPException
    {

        String MN = "isRegistrationRequired";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        boolean bRetVal = true;

        if (serviceDescription != null)
        {

            bRetVal = serviceDescription.isRequiresRegistration();

        } else
        {

            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.LOAD_SERVICEDESCRIPTION_FAILED);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return bRetVal;
    }

    /**
     * Adds/replaces the ServiceDescription object in the DescriptionHandler and
     * stores the object for persistency.
     *
     * @param newServiceDescription ServiceDescription
     */
    public void put(ServiceDescription newServiceDescription) throws WSRPException
    {
        String MN = "put";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        serviceDescription = newServiceDescription;
        WSRPServiceDescription wsrpServDesc = new WSRPServiceDescription(newServiceDescription);
        persistentDataObject.clear();
        persistentDataObject.addObject(wsrpServDesc);

        try
        {

            store(persistentDataObject);

        } catch (WSRPException e)
        {

            serviceDescription = null;
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.SAVE_SERVICEDESCRIPTION_FAILED, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

    }

    /**
     * Returns an array containing portlet descriptions, i.e. one portlet
     * description per producer offered portlet. The array will contains
     * only those descriptions, which match the RegistrationContext criteria. 
     * In addition, proxy portlets are not part of the list. 
     * 
     * @param regContext RegistrationContext
     * 
     * @return    Array of portlet description objects.
     */
    public PortletDescription[] getProducerOfferedPortletDescriptions(
        RegistrationContext regContext,
        String[] desiredLocales)
        throws WSRPException
    {
        String MN = "getProducerOfferedPortletDescriptions";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        // check if we got all available descs
        // TODO: check also if there are descs in the Map that are not valid anymore...
        // but only if size is different after filling in.

        Iterator portlets = provider.getPortletPool().getAllProducerOfferedPortlets();

        while (portlets.hasNext())
        {
            SakaiPortlet portlet = (SakaiPortlet)portlets.next();
            String id = portlet.getPortletHandle();

            PortletDescription desc = (PortletDescription)portletDescs.get(id);

            if (desc == null)
            {
                desc = getPortletDescription(id, regContext, null, desiredLocales);
                if (desc != null) {
                    portletDescs.put(id, desc);
                }
            }
        }

        PortletDescription[] portletDescriptionArray = new PortletDescription[portletDescs.values().size()];

        Iterator descs = portletDescs.values().iterator();

        for (int i = 0; descs.hasNext(); i++)
        {
            portletDescriptionArray[i] = (PortletDescription)descs.next();
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return portletDescriptionArray;

    }

    /**
     * Returns an PortletDescription for the given PortletHandle, only if the 
     * registration context allows this action
     * 
     * @param portletHandle		PortletHandle
     * @param regContext		the registration context
     * @param userContext		the user context
     * @param desiredLocales	the locales for which the description should be returned	
     * 
     * @return PortletDescriptionb
     */
    public PortletDescription getPortletDescription(
        String portletHandle,
        RegistrationContext regContext,
        UserContext userContext,
        String[] desiredLocales)
        throws WSRPException
    {

        PortletDescription portletDescription = null;

        portletDescription = getPortletDescription(portletHandle, desiredLocales);

        return portletDescription;

    }

    /**
     * Returns an PortletDescription, constructed from the corresponding portlet entity
     * 
     * 
     * @param portletHandle String portlet handle
     * @param desiredLocales String[] desired locales
     * 
     * @return PortletDescription
     * 
     * @see PortletDefinition
     * @see PortletEntityRegistry
     */
    protected PortletDescription getPortletDescription(String portletHandle,
    												   String[] desiredLocales)
        throws WSRPException
    {

        String MN = "getPortletDescription";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        trace_getPortletDescription_args(portletHandle, desiredLocales);

        PortletDescription portletDescription = null;

        // get a portletDefinition, to be mapped to an PortletDescription
        SakaiPortlet portlet = (SakaiPortlet) provider.getPortletPool().get(portletHandle);
        if (portlet != null)
        {
            portletDescription = new PortletDescription();
            portletDescription.setPortletHandle(portletHandle);

            // move description, title and shorttitle from Language to PortletDescription
            LocalizedString localizedString;

            String stringLocale = Constants.LOCALE_EN_US;

            // add title
            String titleStr = portlet.getTitle();
            if (titleStr != null && titleStr.length() > 0)
            {
                localizedString = new LocalizedString();
                localizedString.setLang(stringLocale);
                localizedString.setValue(titleStr);
                portletDescription.setTitle(localizedString);
            }

            // Should we skip this, since there is no "short title" or use the title itself
            String shortTitleStr = portlet.getTitle();
            if (shortTitleStr != null && shortTitleStr.length() > 0)
            {
                localizedString = new LocalizedString();
                localizedString.setLang(stringLocale);
                localizedString.setValue(shortTitleStr);
                portletDescription.setShortTitle(localizedString);
            }

            // add description
            String descStr = portlet.getDescription();
            if (descStr != null && descStr.length() > 0)
            {
                localizedString = new LocalizedString();
                localizedString.setLang(stringLocale);
                localizedString.setValue(descStr);
                portletDescription.setDescription(localizedString);
            }

            // add keywords
            Set keywordSet = portlet.getKeywords();

            int numberOfKeywords = keywordSet.size();
            if (numberOfKeywords > 0)
            {
                LocalizedString[] keywords = new LocalizedString[numberOfKeywords];
                Iterator it = keywordSet.iterator();
                int index = 0;
                while (it != null && it.hasNext())
                {
                    keywords[index] = new LocalizedString();
                    keywords[index].setLang(stringLocale);
                    keywords[index++].setValue((String)it.next());
                    
                }
                portletDescription.setKeywords(keywords);
            }

            // add display name
            String displayNameStr = portlet.getTitle();
            if (displayNameStr != null && displayNameStr.length() > 0)
            {
                localizedString = new LocalizedString();
                localizedString.setLang(stringLocale);
                localizedString.setValue(displayNameStr);
            }

            // Tentative solution: set markup to text/html
            MarkupType markupType = new MarkupType();
            markupType.setMimeType(Constants.MIME_TYPE_HTML);

            // set Language locales to markupType locales
            markupType.setLocales(new String[] {stringLocale});

            // tentative solution: set some modes states
            String [] modes = new String[2];
            modes[0] = Modes.view.getValue();
            modes[1] = Modes.help.getValue();
            markupType.setModes(modes);

            // tentative solution: set some window states
            String[] windowStates = new String[3];
            markupType.setWindowStates(windowStates);
            markupType.setWindowStates(0, WindowStates.normal.getValue());
            markupType.setWindowStates(1, WindowStates.minimized.getValue());
            markupType.setWindowStates(2, WindowStates.maximized.getValue());

            // build markupTypes
            portletDescription.setMarkupTypes(new MarkupType [] {markupType});

            // set general default values for the following attributes
            // for the WSRP implementation. 
            portletDescription.setUsesMethodGet(Boolean.FALSE);
            portletDescription.setUserContextStoredInSession(Boolean.FALSE);
            portletDescription.setTemplatesStoredInSession(Boolean.FALSE);
            portletDescription.setHasUserSpecificState(Boolean.TRUE);
            portletDescription.setDoesUrlTemplateProcessing(doesUrlTemplateProcessing);

        } else
        {
            System.out.println("** Exc **");
            // no portlet definition found
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.MISSING_PORTLET_DEFINITION);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return portletDescription;

    }

    /**
     * Returns the portlet description for a given portlet handle
     
       @param portletHandle String representing a portlet's handle
       @return PortletDescription
       @throws WSRPException 
     */
    public PortletDescription getPortletDescription(String portletHandle) throws WSRPException
    {

        return getPortletDescription(portletHandle, null, null, null);

    }

    /**
     * Stores the ServiceDescription to persistent store
     * 
     * @param  persistentDataObject
     * @throws WSRPException
     */
    private void store(PersistentDataObject persistentDataObject) throws WSRPException
    {

        persistentHandler.store(persistentDataObject);

    }

    /**
     * Restores a single ServiceDescritpion
     * @throws WSRPException
     */
    private void restore() throws WSRPException
    {

        persistentDataObject = persistentHandler.restore(persistentDataObject);

        WSRPServiceDescription tempServiceDescription = (WSRPServiceDescription)persistentDataObject.getLastElement();
        this.serviceDescription = tempServiceDescription.toServiceDescription();

    }

    /**
     * Trace input arguments for getPortletDescription method
     * 
     * @param portletHandle as String
     * @param desiredLocales as String[]
     * 
     */
    private void trace_getPortletDescription_args(String portletHandle, String[] desiredLocales)
    {

        if (logger.isLogging(Logger.TRACE_MEDIUM))
        {

            logger.text(Logger.TRACE_MEDIUM, "trace", "INPUT:STRING  :portletHandle=" + portletHandle);

            if (desiredLocales != null && desiredLocales.length > 0)
            {
                for (int x = 0; x < desiredLocales.length; x++)
                {
                    logger.text(
                        Logger.TRACE_MEDIUM,
                        "trace",
                        "INPUT:STRING[" + x + "]:desiredLocales=" + desiredLocales[x]);
                }
            } else
            {
                logger.text(Logger.TRACE_MEDIUM, "trace", "INPUT:STRING[]:desiredLocales=NULL or length=0");
            }
        }
    }
}
