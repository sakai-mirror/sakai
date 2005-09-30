/**********************************************************************************
*
* $Header: /cvs/sakai2/syllabus/syllabus-app/src/java/org/sakaiproject/tool/syllabus/FileUploadFilter.java,v 1.1 2005/05/06 14:42:28 cwen.iupui.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.syllabus;

import java.io.FileWriter;
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


public class FileUploadFilter implements Filter 
{
  private int sizeThreshold = 2;
  private long sizeMax = 2048;
  private String repositoryPath = "/fileUpload";
  private FileWriter fw;
  
  public void init(FilterConfig config) throws ServletException 
  {
    repositoryPath = config.getInitParameter("org.sakaiproject.tool.syllabus.FileUploadFilter.repositoryPath");
    
    try 
    {
      String paramValue = config.getInitParameter("org.sakaiproject.tool.syllabus.FileUploadFilter.sizeThreshold");

      if (paramValue != null) 
        sizeThreshold = Integer.parseInt(paramValue);
      paramValue = config.getInitParameter("org.sakaiproject.tool.syllabus.FileUploadFilter.sizeMax");
      if (paramValue != null) 
        sizeMax = Long.parseLong(paramValue);
    }
    catch (NumberFormatException ex) 
    {
      ServletException servletEx = new ServletException();
      servletEx.initCause(ex);
      throw servletEx;
    }
  }
  
  public void destroy() 
  {
  }
  
  public void doFilter(ServletRequest request, 
          ServletResponse response, FilterChain chain) 
  				throws IOException, ServletException 
  {
    if (!(request instanceof HttpServletRequest)) 
    {
      chain.doFilter(request, response);
      return;
    }
      
    HttpServletRequest httpRequest = (HttpServletRequest) request;
      
    boolean isMultipartContent = FileUpload.isMultipartContent(httpRequest);
    if (!isMultipartContent) 
    {
      chain.doFilter(request, response);
      return;
    }
      
    DiskFileUpload upload = new DiskFileUpload();
    if (repositoryPath != null) 
      upload.setRepositoryPath(repositoryPath);
      
    try 
    {
      List list = upload.parseRequest(httpRequest);
      final Map map = new HashMap();
      for (int i = 0; i < list.size(); i++) 
      {
        FileItem item = (FileItem) list.get(i);
        String str = item.getString();
        if (item.isFormField())
          map.put(item.getFieldName(), new String[] { str });
        else
          httpRequest.setAttribute(item.getFieldName(), item);
      }
          
      chain.doFilter(new 
                  HttpServletRequestWrapper(httpRequest) {
              public Map getParameterMap() {
                  return map;
              }                   
              public String[] getParameterValues(String name) {
                  Map map = getParameterMap();
                  return (String[]) map.get(name);
              }
              public String getParameter(String name) {
                  String[] params = getParameterValues(name);
                  if (params == null) return null;
                  return params[0];
              }
              public Enumeration getParameterNames() {
                  Map map = getParameterMap();
                  return Collections.enumeration(map.keySet());
              }
          }, response);
    } 
    catch (FileUploadException ex) 
    {
      ServletException servletEx = new ServletException();
      servletEx.initCause(ex);
      throw servletEx;
    }      
  }   
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/syllabus/syllabus-app/src/java/org/sakaiproject/tool/syllabus/FileUploadFilter.java,v 1.1 2005/05/06 14:42:28 cwen.iupui.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
