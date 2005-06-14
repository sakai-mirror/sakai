/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/servletfilter/SetupSakaiServicesFilter.java,v 1.9.2.3 2005/03/21 15:19:09 lance.indiana.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
* 
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
* 
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

package org.sakaiproject.util.servletfilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.util.Setup;

/**
 * This filter allows arbitrary web applications (servlets) to access existing
 * Sakai services.  This filter should be declared in the web.xml of a web
 * application that will use Sakai services.  The services will then be available
 * through the Sakai ComponentManager.
 * @see org.sakaiproject.service.framework.component.cover.ComponentManager
 */
public class SetupSakaiServicesFilter implements Filter
{

    /**
     * The filter configuration object we are associated with. If this value is
     * null, this filter instance is not currently configured.
     */
    private FilterConfig m_filterConfig = null;

    /**
     * Whether to ensure that the webapp shares the same HttpSession with the other Sakai
     * webapps; this is enforced by rewriting URLs to pass through the TunnelServlet.
     */
    private boolean m_forceSharedHttpSessionAcrossWebapps = false;
        
    /**
     * Whether to coerce passing the PID (identifies the current tool/page) on _all_ URLs.
     */
    private boolean m_passthroughPID = true;
    
    /**
     * Take this filter out of service.
     */
    public void destroy()
    {
        this.m_filterConfig = null;
    }

 
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {

        if (m_filterConfig == null) return;
               
		try
		{
			if (Setup.setup( (HttpServletRequest) request, (HttpServletResponse) response ))
			{
			    // an error occured, stop here
				return;
			}
		}
		catch (IOException e)
		{
			// IOException typically happen because the connection to the client browser
		    // has closed.  So this exception is routine, and shouldn't be reported in the
		    // server logs.
		    try
		    {
				if (response instanceof HttpServletResponse) ((HttpServletResponse)response).sendError(400);
		    } 
		    catch (Exception e2)
		    {
		        // ignore addition exception while reporting the first exception
		    }
		    return;
		}
		catch (Throwable any)
		{
			// if there's any other exception in setup, return an error
			if (response instanceof HttpServletResponse) ((HttpServletResponse)response).sendError(400);
			Logger.warn(this + "Setup exception: " + any);
			return;
		}
		
		if (response instanceof HttpServletResponse && (m_passthroughPID || m_forceSharedHttpSessionAcrossWebapps))
		{
		    // wrap the response
		    response = new WrappedResponse((HttpServletResponse) response, (HttpServletRequest) request);	
		}
		
        // Pass control on to the next filter
        chain.doFilter(request, response);

    }

    /**
     * Place this filter into service.
     * 
     * @param filterConfig
     *            The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.m_filterConfig = filterConfig;
        
        String str = filterConfig.getInitParameter("shareHttpSession");
        if (str != null && ("true".equals(str) || "yes".equals(str)))
        {
            m_forceSharedHttpSessionAcrossWebapps = true;
        }
        
        str = filterConfig.getInitParameter("passthroughPID");
        if (str != null && ("true".equals(str) || "yes".equals(str)))
        {
            m_passthroughPID = true;
        }
    }


    public String toString()
    {

        if (m_filterConfig == null) return ("SetupSakaiServicesFilter()");
        StringBuffer sb = new StringBuffer("SetupSakaiServicesFilter(");
        sb.append(m_filterConfig);
        sb.append(")");
        return (sb.toString());

    }
    
	//protected String getPid(ServletRequest req)
	//{
	//	return ((ParameterParser) req.getAttribute(Setup.ATTR_PARAMS)).getString("pid");
	//}
    
	/** 
	 * wraps the response object to modify outputted URLs.
	 */
    class WrappedResponse extends HttpServletResponseWrapper
    {
        private HttpServletRequest m_req;
        
        public WrappedResponse(HttpServletResponse res, HttpServletRequest req)
        {
            super(res);
            m_req = req;
        }
        
        public String encodeRedirectUrl(String url)
        {
            return rewriteURL(super.encodeRedirectUrl(url));
        }
        
        public String encodeRedirectURL(String url)
        {
            return rewriteURL(super.encodeRedirectURL(url));
        }
       
        public String encodeUrl(String url)
        {
            return rewriteURL(super.encodeUrl(url));
        }
        
        public String encodeURL(String url)
        {
            return rewriteURL(super.encodeURL(url));
        }
        
        public void sendRedirect(String url) throws IOException
        {
            super.sendRedirect(rewriteURL(url));
        }
       
        
        /** 
         * Rewrites the given URL so that (if so configured)
         * it goes through the tunnel servlet, and (if so configured)
         * the tool ID (pid) is included as a parameter in the URL.
         */
        private String rewriteURL(String givenurl)
        {
            // TODO: What about URLs to other servers, not this one?
            // TODO: What about parsing the URL fully correctly (not using indexOf)?
            String url = givenurl;
            if (url == null || url.length() == 0) return url;
            
            // make sure that the current tool ID (pid) is a request parameter of the URL.
            if (m_passthroughPID)
            {
                String pid = m_req.getParameter("pid");
                if (pid != null && pid.length() > 0)
                {
                    // only append it if its not already part of the URL
                    if (url.indexOf("pid=") < 0)
                    {
	        		    if (url.indexOf("?") < 0)
	        		    {
	        		        url = url + "?";
	        		    }
	        		    else
	        		    {
	        		        url = url + "&";
	        		    }
	        		    
	        		    url = url + "pid=" + pid;
                    }
                }
            }  
            
            if (m_forceSharedHttpSessionAcrossWebapps)
            {
	            // check if the given URL already goes through the tunnel servlet
	            if (url.indexOf("/xtunnel/") < 0)
	            {            
		            // rewrite the URL to go through the tunnel, by prepending
		            // "/xtunnel" to the path part of the URL.
		            
		            // skip past the "//" part of the URL (if present)
		            int doInsertAt = 0;
		            if (url.indexOf("//") >= 0)
		            {
		                doInsertAt = url.indexOf("//")+2;
		            }
		            
		            // find the next "/", which starts the path of the URL,
		            // and insert the tunnel at that point
		            doInsertAt = url.indexOf("/", doInsertAt);
		            if (doInsertAt >= 0)
		            {
		                
		                // found a path, insert the tunnel at the start of the path
		                url = url.substring(0, doInsertAt) + "/xtunnel" + url.substring(doInsertAt, url.length());
		            }
	            }
            }
            
            if (Logger.isDebugEnabled() && !url.equals(givenurl))
            {
                Logger.debug(this+".rewriteURL: OLD URL: " + givenurl + " NEW URL: " + url);
                return url;
            }
            else
            {
                return url;
            }
        }
    } // class WrappedResponse

} // class SetupSakaiServicesFilter

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/servletfilter/SetupSakaiServicesFilter.java,v 1.9.2.3 2005/03/21 15:19:09 lance.indiana.edu Exp $
*
**********************************************************************************/

