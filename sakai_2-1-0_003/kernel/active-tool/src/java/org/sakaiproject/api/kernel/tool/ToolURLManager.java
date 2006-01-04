/**
 * 
 */
package org.sakaiproject.api.kernel.tool;


/**
 * The ToolURLManager interface allows creation of ToolURL that reference the
 * portlet itself.
 * 
 * Sakai tools assume the servlet APIs as the basis for generating markup and
 * getting parameters, yet for presentation inside different portals, the URL
 * encoding API javax.servlet.http.HttpServletResponse#encodeURL is not
 * sufficient. This is because the Servlet API treats all URLs the same (hence a
 * single encodeURL method), whereas portlet technologies such as JSR-168 and
 * WSRP differentiate between URLs based on what they represent. There are
 * primarily three different URL types as distinguished by WSRP (JSR 168 has 2,
 * which is a subset of the three types distinguished by WSRP). The only
 * reasonable way to allow tools to generate markup that can be presented in a
 * portlet is to have the tools differentiate the URLs that are embedded in the
 * markup. Some of this can be done automatically if the URLs are generated
 * using macros or other APIs that allows for this differentiation to be plugged
 * underneath. For instance, most of velocity based tools used different macros
 * for different URL types, so it is possible to plug the appropriate URL
 * encoding underneath the macros when the tool is being rendered as a portlet.
 * However, tools that directly access Servlet APIs to generate markup must use
 * these APIs directly.
 * 
 * Using these APIs is simple. Instead of creating a String URL with the
 * parameters, you create a ToolURL object. You must decide whether the URL type
 * (render, action or resource) to create a ToolURL object. You can then set the
 * request path and request parameters by using methods in ToolURL. Finally, to
 * include it in the generated markup, you convert the ToolURL to a String using
 * the ToolURL#toString method.
 * 
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 * @see javax.portlet.PortletURL
 */

public interface ToolURLManager {
   
    /**
     * Create a URL that is a hyperlink back to this tool. HTTP GET requests
     * initiated by simple &lt;a href&gt; construct falls in this category.
     * 
     * @return a URL that is a hyperlink back to this tool
     */
    ToolURL createRenderURL();

    /**
     * Create a URL that is an action performed on this tool. HTML Form actions
     * that initiate an HTTP POST back to the tool falls in this category.
     * 
     * @return a URL that is an action performed on this tool
     */
    ToolURL createActionURL();

    /**
     * Create a URL for a resource related to the tool, but not necessarily
     * pointing back to the tool. Image files, CSS files, JS files etc. are
     * examples of Resource URLs. Paths for resource URLs may have to be
     * relative to the server, as opposed to being relative to the tool.
     * 
     * @return a URL for a resource
     */
    ToolURL createResourceURL();
}
