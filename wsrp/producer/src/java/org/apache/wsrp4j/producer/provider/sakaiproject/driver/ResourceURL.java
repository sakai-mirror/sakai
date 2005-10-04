/**
 * 
 */
package org.apache.wsrp4j.producer.provider.sakaiproject.driver;

import javax.servlet.http.HttpServletRequest;

import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.producer.provider.URLComposer;
import org.apache.wsrp4j.util.Modes;
import org.apache.wsrp4j.util.WindowStates;
import org.sakaiproject.api.kernel.tool.ToolURL;

/**
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 *
 */
public class ResourceURL extends PortletURLProviderImpl implements ToolURL {

    /**
     * @param request
     */
    public ResourceURL(HttpServletRequest request) {
        super(request);
        action = false;
    }

    public String encodeURL(String url) 
    {
        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.entry(Logger.TRACE_HIGH, "encodeURL()");
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
                logger
                        .text(Logger.TRACE_HIGH, "encodeURL()",
                                "WindowState is null. Setting window state to 'normal'");
            }
        }

        URLComposer urlComposer = provider.getURLComposer();
        if (url != null) {
            url = urlComposer.createResourceURL(getAbsoluteURL(url, request), false/*rewrite*/,
                    this.secure, null/*runtimeContext*/,
                    null/*portletContext*/, null/*userContext*/);
        }

        if (logger.isLogging(Logger.TRACE_HIGH)) {
            logger.exit(Logger.TRACE_HIGH, "encodeURL()", url);
        }
        return url;

    }
}
