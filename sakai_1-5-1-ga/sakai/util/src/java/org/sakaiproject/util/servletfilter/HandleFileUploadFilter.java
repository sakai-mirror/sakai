/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/servletfilter/HandleFileUploadFilter.java,v 1.1.2.4 2005/04/07 22:34:32 janderse.umich.edu Exp $
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;

/**
 * Filter that handles file uploads (multipart/mime) using Apache commons-fileupload.
 * The parameters are made available in the usual fashion (they can be retrieved
 * from the request object). The uploaded files are available through
 * the attributes of the request object:
 * 
 * FileItem item = (FileItem) request.getAttribute("htmlInputFieldName");
 * System.out.println("Filename: " + item.getName() + " File length: " + item.getSize());
 * 
 * One parameter of the filter, the maximum size of allowed file upload,
 * can be configured through the Sakai configuration (as "content.upload.max", in megabytes).
 * 
 * The filter accepts these init parameters:
 * 
 * disabled - If true, the filter will be DISABLED (not happen) for the given path.
 *    This allows someone to use a different file upload filter (MyFaces for example) if they wish, instead
 *    of this filter.
 * 
 * sizeMax - Maximum allowed size (in bytes) of a single request.  Limits the size of uploaded files.
 * 
 * encoding - The character encoding to use for parameters, etc.  Defaults to UTF-8.  Should match
 *    the character encoding specified by the HTML page that contains the file upload form element.
 * 
 * repositoryPath - Where to store the temporary upload files.  Defaults to the webserver temp directory.
 * 
 * sizeThreshold - The size (in bytes) to switch from in-memory to on-disk storage.
 * 
 */
public class HandleFileUploadFilter implements Filter
{
    private FilterConfig m_filterConfig = null;
    
    private int m_sizeThreshold = -1;

    private long m_sizeMax = 1L * 1024L * 1024L;

    private String m_repositoryPath;
    
    private String m_encoding = "UTF-8";
    
    /** Whether the filter is disabled (so it does nothing, doesn't parse files */
    private boolean m_disabled = false;

    public void init(FilterConfig config) throws ServletException
    {
        m_filterConfig = config;
        
        String encoding = config.getInitParameter("encoding");
        if (encoding != null && encoding.length() > 0) m_encoding = encoding;
        
        String disabled = config.getInitParameter("disabled");
        if ("true".equals(disabled) || "yes".equals(disabled))
        {
            m_disabled = true;
        }
        
        m_repositoryPath = config.getInitParameter("repositoryPath");
        try
        {
            String paramValue = config.getInitParameter("sizeThreshold");
            if (paramValue != null) m_sizeThreshold = Integer.parseInt(paramValue);
            
            paramValue = config.getInitParameter("sizeMax");
            if (paramValue != null) m_sizeMax = Long.parseLong(paramValue);
 
        }
        catch (NumberFormatException ex)
        {
            ServletException servletEx = new ServletException();
            servletEx.initCause(ex);
            throw servletEx;
        }
        //System.out.println("*** repositoryPath="+repositoryPath);
    }

    public void destroy()
    {
    }
    
    /* this filter is disabled (shouldn't execute) on this request,
     * if this filter itself is configured disabled, _OR_ if 
     * the filter already appears somewhere else on this request.
     */ 
    protected boolean isFilterDisabled(HttpServletRequest req)
    {
        if (m_disabled || req.getAttribute(this.getClass().getName()) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        if (!(request instanceof HttpServletRequest))
        {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        boolean disabled = isFilterDisabled(httpRequest);
        // mark the request showing that this filter has already been executed
        // so that it is not executed again even if it appears elsewhere on the filter chain.
        request.setAttribute(this.getClass().getName(), "true");       
        
        // check whether the filter is disabled
        if (disabled)
        {
            chain.doFilter(request, response);
            return;
        }
         
        boolean isMultipartContent = FileUpload.isMultipartContent(httpRequest);
        if (!isMultipartContent)
        {
            chain.doFilter(request, response);
            return;
        }

        // call HERE, NOT in init(), since this calls into the ServerConfigurationService.
        // that service is not available at init() time (since it needs to be init()ed first!).
        m_sizeMax = getSizeMax();
        
        // Map of request parameters, parsed parameters, and uploaded files
        Map map = new HashMap();        
        
        DiskFileUpload upload = new DiskFileUpload();
        upload.setHeaderEncoding(m_encoding);
        if (m_repositoryPath != null) upload.setRepositoryPath(m_repositoryPath);
        if (m_sizeThreshold > 0) upload.setSizeThreshold(m_sizeThreshold);
        if (m_sizeMax > 0) upload.setSizeMax(m_sizeMax);
        
        try
        {
            // parse multipart encoded parameters
            List list = upload.parseRequest(httpRequest);
            for (int i = 0; i < list.size(); i++)
            {
                FileItem item = (FileItem) list.get(i);
                if (item.isFormField())
                {
                   String str = item.getString(m_encoding);
                   map.put(item.getFieldName(), new String[] { str });
                    //System.out.println("item is form field, field name=
                    // "+item.getFieldName()+":"+str);
                }
                else
                {
                    httpRequest.setAttribute(item.getFieldName(), item);
                    //System.out.println("item is NOT form field,
                    // fieldname="+item.getFieldName()
                    // +"name="+item.getName()+":"+item);
                }
            }
        }
        catch (FileUploadBase.SizeLimitExceededException ex)
        {
            System.out.println(ex.getMessage());
            //ServletException servletEx = new ServletException();
            //servletEx.initCause(ex);
            //throw servletEx;
        }
        catch (FileUploadException ex)
        {
            System.out.println(ex.getMessage());
            ServletException servletEx = new ServletException();
            servletEx.initCause(ex);
            throw servletEx;           
        }
 
		// add any parameters that were in the URL - make sure to get multiples
		for (Enumeration e = httpRequest.getParameterNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			String[] values = httpRequest.getParameterValues(name);
			map.put(name, values);				
		}
        
        chain.doFilter (new RequestWrapper(httpRequest, map), response);
    }
    
    
    public String toString()
    {
        if (m_filterConfig == null) return ("HandleFileUploadFilter()");
        StringBuffer sb = new StringBuffer("HandleFileUploadFilter(");
        sb.append(m_filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    /** try to read the maximum allowed upload size from the Sakai configuration */
    private long getSizeMax()
    {
        try
        {
            long sizeMaxInMB = (long) ServerConfigurationService.getInt("content.upload.max", -1);
            if (sizeMaxInMB > 0) return sizeMaxInMB * 1024L * 1024L; 
        }
        catch (Exception e)
        {
        }
        
        return m_sizeMax;
    }
    
    /** 
     * Request wrapper that exposes the parameters parsed from 
     * the multipart/mime file upload (along with parameters
     * from the request).
     */
    static class RequestWrapper extends HttpServletRequestWrapper 
    {
        private Map map;
        
        public RequestWrapper(HttpServletRequest req, Map paramMap)
        {
           super (req);
           map = paramMap;
        }
        
        public Map getParameterMap()
        {
            return map;
        }

        // busywork follows ... should have been part of the wrapper
        public String[] getParameterValues(String name)
        {
            String[] ret = null;
            //System.out.println("**#1."+name);
            Map map = getParameterMap();
            return (String[]) map.get(name);
        }

        public String getParameter(String name)
        {
            //System.out.println("**#2."+name);
            String[] params = getParameterValues(name);
            if (params == null) return null;
            return params[0];
        }

        public Enumeration getParameterNames()
        {
            //System.out.println("**#3. keyset");
            Map map = getParameterMap();
            return Collections.enumeration(map.keySet());
        }
    }
}

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/servletfilter/HandleFileUploadFilter.java,v 1.1.2.4 2005/04/07 22:34:32 janderse.umich.edu Exp $
*
**********************************************************************************/
