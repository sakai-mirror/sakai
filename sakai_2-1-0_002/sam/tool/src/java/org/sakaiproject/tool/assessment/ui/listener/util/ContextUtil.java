/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.assessment.ui.listener.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>Description: Action Listener helper utility</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id$
 */

public class ContextUtil
{

  private static Log log = LogFactory.getLog(ContextUtil.class);

  private static ServletContext M_servletContext = null;
  /**
   * Determine if we have been passed a parameter ending in the param string,
   * else null.  We are doing an endsWith test, since the default JSF renderer
   * embeds the parent identity in the HTML id string; we look for the id that was
   * specified in the JSF.
   *
   *
   * @param lookup JSF id String
   * @return String the full parameter
   */
  public static String lookupParam(String lookup)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map requestParams = context.getExternalContext().
                        getRequestParameterMap();

    Iterator iter = requestParams.keySet().iterator();
    while (iter.hasNext())
    {
      String currKey = (String) iter.next();
      if (currKey.endsWith(lookup))
      {
        return (String) requestParams.get(currKey);
      }
    }
    return null;
  }
  /**
   * Determine if we have been passed a parameter that contains a given string,
   * else null. Typically this would be where you want to check for one of a set
   * of similar commandLinks or commandButtons, such as the sortBy headings in
   * evaluation.
   *
   * @param paramPart String to look for
   * @return String last part of full parameter, corresponding to JSF id
   */
  public static String paramLike(String paramPart)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map requestParams = context.getExternalContext().
                        getRequestParameterMap();

    Iterator iter = requestParams.keySet().iterator();
    while (iter.hasNext())
    {
      String currKey = (String) iter.next();

      int location = currKey.indexOf(paramPart);
      if (location > -1)
      {
        return currKey.substring(location);
      }
    }
    return null;
  }

  /**
   * Determine if we have been passed a parameter that contains a given string,
   * return ArrayList of these Strings, else return empty list.
   *
   * Typically this would be where you want to check for one of a set
   * of similar radio buttons commandLinks or commandButtons.
   *
   * @param paramPart String to look for
   * @return ArrayList of last part Strings of full parameter, corresponding to JSF id
   */
  public static ArrayList paramArrayLike(String paramPart)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map requestParams = context.getExternalContext().
                        getRequestParameterMap();
    ArrayList list = new ArrayList();

    Iterator iter = requestParams.keySet().iterator();
    while (iter.hasNext())
    {
      String currKey = (String) iter.next();

      int location = currKey.indexOf(paramPart);
      if (location > -1)
      {
        list.add(currKey.substring(location));
      }
    }
    return list;

  }

  /**
 * Determine if we have been passed a parameter that contains a given string,
 * else null. Typically this would be where you want to check for one of a set
 * of similar commandLinks or commandButtons, such as the sortBy headings in
 * evaluation.
 *
 * @param paramPart String to look for
 * @return String the value of the first hit
 */
public static String paramValueLike(String paramPart)
{
  FacesContext context = FacesContext.getCurrentInstance();
  Map requestParams = context.getExternalContext().
                      getRequestParameterMap();

  Iterator iter = requestParams.keySet().iterator();
  while (iter.hasNext())
  {
    String currKey = (String) iter.next();

    int location = currKey.indexOf(paramPart);
    if (location > -1)
    {
      return (String) requestParams.get(currKey);
    }
  }
  return null;
}

/**
 * Determine if we have been passed a parameter that contains a given string,
 * return ArrayList of the corresponding values, else return empty list.
 *
 * Typically this would be where you want to check for one of a set
 * of similar radio buttons commandLinks or commandButtons.
 *
 * @param paramPart String to look for
 * @return ArrayList of corresponding values
 */
public static ArrayList paramArrayValueLike(String paramPart)
{
  FacesContext context = FacesContext.getCurrentInstance();
  Map requestParams = context.getExternalContext().
                      getRequestParameterMap();
  ArrayList list = new ArrayList();

  Iterator iter = requestParams.keySet().iterator();
  while (iter.hasNext())
  {
    String currKey = (String) iter.next();

    int location = currKey.indexOf(paramPart);
    if (location > -1)
    {
      list.add((String) requestParams.get(currKey));
    }
  }
  return list;

}



  /**
   * Helper method to look up backing bean.
   * Don't forget to cast!
   *   e.g. (TemplateBean) ContextUtil.lookupBean("template")
   * @param context the faces context
   * @return the backing bean
   * @throws FacesException
   */
  public static Serializable lookupBean(String beanName)
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ApplicationFactory factory = (ApplicationFactory) FactoryFinder.
                                 getFactory(
                                 FactoryFinder.APPLICATION_FACTORY);
    Application application = factory.getApplication();
    Serializable bean = (Serializable)
                        application.getVariableResolver().resolveVariable(
                        facesContext, beanName);
    return bean;
  }

  /**
   * Helper method to look up backing bean, when OUTSIDE faces in a servlet.
   * Don't forget to cast!
   *   e.g. (TemplateBean) ContextUtil.lookupBean("template")
   *
   * @param beanName
   * @param request servlet request
   * @param response servlet response
   * @return the backing bean
   */
  public static Serializable lookupBeanFromExternalServlet(String beanName,
    HttpServletRequest request, HttpServletResponse response)
  {
    // prepare lifecycle
    LifecycleFactory lFactory = (LifecycleFactory)
        FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
    Lifecycle lifecycle =
        lFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

    FacesContextFactory fcFactory = (FacesContextFactory)
        FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

    // in the integrated environment, we can't get the ServletContext from the
    // HttpSession of the request - because the HttpSession is webcontainer-wide,
    // its not tied to a particular servlet.
    ServletContext servletContext = M_servletContext;
     if (servletContext == null)
    {
    	servletContext = request.getSession().getServletContext();
    }

    FacesContext facesContext =
        fcFactory.getFacesContext(servletContext, request, response, lifecycle);

    ApplicationFactory factory = (ApplicationFactory) FactoryFinder.
                                 getFactory(
                                 FactoryFinder.APPLICATION_FACTORY);
    Application application = factory.getApplication();
    Serializable bean = (Serializable)
                        application.getVariableResolver().resolveVariable(
                        facesContext, beanName);
    return bean;
  }
	/**
	 * Called by LoginServlet
	 */
	public static void setServletContext(ServletContext context)
	{
		M_servletContext = context;
	}


  /**
  * Gets a localized message string based on the locale determined by the
  * FacesContext.
  * @param key The key to look up the localized string
  */
  public static String getLocalizedString(String bundleName, String key) {
    Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    ResourceBundle rb = ResourceBundle.getBundle(bundleName, locale);
    return rb.getString(key);
  }

}