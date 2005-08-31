/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The Regents of the University of Michigan,
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

package org.sakaiproject.tool.section.jsf;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.sakaiproject.tool.section.jsf.backingbean.MessagingBean;

public class JsfUtil {
	
	public static Locale getLocale() {
		return FacesContext.getCurrentInstance().getViewRoot().getLocale();
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
		return context.getApplication().getVariableResolver().resolveVariable(context, name);
	}

	public static void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

    public static void addInfoMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
	}

    public static void addRedirectSafeMessage(String message) {
        MessagingBean mb = (MessagingBean)resolveVariable("messagingBean");
        // We only send informational messages across pages.
        mb.addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

	public static String getLocalizedMessage(String key) {
        String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
        ResourceBundle rb = ResourceBundle.getBundle(bundleName, getLocale());
        return rb.getString(key);
	}

	public static String getStringFromParam(String string) {
		return (String)FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get(string);
	}
}



/**********************************************************************************
 * $Id$
 *********************************************************************************/
