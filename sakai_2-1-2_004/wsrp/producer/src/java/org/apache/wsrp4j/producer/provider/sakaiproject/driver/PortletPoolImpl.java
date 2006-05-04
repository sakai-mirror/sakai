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

package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.servlet.ServletConfig;

import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.persistence.PersistentDataObject;
import org.apache.wsrp4j.persistence.PersistentHandler;
import org.apache.wsrp4j.producer.provider.ConsumerConfiguredPortlet;
import org.apache.wsrp4j.producer.provider.Portlet;
import org.apache.wsrp4j.producer.provider.PortletPool;
import org.apache.wsrp4j.producer.provider.ProducerOfferedPortlet;
import org.apache.wsrp4j.producer.provider.Provider;
import org.apache.wsrp4j.util.HandleGenerator;
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;

/**
 * <p>
 * This class implements the PortletPool interface. ProducerOfferedPortlets and
 * ConsumerConfiguredPortlets are kept within different hashmaps associating
 * portlet handles (keys) with portlet-objects (values). See interfaces
 * ProducerOfferedPortlet and ConsumerConfiguredPortlet.
 * </p>
 * <p>
 * TODO:
 * <ul>
 * <li>Synchronization aspects are not considered so far. This could be
 * achieved by using a hashtable instead of a hashmap.</li>
 * </li>
 * </p>
 *
 * @author <a href="mailto:Peter.Fischer@de.ibm.com">Peter Fischer</a>
 *
 * @version 1.1
 * 
 * @see PortletPool
 * 
 */
public class PortletPoolImpl implements PortletPool {

    // provider
    private Provider provider = null;

    // handle generator
    private HandleGenerator handleGenerator = null;

    // persistence
    private PersistentDataObject persistentDataObject = null;

    private PersistentHandler persistentHandler = null;

    // holds consumer configured portlets
    private Map consumerConfiguredPortlets = new HashMap();

    // holds producer offered portlets
    private Map producerOfferedPortlets = new HashMap();

    // for logging and exception support
    private Logger logger = LogManager.getLogManager().getLogger(
            this.getClass());

    // TODO: read this value from a property file
    private static boolean REGISTRATION_REQUIRED = true;

    /**
     * Private constructor of PortletPoolImpl.
     * 
     * Creates a HashMap for the ProducerOfferedPortlet and
     * ConsumerConfiguredPortlet object instances. 
     */
    private PortletPoolImpl() {    
    }

    public static PortletPool create(Provider provider) {
        PortletPoolImpl pool = new PortletPoolImpl();
        pool.provider = provider;
        return pool;
    }

    /**
     * Initialize the portlet pool
     * 
     * @param servletConfig
     *            servlet configuration
     * @param properties
     */
    public void init(ServletConfig servletConfig, Properties properties)
            throws Exception {
    	// Initialize the Internal HashTable
    	getAllProducerOfferedPortlets();
    }

    /**
     * <p>
     * Clones the portlet (ProducerOffered or ConsumerConfiguredPortlet)
     * associated by portlet-handle. Only the portlet-object is cloned, not the
     * portlet-description the portlet references, i.e. the resulting cloned
     * portlet references the same portlet-description as its original.
     * </p>
     * <p>
     * Assigns a new portletHandle to the clone to enable identification.
     * </p>
     * <p>
     * Adds the new ConsumerConfiguredPortlet to the hashmap after cloning.
     * </p>
     * 
     * @param portletHandle
     *            String identifying the portlet to be cloned.
     * 
     * @return ConsumerConfiguredPortlet The portlet-clone.
     */
    public synchronized Portlet clone(String portletHandle) {
        return null;
    }

    /**
     * Returns all ProducerOfferedPortlet objects.
     * 
     * @return Iterator of an portlet collection
     */
    public Iterator getAllProducerOfferedPortlets() {

        String MN = "getAllProducerOfferedPortlets";
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Iterator portlets = ToolManager.findTools(null, null).iterator();
        LinkedList result = new LinkedList();

        while (portlets.hasNext()) {
            Tool tool = (Tool) portlets.next();
            String id = tool.getId();
            Portlet portlet = (SakaiPortlet) producerOfferedPortlets.get(id);
            if (portlet == null) {
                ActiveTool activeTool = ActiveToolManager.getActiveTool(id);
                portlet = SakaiPortlet.getSakaiPortlet(id, activeTool);
                producerOfferedPortlets.put(id, portlet);
            }
            if (portlet != null)
                result.add(portlet);
        }

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
        return result.iterator();
    }

    private boolean isConsumerConfiguredPortlet(String id) {
        return consumerConfiguredPortlets.containsKey(id);
    }

    /**
     * Returns all ConsumerConfiguredPortlets currently stored in the hashmap
     * consumerConfiguredPortlets.
     * 
     * @return Iterator of an portlet collection containing all portlets.
     */
    public Iterator getAllConsumerConfiguredPortlets() {
        return consumerConfiguredPortlets.values().iterator();
    }

    /**
     * Returns a certain portlet identified by portletHandle.
     * 
     * @param portletHandle
     *            String representing the portletHandle.
     * 
     * @return ProducerOfferedPortlet object associated with portletHandle.
     */
    public Portlet get(String portletHandle) throws WSRPException {

        String MN = "get";
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Portlet portlet = null;

        if (isConsumerConfiguredPortlet(portletHandle))
            portlet = (Portlet) consumerConfiguredPortlets.get(portletHandle);
        else
            portlet = (Portlet) producerOfferedPortlets.get(portletHandle);

        if (portlet == null) {            
        	// First check if this is a "registered portlet"
            ActiveTool activeTool = ActiveToolManager.getActiveTool(portletHandle);
            if (activeTool != null) {
                portlet = (ProducerOfferedPortlet) SakaiPortlet.getSakaiPortlet(portletHandle, activeTool);
            } else {
            	ToolConfiguration toolConfig = SiteService.findTool(portletHandle);
            	if (toolConfig != null) {
            		portlet = (ConsumerConfiguredPortlet) SakaiPortlet.getSakaiPortlet(portletHandle, toolConfig);
            	}
            }
        }   // portlet not found
        if (portlet == null) WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.INVALID_PORTLET_HANDLE);

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return portlet;
    }

    /**
     * Deletes the portlet identified by portletHandle from the PortletPool.
     * Only consumer configured portlets can be deleted, NOT producer offered
     * ones. After update, the persistent file store is refreshed. Deletes all
     * existing portlet sessions (SessionHandler) and portlet states
     * (PortletStateManager) as well.
     * 
     * @param portletHandle
     *            String representing the portletHandle.
     * 
     * @return Boolean indicating if deletion was successful. Returns false if
     *         the portlet handle refers to a producer offered portlet.
     */
    public synchronized boolean destroy(String portletHandle) {

        String MN = "destroy";
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        boolean retVal = true;

        ConsumerConfiguredPortlet cce = (ConsumerConfiguredPortlet) consumerConfiguredPortlets
                .get(portletHandle);

        // TODO: delete portlet states as well
        // PortletStateManager portletStateManager =
        // provider.getPortletStateManager();

        /*
         * // ensure that the portlet to be deleted is a consumer configured
         * portlet if (cce != null) {
         *  // 1) delete portlet in portlet pool
         *  // get portlet PortletEntityImpl portletEntity =
         * (PortletEntityImpl)getPortletEntity(ObjectID.createFromString(portletHandle));
         * 
         * if (portletEntity != null) {
         * 
         * getPortletEntities().remove(portletEntity.getId().toString());
         * 
         * PortletEntityList portletEntityList =
         * portletEntity.getPortletApplicationEntity().getPortletEntityList();
         * ((PortletEntityListImpl)portletEntityList).remove(portletEntity);
         * 
         * try {
         *  // update persistent objects in PortletEntityRegistryServiceFile
         * super.store();
         *  // 2) delete corresponding consumer configured portlet in portlet
         * pool
         *  // remove cce consumerConfiguredPortlets.remove(portletHandle);
         *  // TODO: destroy portlet states as well //
         * portletStateManager.destroy(portletHandle);
         *  // delete the persistentFileStore for this portlet
         * persistentDataObject.clear(); persistentDataObject.addObject(cce);
         * delete(persistentDataObject); } catch (IOException ee) { retVal =
         * false; } }
         * 
         * if (logger.isLogging(Logger.TRACE_HIGH)) {
         * logger.exit(Logger.TRACE_HIGH, MN); }
         * 
         * return retVal;
         *  } // deleting a producer offered portlet is not allowed else {
         * 
         * if (logger.isLogging(Logger.TRACE_HIGH)) {
         * logger.exit(Logger.TRACE_HIGH, MN); }
         * 
         * return false; }
         */
        return false;

    }

    /**
     * Deletes several portlets from the portlet pool.
     * 
     * @param portletHandles
     *            Iterator of portletHandles.
     * 
     * @return Iterator of portlet handles refering to those portlets that could
     *         not be deleted (e.g. producer offered portlets)
     */
    public Iterator destroySeveral(Iterator portletHandles) {

        String MN = "destroySeveral";
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        LinkedList result = new LinkedList();

        while (portletHandles.hasNext()) {
            try {
                String handle = portletHandles.next().toString();
                if (!destroy(handle)) {
                    result.add(handle);
                }
            } catch (NoSuchElementException e) {
                // TODO
            }
        }

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return result.iterator();
    }

    /**
     * Store the object to persistent store
     * 
     * @param persistentDataObject
     * @throws CommonException
     */
    private void store(PersistentDataObject persistentDataObject)
            throws WSRPException {
        persistentHandler.store(persistentDataObject);
    }

    /**
     * Restores all available ConsumerConfiguredPortlets from persistent file
     * store
     * 
     * @throws CommonException
     */
    private void restore() throws WSRPException {

        String MN = "restore";
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        consumerConfiguredPortlets.clear();

        persistentDataObject = persistentHandler
                .restoreMultiple(persistentDataObject);

        Iterator iterator = persistentDataObject.getObjects();

        while (iterator.hasNext()) {
            ConsumerConfiguredPortlet cce = (ConsumerConfiguredPortlet) iterator
                    .next();
            consumerConfiguredPortlets.put(cce.getPortletHandle(), cce);
        }

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

    }

    /**
     * Delete the object from persistent store
     * 
     * @param persistentDataObject
     */
    private void delete(PersistentDataObject persistentDataObject) {

        persistentHandler.delete(persistentDataObject);
    }

}
