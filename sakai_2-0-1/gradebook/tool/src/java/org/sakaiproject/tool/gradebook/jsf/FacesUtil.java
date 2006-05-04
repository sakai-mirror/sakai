/**********************************************************************************
*
* $Header: /cvs/sakai2/gradebook/tool/src/java/org/sakaiproject/tool/gradebook/jsf/FacesUtil.java,v 1.4 2005/05/26 18:04:56 josh.media.berkeley.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The MIT Corporation
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

package org.sakaiproject.tool.gradebook.jsf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.gradebook.ui.MessagingBean;

/**
 * A noninstantiable utility class, because every JSF project needs one.
 */
public class FacesUtil {
	private static final Log logger = LogFactory.getLog(FacesUtil.class);

	// Enforce noninstantiability.
	private FacesUtil() {
	}

	/**
	 * If the JSF h:commandLink component includes f:param children, those name-value pairs
	 * are put into the request parameter map for later use by the action handler. Unfortunately,
	 * the same isn't done for h:commandButton. This is a workaround to let arguments
	 * be associated with a button.
	 *
	 * Because action listeners are guaranteed to be executed before action methods, an
	 * action listener can use this method to update any context the action method might need.
	 */
	public static final Map getEventParameterMap(FacesEvent event) {
		Map parameterMap = new HashMap();
		List children = event.getComponent().getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext(); ) {
			Object next = iter.next();
			if (next instanceof UIParameter) {
				UIParameter param = (UIParameter)next;
				parameterMap.put(param.getName(), param.getValue());
			}
		}
		if (logger.isDebugEnabled()) logger.debug("parameterMap=" + parameterMap);
		return parameterMap;
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

	/**
	 * Because POST arguments aren't carried over redirects, the easiest way to
	 * get bookmarkable URLs is to use "h:outputLink" rather than "h:commandLink" or
	 * "h:commandButton", and to add query string parameters via "f:param". However,
	 * if the value of the output link is something like "editAsg.jsf", we've introduced
	 * untestable assumptions about the local naming and navigation configurations.
	 * This method will safely return the output link value corresponding to the
	 * specified "from-outcome" view ID.
	 */
	public static final String getActionUrl(String action) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getViewHandler().getActionURL(context, action);
	}

	/**
	 * Methods to centralize our approach to messages, since we may
	 * have to adapt the default Faces implementation.
	 */
	public static void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

    public static void addMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
	}

	public static void addUniqueErrorMessage(String message) {
		if (!hasMessage(message)) {
			addErrorMessage(message);
		}
	}

	private static boolean hasMessage(String message) {
		for(Iterator iter = FacesContext.getCurrentInstance().getMessages(); iter.hasNext();) {
			FacesMessage facesMessage = (FacesMessage)iter.next();
			if(facesMessage.getSummary() != null && facesMessage.getSummary().equals(message)) {
				return true;
			}
		}
		return false;
	}

    /**
     * We want to use standard faces messaging for intra-page messages, such
     * as validation checking, but we want to use the custom messaging approach
     * for inter-page messaging.  So, for now we're going to add the inter-page
     * messages to the custom MessagingBean.
     *
     * @param message
     */
    public static void addRedirectSafeMessage(String message) {
        MessagingBean mb = (MessagingBean)resolveVariable("messagingBean");
        // We only send informational messages across pages.
        mb.addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    /**
     * JSF 1.1 provides no way to cleanly discard input fields from a table when
     * we know we won't use them. Ideally in such circumstances we'd specify an
     * "immediate" action handler (to skip unnecessary validation checks and
     * model updates), and then overwrite any existing values. However,
     * JSF absolutely insists on keeping any existing input components as
     * they are if validation and updating hasn't been done. When the table
     * is re-rendered, all of the readonly portions of the columns will be
     * refreshed from the backing bean, but the input fields will
     * keep their now-incorrect values.
     *
     * <p>
     * The easiest practical way to deal with this limitation is to avoid
     * "immediate" actions when a table contains input fields, avoid side-effects
     * from the bogus model updates, and stick the user with the inconvenience
     * of unnecessary validation errors.
     *
     * <p>
     * The only other solution we've found is to have the backing bean bind to
     * the data table component (which just means storing a transient
     * pointer to the UIData or HtmlDataTable when it's passed to the
     * bean's "setTheDataTable" method), and then to have the action handler call
     * this method to walk the table, look for UIInputs on each row, and
     * perform the necessary magic on each to force reloading from the data model.
     *
     * <p>
     * Usage:
     * <pre>
     *   private transient HtmlDataTable dataTable;
     *   public HtmlDataTable getDataTable() {
     *     return dataTable;
     *   }
     *   public void setDataTable(HtmlDataTable dataTable) {
     *     this.dataTable = dataTable;
     *   }
     *   public void processImmediateIdSwitch(ActionEvent event) {
     *      // ... modify the current ID ...
     *      FacesUtil.clearAllInputs(dataTable);
     *   }
     * </pre>
     */
     public static void clearAllInputs(UIComponent component) {
     	if (logger.isDebugEnabled()) logger.debug("clearAllInputs " + component);
     	if (component instanceof UIInput) {
			if (logger.isDebugEnabled()) logger.debug("  setValid, setValue, setLocalValueSet, setSubmittedValue");
			UIInput uiInput = (UIInput)component;
			uiInput.setValid(true);
			uiInput.setValue(null);
			uiInput.setLocalValueSet(false);
			uiInput.setSubmittedValue(null);

     	} else if (component instanceof UIData) {
     		UIData dataTable = (UIData)component;
			int first = dataTable.getFirst();
			int rows = dataTable.getRows();
			int last;
			if (rows == 0) {
				last = dataTable.getRowCount();
			} else {
				last = first + rows;
			}
			for (int rowIndex = first; rowIndex < last; rowIndex++) {
				dataTable.setRowIndex(rowIndex);
				if (dataTable.isRowAvailable()) {
					for (Iterator iter = dataTable.getChildren().iterator(); iter.hasNext(); ) {
						clearAllInputs((UIComponent)iter.next());
					}
				}
			}
		} else {
			for (Iterator iter = component.getChildren().iterator(); iter.hasNext(); ) {
				clearAllInputs((UIComponent)iter.next());
			}
		}
     }

    /**
     * Gets a localized message string based on the locale determined by the
     * FacesContext.
     * @param key The key to look up the localized string
     */
    public static String getLocalizedString(FacesContext context, String key) {
        String bundleName = context.getApplication().getMessageBundle();
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ResourceBundle rb = ResourceBundle.getBundle(bundleName, locale);
        return rb.getString(key);
    }

}
/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/gradebook/tool/src/java/org/sakaiproject/tool/gradebook/jsf/FacesUtil.java,v 1.4 2005/05/26 18:04:56 josh.media.berkeley.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
