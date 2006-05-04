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
public class ActionURL extends PortletURLProviderImpl implements ToolURL {

    /**
     * @param request
     */
    public ActionURL(HttpServletRequest request) {
        super(request);
        action = true;
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
        String navigationalState = null;
        if ((url != null) && (url.length() > 0)) {
            navigationalState = Base64.encode(url.getBytes());
        }

        String actionStateStr = null;
        if ((parameters != null) && (!parameters.isEmpty())) {
            actionStateStr = Base64.encode(encodeParameters().getBytes());
        }

        // TODO: introduce mode / state mapping class instead of string appending...
        url = urlComposer.createBlockingActionURL(mode.toString(),
                navigationalState, actionStateStr, state.toString(), secure,
                runtimeContext, portletContext, userContext);

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, "encodeURL()", url);
        }
        return url;
    }
}
