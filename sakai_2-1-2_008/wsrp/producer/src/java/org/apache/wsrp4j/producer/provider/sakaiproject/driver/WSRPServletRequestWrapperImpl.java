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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
* WSRP4J specific servlet response implementation. This allows
* to set the locale.
* 
* @author Stephan.Laertz@de.ibm.com  
* @author Richard Jacob
**/
public class WSRPServletRequestWrapperImpl extends HttpServletRequestWrapper
{
    private Locale locale = null;
    private String mimeType = null;
    private Map parameters = null;
    private boolean methodOverride = false;
    private String method = null;
    
    // The extra path information extracted from the navigational State, if it has any
    private String pathInfo = null;
    
    /**
      Constructor
      @param request HttpServletRequest
      @param window PortletWindow
    */
    public WSRPServletRequestWrapperImpl(HttpServletRequest request, 
                    Locale locale, String mimeType, Map parameters,
                    String pathInfo)
    {
        super(request);

        this.locale = locale;        
        this.mimeType = mimeType;
        this.parameters = parameters;
        this.pathInfo = pathInfo;
    }

    public Locale getLocale()
    {
        return this.locale;
    }

    public Enumeration getLocales()
    {
        Vector v = new Vector();
        v.add(this.locale);
        return v.elements();
    }        
    
    public String getContentType() {
        return this.mimeType+"; charset=UTF-8";  // TODO
    }
    
    public Map getParameterMap() {
    	return parameters;
    }
    
	public String getParameter(String name) 
	{
        String [] values = getParameterValues(name);
        if ((values != null) && (values.length > 0)) 
            return values[0];
        else 
            return null;
	}
    
	public Enumeration getParameterNames() 
	{
		return Collections.enumeration(this.getParameterMap().keySet());
	}

	public String[] getParameterValues(String name) 
	{
		return (String[]) this.getParameterMap().get(name);
	}
    
    protected void setMethod(String method)
    {
        if (!method.equalsIgnoreCase(getMethod())) {
            this.method = method;
            methodOverride = true;
        }
    }
    public String getMethod()
    {
        if (methodOverride)
            return method;
        else return super.getMethod();
    }
        
    public String getPathInfo()
    {
        return pathInfo;
    }
}
