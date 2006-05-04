/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California and The Regents of the University of Michigan
*
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

package org.sakaiproject.tool.section.jsf;

import java.sql.Time;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.jsf.util.ConversionUtil;
import org.sakaiproject.tool.section.jsf.backingbean.MessagingBean;

/**
 * A utility to help deal with common tasks in JSF.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class JsfUtil {
	private static final Log log = LogFactory.getLog(JsfUtil.class);

	public static final String TIME_PATTERN_DISPLAY = "h:mm";
	public static final String TIME_PATTERN_LONG = "h:mm a";
	public static final String TIME_PATTERN_SHORT = "h a";

	public static Locale getLocale() {
		Locale locale;
		try {
			locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		} catch (Exception e) {
			log.warn("Could not get locale from FacesContext (perhaps this was this called during testing?)");
			locale = Locale.US;
		}
		return locale;
	}
	
	/**
	 * To cut down on configuration noise, allow access to request-scoped beans from
	 * session-scoped beans, and so on, this method lets the caller try to find
	 * anything anywhere that Faces can look for it.
	 *
	 * WARNING: If what you're looking for is a managed bean and it isn't found,
	 * it will be created as a result of this call.
	 */
	public static final Object resolveVariable(String name) {
		FacesContext context = FacesContext.getCurrentInstance();
		Object result = context.getApplication().getVariableResolver().resolveVariable(context, name);
		if(log.isDebugEnabled()) log.debug("JSF variable " + name + " resolved to " + result);
		return result;
	}

	public static void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

	public static void addErrorMessage(String message, String componentId) {
		FacesContext.getCurrentInstance().addMessage(componentId, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

	public static void addInfoMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
	}

    public static void addRedirectSafeInfoMessage(String message) {
        MessagingBean mb = (MessagingBean)resolveVariable("messagingBean");
        mb.addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    public static void addRedirectSafeWarnMessage(String message) {
        MessagingBean mb = (MessagingBean)resolveVariable("messagingBean");
        mb.addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
    }

    public static String getLocalizedMessage(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        String bundleName;
        
        // We need to be able to call this from tests.  Is there a way to manually construct a FacesContext?
        if(context == null) {
        	bundleName = "org.sakaiproject.tool.section.bundle.Messages";
        } else {
        	bundleName = context.getApplication().getMessageBundle();
        }
        ResourceBundle rb = ResourceBundle.getBundle(bundleName, getLocale());
        return rb.getString(key);
	}
	
    public static String getLocalizedMessage(String key, String[] params) {
    	String rawString = getLocalizedMessage(key);
        MessageFormat format = new MessageFormat(rawString);
        return format.format(params);
    }


	public static String getStringFromParam(String string) {
		return (String)FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get(string);
	}
	
	/**
	 * Converts a string and a boolean (am) into a java.sql.Time object.
	 * 
	 * @param str
	 * @param am
	 * @return
	 */
	public static Time convertStringToTime(String str, boolean am) {
		if(StringUtils.trimToNull(str) == null) {
			return null;
		}

		// Set the am/pm flag to ensure that the time is parsed properly
		if(am) {
			str = str + " AM";
		} else {
			str = str + " PM";
		}
		
		String pattern = (str.indexOf(':') != -1) ? JsfUtil.TIME_PATTERN_LONG : JsfUtil.TIME_PATTERN_SHORT;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date;
		try {
			date = sdf.parse(str);
		} catch (ParseException pe) {
			throw new RuntimeException("A bad date made it through validation!  This should never happen!");
		}
		return ConversionUtil.convertDateToTime(date, am);
	}

}