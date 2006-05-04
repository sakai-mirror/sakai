/**
 * 
 */
package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import javax.servlet.http.HttpServletRequest;

import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.provider.URLComposer;
import org.apache.wsrp4j.producer.util.Base64;
import org.apache.wsrp4j.util.Modes;
import org.apache.wsrp4j.util.WindowStates;
import org.sakaiproject.api.kernel.tool.ToolURL;

/**
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 *
 */
public class RenderURL extends PortletURLProviderImpl implements ToolURL 
{    
    public RenderURL(HttpServletRequest request) {
        super(request);
        action = false;
    }

    /* (non-Javadoc)
     * @see org.apache.wsrp4j.producer.provider.sakaiproject.driver.PortalURLProvider#toString()
     */
    public String encodeURL(String url)
    {
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, "encodeURL()");
        }


        url = getRelativeURL(url, request);
        // If the URL still begins with <scheme>:// after potentially filtering out references to this server,
        // we treat it as an external reference and do not encode so that browser can directly get it.
        int pos = url.indexOf("://");
        if (pos > 0) {
            String scheme = url.substring(0, pos).toLowerCase();
            if (URLSCHEMES.contains(scheme)) 
                return url;
        }

        if (mode == null) {
            mode = Modes.view;
            if (logger.isLogging(Logger.TRACE_HIGH)) {
                logger.text(Logger.TRACE_HIGH, "encodeURL()",
                        "PortletMode is null. Setting portlet mode to 'view'");
            }
        }

        if (state == null) {
            state = WindowStates.normal;
            if (logger.isLogging(Logger.TRACE_HIGH)) {
                logger.text(Logger.TRACE_HIGH, "encodeURL()",
                            "WindowState is null. Setting window state to 'normal'");
            }
        }

        URLComposer urlComposer = provider.getURLComposer();
        if ((parameters != null) && (!parameters.isEmpty())) {
            String naviStateStr = encodeParameters();
            if ((url.length() == 0) || (url.indexOf('?') == -1))
            {
                url = url + "?" + naviStateStr;
            }
            else {
                url = url + "&" + naviStateStr;
            } 
        }
        String navigationalState = Base64.encode(url.getBytes());

        url = urlComposer.createRenderURL(mode.toString(), navigationalState,
                state.toString(), secure, runtimeContext, portletContext,
                userContext);

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, "encodeURL()", url);
        }
        return url;
    }  
}
