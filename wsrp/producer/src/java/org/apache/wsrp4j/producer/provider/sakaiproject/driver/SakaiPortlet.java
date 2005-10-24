/**
 * 
 */
package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oasis.names.tc.wsrp.v1.types.ModelDescription;
import oasis.names.tc.wsrp.v1.types.Property;
import oasis.names.tc.wsrp.v1.types.PropertyList;

import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.provider.ConsumerConfiguredPortlet;
import org.apache.wsrp4j.producer.provider.Portlet;
import org.apache.wsrp4j.producer.provider.PortletState;
import org.apache.wsrp4j.producer.provider.ProducerOfferedPortlet;
import org.apache.wsrp4j.util.Constants;
import org.sakaiproject.api.kernel.id.cover.IdManager;
import org.sakaiproject.api.kernel.session.ContextSession;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 *
 */
public abstract class SakaiPortlet implements Portlet {
    
    public static final String FRAGMENT = Tool.FRAGMENT;

    public static final String PORTLET = Tool.PORTLET;

    protected String portletHandle;
    
    protected ActiveTool tool;
   
    // log and trace support
    protected static Logger logger = LogManager.getLogManager().getLogger(SakaiPortlet.class);
    
    protected PortletState state = null;

    public static SakaiPortlet getSakaiPortlet(String handle, ActiveTool tool)
    {       
    	RegisteredPortlet portlet = new RegisteredPortlet(handle, tool);
    	portlet.setRegistrationRequired(true);
    	return portlet;
    }

    public static SakaiPortlet getSakaiPortlet(String handle, ToolConfiguration toolConfig)
    {           	
    	Tool tool = toolConfig.getTool();
    	if (tool instanceof ActiveTool) {
        	ConfiguredPortlet portlet = new ConfiguredPortlet(handle, (ActiveTool)tool);
        	portlet.setConfiguration(toolConfig.getPlacementConfig());
        	portlet.setPlacement(toolConfig);
        	return portlet;
    	}
    	else {
    		logger.entry(Logger.ERROR, "getSakaiPortlet", "PortletHandle: " + handle + " does not contain an ActiveTool");
    		return null;
    	}
    }

    protected SakaiPortlet(String handle, ActiveTool tool) 
    {
        this.portletHandle = handle;
        this.tool = tool;
        this.state = new SakaiPortletState();
    }
    
    public String getPortletHandle() {
        return portletHandle;
    }

    public void setPortletHandle(String portletHandle) {
        this.portletHandle = portletHandle;
    }
    
    public PortletState getPortletState()
    {
        return state;
    }
    
    public void render(HttpServletRequest req, HttpServletResponse resp, Placement placement) 
    {
        tool.include(req, resp, placement, req.getContextPath() + req.getServletPath(), req.getPathInfo());
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.Tool#getCategories()
     */
    public Set getCategories() {
        return tool.getCategories();
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.Tool#getDescription()
     */
    public String getDescription() {
        return tool.getDescription();
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.Tool#getKeywords()
     */
    public Set getKeywords() {
        return tool.getKeywords();
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.Tool#getTitle()
     */
    public String getTitle() {
        return tool.getTitle();
    }

    protected abstract Properties getConfiguration(boolean writable);

    public abstract Placement getPlacement(String portletInstance);
    
    /*
    public boolean equals(Object obj) {
        if (obj instanceof SakaiPortlet) {
            SakaiPortlet that = (SakaiPortlet)obj;
            return (this.portletHandle.equals(that.portletHandle) &&
                    this.tool.equals(that.tool) &&
                    this.delegate.equals(that.delegate) &&
                    this.state.equals(that.state));
        } 
        return false;
    } 
    */
 
    public static class RegisteredPortlet extends SakaiPortlet implements ProducerOfferedPortlet
    {
        private Set cloneHandles;
        private boolean requiresRegistration = true;

        public RegisteredPortlet(String handle, ActiveTool tool) {
            super(handle, tool);
        }
        
        public void addClone(ConsumerConfiguredPortlet cce) {
            cloneHandles.add(cce.getPortletHandle());
        }

        public Object clone() {
            // TODO Auto-generated method stub
            return null;
        }

        public void deleteClone(ConsumerConfiguredPortlet cce) {
            cloneHandles.remove(cce.getPortletHandle());
        }

        public Iterator getClones() {
            return cloneHandles.iterator();
        }

        public boolean isRegistrationRequired() {
            return requiresRegistration;
        }

        public void setRegistrationRequired(boolean required) {
            this.requiresRegistration = required;
        }

        public String getPortletHandle() {
            return portletHandle;
        }

        public void setPortletHandle(String handle) {
            portletHandle = handle;
        }

        /**
         * For a registered tool, create a new user-unique, instance-unique placement and store 
         * it in the session
         */
        public Placement getPlacement(String instanceKey)
        {
        	String context = "mercury";
            Session session = SessionManager.getCurrentSession();
            ContextSession ctxSession = session.getContextSession(context);

            String key = "wsrp-placement-" + portletHandle + "-" + instanceKey;
            Placement p = (Placement) ctxSession.getAttribute(key);
            if (p == null)
            {
            	p = new org.sakaiproject.util.Placement(
                        IdManager.createUuid(), 
                        tool, 
                        getConfiguration(false), 
                        context, 
                        tool.getTitle());
                ctxSession.setAttribute(key, p);
            }
            return p;
        }

        protected Properties getConfiguration(boolean writable) {
            return tool.getRegisteredConfig();
        }
        
    }
    
    public static class ConfiguredPortlet extends SakaiPortlet implements ConsumerConfiguredPortlet
    {
        private String parentHandle;
        private Properties configuration;
        private Placement placement;
        
        public ConfiguredPortlet(String handle, ActiveTool tool) {
            super(handle, tool);
            this.parentHandle = tool.getId();
        }

        public void setPlacement(Placement placement) {
        	this.placement = placement;
		}

		public String getParentHandle() {
            return parentHandle;
        }

        public void setParentHandle(String handle) {
            this.parentHandle = handle;
        }

        public Object clone() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getPortletHandle() {
            return portletHandle;
        }

        public void setPortletHandle(String handle) {
            portletHandle = handle;
        }
        
        public void setConfiguration(Properties config)
        {
            this.configuration = config;
        }

        protected Properties getConfiguration(boolean writable) {
            return this.configuration;
        }

		public Placement getPlacement(String portletInstance) {
			// For site-specific tools, we ignore the instance-key
			return placement;
		}
    }
    
    public class SakaiPortletState implements PortletState
    {

        private ModelDescription modelDescription;

        public void setPropertyMap(HashMap propertyMap) {
            Properties config = getConfiguration(true);
            if (config != null) {
                config.putAll(propertyMap);
            } else {
                // TODO: should we throw exception instead?
                if (logger.isLogging(Logger.ERROR)) {
                    logger.entry(Logger.ERROR, "setAsPropertyList: Cannot set properties because configuration is not writable");                
                }
            }
        }

        public Element getAsElement() {
            Properties config = getConfiguration(false);
            String [] names = new String [0];        
            if (config != null) {
                names = (String[]) config.keySet().toArray(names);
            }
            return getAsElement(names);
        }

        public Element getAsElement(String[] names) {
            Document document = createDocument();
            Element propertyList = null;
            if (document != null) 
            { 
                propertyList = document.createElement("propertyList");       
                Properties config = getConfiguration(false);
                if (config != null) 
                {
                    for (int i=0; i<names.length; i++) 
                    {
                        String name = names[i];
                        if ((name == null) || !config.containsKey(name)) continue;
                        
                        Element properties = document.createElement("properties");
                        properties.setAttribute("name", name);
                        properties.setAttributeNS("xml", "lang", Constants.LOCALE_EN_US);
                        Element stringValue = document.createElement("stringValue");
                        Text text = document.createTextNode(config.getProperty(name));
                        stringValue.appendChild(text);
                        properties.appendChild(stringValue);
                        propertyList.appendChild(properties);
                    }
                }
            }
            return propertyList;
        }

        public PropertyList getAsPropertyList() {
            Properties config = getConfiguration(false);
            String [] names = new String [0];        
            if (config != null) {
                names = (String[]) config.keySet().toArray(names);
            }
            return getAsPropertyList(names);
        }

        public PropertyList getAsPropertyList(String[] names) 
        {
            PropertyList list = new PropertyList();
            Property [] properties = new Property[names.length];
            Properties config = getConfiguration(false);
            if (config != null) 
            {
                for (int i=0; i<names.length; i++) 
                {
                    String name = names[i];
                    Property p = new Property();
                    p.setLang(Constants.LOCALE_EN_US);
                    p.setName(name);
                    p.setStringValue(config.getProperty(name));
                    properties[i++] = p;
                }
                list.setProperties(properties);
            }
            return list;
        }

        public String getAsString() {
            Properties config = getConfiguration(false);
            String [] names = new String [0];        
            if (config != null) {
                names = (String[]) config.keySet().toArray(names);
            }
            return getAsString(names);
        }

        public String getAsString(String[] names) {
            StringBuffer sb = new StringBuffer();
            sb.append("<propertyList>");
            Properties config = getConfiguration(false);
            if (config != null)
            {
                for (int i = 0; i < names.length; i++) {
                    String name = names[i];
                    sb.append("<properties name=\"").append(name).append("\" ");
                    sb.append("xml:lang=\"").append(Constants.LOCALE_EN_US).append("\">");
                    sb.append("<stringValue>");
                    sb.append(config.getProperty(name));
                    sb.append("</stringValue>");
                    sb.append("</properties>");
                }
            }
            sb.append("</propertyList>");
            return sb.toString();
        }

        public ModelDescription getModelDescription() {
            return this.modelDescription;
        }

        public HashMap getPropertyMap() {
            Properties config = getConfiguration(false);
            if (config != null) {
                HashMap map = new HashMap(config.size());
                map.putAll(config);
                return map;
            }
            else return new HashMap();
        }

        public void setAsElement(Element props) {
            // TODO Auto-generated method stub
            
        }

        public void setAsPropertyList(PropertyList props) {
            Properties config = getConfiguration(true);
            if (config != null) {
                Property [] properties = props.getProperties();
                for (int i=0; i<properties.length; i++) {
                    config.setProperty(properties[i].getName(), properties[i].getStringValue());
                }
            } else {
                // TODO: should we throw exception instead?
                if (logger.isLogging(Logger.ERROR)) {
                    logger.entry(Logger.ERROR, "setAsPropertyList: Cannot set properties because configuration is not writable");                
                }
            }
        }

        public void setAsString(String state) {
            // TODO Auto-generated method stub
            
        }

        public void setModelDescription(ModelDescription modelDescription) {
            this.modelDescription = modelDescription;
        }

        public String getPortletHandle() {
            return portletHandle;
        }

        private Document createDocument() 
        {
          try {
              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
              DocumentBuilder builder = factory.newDocumentBuilder();
              Document document = builder.newDocument();
              return document;
            } catch (ParserConfigurationException e) {
                if (logger.isLogging(Logger.ERROR)) {
                    logger.entry(Logger.ERROR, "createDocument", e);
                }
            }
            return null;
        }
    }


}
