/**
 * 
 */
package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.api.kernel.tool.ToolURL;
import org.sakaiproject.api.kernel.tool.ToolURLManager;

/**
 * This is a default implementation of the ToolURLManager and it comes with a default implementation
 * of ToolURL for all three URL types (render, action and resource). This implementation can be used by
 * most portals that don't do any special URL encoding. 
 * 
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 */
public class SakaiURLManager implements ToolURLManager {

    private HttpServletRequest m_request;
    
    /**
     * Constructor for ToolURLComponent
     * @param req HttpServletRequest that the URLs will be generated for
     * @param resp HttpServletResponse that the URL will be encoded for
     */
    
    public SakaiURLManager(HttpServletRequest req)
    {
        m_request = req;
    }
    
    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.ToolURLManager#createLinkURL()
     */
    public ToolURL createRenderURL() {
        return new RenderURL(m_request);        
     }

    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.ToolURLManager#createActionURL()
     */
    public ToolURL createActionURL() {
        return new ActionURL(m_request);        
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.api.kernel.tool.ToolURLManager#createResourceURL()
     */
    public ToolURL createResourceURL() {
        return new ResourceURL(m_request);        
    }
}
